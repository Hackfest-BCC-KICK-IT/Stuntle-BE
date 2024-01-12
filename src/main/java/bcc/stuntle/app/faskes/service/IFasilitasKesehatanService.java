package bcc.stuntle.app.faskes.service;

import bcc.stuntle.dto.FasilitasKesehatanDto;
import bcc.stuntle.entity.FasilitasKesehatan;
import bcc.stuntle.entity.Response;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IFasilitasKesehatanService {

    Mono<ResponseEntity<Response<FasilitasKesehatan>>> create(FasilitasKesehatanDto.Create dto);
    Mono<ResponseEntity<Response<FasilitasKesehatan>>> login(FasilitasKesehatanDto.Login dto);
}
