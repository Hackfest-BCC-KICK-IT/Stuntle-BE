package bcc.stuntle.app.resep_makanan.service;

import bcc.stuntle.app.resep_makanan.repository.ResepMakananRepository;
import bcc.stuntle.app.storage.repository.StorageRepository;
import bcc.stuntle.constant.BayiAnakConstant;
import bcc.stuntle.constant.ImageConstant;
import bcc.stuntle.constant.KehamilanConstant;
import bcc.stuntle.dto.ResepMakananDto;
import bcc.stuntle.entity.PaginationResult;
import bcc.stuntle.entity.ResepMakanan;
import bcc.stuntle.entity.Response;
import bcc.stuntle.exception.DataTidakDitemukanException;
import bcc.stuntle.exception.DatabaseException;
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
public class ResepMakananService implements IResepMakananService{

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ResepMakananRepository repository;

    @Override
    public Mono<ResponseEntity<Response<ResepMakanan>>> create(Long faskesId, ResepMakananDto.Create dto, Mono<FilePart> image){
        try{
            return image
                    .flatMap((f) -> Mono.fromCallable(() -> {
                        File file = File.createTempFile(ImageConstant.TEMP_FILE_PREFIX, f.filename());
                        return f.transferTo(file).then(Mono.just(file));
                    }))
                    .flatMap((v) -> v)
                    .flatMap((f) -> Mono.fromCallable(() -> {
                        try {
                            byte[] bytes =  Files.readAllBytes(f.toPath());
                            f.delete();
                            return bytes;
                        } catch (IOException e) {
                            throw new DatabaseException(String.format("terjadi kesalahan pada saat membaca byte file dengan pesan sistem %s", e.getLocalizedMessage()));
                        }
                    }))
                    .flatMap((b) -> this.storageRepository.create(b))
                    .flatMap((r) -> Mono.fromCallable(() -> {
                        ResepMakanan resepMakanan = dto.toResepMakanan();
                        resepMakanan.setFkFaskesId(faskesId);
                        resepMakanan.setUrlGambar(r.getSecureUrl());
                        resepMakanan.setPublicId(r.getPublicId());
                        return resepMakanan;
                    }))
                    .flatMap((d) -> this.repository.create(d))
                    .flatMap((r) -> Mono.fromCallable(() -> ResponseUtil
                            .sendResponse(
                                    HttpStatus.CREATED,
                                    Response.<ResepMakanan>builder()
                                            .message("sukses membuat resep makanan")
                                            .success(true)
                                            .data(r)
                                            .build()
                            )
                    ));
        } catch(DatabaseException ex){
            return Mono.error(new DatabaseException(String.format("terjadi kesalahan pada saat menyimpan image dengan pesan %s", ex.getLocalizedMessage())));
        }
    }

    @Override
    public Mono<ResponseEntity<Response<List<ResepMakanan>>>> getList(Long id, Pageable pageable) {
        return this.repository
                .getList(id, pageable)
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data resep makanan tidak ditemukan")))
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<List<ResepMakanan>>builder()
                                .data(d.getContent())
                                .success(true)
                                .message("sukses mendapatkan list data resep makanan")
                                .pagination(
                                        PaginationResult
                                                .<List<ResepMakanan>>builder()
                                                .currentPage(pageable.getPageNumber())
                                                .currentElement(d.getNumberOfElements())
                                                .totalPage(d.getTotalPages())
                                                .totalElement(d.getTotalElements())
                                                .build()
                                )
                                .build()
                )));
    }

    @Override
    public Mono<ResponseEntity<Response<Void>>> delete(Long id) {
        return this.repository
                .delete(id)
                .then(Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(
                                HttpStatus.CREATED,
                                Response.<Void>builder()
                                        .message("sukses menghapus resep makanan")
                                        .success(true)
                                        .data(null)
                                        .build()
                        )));
    }

    @Override
    public Mono<ResponseEntity<Response<Long>>> count(Long faskesId) {
        return this.repository
                .count(Example.of(
                        ResepMakanan.builder().fkFaskesId(faskesId).build()
                ))
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
    public Mono<ResponseEntity<Response<List<ResepMakanan>>>> getList(Long id, ResepMakananDto.GetListByIbuHamil dto, Pageable pageable) {
        ResepMakanan resepMakanan = dto.toResepMakanan();
        resepMakanan.setTargetResep(KehamilanConstant.IBU_HAMIL);
        return this.repository
                .getList(id, resepMakanan, pageable)
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data resep makanan tidak ditemukan")))
                .map(
                        (d) -> ResponseUtil.sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<List<ResepMakanan>>builder()
                                        .message("sukses mendapatkan data hasil pencarian resep makanan")
                                        .success(true)
                                        .data(d.getContent())
                                        .pagination(
                                                PaginationResult
                                                        .<List<ResepMakanan>>builder()
                                                        .currentPage(pageable.getPageNumber())
                                                        .currentElement(d.getNumberOfElements())
                                                        .totalPage(d.getTotalPages())
                                                        .totalElement(d.getTotalElements())
                                                        .build()
                                        )
                                        .build()
                        )
                );
    }

    @Override
    public Mono<ResponseEntity<Response<List<ResepMakanan>>>> getList(Long id, ResepMakananDto.GetListByBayiAnak dto, Pageable pageable) {
        ResepMakanan resepMakanan = dto.toResepMakanan();
        resepMakanan.setTargetResep(BayiAnakConstant.BAYI_ANAK);
        return this.repository
                .getList(id, resepMakanan, pageable)
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data resep makanan tidak ditemukan")))
                .map(
                        (d) -> ResponseUtil.sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<List<ResepMakanan>>builder()
                                        .message("sukses mendapatkan data hasil pencarian resep makanan")
                                        .success(true)
                                        .data(d.getContent())
                                        .pagination(
                                                PaginationResult
                                                        .<List<ResepMakanan>>builder()
                                                        .currentPage(pageable.getPageNumber())
                                                        .currentElement(d.getNumberOfElements())
                                                        .totalPage(d.getTotalPages())
                                                        .totalElement(d.getTotalElements())
                                                        .build()
                                        )
                                        .build()
                        )
                );
    }
}
