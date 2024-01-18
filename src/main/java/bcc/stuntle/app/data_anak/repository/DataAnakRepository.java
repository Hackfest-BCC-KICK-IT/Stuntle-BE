package bcc.stuntle.app.data_anak.repository;

import bcc.stuntle.app.pemeriksaan_anak.repository.PemeriksaanAnakRepository;
import bcc.stuntle.constant.DataAnakRedisConstant;
import bcc.stuntle.entity.DataAnak;
import bcc.stuntle.entity.DataPemeriksaanAnak;
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
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
@Slf4j
public class DataAnakRepository {

    @Autowired
    private IDataAnakRepository repository;

    @Autowired
    private PemeriksaanAnakRepository pemeriksaanAnakRepository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<DataAnak> save(DataAnak dataAnak){
        var ops = this.redisTemplate.opsForValue();
        return this.redisTemplate.keys(DataAnakRedisConstant.ALL)
                .flatMap((k) -> ops.delete(k).doOnNext((data) -> log.info("delete key {}", k)))
                .then(Mono.from(this.repository.save(dataAnak)));
    }

    public Mono<DataAnak> get(Example<DataAnak> example){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(DataAnakRedisConstant.GET_EXAMPLE, example);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on DataAnakRepository.get(example)");
                    return this.repository
                            .findOne(example)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data anak tidak ditemukan")))
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((dataAnakStr) -> {
                    var dataAnak = ObjectMapperUtils.readValue(dataAnakStr, DataAnak.class);
                    var period = Period.between(dataAnak.getTanggalLahir(), LocalDate.now());
                    dataAnak.setUsiaAnak(String.format("%s Tahun %s Bulan %s Hari", period.getYears(), period.getMonths(), period.getDays()));
                    return ops.set(key, dataAnakStr, Duration.ofMinutes(1))
                            .then(Mono.just(dataAnak));
                });
    }

    public Flux<DataAnak> getList(Example<DataAnak> example){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(DataAnakRedisConstant.GET_LIST_EXAMPLE, example);
        return ops.get(key)
                .switchIfEmpty(Mono.from(Flux.defer(() -> {
                    log.info("redis result null on DataAnakRepository.getList(example)");
                    return this.repository.findAll(example).switchIfEmpty(Mono.error(new DataTidakDitemukanException("data anak tidak ditemukan")))
                            .map(ObjectMapperUtils::writeValueAsString);
                } )))
                .flatMapMany((dataAnakStr) -> {
                    var listDataAnak = ObjectMapperUtils.readListValue(dataAnakStr, DataAnak.class);
                    listDataAnak.forEach((dataAnak -> {
                        var period = Period.between(dataAnak.getTanggalLahir(), LocalDate.now());
                        dataAnak.setUsiaAnak(String.format("%s Tahun %s Bulan %s Hari", period.getYears(), period.getMonths(), period.getDays()));
                    }));
                    return ops.set(key, dataAnakStr, Duration.ofMinutes(1))
                            .thenMany(Flux.fromIterable(listDataAnak));
                });
    }

