package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "orang_tua")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class Orangtua {

    @Id
    private Long id;

    private String namaIbu;

    private String namaAyah;

    private String email;

    private String password;

    @Column("image_url")
    private String imageUrl;

    @Column("is_connect_faskes")
    private Boolean isConnectedWithFaskes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate deletedAt;
}
