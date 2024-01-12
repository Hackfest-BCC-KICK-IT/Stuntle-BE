package bcc.stuntle.dto;

import bcc.stuntle.entity.Artikel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ArtikelDto {
    public record Create(
            @NotNull(message = "judul artikel harus ada")
            @NotBlank(message = "judul artikel tidak boleh kosong")
            String judulArtikel,

            @NotNull(message = "nama peninjau harus ada")
            @NotBlank(message = "nama peninjau tidak boleh kosong")
            String namaPeninjau,

            @NotNull(message = "isi artikel harus ada")
            @NotBlank(message = "isi artikel tidak boleh kosong")
            String isiArtikel
    ){
        public Artikel toArtikel(){
            return Artikel
                    .builder()
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .judulArtikel(this.judulArtikel)
                    .isiText(this.isiArtikel)
                    .peninjau(this.namaPeninjau)
                    .build();
        }
    }
}
