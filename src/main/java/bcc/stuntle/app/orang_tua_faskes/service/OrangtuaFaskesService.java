package bcc.stuntle.app.orang_tua_faskes.service;

import bcc.stuntle.app.faskes.repository.FasilitasKesehatanRepository;
import bcc.stuntle.app.orang_tua_faskes.repository.OrangtuaFaskesRepository;
import bcc.stuntle.app.ortu.repository.OrangtuaRepository;
import bcc.stuntle.entity.*;
import bcc.stuntle.exception.DataTidakDitemukanException;
import bcc.stuntle.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
class OrangtuaFaskesService implements IOrangtuaFaskesService {

    @Autowired
    private OrangtuaRepository repository;

    @Autowired
    private OrangtuaFaskesRepository orangtuaFaskesRepository;

    @Autowired
    private FasilitasKesehatanRepository faskesRepository;

    @Override
    public Mono<ResponseEntity<Response<OrangtuaFaskes>>> connectFaskes(Long id, String kodeUnik) {
        Mono<Orangtua> orangtua = this.repository
                .findById(id)
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data orangtua tidak ditemukan")))
                .flatMap((d) -> {
                    d.setIsConnectedWithFaskes(true);
                    return this.repository.update(d);
                });
        Mono<FasilitasKesehatan> faskes = this.faskesRepository
                .findOne(Example.of(FasilitasKesehatan
                                .builder()
                                .kodeUnik(kodeUnik)
                                .build()
                        )
                )
                .switchIfEmpty(Mono.error(new DataTidakDitemukanException("data faskes tidak ditemukan")));
        return Mono.zip(orangtua, faskes)
                .flatMap((t) -> {
                    Orangtua ortuData = t.getT1();
                    FasilitasKesehatan faskesData = t.getT2();
                    return this.orangtuaFaskesRepository.create(
                            OrangtuaFaskes
                                .builder()
                                .fkFaskesId(faskesData.getId())
                                .fkOrtuId(ortuData.getId())
                                .createdAt(LocalDate.now())
                                .updatedAt(LocalDate.now())
                                .build()
                    );
                })
                .flatMap((d) ->
                        Mono.fromCallable(() -> ResponseUtil.sendResponse(
                                HttpStatus.CREATED,
                                Response
                                        .<OrangtuaFaskes>builder()
                                        .success(true)
                                        .data(d)
                                        .message("sukses menghubungkan data orangtua dengan faskes")
                                        .build()
                        )
                    )
                );
    }

    @Override
    public Mono<ResponseEntity<Response<List<OrangtuaFaskes>>>> getList(Long faskesId, Pageable page) {
        return this
                .orangtuaFaskesRepository
                .getList(faskesId, page)
                .flatMap((d) -> Mono.fromCallable(() ->
                        ResponseUtil
                        .sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<List<OrangtuaFaskes>>builder()
                                        .message("sukses mendapatkan data koneksi orangtua faskes")
                                        .success(true)
                                        .data(d.getContent())
                                        .pagination(
                                                PaginationResult
                                                        .<List<OrangtuaFaskes>>builder()
                                                        .totalElement(d.getTotalElements())
                                                        .totalPage(d.getTotalPages())
                                                        .currentPage(page.getPageNumber())
                                                        .currentElement(d.getContent().size())
                                                        .build()
                                        )
                                        .build()
                        )
                    )
                );
    }

    @Override
    public Mono<ResponseEntity<Response<OrangtuaFaskesDescription>>> getOrtuFaskes(Long ortuId) {
        return this.orangtuaFaskesRepository
                .getListForOrtu(ortuId, PageRequest.of(0, 1))
                .map(Page::getContent)
                .flatMap((content) -> {
                    if(content.isEmpty()){
                        return Mono.error(new DataTidakDitemukanException("data ortu faskes tidak ditemukan"));
                    } else {
                        return Mono.just(content.get(0));
                    }
                })
                .flatMap((content) -> Mono.zip(
                        this.repository.findById(content.getFkOrtuId()),
                        this.faskesRepository.findOne(Example.of(
                                FasilitasKesehatan
                                        .builder()
                                        .id(content.getFkFaskesId())
                                        .build()
                        ))
                ))
                .map((t) -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response
                                .<OrangtuaFaskesDescription>builder()
                                .success(true)
                                .data(
                                        OrangtuaFaskesDescription
                                                .builder()
                                                .faskes(t.getT2())
                                                .orangtua(t.getT1())
                                                .build()
                                )
                                .message("sukses mendapatkan data deskripsi orangtua faskes")
                                .build()
                ))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
