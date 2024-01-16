package bcc.stuntle.dto;

import bcc.stuntle.entity.Orangtua;
import bcc.stuntle.util.BcryptUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public class OrangtuaDto {

    public record Create(
            @NotNull(message = "nama ibu harus ada")
            @NotBlank(message = "nama ibu tidak boleh kosong")
            String namaIbu,

            @NotNull(message = "nomorTelepon harus ada")
            @NotBlank(message = "nomorTelepon tidak boleh kosong")
            String nomorTelepon,

            @Email
            @NotNull(message = "email harus ada")
            @NotBlank(message = "email tidak boleh kosong")
            String email,

            @NotNull(message = "password harus ada")
            @NotBlank(message = "password tidak boleh kosong")
            @Length(message = "panjang minimal 4", min = 4)
            String password
    ){
        public Orangtua toOrangtua(){
            return Orangtua.builder()
                    .nomorTelepon(this.nomorTelepon)
                    .namaIbu(this.namaIbu)
                    .email(this.email)
                    .password(BcryptUtil.encode(this.password))
                    .createdAt(LocalDate.now())
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

    public record Update(
            String namaAyah,
            String namaIbu,
            String email,
            String nomorTelepon
    ){
        public Orangtua toOrangtua(){
            return Orangtua
                    .builder()
                    .namaAyah(this.namaAyah)
                    .namaIbu(this.namaIbu)
                    .email(this.email)
                    .nomorTelepon(this.nomorTelepon)
                    .updatedAt(LocalDate.now())
                    .build();
        }
    }
}
