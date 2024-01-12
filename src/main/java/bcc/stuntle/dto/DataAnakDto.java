package bcc.stuntle.dto;

import bcc.stuntle.constant.DateTimeConstant;
import bcc.stuntle.entity.DataAnak;
import bcc.stuntle.entity.JenisKelamin;
import bcc.stuntle.entity.KondisiLahir;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataAnakDto {

    public record Create(
            @NotNull(message = "nama anak harus ada")
            @NotBlank(message = "nama anak tidak boleh kosong")
            String namaAnak,

            @NotNull(message = "tanggal lahir anak harus ada")
            @NotBlank(message = "tanggal lahir anak tidak boleh kosong")
            String tanggalLahirAnak,

            @Schema(name = "jenisKelamin",example = "laki/perempuan")
            @NotNull(message = "jenis kelamin harus ada")
            JenisKelamin jenisKelamin,

            @Schema(name = "kondisiLahir",example = "prematur/sehat/lainnya")
            @NotNull(message = "kondisi lahir harus ada")
            KondisiLahir kondisiLahir,

            @NotNull(message = "berat badan lahir harus ada")
            @PositiveOrZero(message = "nilai minimal adalah 0")
            Double beratBadanLahir,

            @NotNull(message = "panjang badan lahir harus ada")
            @PositiveOrZero(message = "nilai minimal adalah 0")
            Double panjangBadanLahir,

            @NotNull(message = "lingkar kepala harus ada")
            @PositiveOrZero(message = "nilai minimal adalah 0")
            Double lingkarKepala
    ){
            public DataAnak toDataAnak(){
                    return DataAnak
                            .builder()
                            .beratBadanLahir(this.beratBadanLahir)
                            .namaAnak(this.namaAnak)
                            .kondisiLahir(this.kondisiLahir.name())
                            .tanggalLahir(LocalDate.parse(this.tanggalLahirAnak, DateTimeFormatter.ofPattern(DateTimeConstant.DD_MM_YYYY)))
                            .jenisKelamin(this.jenisKelamin.name())
                            .lingkarKepala(this.lingkarKepala)
                            .panjangBadanLahir(this.panjangBadanLahir)
                            .createdAt(LocalDate.now())
                            .updatedAt(LocalDate.now())
                            .build();
            }
    }

    public record SearchByName(
            @NotNull(message = "nama ortu harus ada")
            @NotBlank(message = "nama ortu tidak boleh kosong")
            String namaOrtu
    ){}

    public record Update(
            String namaAnak,
            String tanggalLahirAnak,
            @Schema(name = "jenisKelamin",example = "laki/perempuan")
            JenisKelamin jenisKelamin,

            @Schema(name = "kondisiLahir",example = "prematur/sehat/lainnya")
            KondisiLahir kondisiLahir,

            @PositiveOrZero(message = "nilai minimal adalah 0")
            Double beratBadanLahir,

            @PositiveOrZero(message = "nilai minimal adalah 0")
            Double panjangBadanLahir,

            @PositiveOrZero(message = "nilai minimal adalah 0")
            Double lingkarKepala
    ){
        public DataAnak toDataAnak(){
            /*
                Will migrate to MapStruct soon since a lot of null checking :D
             */
            var builder =  DataAnak
                    .builder()
                    .beratBadanLahir(this.beratBadanLahir)
                    .namaAnak(this.namaAnak)
                    .lingkarKepala(this.lingkarKepala)
                    .panjangBadanLahir(this.panjangBadanLahir)
                    .updatedAt(LocalDate.now());
            if(this.tanggalLahirAnak != null){
                builder.tanggalLahir(LocalDate.parse(this.tanggalLahirAnak, DateTimeFormatter.ofPattern(DateTimeConstant.DD_MM_YYYY)));
            }
            if(this.kondisiLahir != null){
                builder.kondisiLahir(this.kondisiLahir.name());
            }
            if(this.jenisKelamin != null){
                builder.jenisKelamin(this.jenisKelamin.name());
            }
            return builder.build();
        }
    }
}
