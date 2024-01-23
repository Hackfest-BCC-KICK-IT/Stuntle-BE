package bcc.stuntle.app.dashboard.service;

import bcc.stuntle.app.ajukan_bantuan.repository.AjukanBantuanRepository;
import bcc.stuntle.app.artikel.repository.ArtikelRepository;
import bcc.stuntle.app.data_anak.repository.DataAnakRepository;
import bcc.stuntle.app.data_kehamilan.repository.DataKehamilanRepository;
import bcc.stuntle.app.orang_tua_faskes.repository.OrangtuaFaskesRepository;
import bcc.stuntle.app.pemeriksaan_anak.repository.PemeriksaanAnakRepository;
import bcc.stuntle.app.pemeriksaan_kehamilan.repository.PemeriksaanKehamilanRepository;
import bcc.stuntle.app.resep_makanan.repository.ResepMakananRepository;
import bcc.stuntle.entity.*;
import bcc.stuntle.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@Transactional
@Slf4j
public class DashboardService implements IDashboardService{

    @Autowired
    private OrangtuaFaskesRepository ortuFaskesRepository;

    @Autowired
    private ResepMakananRepository resepMakananRepository;

    @Autowired
    private ArtikelRepository artikelRepository;

    @Autowired
    private AjukanBantuanRepository ajukanBantuanRepository;

    @Autowired
    private DataKehamilanRepository dataKehamilanRepository;

    @Autowired
    private PemeriksaanKehamilanRepository pemeriksaanKehamilanRepository;

    @Autowired
    private DataAnakRepository dataAnakRepository;

    @Autowired
    private PemeriksaanAnakRepository pemeriksaanAnakRepository;

