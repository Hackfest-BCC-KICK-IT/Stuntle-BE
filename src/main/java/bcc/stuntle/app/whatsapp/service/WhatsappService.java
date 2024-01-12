package bcc.stuntle.app.whatsapp.service;

import bcc.stuntle.app.whatsapp.repository.WhatsappRepository;
import bcc.stuntle.dto.WhatsappDto;
import bcc.stuntle.entity.GrupWhatsapp;
import bcc.stuntle.entity.PaginationResult;
import bcc.stuntle.entity.Response;
import bcc.stuntle.mapper.WhatsappMapper;
import bcc.stuntle.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@Slf4j
public class WhatsappService implements IWhatsappService{

    @Autowired
    private WhatsappRepository repository;

    @Override
    public Mono<ResponseEntity<Response<GrupWhatsapp>>> create(Long faskesId, WhatsappDto.CreateUpdate dto) {
        GrupWhatsapp whatsapp = dto.toWhatsapp();
        whatsapp.setFkFaskesId(faskesId);
        return this.repository
                .save(whatsapp)
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(
                                HttpStatus.CREATED,
                                Response
                                        .<GrupWhatsapp>builder()
                                        .data(d)
                                        .success(true)
                                        .message("sukses membuat grup whatsapp")
                                        .build()
                        )
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<GrupWhatsapp>>> delete(Long whatsappId) {
        return this.repository
                .delete(whatsappId)
                .then(Mono.fromCallable(() ->
                        ResponseUtil
                                .sendResponse(
                                        HttpStatus.OK,
                                        Response
                                                .<GrupWhatsapp>builder()
                                                .message("sukses menghapus data grup whatsapp")
                                                .success(true)
                                                .build()
                                )
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<GrupWhatsapp>>> update(Long whatsappId, WhatsappDto.CreateUpdate dto) {
        GrupWhatsapp tempWhatsapp = dto.toWhatsapp();
        return this.repository
                .get(Example.of(GrupWhatsapp.builder().id(whatsappId).build()))
                .flatMap((d) -> {
                    WhatsappMapper.INSTANCE.update(tempWhatsapp, d);
                    d.setUpdatedAt(LocalDate.now());
                    return Mono.just(d);
                })
                .flatMap((d) -> this.repository.save(d))
                .flatMap((d) -> Mono.fromCallable(() ->
                    ResponseUtil
                            .sendResponse(
                                    HttpStatus.OK,
                                    Response
                                            .<GrupWhatsapp>builder()
                                            .data(d)
                                            .message("sukses mengupdate data grup whatsapp")
                                            .success(true)
                                            .build()
                            )
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<GrupWhatsapp>>>> getList(Long faskesId, Pageable page) {
        return this.repository
                .getList(faskesId, page)
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil.sendResponse(
                    HttpStatus.OK,
                    Response
                            .<List<GrupWhatsapp>>builder()
                            .success(true)
                            .message("sukses mendapatkan data grup whatsapp")
                            .data(d.getContent())
                            .pagination(
                                    PaginationResult
                                            .<List<GrupWhatsapp>>builder()
                                            .totalElement(d.getTotalElements())
                                            .totalPage(d.getTotalPages())
                                            .currentElement(d.getNumberOfElements())
                                            .currentPage(page.getPageNumber())
                                            .build()
                            )
                            .build()
                )));
    }

    @Override
    public Mono<ResponseEntity<Response<GrupWhatsapp>>> get(Long whatsappId) {
        return this.repository
                .get(Example.of(
                        GrupWhatsapp
                                .builder()
                                .id(whatsappId)
                                .build()
                ))
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(
                                HttpStatus.OK,
                                Response.
                                        <GrupWhatsapp>builder()
                                        .data(d)
                                        .success(true)
                                        .message("sukses mendapatkan data grup whatsapp")
                                        .build()
                        )
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<GrupWhatsapp>>>> getListForOrtu(Long ortuId, Pageable pageable) {
        return this.repository
                .getListForOrtu(ortuId, pageable)
                .map((grupWhatsapps -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<GrupWhatsapp>>builder()
                                .pagination(
                                        PaginationResult
                                                .<List<GrupWhatsapp>>builder()
                                                .totalElement(grupWhatsapps.getTotalElements())
                                                .totalPage(grupWhatsapps.getTotalPages())
                                                .currentElement(grupWhatsapps.getNumberOfElements())
                                                .currentPage(pageable.getPageNumber())
                                                .build()
                                )
                                .data(grupWhatsapps.getContent())
                                .success(true)
                                .message("sukses mendapatkan data grup whatsapp")
                                .build()
                )));
    }
}
