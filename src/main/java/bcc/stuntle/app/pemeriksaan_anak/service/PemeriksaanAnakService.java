package bcc.stuntle.app.pemeriksaan_anak.service;

import bcc.stuntle.app.pemeriksaan_anak.repository.PemeriksaanAnakRepository;
import bcc.stuntle.dto.DataPemeriksaanAnakDto;
import bcc.stuntle.entity.DataPemeriksaanAnak;
import bcc.stuntle.entity.PaginationResult;
import bcc.stuntle.entity.Response;
import bcc.stuntle.util.ResponseUtil;
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
public class PemeriksaanAnakService implements IPemeriksaanAnakService{

    @Autowired
    private PemeriksaanAnakRepository repository;

    @Override
    public Mono<ResponseEntity<Response<DataPemeriksaanAnak>>> create(Long ortuId, Long faskesId, Long dataAnakId, DataPemeriksaanAnakDto.Create dto) {
        DataPemeriksaanAnak data = dto.toDataPemeriksaanAnak();
        data.setFkOrtuId(ortuId);
        data.setFkFaskesId(faskesId);
        data.setFkDataAnak(dataAnakId);
        return this.repository
                .save(data)
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.CREATED,
                        Response
                                .<DataPemeriksaanAnak>builder()
                                .message("sukses membuat data pemeriksaan anak")
                                .success(true)
                                .data(d)
                                .build()
                )));
    }

    @Override
    public Mono<ResponseEntity<Response<List<DataPemeriksaanAnak>>>> getList(Long ortuId, Long faskesId, Long dataAnakId, Pageable page) {
        return Mono.from(this
                .repository
                .getList(ortuId, faskesId, dataAnakId, page)
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<List<DataPemeriksaanAnak>>builder()
                                        .data(d.getContent())
                                        .pagination(
                                                PaginationResult
                                                        .<List<DataPemeriksaanAnak>>builder()
                                                        .totalElement(d.getTotalElements())
                                                        .totalPage(d.getTotalPages())
                                                        .currentElement(d.getNumberOfElements())
                                                        .currentPage(page.getPageNumber())
                                                        .build()
                                        )
                                        .success(true)
                                        .message("sukses mendapatkan data pemeriksaan anak")
                                        .build()
                        )))
        );
    }

    @Override
    public Mono<ResponseEntity<Response<List<DataPemeriksaanAnak>>>> getList(List<Long> pemeriksaanIds) {
        return this
                .repository
                .getList(pemeriksaanIds)
                .map((d) -> ResponseUtil
                        .sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<List<DataPemeriksaanAnak>>builder()
                                        .data(d)
                                        .success(true)
                                        .message("sukses mendapatkan data pemeriksaan anak")
                                        .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<DataPemeriksaanAnak>>> get(Long id) {
        return this.repository
                .findById(id)
                .map((d) -> ResponseUtil.sendResponse(
                        HttpStatus.CREATED,
                        Response
                                .<DataPemeriksaanAnak>builder()
                                .message("sukses mendapatkan data pemeriksaan anak")
                                .success(true)
                                .data(d)
                                .build()
                ));
    }
}
