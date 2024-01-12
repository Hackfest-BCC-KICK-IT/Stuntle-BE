package bcc.stuntle.app.data_anak.service;

import bcc.stuntle.app.data_anak.repository.DataAnakRepository;
import bcc.stuntle.app.orang_tua_faskes.repository.OrangtuaFaskesRepository;
import bcc.stuntle.app.ortu.repository.OrangtuaRepository;
import bcc.stuntle.app.pemeriksaan_anak.repository.PemeriksaanAnakRepository;
import bcc.stuntle.dto.DataAnakDto;
import bcc.stuntle.entity.*;
import bcc.stuntle.exception.DataTidakDitemukanException;
import bcc.stuntle.exception.DatabaseException;
import bcc.stuntle.mapper.DataAnakMapper;
import bcc.stuntle.util.CollectionUtils;
import bcc.stuntle.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class DataAnakService implements IDataAnakService{

    @Autowired
    private DataAnakRepository repository;

    @Autowired
    private OrangtuaRepository orangtuaRepository;

    @Autowired
    private OrangtuaFaskesRepository ortuFaskesRepository;

    @Autowired
    private PemeriksaanAnakRepository pemeriksaanAnakRepository;

    @Override
    public Mono<ResponseEntity<Response<DataAnak>>> create(Long id, DataAnakDto.Create dto) {
        DataAnak dataAnak = dto.toDataAnak();
        dataAnak.setFkOrtuId(id);
        return this.orangtuaRepository
                .findOne(
                        Example.of(Orangtua
                                .builder()
                                .id(id)
                                .build())
                )
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data orang tua tidak ditemukan")))
                .flatMap((d) -> {
                    if(d.getIsConnectedWithFaskes() == null){
                        return Mono.error(new DatabaseException("pastikan orangtua sudah terhubung dengan fasilitas kesehatan"));
                    }
                    if(d.getIsConnectedWithFaskes()){
                        return Mono.just(d);
                    } else {
                        return Mono.error(new DatabaseException("pastikan orangtua sudah terhubung dengan fasilitas kesehatan"));
                    }
                })
                .then(this.repository.save(dataAnak))
                .flatMap((d) -> Mono.fromCallable(() -> ResponseUtil
                        .sendResponse(HttpStatus.CREATED,
                                Response
                                        .<DataAnak>builder()
                                        .data(d)
                                        .message("sukses membuat data anak")
                                        .success(true)
                                        .build()
                        ))
                );
    }

    @Override
    public Mono<ResponseEntity<Response<DataAnak>>> get(Long id) {
        return this.repository
                .get(Example.of(
                        DataAnak.builder().id(id).build()
                ))
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<DataAnak>builder()
                                .data(res)
                                .message("sukses mendapatkan data anak")
                                .success(true)
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<List<DataAnak>>>> getList(Long id, Pageable pageable) {
        return this.repository
                .getList(id, pageable)
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data anak tidak ditemukan")))
                .flatMap((d) ->
                    Mono.fromCallable(() -> ResponseUtil
                           .sendResponse(
                                   HttpStatus.OK,
                                   Response
                                           .<List<DataAnak>>builder()
                                           .message("sukses menemukan data anak")
                                           .data(d.getContent())
                                           .pagination(
                                                   PaginationResult
                                                           .<List<DataAnak>>builder()
                                                           .totalPage(d.getTotalPages())
                                                           .currentPage(pageable.getPageNumber())
                                                           .currentElement(d.getContent().size())
                                                           .totalElement(d.getTotalElements())
                                                           .build()
                                           )
                                           .success(true)
                                           .build()
                           )
                   )
                );
    }

    @Override
    public Mono<ResponseEntity<Response<Map<String, Long>>>> count(Long faskesId) {
        return this.ortuFaskesRepository
                .getList(faskesId, Pageable.unpaged())
                .map(Page::getContent)
                .map((d) -> d.stream().parallel().map(OrangtuaFaskes::getFkOrtuId).toList())
                .flatMap((ortuIds) -> this.repository.getList(ortuIds, Pageable.unpaged()))
                .map(Page::getContent)
                .map((dataAnaks) -> dataAnaks.stream().map(DataAnak::getId).toList())
                .flatMap((dataAnakIds) ->
                             this
                                    .pemeriksaanAnakRepository
                                    .getList(dataAnakIds, Pageable.unpaged())
                                    .map(Page::getContent)
                                    .map((dataPemeriksaanAnaks) ->
                                         ResponseUtil
                                                .sendResponse(
                                                        HttpStatus.OK,
                                                        Response
                                                                .<Map<String, Long>>builder()
                                                                .success(true)
                                                                .message("sukses mendapatkan data statistik pemeriksaan anak")
                                                                .data(
                                                                        CollectionUtils.ofLinkedHashMap(
                                                                                new String[]{"jumlahProfilAnak", "jumlahSudahPeriksa", "jumlahBelumPeriksa"},
                                                                                new Long[]{(long) dataAnakIds.size(), (long) dataPemeriksaanAnaks.size(), (long) dataAnakIds.size() - dataPemeriksaanAnaks.size()}
                                                                        )
                                                                )
                                                                .build()
                                                )
                                    )
                )
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ResponseEntity<Response<DataAnakOrtu>>> getList(Long faskesId, DataAnakDto.SearchByName dto, Pageable pageable) {
        return this.ortuFaskesRepository
                .getList(faskesId, Pageable.unpaged())
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data orangtua tidak ditemukan")))
                .map(Page::getContent)
                .map((p) -> p.stream().parallel().map(OrangtuaFaskes::getFkOrtuId).toList())
                .flatMap((ortuIds) ->
                        this
                                .repository
                                .getList(ortuIds, Pageable.unpaged())
                                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data orangtua tidak ditemukan pada data anak")))
                                .map(Page::getContent)
                                .zipWith(this.orangtuaRepository.findAll(ortuIds, dto.namaOrtu()))
                )
                .map((t) -> {
                    List<DataAnak> dataAnaks = t.getT1();
                    List<Orangtua> orangtuas = t.getT2();
                    List<Long> idOrangtuas = orangtuas.stream().parallel().map(Orangtua::getId).toList();

                    Map<Long, List<DataAnak>> mapDataAnak = new LinkedHashMap<>();
                    Map<Long, Orangtua> mapDataOrtu = new LinkedHashMap<>();
                    List<Long> idsOrtuExist = new ArrayList<>();

                    List<DataAnak> res = new ArrayList<>();
                    List<Orangtua> resOrtu = new ArrayList<>();

                    dataAnaks.forEach((anak) -> {
                        List<DataAnak> dataAnak = mapDataAnak.get(anak.getFkOrtuId());
                        if(dataAnak == null || dataAnak.isEmpty()){
                            List<DataAnak> listAnak = new ArrayList<>();
                            listAnak.add(anak);
                            mapDataAnak.put(anak.getFkOrtuId(), listAnak);
                        } else {
                            dataAnak.add(anak);
                        }
                    });

                    orangtuas.forEach((ortu) -> {
                        mapDataOrtu.put(ortu.getId(), ortu);
                    });

                    idOrangtuas.forEach((idOrtu) -> {
                        List<DataAnak> listDataAnak = mapDataAnak.get(idOrtu);
                        if(listDataAnak != null){
                            res.addAll(listDataAnak);
                            idsOrtuExist.add(idOrtu);
                        }
                    });

                    idsOrtuExist.forEach((idOrtu) -> resOrtu.add(mapDataOrtu.get(idOrtu)));

                    int offset = (int) pageable.getOffset() * pageable.getPageSize();
                    List<DataAnak> finalRes = List.copyOf(res);
                    if(finalRes.size() >= offset + pageable.getPageSize()){
                         finalRes = finalRes.subList(offset + 1, offset + pageable.getPageSize());
                    }

                    DataAnakOrtu dataAnakOrtu = DataAnakOrtu
                            .builder()
                            .dataAnak(finalRes)
                            .ortu(resOrtu)
                            .build();

                    return ResponseUtil.sendResponse(
                            HttpStatus.OK,
                            Response
                                    .<DataAnakOrtu>builder()
                                    .data(dataAnakOrtu)
                                    .message("sukses mendapatkan hasil pencarian data anak")
                                    .success(true)
                                    .pagination(
                                            PaginationResult
                                                    .<DataAnakOrtu>builder()
                                                    .currentElement(finalRes.size())
                                                    .currentPage(pageable.getPageNumber())
                                                    .totalElement(dataAnaks.size())
                                                    .totalPage((int)Math.ceil((double)dataAnaks.size()/ pageable.getPageSize()))
                                                    .build()
                                    )
                                    .build()
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ResponseEntity<Response<Void>>> update(Long dataAnakId, DataAnakDto.Update dto) {
        return this.repository
                .get(Example.of(
                        DataAnak
                                .builder()
                                .id(dataAnakId)
                                .build()
                ))
                .flatMap((res) -> {
                    res = DataAnakMapper.INSTANCE.update(dto.toDataAnak(), res);
                    return this.repository.save(res);
                })
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<Void>builder()
                                .success(true)
                                .message("sukses mengupdate data anak")
                                .build()
                ));
    }

    @Override
    public Mono<ResponseEntity<Response<Void>>> delete(Long dataAnakId) {
        return this.repository
                .get(Example.of(
                        DataAnak
                                .builder()
                                .id(dataAnakId)
                                .build()
                ))
                .flatMap((dataAnak) -> {
                    dataAnak.setDeletedAt(LocalDate.now());
                    return this.repository.save(dataAnak);
                })
                .map((res) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<Void>builder()
                                .success(true)
                                .message("sukses menghapus data anak")
                                .build()
                ));
    }

}
