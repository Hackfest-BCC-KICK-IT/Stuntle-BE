package bcc.stuntle.app.ortu_resep_makanan_artikel_tersimpan.repository;

import bcc.stuntle.constant.OrtuResepMakananArtikelRedisConstant;
import bcc.stuntle.entity.ResepMakananArtikelTersimpan;
import bcc.stuntle.exception.DataTidakDitemukanException;
import bcc.stuntle.exception.DatabaseException;
import bcc.stuntle.util.ObjectMapperUtils;
import bcc.stuntle.util.PageableUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class OrtuResepMakananArtikelRepository implements IOrtuResepMakananArtikelRepository{

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Override
    public Mono<Long> create(ResepMakananArtikelTersimpan resepMakananArtikelTersimpan) {
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate
                .keys(OrtuResepMakananArtikelRedisConstant.ALL)
                .flatMap((key) -> ops.delete(key).doOnNext((data) -> log.info("delete key {}", key)))
                .then(this.databaseClient
                        .sql(this.CREATE)
                        .bind("$1", resepMakananArtikelTersimpan.getFkArtikelId())
                        .bind("$2", resepMakananArtikelTersimpan.getFkOrtuId())
                        .bind("$3", resepMakananArtikelTersimpan.getFkResepMakananId())
                        .bind("$4", resepMakananArtikelTersimpan.getJenis())
                        .fetch()
                        .rowsUpdated());
    }

    @Override
    public Mono<ResepMakananArtikelTersimpan> get(Long ortuId, Long resepMakananId, Long artikelId, String jenis) {
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(OrtuResepMakananArtikelRedisConstant.GET, ortuId, resepMakananId, artikelId, jenis);
        Query query = Query.query(
                Criteria.where("fk_ortu_id").is(ortuId)
                        .and(
                                Criteria.where("fk_resep_makanan_id").is(resepMakananId)
                        )
                        .and(
                                Criteria.where("fk_artikel_id").is(artikelId)
                        )
                        .and(
                                Criteria.where("jenis").is(jenis)
                        )
                        .and(
                                Criteria.where("deleted_at").isNull()
                        )
        ).limit(1);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on OrtuResepMakananRepository.get(ortuId, resepMakananId, artikelId, jenis)");
                    return this.template
                            .select(query, ResepMakananArtikelTersimpan.class)
                            .collectList()
                            .flatMap((list) -> {
                                if(list.isEmpty()){
                                    return Mono.error(new DataTidakDitemukanException("data tersimpan tidak ditemukan"));
                                } else {
                                    return Mono.just(list.get(0));
                                }
                            })
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((dStr) -> {
                    var resepMakananTersimpan = ObjectMapperUtils.readValue(dStr, ResepMakananArtikelTersimpan.class);
                    return ops.set(key, dStr, Duration.ofMinutes(1))
                            .then(Mono.just(resepMakananTersimpan));
                });
    }

    @Override
    public Mono<Long> delete(Long ortuId, Long resepMakananId, Long artikelId, String jenis) {
        var ops = this.redisTemplate.opsForValue();
        Query query = Query.query(
                Criteria.where("fk_ortu_id").is(ortuId)
                        .and(
                                Criteria.where("fk_resep_makanan_id").is(resepMakananId)
                        )
                        .and(
                                Criteria.where("fk_artikel_id").is(artikelId)
                        )
                        .and(
                                Criteria.where("jenis").is(jenis)
                        )
        );
        return this.redisTemplate
                .keys(OrtuResepMakananArtikelRedisConstant.ALL)
                .flatMap((key) -> ops.delete(key).doOnNext((data) -> log.info("delete key {}", key)))
                .then(
                        this.template
                        .update(query, Update.update("deleted_at", LocalDate.now()), ResepMakananArtikelTersimpan.class)
                        .flatMap((l) -> {
                            if(l < 1){
                                return Mono.error(new DatabaseException("data tersimpan tidak ditemukan"));
                            } else {
                                return Mono.just(l);
                            }
                        })
                );
    }

    @Override
    public Mono<Long> activate(Long ortuId, Long resepMakananId, Long artikelId, String jenis) {
        var ops = this.redisTemplate.opsForValue();
        Query query = Query.query(
                Criteria.where("fk_ortu_id").is(ortuId)
                        .and(
                                Criteria.where("fk_resep_makanan_id").is(resepMakananId)
                        )
                        .and(
                                Criteria.where("fk_artikel_id").is(artikelId)
                        )
                        .and(
                                Criteria.where("jenis").is(jenis)
                        )
        );
        return this.redisTemplate
                .keys(OrtuResepMakananArtikelRedisConstant.ALL)
                .flatMap((key) -> ops.delete(key).doOnNext((data) -> log.info("delete key {}", key)))
                .then(
                        this.template
                                .update(query, Update.update("deleted_at", null), ResepMakananArtikelTersimpan.class)
                                .flatMap((l) -> {
                                    if(l < 1){
                                        return Mono.error(new DatabaseException("data tersimpan tidak ditemukan"));
                                    } else {
                                        return Mono.just(l);
                                    }
                                })
                );
    }

    @Override
    public Mono<Page<ResepMakananArtikelTersimpan>> getList(Long ortuId, Pageable pageable) {
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(OrtuResepMakananArtikelRedisConstant.GET_LIST, ortuId, PageableUtils.toString(pageable));
        Query query = Query.query(
                Criteria.where("fk_ortu_id").is(ortuId)
                        .and(
                                Criteria.where("deleted_at").isNull()
                        )
        ).with(pageable);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on OrtuResepMakananRepository.getList(ortuId, pageable)");
                    return this.template
                            .select(query, ResepMakananArtikelTersimpan.class)
                            .collectList()
                            .zipWith(this.template.count(query, ResepMakananArtikelTersimpan.class))
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var listObj = ObjectMapperUtils.readListValue(listStr, Object.class);

                    var t1 = ObjectMapperUtils.mapper.convertValue(listObj.get(0), new TypeReference<List<ResepMakananArtikelTersimpan>>() {});
                    var t2 = (Integer) listObj.get(1);

                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, pageable, t2)));
                });
    }
}
