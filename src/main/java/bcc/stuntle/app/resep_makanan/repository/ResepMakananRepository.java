package bcc.stuntle.app.resep_makanan.repository;

import bcc.stuntle.constant.ResepMakananRedisConstant;
import bcc.stuntle.entity.ResepMakanan;
import bcc.stuntle.exception.DataTidakDitemukanException;
import bcc.stuntle.util.ObjectMapperUtils;
import bcc.stuntle.util.PageableUtils;
import bcc.stuntle.util.QueryUtils;
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
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ResepMakananRepository {

    @Autowired
    private IResepMakananRepository repository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<ResepMakanan> create(ResepMakanan resepMakanan){
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate
                .keys(ResepMakananRedisConstant.ALL)
                .flatMap((key) -> ops.delete(key).doOnNext((d) -> log.info("remove key {}", d)))
                .then(this.repository.save(resepMakanan));
    }

    public Mono<Page<ResepMakanan>> getList(Long id, Pageable pageable) {
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(ResepMakananRedisConstant.GET_LIST, id, PageableUtils.toString(pageable));
        Query query = Query.query(
                CriteriaDefinition.from(
                        Criteria.where("fk_faskes_id").is(id)
                                .and(
                                        Criteria.where("deleted_at").isNull()
                                )
                )
        ).limit(pageable.getPageSize()).offset(pageable.getOffset());
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on ResepMakananRepository.getList");
                    return this.template
                            .select(query, ResepMakanan.class)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data resep makanan tidak ditemukan")))
                            .collectList()
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var listObject = ObjectMapperUtils.readListValue(listStr, Object.class);
                    var t1 = ObjectMapperUtils.mapper.convertValue(listObject.get(0), new TypeReference<List<ResepMakanan>>() {
                    });
                    var t2 = (Integer) listObject.get(1);
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, pageable, t2)));
                });
    }

    public Mono<Page<ResepMakanan>> getList(Long id, ResepMakanan resepMakanan, Pageable pageable){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(ResepMakananRedisConstant.GET_LIST_RESEP_MAKANAN, resepMakanan, pageable);
        Optional<Query> optQuery = QueryUtils.createQuerySearch(resepMakanan, false);
        Query query = optQuery.orElse(Query.empty());
        query = query.offset(pageable.getOffset()).limit(pageable.getPageSize());
        var finQuery = query;
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on ResepMakananRepository.getList(resepMakanan, pageable)");
                    return this.template
                            .select(finQuery, ResepMakanan.class)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data resep makanan tidak ditemukan")))
                            .collectList()
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var listObject = ObjectMapperUtils.readListValue(listStr, Object.class);
                    var t1 = ObjectMapperUtils.mapper.convertValue(listObject.get(0), new TypeReference<List<ResepMakanan>>() {});
                    var t2 = (Integer) listObject.get(1);
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, pageable, t2)));
                });
    }

    public Mono<Void> delete(Long id){
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate
                .keys(ResepMakananRedisConstant.ALL)
                .flatMap((key) -> ops.delete(key).doOnNext((d) -> log.info("remove key {}", d)))
                .then(
                        this.repository
                                .findOne(Example.of(ResepMakanan.builder().id(id).build()))
                                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data resep makanan tidak ditemukan")))
                                .flatMap((e) -> {
                                    e.setDeletedAt(LocalDate.now());
                                    return this.repository.save(e);
                                })
                                .then(Mono.empty())
                );
    }

    public Mono<Long> count(Example<ResepMakanan> example){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(ResepMakananRedisConstant.COUNT, example);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on ResepMakananRepository.count(example)");
                    return this.repository
                            .count(example)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((nStr) -> {
                    var number = ObjectMapperUtils.readValue(nStr, Integer.class);
                    return ops.set(key, nStr, Duration.ofMinutes(1))
                            .then(Mono.just(number.longValue()));
                });
    }
}
