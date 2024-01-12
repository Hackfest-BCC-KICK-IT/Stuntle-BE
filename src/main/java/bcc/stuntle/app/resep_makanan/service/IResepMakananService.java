package bcc.stuntle.app.resep_makanan.service;

import bcc.stuntle.dto.ResepMakananDto;
import bcc.stuntle.entity.ResepMakanan;
import bcc.stuntle.entity.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IResepMakananService {
    Mono<ResponseEntity<Response<ResepMakanan>>> create(Long faskesId, ResepMakananDto.Create dto, Mono<FilePart> image);
    Mono<ResponseEntity<Response<List<ResepMakanan>>>> getList(Long id, Pageable pageable);
    Mono<ResponseEntity<Response<Void>>> delete(Long id);
    Mono<ResponseEntity<Response<Long>>> count(Long faskesId);
    Mono<ResponseEntity<Response<List<ResepMakanan>>>> getList(Long id, ResepMakananDto.GetListByIbuHamil dto, Pageable pageable);
    Mono<ResponseEntity<Response<List<ResepMakanan>>>> getList(Long id, ResepMakananDto.GetListByBayiAnak dto, Pageable pageable);
}
