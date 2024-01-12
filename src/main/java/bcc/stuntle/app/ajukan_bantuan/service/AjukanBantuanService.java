package bcc.stuntle.app.ajukan_bantuan.service;

import bcc.stuntle.app.ajukan_bantuan.repository.AjukanBantuanRepository;
import bcc.stuntle.dto.AjukanBantuanDto;
import bcc.stuntle.entity.AjukanBantuan;
import bcc.stuntle.entity.PaginationResult;
import bcc.stuntle.entity.Response;
import bcc.stuntle.entity.StatusAjuan;
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
import java.util.Map;

@Service
@Transactional
@Slf4j
public class AjukanBantuanService implements IAjukanBantuanService{

    @Autowired
    private AjukanBantuanRepository repository;

    @Override
    public Mono<ResponseEntity<Response<AjukanBantuan>>> create(Long ortuId, AjukanBantuanDto.Create dto) {
        AjukanBantuan ajukanBantuan = dto.toAjukanBantuan();
        ajukanBantuan.setFkOrtuId(ortuId);
        return this.repository
                .create(ajukanBantuan)
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.CREATED,
                        Response
                                .<AjukanBantuan>builder()
                                .data(res)
                                .message("sukses membuat ajukan bantuan")
                                .success(true)
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<AjukanBantuan>>>> getListOrtu(Long ortuId, Pageable pageable) {
        return this.repository
                .getListOrtu(ortuId, pageable)
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<AjukanBantuan>>builder()
                                .success(true)
                                .message("sukses mendapatkan data ajukan bantuan orangtua")
                                .data(res.getContent())
                                .pagination(
                                        PaginationResult
                                                .<List<AjukanBantuan>>builder()
                                                .currentPage(pageable.getPageNumber())
                                                .currentElement(res.getNumberOfElements())
                                                .totalPage(res.getTotalPages())
                                                .totalElement(res.getTotalElements())
                                                .build()
                                )
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<AjukanBantuan>>>> getListFaskes(Long faskesId, String statusAjuan, Pageable pageable) {
        return this.repository
                .getListFaskes(faskesId, statusAjuan, pageable)
                .map((p) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<AjukanBantuan>>builder()
                                .pagination(
                                        PaginationResult
                                                .<List<AjukanBantuan>>builder()
                                                .totalElement(p.getTotalElements())
                                                .totalPage(p.getTotalPages())
                                                .currentElement(p.getNumberOfElements())
                                                .currentPage(pageable.getPageNumber())
                                                .build()
                                )
                                .data(p.getContent())
                                .message(String.format("sukses mendapatkan data ajukan bantuan dengan status %s", statusAjuan))
                                .success(true)
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<AjukanBantuan>>> get(Long id) {
        return this.repository
                .getById(id)
                .map(ajukanBantuan -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<AjukanBantuan>builder()
                                .success(true)
                                .message("sukses mendapatkan data ajukan bantuan")
                                .data(ajukanBantuan)
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<AjukanBantuan>>> update(Long id, AjukanBantuanDto.Update dto) {
        return this.repository
                .update(id, dto.toAjukanBantuan())
                .map(ajukanBantuan -> ResponseUtil.sendResponse(
                   HttpStatus.OK,
                   Response
                           .<AjukanBantuan>builder()
                           .data(ajukanBantuan)
                           .message("sukses mengupdate data ajukam bantuan")
                           .success(true)
                           .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<Map<String, ?>>>> count(Long faskesId) {
        return this.repository
                .count(faskesId)
                .map((res) -> {
                    Long numberOfProcess = res.stream().filter((v) -> v.getStatus().equalsIgnoreCase(StatusAjuan.diproses.name())).count();
                    Long numberOfAccepted = res.stream().filter((v) -> v.getStatus().equalsIgnoreCase(StatusAjuan.sukses.name())).count();
                    Long numberOfRejected = res.stream().filter((v) -> v.getStatus().equalsIgnoreCase(StatusAjuan.gagal.name())).count();
                    return Map.ofEntries(
                            Map.entry("numberOfProcess", numberOfProcess),
                            Map.entry("numberOfAccepted", numberOfAccepted),
                            Map.entry("numberOfRejected", numberOfRejected)
                    );
                })
                .map((map) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<Map<String, ?>>builder()
                                .data(map)
                                .message("sukses mendapatkan statistik ajuan pada faskes")
                                .success(true)
                                .build()
                ));
    }

}
