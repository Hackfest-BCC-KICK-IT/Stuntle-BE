package bcc.stuntle.app.pemeriksaan_anak.repository;

import bcc.stuntle.constant.PemeriksaanAnakRedisConstant;
import bcc.stuntle.entity.DataPemeriksaanAnak;
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
public class PemeriksaanAnakRepository {

    @Autowired
    private IPemeriksaanAnakRepository repository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<DataPemeriksaanAnak> save(DataPemeriksaanAnak data){
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate
                .keys(PemeriksaanAnakRedisConstant.ALL)
                .flatMap((key) -> ops.delete(key).doOnNext((d) -> log.info("remove key {}", key)))
                .then(this.repository.save(data));
    }

    public Mono<Page<DataPemeriksaanAnak>> getList(Long ortuId, Long faskesId, Long kehamilanId, Pageable page){
        var ops = this.redisTemplate.opsForValue();
        Query query = Query
                .query(
                        CriteriaDefinition.from(
                                Criteria.where("fk_ortu_id").is(ortuId)
                                        .and(
                                                Criteria.where("fk_faskes_id").is(faskesId)
                                        )
                                        .and(
                                                Criteria.where("fk_data_anak").is(kehamilanId)
                                        )
                        )
                )
                .offset(page.getOffset())
                .limit(page.getPageSize());
        var key = String.format(PemeriksaanAnakRedisConstant.GET_LIST, ortuId, faskesId, kehamilanId, PageableUtils.toString(page));
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on PemeriksaanAnakRepository.getList(ortuId, faskesId, kehamilanId, page)");
                    return Mono.from(this
                            .template
                            .select(query, DataPemeriksaanAnak.class)
                            .collectList()
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString)
                    );
                }))
                .flatMap((listStr) -> {
                    var listObj = ObjectMapperUtils.readListValue(listStr, Object.class);
                    var t1 = ObjectMapperUtils.mapper.convertValue(listObj.get(0), new TypeReference<List<DataPemeriksaanAnak>>() {});
                    var t2 = (Integer) listObj.get(1);
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, page, t2)));
                });
    }

    public Mono<Page<DataPemeriksaanAnak>> getList(List<Long> dataAnakIds, Pageable pageable){
        var ops = this.redisTemplate.opsForValue();
        Query query = Query.query(
                Criteria.where("fk_data_anak").in(dataAnakIds)
                        .and(
                                Criteria.where("deleted_at").isNull()
                        )
        ).offset(pageable.getOffset()).limit(pageable.getPageSize());
        var key = String.format(PemeriksaanAnakRedisConstant.GET_LIST_IDS, dataAnakIds, PageableUtils.toString(pageable));
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on PemeriksaanAnakRepository.getList(dataAnakIds, pageable)");
                    return this.template
                            .select(query, DataPemeriksaanAnak.class)
                            .switchIfEmpty(Flux.fromIterable(List.of()))
                            .collectList()
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var listObj = ObjectMapperUtils.readListValue(listStr, Object.class);
                    var t1 = ObjectMapperUtils.mapper.convertValue(listObj.get(0), new TypeReference<List<DataPemeriksaanAnak>>() {});
                    var t2 = (Long) listObj.get(1);
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, pageable, t2)));
                });
    }

    public Mono<List<DataPemeriksaanAnak>> getList(Example<DataPemeriksaanAnak> example){
        return this.repository.findAll(example).collectList();
    }
}
