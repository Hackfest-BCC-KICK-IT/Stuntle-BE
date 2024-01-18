package bcc.stuntle.app.data_kehamilan.service;

import bcc.stuntle.app.data_kehamilan.repository.DataKehamilanRepository;
import bcc.stuntle.app.orang_tua_faskes.repository.OrangtuaFaskesRepository;
import bcc.stuntle.app.ortu.repository.OrangtuaRepository;
import bcc.stuntle.app.pemeriksaan_kehamilan.repository.PemeriksaanKehamilanRepository;
import bcc.stuntle.dto.DataKehamilanDto;
import bcc.stuntle.entity.*;
import bcc.stuntle.exception.DataTidakDitemukanException;
import bcc.stuntle.exception.DatabaseException;
import bcc.stuntle.mapper.DataKehamilanMapper;
import bcc.stuntle.util.CollectionUtils;
import bcc.stuntle.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DataKehamilanService implements IDataKehamilanService{

    @Autowired
    private DataKehamilanRepository repository;

    @Autowired
    private OrangtuaRepository ortuRepository;

    @Autowired
    private OrangtuaFaskesRepository ortuFaskesRepository;

    @Autowired
    private PemeriksaanKehamilanRepository pemeriksaanKehamilanRepository;

    @Override
    public Mono<ResponseEntity<Response<DataKehamilan>>> create(Long id, DataKehamilanDto.Create dto) {
        DataKehamilan dataKehamilan = dto.toDataKehamilan();
        dataKehamilan.setFkOrtuId(id);
        dataKehamilan.setPrediksiTanggalLahir(dataKehamilan.getTanggalPertamaHaid().plusMonths(9));
        return this
                .ortuRepository.findOne(Example.of(
                        Orangtua
                                .builder()
                                .id(id)
                                .build()
                    )
                )
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data orangtua tidak ditemukan")))
                .flatMap((d) -> {
                    if(d.getIsConnectedWithFaskes() == null){
                        return Mono.error(new DatabaseException("pastikan orangtua sudah terhubung dengan fasilitas kesehatan"));
                    }
                    if(d.getIsConnectedWithFaskes()){
                        return Mono.just(d);
                    } else {
                        return Mono.error(new DatabaseException("pastikan orangtua sudah terhubung dengan fasilitas kesehatan"));
                    }
                })
                .then(this.repository.save(dataKehamilan))
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(
                                HttpStatus.CREATED,
                                Response
                                        .<DataKehamilan>builder()
                                        .message("sukses membuat data kehamilan")
                                        .data(d)
                                        .success(true)
                                        .build()
                        ))
                );
    }

    @Override
    public Mono<ResponseEntity<Response<DataKehamilan>>> get(Long id) {
        return this.repository
                .find(Example
                        .of(DataKehamilan
                                .builder()
                                .id(id)
                                .build())
                )
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data kehamilan tidak ditemukan")))
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(HttpStatus.OK,
                                Response
                                        .<DataKehamilan>builder()
                                        .data(d)
                                        .success(true)
                                        .message("sukses mendapatkan data kehamilan")
                                        .build()))
                );
    }

    public Mono<ResponseEntity<Response<Map<String, Long>>>> count(Long faskesId){
        return this.ortuFaskesRepository
                .getList(faskesId, Pageable.unpaged())
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data orangtua faskes tidak ditemukan")))
                .map(Page::getContent)
                .map((d) -> d.stream().parallel().map(OrangtuaFaskes::getFkOrtuId).toList())
                .flatMap((ortuIds) -> {
                    Mono<List<DataKehamilan>> dataKehamilan = this.repository
                            .count(ortuIds, faskesId);
                    return dataKehamilan
                            .map((v) -> {
                                var dataKehamilanIds = v.stream().parallel().map(DataKehamilan::getId).collect(Collectors.toList());
                                return this
                                        .pemeriksaanKehamilanRepository
                                        .count(dataKehamilanIds)
                                        .map((dataPemeriksaanKehamilans) -> ResponseUtil
                                                .sendResponse(
                                                        HttpStatus.OK,
                                                        Response
                                                                .<Map<String, Long>>builder()
                                                                .success(true)
                                                                .message("sukses mendapatkan data statistik ibu hamil")
                                                                .data(
                                                                        CollectionUtils.ofLinkedHashMap(
                                                                                new String[]{"jumlahProfilCalonBayi", "jumlahSudahPeriksa", "jumlahBelumPeriksa"},
                                                                                new Long[]{Integer.toUnsignedLong(v.size()), Integer.toUnsignedLong(dataPemeriksaanKehamilans.size()), Integer.toUnsignedLong(v.size() - dataPemeriksaanKehamilans.size())}
                                                                        )
                                                                )
                                                                .build()
                                                ));
                            });
                })
                .flatMap((v) -> v)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ResponseEntity<Response<List<DataKehamilan>>>> getList(Long ortuId, Pageable pageable) {
        return this.repository
                .getList(ortuId, pageable)
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<DataKehamilan>>builder()
                                .pagination(
                                        PaginationResult
                                                .<List<DataKehamilan>>builder()
                                                .totalElement(res.getTotalElements())
                                                .totalPage(res.getTotalPages())
                                                .currentElement(res.getNumberOfElements())
                                                .currentPage(pageable.getPageNumber())
                                                .build()
                                )
                                .data(res.getContent())
                                .success(true)
                                .message("sukses mendapatkan list data kehamilan")
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<Void>>> update(Long dataKehamilanId, DataKehamilanDto.Update dto) {
        return this.repository
                .find(Example.of(
                        DataKehamilan
                                .builder()
                                .id(dataKehamilanId)
                                .build()
                ))
                .flatMap((dataKehamilan) -> {
                    DataKehamilan dataKehamilanDto = dto.toDataKehamilan();
                    dataKehamilan = DataKehamilanMapper.INSTANCE.update(dataKehamilanDto, dataKehamilan);
                    return this.repository.save(dataKehamilan);
                })
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<Void>builder()
                                .message("sukses mengupdate data kehamilan")
                                .success(true)
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<Void>>> delete(Long dataKehamilanId) {
        return this.repository
                .find(Example.of(
                        DataKehamilan
                                .builder()
                                .id(dataKehamilanId)
                                .build()
                ))
                .flatMap((dataKehamilan) -> {
                    dataKehamilan.setDeletedAt(LocalDate.now());
                    return this.repository.save(dataKehamilan);
                })
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<Void>builder()
                                .success(true)
                                .message("sukses menghapus data kehamilan")
                                .build()
                ));
    }

    public Mono<List<DataKehamilan>> getListByFaskes(Long faskesId){
        return this.repository
                .getListByFaskes(faskesId);
    }
}
