package bcc.stuntle.app.artikel.service;

import bcc.stuntle.app.artikel.repository.ArtikelRepository;
import bcc.stuntle.app.storage.repository.StorageRepository;
import bcc.stuntle.dto.ArtikelDto;
import bcc.stuntle.entity.Artikel;
import bcc.stuntle.entity.PaginationResult;
import bcc.stuntle.entity.Response;
import bcc.stuntle.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ArtikelService implements IArtikelService{

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ArtikelRepository artikelRepository;

    @Override
    public Mono<ResponseEntity<Response<Artikel>>> create(ArtikelDto.Create dto, Long faskesId, Mono<FilePart> image) {
        return image
                .flatMap((d) -> {
                    try {
                        File file = File.createTempFile("image_", d.filename());
                        return d.transferTo(file).then(Mono.just(file));
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                })
                .flatMap((d) -> {
                    try {
                        byte[] bytes = Files.readAllBytes(d.toPath());
                        d.delete();
                        return this.storageRepository.create(bytes);
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                })
                .flatMap((d) -> {
                    Artikel artikel = dto.toArtikel();
                    artikel.setPublicId(d.getPublicId());
                    artikel.setLinkGambar(d.getSecureUrl());
                    artikel.setFkFaskesId(faskesId);
                    return this.artikelRepository.save(artikel);
                })
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(
                                HttpStatus.CREATED,
                                Response
                                        .<Artikel>builder()
                                        .data(d)
                                        .message("sukses membuat artikel")
                                        .success(true)
                                        .build()
                        )));
    }

    @Override
    public Mono<ResponseEntity<Response<List<Artikel>>>> getList(Long faskesId, Pageable pageable) {
        return this.artikelRepository
                .getList(faskesId, pageable)
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<Artikel>>builder()
                                .success(true)
                                .message("sukses mendapatkan list artikel")
                                .data(d.getContent())
                                .pagination(
                                        PaginationResult
                                                .<List<Artikel>>builder()
                                                .currentPage(pageable.getPageNumber())
                                                .currentElement(d.getNumberOfElements())
                                                .totalElement(d.getTotalElements())
                                                .totalPage(d.getTotalPages())
                                                .build()
                                )
                                .build()
                )));
    }

    @Override
    public Mono<ResponseEntity<Response<Void>>> delete(Long id) {
        return this.artikelRepository
                .delete(id)
                .then(Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<Void>builder()
                                        .message("sukses menghapus data artikel")
                                        .success(true)
                                        .build()
                    ))
                );
    }

    @Override
    public Mono<ResponseEntity<Response<Long>>> count(Long faskesId) {
        return this.artikelRepository
                .count(Example.of(Artikel.builder().fkFaskesId(faskesId).build()))
                .flatMap((d) -> Mono.fromCallable(() ->
                        ResponseUtil.sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<Long>builder()
                                        .data(d)
                                        .success(true)
                                        .message("sukses mendapatkan jumlah data makanan")
                                        .build()
                        )
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<Artikel>>>> getList(String judulArtikel, Pageable pageable) {
        return this.artikelRepository
                .getList(judulArtikel, pageable)
                .map((p) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<Artikel>>builder()
                                .pagination(
                                        PaginationResult
                                                .<List<Artikel>>builder()
                                                .totalElement(p.getTotalElements())
                                                .totalPage(p.getTotalPages())
                                                .currentElement(p.getNumberOfElements())
                                                .currentPage(pageable.getPageNumber())
                                                .build()
                                )
                                .data(p.getContent())
                                .success(true)
                                .message("sukses mendapatkan list data artikel")
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<Artikel>>>> getList(Pageable pageable) {
        return this.artikelRepository
                .getList(pageable)
                .map((p) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<Artikel>>builder()
                                .message("sukses mendapatkan data list artikel")
                                .success(true)
                                .data(p.getContent())
                                .pagination(
                                        PaginationResult
                                                .<List<Artikel>>builder()
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
