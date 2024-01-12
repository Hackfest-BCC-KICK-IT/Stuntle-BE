package bcc.stuntle.app.orang_tua_faskes.service;

import bcc.stuntle.entity.OrangtuaFaskes;
import bcc.stuntle.entity.OrangtuaFaskesDescription;
import bcc.stuntle.entity.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IOrangtuaFaskesService {

    Mono<ResponseEntity<Response<OrangtuaFaskes>>> connectFaskes(Long id, String kodeUnik);
    Mono<ResponseEntity<Response<List<OrangtuaFaskes>>>> getList(Long faskesId, Pageable page);
    Mono<ResponseEntity<Response<OrangtuaFaskesDescription>>> getOrtuFaskes(Long ortuId);
}
