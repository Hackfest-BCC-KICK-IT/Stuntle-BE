package bcc.stuntle.app.prediction.service;

import bcc.stuntle.entity.Prediction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PredictionService implements IPredictionService{

    @Value("${prediction.url}")
    private String predictionUrl;

    private final String PART_PREDICTION_FILE = "file";

    private final String PART_PREDICTION_AGE = "data";

    @Override
    public Mono<Prediction> predict(Mono<FilePart> filePart, Double age) {
        var builder = new MultipartBodyBuilder();
        builder.part(PART_PREDICTION_FILE, filePart);
        builder.part(PART_PREDICTION_AGE, age);
        return WebClient.create(String.format("%s/predict", this.predictionUrl))
                .post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(
                        builder.build()
                ))
                .retrieve()
                .bodyToMono(Prediction.class);
    }
}
