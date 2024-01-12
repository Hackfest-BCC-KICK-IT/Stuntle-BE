package bcc.stuntle.app.ortu.service;

import bcc.stuntle.dto.OrangtuaDto;
import bcc.stuntle.entity.Orangtua;
import bcc.stuntle.entity.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface IOrangtuaService {
     Mono<ResponseEntity<Response<Orangtua>>> create(OrangtuaDto.Create dto);
     Mono<ResponseEntity<Response<Orangtua>>> login(OrangtuaDto.Login dto);
     Mono<ResponseEntity<Response<Orangtua>>> get(Long id);
     Mono<ResponseEntity<Response<Orangtua>>> update(Long id, Orangtua orangtua, Mono<FilePart> file);
}
