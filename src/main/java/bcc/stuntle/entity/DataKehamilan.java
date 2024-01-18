package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.List;


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

    private Long usiaKehamilan;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate tanggalPertamaHaid;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate prediksiTanggalLahir;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate deletedAt;

    @Column("fk_ortu_id")
    private Long fkOrtuId;

    @Transient
    private List<Long> fkPemeriksaanIds;

    @Transient
    private String namaAyah;

    @Transient
    private String namaIbu;
}
