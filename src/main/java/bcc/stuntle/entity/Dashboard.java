package bcc.stuntle.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class Dashboard {

    private Long jumlahOrangtuaTerhubung;

    private Long jumlahResepMakananTerupload;

    private Long jumlahArtikelTerupload;

    private Long jumlahAjukanBantuanDiproses;

    private Long jumlahAjukanBantuanDiterima;

    private Long jumlahAjukanBantuanDitolak;

    private Long jumlahProfilBayiTerdata;

    private Long jumlahAdaDataPemeriksaanIbuHamil;

    private Long jumlahBelumAdaDataPemeriksaanIbuHamil;

    private Long jumlahPemeriksaanKehamilanBaik;

    private Long jumlahPemeriksaanKehamilanLemah;

    private Long jumlahPemeriksaanKehamilanBeresiko;

    private Long jumlahProfilAnakTerdata;

    private Long jumlahAdaDataPemeriksaanAnak;

    private Long jumlahBelumAdaDataPemeriksaanAnak;

    private Long jumlahPemeriksaanAnakBaik;

    private Long jumlahPemeriksaanAnakBerpotensi;

    private Long jumlahPemeriksaanAnakTerindikasi;
}
