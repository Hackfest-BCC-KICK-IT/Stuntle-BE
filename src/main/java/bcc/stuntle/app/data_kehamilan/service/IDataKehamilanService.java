package bcc.stuntle.app.data_kehamilan.service;

import bcc.stuntle.dto.DataKehamilanDto;
import bcc.stuntle.entity.DataKehamilan;
import bcc.stuntle.entity.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface IDataKehamilanService {

    Mono<ResponseEntity<Response<DataKehamilan>>> create(Long id, DataKehamilanDto.Create dto);
    Mono<ResponseEntity<Response<DataKehamilan>>> get(Long id);
    Mono<ResponseEntity<Response<Map<String, Long>>>> count(Long faskesId);

    /*
    Get List by Ortu Id
     */
    Mono<ResponseEntity<Response<List<DataKehamilan>>>> getList(Long ortuId, Pageable pageable);
    Mono<ResponseEntity<Response<Void>>> update(Long dataKehamilanId, DataKehamilanDto.Update dto);
    Mono<ResponseEntity<Response<Void>>> delete(Long dataKehamilanId);
}
