package bcc.stuntle.app.storage.repository;

import bcc.stuntle.entity.StorageResponse;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IStorageRepository {
    Mono<StorageResponse> create(byte[] file);
    Mono<Void> destroy(String publicId);
}