    @Override
    public Mono<ResponseEntity<Response<Dashboard>>> getDashboard(Long faskesId) {

        var fetchOrangtuaFaskes = this.ortuFaskesRepository
                .getList(faskesId, Pageable.unpaged())
                .map(Page::getContent);

        var fetchResepMakanan = this.resepMakananRepository
                .getList(faskesId, Pageable.unpaged())
                .map(Page::getContent);

        var fetchArtikel = this.artikelRepository
                .getList(faskesId, Pageable.unpaged())
                .map(Page::getContent);

        var ajukanBantuanDiproses = this.ajukanBantuanRepository
                .getListFaskes(faskesId, StatusAjuan.diproses.name(), Pageable.unpaged())
                .map(Page::getContent);

        var ajukanBantuanDiterima = this.ajukanBantuanRepository
                .getListFaskes(faskesId, StatusAjuan.sukses.name(), Pageable.unpaged())
                .map(Page::getContent);

        var ajukanBantuanDitolak = this.ajukanBantuanRepository
                .getListFaskes(faskesId, StatusAjuan.gagal.name(), Pageable.unpaged())
                .map(Page::getContent);

        var dataAnakTerdata = fetchOrangtuaFaskes
                .map((listOrtuFaskes) -> listOrtuFaskes.stream().map((ortuFaskes) -> ortuFaskes.getFkOrtuId()).toList())
                .flatMap((ortuId) -> this.dataAnakRepository.getList(ortuId, Pageable.unpaged()))
                .map(Page::getContent);

        var dataPemeriksaanAnak = dataAnakTerdata
                .map((listDataAnak) -> listDataAnak.stream().map(DataAnak::getId).toList())
                .flatMap((dataAnakIds) -> this.pemeriksaanAnakRepository.getList(dataAnakIds, Pageable.unpaged()))
                .map(Page::getContent);

        var dataBayiTerdata = fetchOrangtuaFaskes
                .map((listOrtuFaskes) -> listOrtuFaskes.stream().map((ortuFaskes) -> ortuFaskes.getFkOrtuId()).toList())
                .flatMap((ortuId) -> this.dataKehamilanRepository.getList(ortuId, Pageable.unpaged()))
                .map(Page::getContent);

        var dataPemeriksaanBayi = dataBayiTerdata
                .map((listDataAnak) -> listDataAnak.stream().map(DataKehamilan::getId).toList())
                .flatMap((dataKehamilanIds) -> this.pemeriksaanKehamilanRepository.getList(dataKehamilanIds, Pageable.unpaged()));

        var zip = Mono.zip(
                fetchOrangtuaFaskes,
                fetchResepMakanan,
                fetchArtikel,
                ajukanBantuanDiproses,
                ajukanBantuanDiterima,
                ajukanBantuanDitolak,
                dataAnakTerdata.zipWith(dataPemeriksaanAnak),
                dataBayiTerdata.zipWith(dataPemeriksaanBayi)
        );

        return zip.map((t8) -> {

            var dashboardData = Dashboard
                    .builder();

            var ortuFaskes = t8.getT1();
            var resepMakanan = t8.getT2();
            var artikel = t8.getT3();
            var dataAjukanBantuanDiproses = t8.getT4();
            var dataAjukanBantuanDiterima = t8.getT5();
            var dataAjukanBantuanDitolak = t8.getT6();
            var t2DataAnak = t8.getT7();
            var t2DataBayi = t8.getT8();

            dashboardData
                    .jumlahOrangtuaTerhubung((long)ortuFaskes.size())
                    .jumlahResepMakananTerupload((long)resepMakanan.size())
                    .jumlahArtikelTerupload((long)artikel.size())
                    .jumlahAjukanBantuanDiproses((long)dataAjukanBantuanDiproses.size())
                    .jumlahAjukanBantuanDiterima((long)dataAjukanBantuanDiterima.size())
                    .jumlahAjukanBantuanDitolak((long)dataAjukanBantuanDitolak.size());

            var dataAnak = t2DataAnak.getT1();
            var pemeriksaanAnaks = t2DataAnak.getT2();

            var nSudahPeriksaDataAnak = 0;

            for(var d: dataAnak){
                if(d.getFkDataPemeriksaanAnak() != null && !d.getFkDataPemeriksaanAnak().isEmpty()){
                    nSudahPeriksaDataAnak++;
                }
            }

            dashboardData
                    .jumlahProfilAnakTerdata((long)dataAnak.size())
                    .jumlahAdaDataPemeriksaanAnak((long)nSudahPeriksaDataAnak)
                    .jumlahBelumAdaDataPemeriksaanAnak((long)(dataAnak.size()) - nSudahPeriksaDataAnak);

            var pemeriksaanAnakBaik = this.filterLatestDataPemeriksaanAnak(pemeriksaanAnaks, StatusAnak.baik.name());
            var pemeriksaanAnakBerpotensi = this.filterLatestDataPemeriksaanAnak(pemeriksaanAnaks, StatusAnak.berpotensi.name());
            var pemeriksaanAnakTerindikasi = this.filterLatestDataPemeriksaanAnak(pemeriksaanAnaks, StatusAnak.terindikasi.name());

            dashboardData
                    .jumlahPemeriksaanAnakBaik((long)pemeriksaanAnakBaik.size())
                    .jumlahPemeriksaanAnakBerpotensi((long)pemeriksaanAnakBerpotensi.size())
                    .jumlahPemeriksaanAnakTerindikasi((long)pemeriksaanAnakTerindikasi.size());

            var dataKehamilan = t2DataBayi.getT1();
            var dataPemeriksaanKehamilan = t2DataBayi.getT2();

            var nSudahPeriksaDataKehamilan = 0;

            for(var d: dataKehamilan){
                if(d.getFkPemeriksaanIds() != null && !d.getFkPemeriksaanIds().isEmpty()){
                    nSudahPeriksaDataKehamilan++;
                }
            }

            dashboardData
                    .jumlahProfilBayiTerdata((long)dataKehamilan.size())
                    .jumlahAdaDataPemeriksaanIbuHamil((long)nSudahPeriksaDataKehamilan)
                    .jumlahBelumAdaDataPemeriksaanIbuHamil((long)(dataKehamilan.size() - nSudahPeriksaDataKehamilan));

            var pemeriksaanKehamilanBaik = this.filterLatestDataPemeriksaanKehamilan(dataPemeriksaanKehamilan, StatusKehamilan.baik.name());
            var pemeriksaanKehamilanLemah = this.filterLatestDataPemeriksaanKehamilan(dataPemeriksaanKehamilan, StatusKehamilan.lemah.name());
            var pemeriksaanKehamilanBeresiko = this.filterLatestDataPemeriksaanKehamilan(dataPemeriksaanKehamilan, StatusKehamilan.beresiko.name());

            dashboardData
                    .jumlahPemeriksaanKehamilanBaik((long)pemeriksaanKehamilanBaik.size())
                    .jumlahPemeriksaanKehamilanBeresiko((long)pemeriksaanKehamilanBeresiko.size())
                    .jumlahPemeriksaanKehamilanLemah((long)pemeriksaanKehamilanLemah.size());

            return dashboardData.build();
        })
                .map((dashboard) -> ResponseUtil
                        .sendResponse(
                                HttpStatus.OK,
                                Response
                                        .<Dashboard>builder()
                                        .success(true)
                                        .message("sukses mendapatkan data dashboard")
                                        .data(dashboard)
                                        .build()
                        ))
        .subscribeOn(Schedulers.boundedElastic());
    }

