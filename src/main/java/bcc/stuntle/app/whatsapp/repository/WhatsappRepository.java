package bcc.stuntle.app.whatsapp.repository;

import bcc.stuntle.app.orang_tua_faskes.repository.OrangtuaFaskesRepository;
import bcc.stuntle.constant.WhatsappRedisConstant;
import bcc.stuntle.entity.GrupWhatsapp;
import bcc.stuntle.entity.OrangtuaFaskes;
import bcc.stuntle.exception.DataTidakDitemukanException;
import bcc.stuntle.exception.DatabaseException;
import bcc.stuntle.util.ObjectMapperUtils;
import bcc.stuntle.util.PageableUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class WhatsappRepository {

    @Autowired
    private IWhatsappRepository repository;

    @Autowired
    private OrangtuaFaskesRepository ortuFaskesRepository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<GrupWhatsapp> save(GrupWhatsapp data){
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate
                .keys(WhatsappRedisConstant.ALL)
                .flatMap((key) -> ops.delete(key).doOnNext((d) -> log.info("remove key {}", key)))
                .then(
                        this
                        .repository
                        .save(data)
                );
    }

    public Mono<Void> delete(Long id){
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate
                .keys(WhatsappRedisConstant.ALL)
                .flatMap((key) -> ops.delete(key).doOnNext((d) -> log.info("remove key {}", key)))
                .then(
                        this.repository
                            .findById(id)
                            .flatMap((d) -> {
                                d.setDeletedAt(LocalDate.now());
                                return this.repository.save(d);
                            })
                            .then(Mono.empty())
                );
    }

    public Mono<Page<GrupWhatsapp>> getList(Long faskesId, Pageable page){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(WhatsappRedisConstant.GET_LIST, faskesId, page);
        Query query = Query.query(
                CriteriaDefinition.from(
                        Criteria.where("fk_faskes_id").is(faskesId)
                                .and(
                                        Criteria.where("deleted_at").isNull()
                                )
                )
        )
                .offset(page.getOffset())
                .limit(page.getPageSize());
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on WhatsappRepository.getList(faskesId, page)");
                    return  this
                            .template
                            .select(query, GrupWhatsapp.class)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data whatsapp tidak ditemukan")))
                            .collectList()
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var listObject = ObjectMapperUtils.readListValue(listStr, Object.class);

                    var t1 = ObjectMapperUtils.mapper.convertValue(listObject.get(0), new TypeReference<List<GrupWhatsapp>>() {});
                    var t2 = (Integer) listObject.get(1);

                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, page, t2)));
                });
    }

    public Mono<GrupWhatsapp> get(Example<GrupWhatsapp> ex){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(WhatsappRedisConstant.GET, ex);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on WhatsappRepository.get(example)");
                    return this.repository
                            .findOne(ex)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((grupWhatsappStr) -> {
                    var whatsapp = ObjectMapperUtils.readValue(grupWhatsappStr, GrupWhatsapp.class);
                    return ops.set(key, grupWhatsappStr, Duration.ofMinutes(1))
                            .then(Mono.just(whatsapp));
                });
    }

    public Mono<Page<GrupWhatsapp>> getListForOrtu(Long ortuId, Pageable pageable){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(WhatsappRedisConstant.GET_LIST_ORTU, ortuId, PageableUtils.toString(pageable));
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on WhatsappRepositoru.getListForOrtu(ortuId, pageable)");
                    return this.ortuFaskesRepository
                            .getListForOrtu(ortuId, Pageable.unpaged())
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data whatsapp tidak ditemukan")))
                            .map(Page::getContent)
                            .map((ortuFaskes) -> ortuFaskes.stream().map(OrangtuaFaskes::getFkFaskesId).toList())
                            .map((faskesIds) ->
                                    faskesIds.stream().parallel().map((id) -> {
                                                Query query = Query.query(
                                                        Criteria.where("fk_faskes_id").is(id)
                                                                .and(
                                                                        Criteria.where("deleted_at").isNull()
                                                                )
                                                );
                                                return this.template.select(query, GrupWhatsapp.class);
                                            }
                                    ).toList()
                            )
                            .map((listFluxs) -> listFluxs.stream().reduce((Flux::concat)))
                            .map((d) -> d.orElse(Flux.error(new DatabaseException("data whatsapp tidak ditemukan"))))
                            .flatMap((d) -> d.collectList())
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var listObject = ObjectMapperUtils.readListValue(listStr, Object.class);

                    var t1 = ObjectMapperUtils.mapper.convertValue(listObject.get(0), new TypeReference<List<GrupWhatsapp>>() {});
                    var t2 = (Integer) listObject.get(1);

                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(
                                    new PageImpl<>(t1, pageable, t2)
                            ));
                });
    }
}
