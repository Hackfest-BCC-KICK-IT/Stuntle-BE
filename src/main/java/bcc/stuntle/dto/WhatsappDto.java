package bcc.stuntle.dto;

import bcc.stuntle.entity.GrupWhatsapp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class WhatsappDto {

    public record CreateUpdate(
            @NotNull(message = "nama grup harus ada")
            @NotBlank(message = "nama grup tidak boleh kosong")
            String namaGrup,

            @NotNull(message = "nama gr harus ada")
            @NotBlank(message = "nama ibu tidak boleh kosong")
            String linkGrupWhatsapp
    ){
        public GrupWhatsapp toWhatsapp(){
            return GrupWhatsapp
                    .builder()
                    .namaGrup(this.namaGrup)
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .linkGrup(this.linkGrupWhatsapp)
                    .build();
        }
    }
}
