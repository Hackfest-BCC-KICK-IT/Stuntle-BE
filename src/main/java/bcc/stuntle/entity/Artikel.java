package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "artikel")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class Artikel {

    @Id
    private Long id;

    private String publicId;

    private String linkGambar;

    private String judulArtikel;

    private String peninjau;

    private String isiText;

    private Long fkFaskesId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate deletedAt;
}
