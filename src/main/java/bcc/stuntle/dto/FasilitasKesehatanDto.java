package bcc.stuntle.dto;

import bcc.stuntle.entity.FasilitasKesehatan;
import bcc.stuntle.util.BcryptUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public class FasilitasKesehatanDto {

    public record Create(
            @NotNull(message = "nama faskes harus ada")
            @NotBlank(message = "nama faskes tidak boleh kosong")
            String namaFaskes,

            @Email
            @NotNull(message = "email harus ada")
            @NotBlank(message = "email tidak boleh kosong")
            String email,

            @Length(min = 4, message = "panjang minimal password 4")
            @NotNull(message = "password harus ada")
            @NotBlank(message = "password tidak boleh kosong")
            String password,

            @NotNull(message = "alamat faskes harus ada")
            @NotBlank(message = "alamat faskes tidak boleh kosong")
            String alamatFasilitas,

            @NotNull(message = "nomor telepon harus ada")
            @NotBlank(message = "nomor telepon tidak boleh kosong")
            String nomorTelepon
    ){
        public FasilitasKesehatan toFaskes(){
            return FasilitasKesehatan
                    .builder()
                    .createdAt(LocalDate.now())
                    .email(this.email)
                    .password(BcryptUtil.encode(this.password))
                    .username(this.namaFaskes)
                    .nomorTelepon(this.nomorTelepon)
                    .alamatFaskes(this.alamatFasilitas)
                    .updatedAt(LocalDate.now())
                    .build();
        }
    }

    public record Login(
            @Email
            @NotNull(message = "email harus ada")
            @NotBlank(message = "email tidak boleh kosong")
            String email,

            @NotNull(message = "password harus ada")
            @NotBlank(message = "password tidak boleh kosong")
            String password
    ){}
}
