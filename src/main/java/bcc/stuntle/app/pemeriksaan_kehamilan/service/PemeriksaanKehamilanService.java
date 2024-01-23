package bcc.stuntle.app.pemeriksaan_kehamilan.service;

import bcc.stuntle.app.pemeriksaan_kehamilan.repository.PemeriksaanKehamilanRepository;
import bcc.stuntle.dto.DataPemeriksaanKehamilanDto;
import bcc.stuntle.entity.DataPemeriksaanKehamilan;
import bcc.stuntle.entity.PaginationResult;
import bcc.stuntle.entity.Response;
import bcc.stuntle.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Transactional
@Slf4j
public class PemeriksaanKehamilanService implements IPemeriksaanKehamilanService{

    @Autowired
    private PemeriksaanKehamilanRepository repository;

    @Override
    public Mono<ResponseEntity<Response<DataPemeriksaanKehamilan>>> create(Long ortuId, Long faskesId, Long dataKehamilanId, DataPemeriksaanKehamilanDto.Create dto) {
        return this.repository
                .save(ortuId, faskesId, dataKehamilanId, dto.toDataPemeriksaanKehamilan())
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(
                                HttpStatus.CREATED,
                                Response.<DataPemeriksaanKehamilan>builder()
                                        .data(d)
                                        .message("sukses membuat data pemeriksaan kehamilan")
                                        .success(true)
                                        .build()
                        )))
                .switchIfEmpty(Mono.just(
                        ResponseUtil.sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<DataPemeriksaanKehamilan>builder()
                                        .message("sukses mendapatkan data pemeriksaan kehamilan")
                                        .success(true)
                                        .build()
                        )
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<DataPemeriksaanKehamilan>>>> getList(Long ortuId, Long faskesId, Long dataKehamilanId, Pageable page) {
        return this.repository
                .getList(ortuId, faskesId, dataKehamilanId, page)
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<DataPemeriksaanKehamilan>>builder()
                                .message("sukses mendapatkan data pemeriksaan kehamilan")
                                .success(true)
                                .data(d.getContent())
                                .pagination(
                                        PaginationResult
                                                .<List<DataPemeriksaanKehamilan>>builder()
                                                .currentPage(page.getPageNumber())
                                                .currentElement(d.getNumberOfElements())
                                                .totalPage(d.getTotalPages())
                                                .totalElement(d.getTotalElements())
                                                .build()
                                )
                                .build()
                )))
                .switchIfEmpty(Mono.just(
                        ResponseUtil.sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<List<DataPemeriksaanKehamilan>>builder()
                                        .message("sukses mendapatkan data pemeriksaan kehamilan")
                                        .success(true)
                                        .build()
                        )
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<DataPemeriksaanKehamilan>>>> getList(List<Long> pemeriksaanIds) {
        return this
                .repository
                .getList(pemeriksaanIds)
                .map((d) -> {
                    return ResponseUtil.sendResponse(
                            HttpStatus.OK,
                            Response
                                    .<List<DataPemeriksaanKehamilan>>builder()
                                    .message("sukses mendapatkan data pemeriksaan kehamilan")
                                    .success(true)
                                    .data(d)
                                    .build()
                    );
                })
                .switchIfEmpty(Mono.just(
                        ResponseUtil.sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<List<DataPemeriksaanKehamilan>>builder()
                                        .message("sukses mendapatkan data pemeriksaan kehamilan")
                                        .success(true)
                                        .build()
                        )
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<DataPemeriksaanKehamilan>>> get(Long id) {
        return this.repository
                .findById(id)
                .map((d) -> {
                    return ResponseUtil.sendResponse(
                            HttpStatus.OK,
                            Response
                                    .<DataPemeriksaanKehamilan>builder()
                                    .message("sukses mendapatkan data pemeriksaan kehamilan")
                                    .success(true)
                                    .data(d)
                                    .build()
                    );
                })
                .switchIfEmpty(Mono.just(
                        ResponseUtil.sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<DataPemeriksaanKehamilan>builder()
                                        .message("sukses mendapatkan data pemeriksaan kehamilan")
                                        .success(true)
                                        .build()
                        )
                ));
    }
}
