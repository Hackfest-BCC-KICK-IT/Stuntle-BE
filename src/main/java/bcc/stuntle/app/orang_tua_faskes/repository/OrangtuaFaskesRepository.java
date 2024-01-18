package bcc.stuntle.app.orang_tua_faskes.repository;

import bcc.stuntle.app.ortu.repository.OrangtuaRepository;
import bcc.stuntle.constant.OrangtuaFaskesRedisConstant;
import bcc.stuntle.entity.OrangtuaFaskes;
import bcc.stuntle.exception.DataTidakDitemukanException;
import bcc.stuntle.util.ObjectMapperUtils;
import bcc.stuntle.util.PageableUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class OrangtuaFaskesRepository {

    @Autowired
    private IOrangtuaFaskesRepository repository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Autowired
    private OrangtuaRepository ortuRepository;

    public Mono<OrangtuaFaskes> create(OrangtuaFaskes ortuFaskes){
        return this.template
                .insert(OrangtuaFaskes.class)
                .using(ortuFaskes);
    }

    /***
     * This method get List of OrangtuaFaskes data with filter faskesId
     */
    public Mono<Page<OrangtuaFaskes>> getList(Long faskesId, Pageable pageable){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(OrangtuaFaskesRedisConstant.GET_LIST_FASKES_ID, faskesId,  PageableUtils.toString(pageable));
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on OrangtuaFaskesRepository.getList(faskesId, pageable)");
                    Query query = Query.query(
                            CriteriaDefinition.from(
                                    Criteria.where("fk_faskes_id").is(faskesId)
                            )
                    ).with(pageable);
                    return Mono.from(this.template.select(query, OrangtuaFaskes.class).switchIfEmpty(Mono.error(new DataTidakDitemukanException("data orangtua faskes tidak ditemukan"))).collectList())
                            .zipWith(this.repository.count())
                            .flatMap((t) -> Mono.fromCallable(() -> new PageImpl<>(t.getT1(), pageable, t.getT2())))
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((pageStr) -> {
                    var page = ObjectMapperUtils.readPageValue(pageStr, OrangtuaFaskes.class);
                    var list = page.getContent();
                    var flux = Flux.fromStream(list.stream());
                    return flux.flatMap((ortuFaskes) -> this.ortuRepository.findById(ortuFaskes.getFkOrtuId()).map((ortu) -> {
                        log.info("ortu = {}", ortu);
                        ortuFaskes.setNamaAyah(ortu.getNamaAyah());
                        ortuFaskes.setNamaIbu(ortu.getNamaIbu());
                        return ortuFaskes;
                    })).collectList()
                            .map((listOrtuFaskes) -> {
                                return new PageImpl<>(listOrtuFaskes, page.getPageable(), page.getNumber());
                            })
                            .map(ObjectMapperUtils::writeValueAsString);
                })
                .flatMap((pageStr) -> {
                    Page<OrangtuaFaskes> page = ObjectMapperUtils.readPageValue(pageStr, OrangtuaFaskes.class);
                    return ops.set(key, pageStr, Duration.ofMinutes(1))
                            .then(Mono.just(page));
                });
    }

    /***
     * This method get List of OrangtuaFaskes data with filter faskesId
     */
    public Mono<Page<OrangtuaFaskes>> getListForOrtu(Long ortuId, Pageable pageable){
        var pageableStr = PageableUtils.toString(pageable);
        var key = String.format(OrangtuaFaskesRedisConstant.GET_LIST_ORTU_ID, ortuId, pageableStr);
        var ops = this.redisTemplate.opsForValue();
        return ops
                .get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on OrangtuaFaskesRepository.getListForOrtu");
                    Query query = Query.query(
                            CriteriaDefinition.from(
                                    Criteria.where("fk_ortu_id").is(ortuId)
                            )
                    ).with(pageable);
                    return Mono
                            .from(
                                    this
                                            .template
                                            .select(query, OrangtuaFaskes.class)
                                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data orangtua faskes tidak ditemukan")))
                                            .collectList())
                            .zipWith(this.repository.count())
                            .flatMap((t) -> Mono.fromCallable(() -> new PageImpl<>(t.getT1(), pageable, t.getT2())))
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((pageStr) -> {
                    var page = ObjectMapperUtils.readPageValue(pageStr, OrangtuaFaskes.class);
                    return ops.set(key, pageStr, Duration.ofMinutes(1))
                            .then(Mono.just(page));
                });
    }

    /***
     * This method get List of OrangtuaFaskes data with filter List of orangtua_faskes ids
     */
    public Mono<List<OrangtuaFaskes>> getList(List<Long> id){
        var key = String.format(OrangtuaFaskesRedisConstant.GET_LIST_IDS, id);
        var ops = this.redisTemplate.opsForValue();
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on OrangtuaFaskesRepository.getList");
                    return this
                            .repository
                            .findAllById(id)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data orangtua faskes tidak ditemukan")))
                            .collectList()
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    log.info("masuk");
                    var list = ObjectMapperUtils.readListValue(listStr, OrangtuaFaskes.class);
                    var flux = Flux.fromStream(list.stream());
                    return flux.flatMap((ortuFaskes) -> this.ortuRepository.findById(ortuFaskes.getFkOrtuId()).map((ortu) -> {
                        log.info("ortu = {}", ortu);
                        ortuFaskes.setNamaAyah(ortu.getNamaAyah());
                        ortuFaskes.setNamaIbu(ortu.getNamaIbu());
                        return ortuFaskes;
                    })).collectList();
                })
                .map(ObjectMapperUtils::writeValueAsString)
                .flatMap((listStr) -> {
                    List<OrangtuaFaskes> list = ObjectMapperUtils.readListValue(listStr, OrangtuaFaskes.class);

                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(list));
                });
    }
}
