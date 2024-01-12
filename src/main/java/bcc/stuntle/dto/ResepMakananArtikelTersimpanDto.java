package bcc.stuntle.dto;

import bcc.stuntle.entity.ResepMakananArtikelTersimpan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResepMakananArtikelTersimpanDto {

    public record Create(
            @NotNull(message = "id artikel harus ada")
            Long artikelId,

            @NotNull(message = "id resep makanan harus ada")
            Long resepMakananId,

            @NotNull(message = "jenis harus ada")
            @NotBlank(message = "jenis tidak boleh kosong")
            String jenis
    ){
        public ResepMakananArtikelTersimpan toResepMakananArtikel(){
            return ResepMakananArtikelTersimpan
                    .builder()
                    .fkArtikelId(artikelId)
                    .fkResepMakananId(resepMakananId)
                    .jenis(this.jenis)
                    .build();
        }
    }
}
