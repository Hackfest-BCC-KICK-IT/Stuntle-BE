package bcc.stuntle.app.data_kehamilan.repository;

import bcc.stuntle.constant.DataKehamilanRedisConstant;
import bcc.stuntle.entity.DataKehamilan;
import bcc.stuntle.exception.DataTidakDitemukanException;
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
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class DataKehamilanRepository {

    @Autowired
    private IDataKehamilanRepository dataKehamilanRepository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<DataKehamilan> save(DataKehamilan data){
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate
                .keys(DataKehamilanRedisConstant.ALL)
                .flatMap((key) -> ops.delete(key).doOnNext((d) -> log.info("remove key {}", key)))
                .then(this.dataKehamilanRepository.save(data));
    }

    public  Mono<DataKehamilan> find(Example<DataKehamilan> example){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(DataKehamilanRedisConstant.GET_EXAMPLE, example);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on DataKehamilanRepository.find(example)");
                    return  this
                            .dataKehamilanRepository
                            .findOne(example)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data kehamilan tidak ditemukan")))
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((dataKehamilanStr) -> {
                    var dataKehamilan = ObjectMapperUtils.readValue(dataKehamilanStr, DataKehamilan.class);
                    return ops.set(key, dataKehamilanStr, Duration.ofMinutes(1))
                            .then(Mono.just(dataKehamilan));
                });
    }

    public Mono<Long> count(Example<DataKehamilan> example){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(DataKehamilanRedisConstant.COUNT, example);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on DataKehamilanRepository.count(example)");
                    return this
                            .dataKehamilanRepository
                            .count(example)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((nStr) -> {
                    var n = ObjectMapperUtils.readValue(nStr, Integer.class);
                    return ops.set(key, nStr, Duration.ofMinutes(1))
                            .then(Mono.just(n.longValue()));
                });
    }

    public Mono<List<DataKehamilan>> count(List<Long> ids, Long fkFaskesId){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(DataKehamilanRedisConstant.COUNT_IDS_ORTU_FASKES, ids, fkFaskesId);
        Query query = Query.query(
                CriteriaDefinition.from(
                        Criteria.where("fk_ortu_id").in(ids)
                                .and(
                                        Criteria.where("deleted_at").isNull()
                                )
                )
        );
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on DataKehamilanRepository.count(ids, fkFaskesId)");
                    return this.template
                            .select(query, DataKehamilan.class)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data kehamilan tidak ditemukan")))
                            .collectList()
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var list = ObjectMapperUtils.readListValue(listStr, DataKehamilan.class);
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(list));
                });
    }

    public Mono<Page<DataKehamilan>> getList(Long ortuId, Pageable pageable){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(DataKehamilanRedisConstant.GET_LIST, ortuId, PageableUtils.toString(pageable));
        Query query = Query.query(
                Criteria.where("fk_ortu_id").is(ortuId)
                        .and(
                                Criteria.where("deleted_at").isNull()
                        )
        ).with(pageable);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on DataKehamilanRepository.getList(ortuId, pageable)");
                    return this.template
                            .select(query, DataKehamilan.class)
                            .collectList()
                            .zipWith(this.dataKehamilanRepository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {

                    var list = ObjectMapperUtils.readListValue(listStr, Object.class);

                    var tempT1 = list.get(0);
                    var t1 = ObjectMapperUtils.mapper.convertValue(tempT1, new TypeReference<List<DataKehamilan>>() {});

                    var t2 = (Integer) list.get(1);

                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, pageable, t2)));
                });
    }
}
