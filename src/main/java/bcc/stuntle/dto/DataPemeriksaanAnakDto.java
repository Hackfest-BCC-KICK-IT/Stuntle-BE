package bcc.stuntle.dto;

import bcc.stuntle.constant.DateTimeConstant;
import bcc.stuntle.entity.DataPemeriksaanAnak;
import bcc.stuntle.entity.StatusAnak;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataPemeriksaanAnakDto {

    public record Create(
            @NotNull(message = "tanggal pemeriksaan harus ada")
            @NotBlank(message = "tanggal pemeriksaan tidak boleh kosong")
            String tanggalPemeriksaan,

            @NotNull(message = "tempat pemeriksaan harus ada")
            @NotBlank(message = "tempat pemeriksaan tidak boleh kosong")
            String tempatPemeriksaan,

            @NotNull(message = "nama pemeriksa harus ada")
            @NotBlank(message = "nama pemeriksa tidak boleh kosong")
            String namaPemeriksa,

            @NotNull(message = "umur anak harus ada")
            @PositiveOrZero(message = "umur anak tidak boleh negative")
            String umurAnak,

            @NotNull(message = "tinggi anak harus ada")
            @PositiveOrZero(message = "tinggi anak tidak boleh negative")
            Double tinggiAnak,

            @NotNull(message = "berat badan anak harus ada")
            @PositiveOrZero(message = "berat badan anak tidak boleh negative")
            Double beratBadanAnak,

            @Schema(defaultValue = "baik/berpotensi/terindikasi")
            @NotNull(message = "status kehamilan harus ada")
            StatusAnak statusAnak,

            @NotNull(message = "pesan tambahan harus ada")
            @NotBlank(message = "pesan tambahan tidak boleh kosong")
            String pesanTambahan
    ){
            public DataPemeriksaanAnak toDataPemeriksaanAnak(){
                    return DataPemeriksaanAnak
                            .builder()
                            .beratBadanAnak(this.beratBadanAnak)
                            .createdAt(LocalDate.now())
                            .tempatPemeriksaan(this.tempatPemeriksaan)
                            .statusAnak(this.statusAnak.name())
                            .updatedAt(LocalDate.now())
                            .namaPemeriksa(this.namaPemeriksa)
                            .tanggalPemeriksaan(LocalDate.parse(this.tanggalPemeriksaan, DateTimeFormatter.ofPattern(DateTimeConstant.DD_MM_YYYY)))
                            .tinggiAnak(this.tinggiAnak)
                            .umurAnak(this.umurAnak)
                            .pesanTambahan(this.pesanTambahan)
                            .build();
            }
    }
}
