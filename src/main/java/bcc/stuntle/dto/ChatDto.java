package bcc.stuntle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChatDto {
    public record Create(
            @NotNull(message = "message harus ada")
            @NotBlank(message = "message tidak boleh kosong")
            String message
    ){}
}
