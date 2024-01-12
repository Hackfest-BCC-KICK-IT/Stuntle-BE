package bcc.stuntle.app.ortu_resep_makanan_artikel_tersimpan.service;

import bcc.stuntle.app.ortu_resep_makanan_artikel_tersimpan.repository.OrtuResepMakananArtikelRepository;
import bcc.stuntle.dto.ResepMakananArtikelTersimpanDto;
import bcc.stuntle.entity.PaginationResult;
import bcc.stuntle.entity.ResepMakananArtikelTersimpan;
import bcc.stuntle.entity.Response;
import bcc.stuntle.exception.DatabaseException;
import bcc.stuntle.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrtuResepMakananArtikelService implements IOrtuResepMakananArtikelService{

    @Autowired
    private OrtuResepMakananArtikelRepository repository;

    @Override
    public Mono<ResponseEntity<Response<ResepMakananArtikelTersimpan>>> create(Long ortuId, ResepMakananArtikelTersimpanDto.Create dto) {
        var resepMakananArtikel = dto.toResepMakananArtikel();
        resepMakananArtikel.setFkOrtuId(ortuId);
        resepMakananArtikel.setCreatedAt(LocalDate.now());
        resepMakananArtikel.setUpdatedAt(LocalDate.now());
        return this.repository
                .create(resepMakananArtikel)
                .flatMap((res) -> {
                    if(res > 0){
                        return Mono.just(
                                ResponseUtil.sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<ResepMakananArtikelTersimpan>builder()
                                        .message("sukses membuat resep makanan dan artikel dengan orangtua")
                                        .data(resepMakananArtikel)
                                        .success(true)
                                        .build()
                        ));
                    } else {
                        return Mono.error(new DatabaseException("no rows affected"));
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<Response<ResepMakananArtikelTersimpan>>> get(Long ortuId, Long resepMakananId, Long artikelId, String jenis) {
        return this.repository
                .get(ortuId, resepMakananId, artikelId, jenis)
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<ResepMakananArtikelTersimpan>builder()
                                .message("sukses membuat resep makanan dan artikel dengan orangtua")
                                .data(res)
                                .success(true)
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<Void>>> delete(Long ortuId, Long resepMakananId, Long artikelId, String jenis) {
        return this.repository
                .delete(ortuId, resepMakananId, artikelId, jenis)
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<Void>builder()
                                .message("sukses menghapus resep makanan dan artikel dengan orangtua")
                                .data(null)
                                .success(true)
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<Void>>> activate(Long ortuId, Long resepMakananId, Long artikelId, String jenis) {
        return this.repository
                .activate(ortuId, resepMakananId, artikelId, jenis)
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<Void>builder()
                                .message("sukses mengaktifkan resep makanan dan artikel dengan orangtua")
                                .data(null)
                                .success(true)
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<ResepMakananArtikelTersimpan>>>> getList(Long ortuId, Pageable pageable) {
        return this.repository
                .getList(ortuId, pageable)
                .map((p) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<ResepMakananArtikelTersimpan>>builder()
                                .message("sukses mendapatkan list hubungan orangtua resep makanan dan artikel")
                                .success(true)
                                .data(p.getContent())
                                .pagination(
                                        PaginationResult
                                                .<List<ResepMakananArtikelTersimpan>>builder()
                                                .currentPage(pageable.getPageNumber())
                                                .currentElement(p.getNumberOfElements())
                                                .totalPage(p.getTotalPages())
                                                .totalElement(p.getTotalElements())
                                                .build()
                                )
                                .build()
                ));
    }
}
