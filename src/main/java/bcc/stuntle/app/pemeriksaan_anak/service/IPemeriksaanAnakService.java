package bcc.stuntle.app.pemeriksaan_anak.service;

import bcc.stuntle.dto.DataPemeriksaanAnakDto;
import bcc.stuntle.entity.DataPemeriksaanAnak;
import bcc.stuntle.entity.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IPemeriksaanAnakService {
    Mono<ResponseEntity<Response<DataPemeriksaanAnak>>> create(Long ortuId, Long faskesId, Long dataAnakId, DataPemeriksaanAnakDto.Create dto);
    Mono<ResponseEntity<Response<List<DataPemeriksaanAnak>>>> getList(Long ortuId, Long faskesId, Long dataAnakId, Pageable page);
}
