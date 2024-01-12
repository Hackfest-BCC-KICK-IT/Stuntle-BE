package bcc.stuntle.dto;

import bcc.stuntle.entity.AjukanBantuan;
import bcc.stuntle.entity.StatusAjuan;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AjukanBantuanDto {

    public record Create(
            @NotNull(message = "judul pengajuan harus ada")
            @NotBlank(message = "judul pengajuan tidak boleh kosong")
            String judulPengajuan,

            @NotNull(message = "deskripsi pengajuan harus ada")
            @NotBlank(message = "deskripsi pengajuan tidak boleh kosong")
            String deskripsiPengajuan
    ){
        public AjukanBantuan toAjukanBantuan(){
            return AjukanBantuan
                    .builder()
                    .judul(this.judulPengajuan)
                    .deskripsi(this.deskripsiPengajuan)
                    .status(StatusAjuan.diproses.name())
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .build();
        }
    }

    public record Update(
            @NotNull(message = "pesan tambahan harus ada")
            @NotBlank(message = "pesan tambahan tidak boleh kosong")
            String pesanTambahan,
            @Schema(example = "gagal/diproses/sukses")
            StatusAjuan statusAjuan
    ){
        public AjukanBantuan toAjukanBantuan(){
            return AjukanBantuan
                    .builder()
                    .status(statusAjuan.name())
                    .pesanTambahan(this.pesanTambahan)
                    .updatedAt(LocalDate.now())
                    .build();
        }
    }
}
