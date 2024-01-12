package bcc.stuntle.dto;

import bcc.stuntle.constant.DateTimeConstant;
import bcc.stuntle.entity.DataPemeriksaanKehamilan;
import bcc.stuntle.entity.StatusKehamilan;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataPemeriksaanKehamilanDto {

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

            @NotNull(message = "usia kandungan harus ada")
            @PositiveOrZero(message = "usia kandungan tidak boleh negative")
            Integer usiaKandungan,

            @NotNull(message = "tekanan darah harus ada")
            @NotBlank(message = "tekanan darah tidak boleh kosong")
            String tekananDarah,

            @NotNull(message = "berat badan ibu harus ada")
            @PositiveOrZero(message = "berat badan ibu tidak boleh negative")
            Double beratBadanIbu,

            @Schema(defaultValue = "baik/lemah/beresiko")
            @NotNull(message = "status kehamilan harus ada")
            StatusKehamilan statusKehamilan,

            @NotNull(message = "pesan tambahan harus ada")
            @NotBlank(message = "pesan tambahan tidak boleh kosong")
            String pesanTambahan
    ){
        public DataPemeriksaanKehamilan toDataPemeriksaanKehamilan(){
            return DataPemeriksaanKehamilan
                    .builder()
                    .beratBadanIbu(this.beratBadanIbu)
                    .statusKehamilan(this.statusKehamilan.name())
                    .namaPemeriksa(this.namaPemeriksa)
                    .pesanTambahan(this.pesanTambahan)
                    .tekananDarah(this.tekananDarah)
                    .tanggalPemeriksaan(LocalDate.parse(this.tanggalPemeriksaan, DateTimeFormatter.ofPattern(DateTimeConstant.DD_MM_YYYY)))
                    .usiaKandungan(this.usiaKandungan)
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .tempatPemeriksaan(this.tempatPemeriksaan)
                    .build();
        }
    }
}
