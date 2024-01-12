package bcc.stuntle.app.artikel.service;

import bcc.stuntle.dto.ArtikelDto;
import bcc.stuntle.entity.Artikel;
import bcc.stuntle.entity.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IArtikelService {
    Mono<ResponseEntity<Response<Artikel>>> create(ArtikelDto.Create dto, Long faskesId, Mono<FilePart> image);
    Mono<ResponseEntity<Response<List<Artikel>>>> getList(Long faskesId, Pageable pageable);
    Mono<ResponseEntity<Response<Void>>> delete(Long id);
    Mono<ResponseEntity<Response<Long>>> count(Long faskesId);
    Mono<ResponseEntity<Response<List<Artikel>>>> getList(String judulArtikel, Pageable pageable);
    Mono<ResponseEntity<Response<List<Artikel>>>> getList(Pageable pageable);
}
