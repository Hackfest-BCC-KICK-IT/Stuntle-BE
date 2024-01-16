package bcc.stuntle.app.prediction.service;

import bcc.stuntle.entity.Prediction;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface IPredictionService {
    Mono<Prediction> predict(Mono<FilePart> filePart, Double age);
}
