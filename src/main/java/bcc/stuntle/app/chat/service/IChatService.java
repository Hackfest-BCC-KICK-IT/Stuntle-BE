package bcc.stuntle.app.chat.service;

import bcc.stuntle.entity.OpenApiClientResponse;
import bcc.stuntle.entity.OpenApiResponse;
import bcc.stuntle.entity.Response;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IChatService {
    Mono<ResponseEntity<Response<OpenApiResponse>>> create(Long ortuId, String message);
    Mono<ResponseEntity<Response<OpenApiClientResponse>>> get(Long ortuId);
}
