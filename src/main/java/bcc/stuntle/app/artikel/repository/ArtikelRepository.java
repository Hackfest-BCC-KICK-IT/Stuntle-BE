package bcc.stuntle.app.artikel.repository;

import bcc.stuntle.constant.ArtikelRedisConstant;
import bcc.stuntle.entity.Artikel;
import bcc.stuntle.exception.DataTidakDitemukanException;
import bcc.stuntle.util.ObjectMapperUtils;
import bcc.stuntle.util.PageableUtils;
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

import java.time.Duration;
import java.time.LocalDate;

@Component
@Slf4j
public class ArtikelRepository {

    @Autowired
    private IArtikelRepository repository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<Artikel> save(Artikel artikel){
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate.keys(ArtikelRedisConstant.ALL)
                .flatMap((key) ->
                        ops
                                .delete(key)
                                .doOnNext((data) -> {
                                    log.info("delete key {}", key);
                                })
                )
                .then(this.repository.save(artikel));
    }

    public Mono<Page<Artikel>> getList(Long faskesId, Pageable pageable){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(ArtikelRedisConstant.GET_LIST_FASKES, faskesId, PageableUtils.toString(pageable));
        Query query = Query
                .query(
                        CriteriaDefinition.from(
                                Criteria.where("fk_faskes_id").is(faskesId)
                                        .and(
                                                Criteria.where("deleted_at").isNull()
                                        )
                        )
                ).with(pageable);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on ArtikelRepository.getList(faskesId, page)");
                    return this
                            .template
                            .select(query, Artikel.class)
                            .switchIfEmpty(Mono.just(Artikel.builder().build()))
                            .collectList()
                            .zipWith(this.repository.count())
                            .flatMap((d) -> Mono.fromCallable(() -> new PageImpl<>(d.getT1(), pageable, d.getT2())))
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((strPage) -> {
                   var page = ObjectMapperUtils.readPageValue(strPage, Artikel.class);
                   return ops.set(key, strPage, Duration.ofMinutes(1))
                           .then(Mono.just(page));
                });
    }

    public Mono<Void> delete(Long id){
        var ops = this.redisTemplate.opsForValue();
        return this.repository
                .findById(id)
                .flatMap((d) -> {
                    d.setDeletedAt(LocalDate.now());
                    return this.redisTemplate
                            .keys(ArtikelRedisConstant.ALL)
                            .flatMap((keys) -> ops.delete(keys).doOnNext((data) -> {log.info("delete key {}", keys);}))
                            .then(this.repository.save(d));
                })
                .then(Mono.empty());
    }

    public Mono<Long> count(Example<Artikel> example){
        return this.repository
                .count(example);
    }

    public Mono<Page<Artikel>> getList(String judulArtikel, Pageable pageable){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(ArtikelRedisConstant.GET_LIST_JUDUL, judulArtikel, PageableUtils.toString(pageable));
        Query query = Query.query(
                Criteria.where("judul_artikel").like(String.format("%%%s%%", judulArtikel)).ignoreCase(true)
                        .and(
                                Criteria.where("deleted_at").isNull()
                        )
        )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on ArtikelRepository.getList(judulArtikel, pageable)");
                    return this.template
                            .select(query, Artikel.class)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data artikel tidak ditemukan")))
                            .collectList()
                            .zipWith(this.repository.count())
                            .map((m) -> new PageImpl<>(m.getT1(), pageable, m.getT2()))
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((str) -> {
                    var artikel = ObjectMapperUtils.readPageValue(str, Artikel.class);
                    return ops.set(key, str, Duration.ofMinutes(1)).then(Mono.just(artikel));
                });
    }

    public Mono<Page<Artikel>> getList(Pageable pageable){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(ArtikelRedisConstant.GET_LIST_PG, PageableUtils.toString(pageable));
        Query query = Query.query(
                Criteria.where("deleted_at").isNull()
        ).limit(pageable.getPageSize()).offset(pageable.getOffset());
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on ArtikelRepository.getList(pageable)");
                    return this.template
                            .select(query, Artikel.class)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data artikel tidak ditemukan")))
                            .collectList()
                            .zipWith(this.repository.count())
                            .map((t) -> new PageImpl<>(t.getT1(), pageable, t.getT2()))
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((str) -> {
                    var artikel = ObjectMapperUtils.readPageValue(str, Artikel.class);
                    return ops.set(key, str, Duration.ofMinutes(1))
                            .then(Mono.just(artikel));
                });
    }
}
