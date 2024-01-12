package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;


@Table("data_kehamilan")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class DataKehamilan {

    @Id
    private Long id;

    private String namaCalonBayi;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate tanggalPertamaHaid;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate deletedAt;

    @Column("fk_ortu_id")
    private Long fkOrtuId;
}