    @SuppressWarnings("unchecked")
    public Mono<Page<DataAnak>> getList(Long id, Pageable pageable){
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(DataAnakRedisConstant.GET_LIST_ID, id, pageable);
        Query query = Query.query(
                CriteriaDefinition.from(
                        Criteria.where("fk_ortu_id").is(id)
                                .and(
                                        Criteria.where("deleted_at").isNull()
                                )
                )
        ).with(pageable);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on DataAnakRepository.getList(id, pageable)");
                    return this.template
                            .select(query, DataAnak.class)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data anak tidak ditemukan")))
                            .flatMap((dataAnak) -> {
                                var pemeriksaanAnakIds = this.pemeriksaanAnakRepository
                                        .getList(
                                                Example.of(
                                                        DataPemeriksaanAnak
                                                                .builder()
                                                                .fkDataAnak(dataAnak.getId())
                                                                .build()
                                                )
                                        )
                                        .map((d) -> d.stream().map(DataPemeriksaanAnak::getId).toList());
                                return pemeriksaanAnakIds
                                        .map((v) -> {
                                            dataAnak.setFkDataPemeriksaanAnak(v);
                                            return dataAnak;
                                        });
                            })
                            .collectList()
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var list = ObjectMapperUtils.readListValue(listStr, Object.class);
                    var listDataStr = ObjectMapperUtils.writeValueAsString(list.get(0));
                    var listData = ObjectMapperUtils.readListValue(listDataStr, DataAnak.class);
                    var n = (Integer) list.get(1);
                    listData.forEach((dataAnak -> {
                        var period = Period.between(dataAnak.getTanggalLahir(), LocalDate.now());
                        dataAnak.setUsiaAnak(String.format("%s Tahun %s Bulan %s Hari", period.getYears(), period.getMonths(), period.getDays()));
                    }));
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.fromCallable(() -> new PageImpl<>(listData, pageable, n)));
                });
    }

    public Mono<Page<DataAnak>> getList(List<Long> ortuIds, Pageable page){
        var key = String.format(DataAnakRedisConstant.GET_LIST_IDS, ortuIds, PageableUtils.toString(page));
        var ops = this.redisTemplate.opsForValue();
        Query query = Query.query(
                Criteria.where("fk_ortu_id").in(ortuIds)
                        .and(
                                Criteria.where("deleted_at").isNull()
                        )
        ).with(page);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on DataAnakRepository.getList(ortuIds, page)");
                    return this.template
                            .select(query, DataAnak.class)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data anak tidak ditemukan")))
                            .collectList()
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var list = ObjectMapperUtils.readListValue(listStr, Object.class);
                    var tempT1 =list.get(0);
                    var t1 = ObjectMapperUtils.mapper.convertValue(tempT1, new TypeReference<List<DataAnak>>() {});
                    var t2 = (Integer) list.get(1);

                    t1.forEach((dataAnak -> {
                        var period = Period.between(dataAnak.getTanggalLahir(), LocalDate.now());
                        dataAnak.setUsiaAnak(String.format("%s Tahun %s Bulan %s Hari", period.getYears(), period.getMonths(), period.getDays()));
                    }));
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, page, t2)));
                });
    }

    /**
     * This method get list data of Data Anak filtered by nama orangtua, and faskes id
     */
    public Mono<Page<DataAnak>> getList(String namaOrtu, List<Long> ortuIds, Pageable pageable){
        var key = String.format(DataAnakRedisConstant.GET_LIST_IDS_NAME, ortuIds, namaOrtu, pageable);
        var ops = this.redisTemplate.opsForValue();
        Query query = Query.query(
                Criteria.where("fk_ortu_id").in(ortuIds)
                        .and(
                                Criteria.where("deleted_at").isNull()
                        )
        ).with(pageable);
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on DataAnakRepository.getList(namaOrtu, ortuIds, pageable)");
                    return this.template
                            .select(query, DataAnak.class)
                            .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data anak tidak ditemukan")))
                            .collectList()
                            .zipWith(this.repository.count())
                            .map(Tuple2::toList)
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var list = ObjectMapperUtils.readListValue(listStr, Object.class);
                    var tempT1 = list.get(0);
                    var t1 = ObjectMapperUtils.mapper.convertValue(tempT1, new TypeReference<List<DataAnak>>() {});
                    var t2 = (Integer) list.get(1);

                    t1.forEach((dataAnak -> {
                        var period = Period.between(dataAnak.getTanggalLahir(), LocalDate.now());
                        dataAnak.setUsiaAnak(String.format("%s Tahun %s Bulan %s Hari", period.getYears(), period.getMonths(), period.getDays()));
                    }));
                    return ops.set(key, t1.toString(), Duration.ofMinutes(1))
                            .then(Mono.just(new PageImpl<>(t1, pageable, t2)));
                });
    }
}