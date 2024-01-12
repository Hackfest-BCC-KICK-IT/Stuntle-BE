package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("data_pemeriksaan_kehamilan")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class DataPemeriksaanKehamilan {

    @Id
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate tanggalPemeriksaan;

    private String tempatPemeriksaan;

    private String namaPemeriksa;

    private Integer usiaKandungan;

    private String tekananDarah;

    private Double beratBadanIbu;

    private String statusKehamilan;

    private String pesanTambahan;

    @Column("fk_ortu_id")
    private Long fkOrtuId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate deletedAt;

    @Column("fk_faskes_id")
    private Long fkFaskesId;

    @Column("fk_data_kehamilan")
    private Long fkDataKehamilan;
}
