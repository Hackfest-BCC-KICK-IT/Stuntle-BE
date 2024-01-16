package bcc.stuntle.dto;

import bcc.stuntle.constant.DateTimeConstant;
import bcc.stuntle.entity.DataKehamilan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataKehamilanDto {

    public record Create(
            @NotNull(message = "nama calon bayi harus ada")
            @NotBlank(message = "nama calon bayi tidak boleh kosong")
            String namaCalonBayi,

            @NotNull(message = "tanggal pertama haid harus ada")
            @NotBlank(message = "tanggal pertama haid tidak boleh kosong")
            String tanggalPertamaHaid
    ){
        public DataKehamilan toDataKehamilan(){
            return DataKehamilan
                    .builder()
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .namaCalonBayi(this.namaCalonBayi)
                    .tanggalPertamaHaid(LocalDate.parse(this.tanggalPertamaHaid, DateTimeFormatter.ofPattern(DateTimeConstant.DD_MM_YYYY)))
                    .build();
        }
    }

    public record Update(
            String namaCalonBayi,
            String tanggalPertamaHaid
    ){
        public DataKehamilan toDataKehamilan(){
            var builder =  DataKehamilan
                    .builder()
                    .updatedAt(LocalDate.now())
                    .namaCalonBayi(this.namaCalonBayi);
            if(this.tanggalPertamaHaid != null){
                builder.tanggalPertamaHaid(LocalDate.parse(this.tanggalPertamaHaid, DateTimeFormatter.ofPattern(DateTimeConstant.DD_MM_YYYY)));
            }
            return builder.build();
        }
    }
}
