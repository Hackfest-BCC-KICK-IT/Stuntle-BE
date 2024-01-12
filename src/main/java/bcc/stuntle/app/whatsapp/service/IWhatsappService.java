package bcc.stuntle.app.whatsapp.service;

import bcc.stuntle.dto.WhatsappDto;
import bcc.stuntle.entity.GrupWhatsapp;
import bcc.stuntle.entity.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IWhatsappService {
    Mono<ResponseEntity<Response<GrupWhatsapp>>> create(Long faskesId, WhatsappDto.CreateUpdate dto);
    Mono<ResponseEntity<Response<GrupWhatsapp>>> delete(Long whatsappId);
    Mono<ResponseEntity<Response<GrupWhatsapp>>> update(Long whatsappId, WhatsappDto.CreateUpdate dto);
    Mono<ResponseEntity<Response<List<GrupWhatsapp>>>> getList(Long faskesId, Pageable page);
    Mono<ResponseEntity<Response<GrupWhatsapp>>> get(Long whatsappId);
    Mono<ResponseEntity<Response<List<GrupWhatsapp>>>> getListForOrtu(Long ortuId, Pageable pageable);
}
