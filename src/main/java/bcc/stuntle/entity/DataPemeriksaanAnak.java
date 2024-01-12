package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("data_pemeriksaan_anak")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class DataPemeriksaanAnak {

    @Id
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate tanggalPemeriksaan;

    private String tempatPemeriksaan;

    private String namaPemeriksa;

    private Integer umurAnak;

    private Double tinggiAnak;

    private Double beratBadanAnak;

    private String statusAnak;

    private String pesanTambahan;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate deletedAt;

    @Column("fk_faskes_id")
    private Long fkFaskesId;

    @Column("fk_ortu_id")
    private Long fkOrtuId;

    @Column("fk_data_anak")
    private Long fkDataAnak;
}