    private List<DataPemeriksaanAnak> filterLatestDataPemeriksaanAnak(List<DataPemeriksaanAnak> data, String statusAnak){
        final var listData = new ArrayList<DataPemeriksaanAnak>();

        var map = new LinkedHashMap<Long, List<DataPemeriksaanAnak>>();

        data.forEach((d) -> {
            var mapData = map.get(d.getFkDataAnak());
            if(mapData == null){
                var tempList = new ArrayList<DataPemeriksaanAnak>();
                tempList.add(d);
                map.put(d.getFkDataAnak(), tempList);
            } else {
                mapData.add(d);
                map.put(d.getFkDataAnak(), mapData);
            }
        });

        map.forEach((k, v) -> {
            if(v != null && !v.isEmpty()){
                var sorted = v.stream()
                        .filter((d) -> d.getStatusAnak().equals(statusAnak))
                        .sorted((x, y) -> {
                            // https://chat.openai.com/share/969deae5-dc17-4f2c-beb4-47e0482f0dbe
                            var xCreatedAt = x.getCreatedAt();
                            var yCreatedAt = y.getCreatedAt();

                            if(yCreatedAt.isAfter(xCreatedAt)){
                                return 1;
                            } else if(yCreatedAt.isBefore(xCreatedAt)){
                                return -1;
                            } else {
                                return 0;
                            }
                        })
                        .toList();

                log.info("sorted: {}", sorted);

                listData.add(sorted.get(0));
            }
        });

        return listData;
    }

    private List<DataPemeriksaanKehamilan> filterLatestDataPemeriksaanKehamilan(List<DataPemeriksaanKehamilan> data, String statusKehamilan){
        final var listData = new ArrayList<DataPemeriksaanKehamilan>();

        var map = new LinkedHashMap<Long, List<DataPemeriksaanKehamilan>>();

        data.forEach((d) -> {
            var mapData = map.get(d.getFkDataKehamilan());
            if(mapData == null){
                var tempList = new ArrayList<DataPemeriksaanKehamilan>();
                tempList.add(d);
                map.put(d.getFkDataKehamilan(), tempList);
            } else {
                mapData.add(d);
                map.put(d.getFkDataKehamilan(), mapData);
            }
        });

        map.forEach((k, v) -> {
            if(v != null && !v.isEmpty()){
                var sorted = v.stream()
                        .filter((d) -> d.getStatusKehamilan().equals(statusKehamilan))
                        .sorted((x, y) -> {
                            // https://chat.openai.com/share/969deae5-dc17-4f2c-beb4-47e0482f0dbe
                            var xCreatedAt = x.getCreatedAt();
                            var yCreatedAt = y.getCreatedAt();

                            if(yCreatedAt.isAfter(xCreatedAt)){
                                return 1;
                            } else if(yCreatedAt.isBefore(xCreatedAt)){
                                return -1;
                            } else {
                                return 0;
                            }
                        })
                        .toList();

                log.info("sorted: {}", sorted);

                listData.add(sorted.get(0));
            }
        });

        return listData;
    }
}
