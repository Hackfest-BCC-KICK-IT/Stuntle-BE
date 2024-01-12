package bcc.stuntle.app.storage.repository;

import bcc.stuntle.entity.StorageResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class StorageRepository implements IStorageRepository{

    @Autowired
    private Cloudinary cloudinary;

    private StorageResponse convertIntoStorageResponse(Map<?, ?> map){
        return StorageResponse
                .builder()
                .publicId((String)map.get("public_id"))
                .secureUrl((String)map.get("secure_url"))
                .signature((String)map.get("signature"))
                .build();
    }

    @Override
    public Mono<StorageResponse> create(byte[] file) {
        return Mono.fromFuture(
                CompletableFuture
                .supplyAsync(() -> {
                    try {
                        var res = this.cloudinary
                                .uploader()
                                .upload(file, ObjectUtils.asMap("folder", "sipas"));
                        return this.convertIntoStorageResponse(res);
                    } catch (IOException e) {
                        throw new RuntimeException(String.format("terjadi kesalahan pada saat menyimpan ke storage dengan pesan %s", e.getMessage()));
                    }
                })
        );
    }

    @Override
    public Mono<Void> destroy(String publicId) {
        return null;
    }
}
