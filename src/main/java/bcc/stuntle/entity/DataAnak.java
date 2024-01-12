package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("data_anak")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class DataAnak {

    @Id
    private Long id;

    private String namaAnak;

    private String jenisKelamin;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column("tanggal_lahir_anak")
    private LocalDate tanggalLahir;

    private String kondisiLahir;

    private Double beratBadanLahir;

    private Double panjangBadanLahir;

    private Double lingkarKepala;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate deletedAt;

    @Column("fk_ortu_id")
    private Long fkOrtuId;
}
