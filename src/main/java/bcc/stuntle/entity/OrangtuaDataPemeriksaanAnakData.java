package bcc.stuntle.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class OrangtuaDataPemeriksaanAnakData {

    private String kategoriTinggi;

    private String interpretasi;

    private String rekomendasiSederhana;

    private String rekomendasiLengkap;
    
    private String rekomendasiTanggal;

    private String rekomendasiUsia;
    
    private String rekomendasiTinggi;

    private List<DataPemeriksaanAnak> dataPemeriksaanAnak;
}
