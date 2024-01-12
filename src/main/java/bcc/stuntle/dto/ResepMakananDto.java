package bcc.stuntle.dto;

import bcc.stuntle.entity.ResepMakanan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ResepMakananDto {

    public record Create(
            @NotNull(message = "judul resep harus ada")
            @NotBlank(message = "judul resep tidak boleh kosong")
            String judulResep,

            @NotNull(message = "target resep harus ada")
            @NotBlank(message = "target resep tidak boleh kosong")
            String targetResep,

            @NotNull(message = "target usia resep harus ada")
            @NotBlank(message = "target usia resep tidak boleh kosong")
            String targetUsiaResep,

            @NotNull(message = "jenis harus ada")
            @NotBlank(message = "jenis tidak boleh kosong")
            String jenis,

            @NotNull(message = "bahan utama harus ada")
            @NotBlank(message = "bahan utama tidak boleh kosong")
            String bahanUtama,

            @NotNull(message = "lama durasi harus ada")
            @NotBlank(message = "lama durasi tidak boleh kosong")
            String lamaDurasi,

            @NotNull(message = "text bahan harus ada")
            @NotBlank(message = "text bahan tidak boleh kosong")
            String bahanText,

            @NotNull(message = "text cara membuat harus ada")
            @NotBlank(message = "text cara membuat tidak boleh kosong")
            String caraMembuatText,

            @NotNull(message = "text nilai gizi harus ada")
            @NotBlank(message = "text nilai gizi tidak boleh kosong")
            String nilaiGiziText
    ){
            public ResepMakanan toResepMakanan(){
                    return ResepMakanan
                            .builder()
                            .judulResep(this.judulResep)
                            .createdAt(LocalDate.now())
                            .bahanText(this.bahanText)
                            .caraMembuatText(this.caraMembuatText)
                            .targetResep(this.targetResep)
                            .bahanUtama(this.bahanUtama)
                            .targetUsiaResep(this.targetUsiaResep)
                            .durasiMemasak(this.lamaDurasi)
                            .nilaiGiziText(this.nilaiGiziText)
                            .jenis(this.jenis)
                            .build();
            }
    }

    public record GetListByIbuHamil(
            String judulResep,
            String jenis,
            String usiaKehamilan,
            String bahanUtama,
            String durasiPembuatan
    ){
            public ResepMakanan toResepMakanan(){
                    return ResepMakanan
                            .builder()
                            .judulResep(this.judulResep)
                            .jenis(this.jenis)
                            .targetUsiaResep(usiaKehamilan)
                            .bahanUtama(bahanUtama)
                            .durasiMemasak(durasiPembuatan)
                            .build();
            }
    }

    public record GetListByBayiAnak(
            String judulResep,
            String jenis,
            String usiaBayiAnak,
            String bahanUtama,
            String durasiPembuatan
    ){
        public ResepMakanan toResepMakanan(){
            return ResepMakanan
                    .builder()
                    .judulResep(this.judulResep)
                    .jenis(this.jenis)
                    .targetUsiaResep(usiaBayiAnak)
                    .bahanUtama(bahanUtama)
                    .durasiMemasak(durasiPembuatan)
                    .build();
        }
    }
}
