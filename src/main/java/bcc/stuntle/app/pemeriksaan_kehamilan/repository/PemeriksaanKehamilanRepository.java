package bcc.stuntle.app.pemeriksaan_kehamilan.repository;

import bcc.stuntle.constant.PemeriksaanKehamilanRedisConstant;
import bcc.stuntle.entity.DataPemeriksaanKehamilan;
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
import java.util.List;

@Component
@Slf4j
public class PemeriksaanKehamilanRepository {

    @Autowired
    private IPemeriksaanKehamilanRepository repository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<DataPemeriksaanKehamilan> save(Long ortuId, Long faskesId, Long dataKehamilanId, DataPemeriksaanKehamilan data){
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate
            .keys(PemeriksaanKehamilanRedisConstant.ALL)
            .flatMap((key) -> ops.delete(key).doOnNext((d) -> log.info("remove key {}", key)))
                    .then(Mono.fromCallable(() -> {
                        data.setFkOrtuId(ortuId);
                        data.setFkFaskesId(faskesId);
                        data.setFkDataKehamilan(dataKehamilanId);
                        return this.repository.save(data);
                    }))
            .flatMap((v) -> v);
    }

    public Mono<Page<DataPemeriksaanKehamilan>> getList(Long ortuId, Long faskesId, Long dataKehamilanId, Pageable page){
        Query query = Query.query(
                CriteriaDefinition.from(
                        Criteria.where("fk_ortu_id").is(ortuId)
                                .and(
                                        Criteria.where("fk_faskes_id").is(faskesId)
                                )
                                .and(
                                        Criteria.where("fk_data_kehamilan").is(dataKehamilanId)
                                )
                )
        )
        .limit(page.getPageSize())
        .offset(page.getOffset());
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(PemeriksaanKehamilanRedisConstant.GET_LIST, ortuId, faskesId, dataKehamilanId, PageableUtils.toString(page));
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on PemeriksaanKehamilanRepository.getList");
                    return this.template
                            .select(query, DataPemeriksaanKehamilan.class)
                            .switchIfEmpty(Flux.fromIterable(List.of()))
                            .collectList()
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var listObj = ObjectMapperUtils.readListValue(listStr, Object.class);
                    var t1 = ObjectMapperUtils.mapper.convertValue(listObj.get(0), new TypeReference<List<DataPemeriksaanKehamilan>>() {});
                    var t2 = (Integer) listObj.get(1);
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, page, t2)));
                });
    }

    public Mono<List<DataPemeriksaanKehamilan>> count(List<Long> ids){
        Query query = Query.query(
                Criteria.where("fk_data_kehamilan").in(ids)
                        .and(
                                Criteria.where("deleted_at").isNull()
                        )
        );
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(PemeriksaanKehamilanRedisConstant.COUNT, ids);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on PemeriksaanKehamilanRepository.count(ids)");
                    return this.template
                            .select(query, DataPemeriksaanKehamilan.class)
                            .switchIfEmpty(Flux.fromIterable(List.of()))
                            .collectList()
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var list = ObjectMapperUtils.readListValue(listStr, DataPemeriksaanKehamilan.class);
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(list));
                });
    }

    public Mono<List<DataPemeriksaanKehamilan>> findByExample(Example<DataPemeriksaanKehamilan> example){
        return this.repository.findAll(example).collectList();
    }

    public Mono<DataPemeriksaanKehamilan> findById(Long id){
        return this
                .repository
                .findById(id);
    }

    public Mono<List<DataPemeriksaanKehamilan>> getList(List<Long> ids){
        return this.repository
                .findAllById(ids)
                .collectList();
    }

    public Mono<List<DataPemeriksaanKehamilan>> getList(List<Long> ids, Pageable page){
        var query = Query
                .query(
                        Criteria.where("fk_data_kehamilan")
                                .in(ids)
                )
                .offset(page.getOffset())
                .limit(page.getPageSize());
        return this.template
                .select(query, DataPemeriksaanKehamilan.class)
                .collectList();
    }
}
