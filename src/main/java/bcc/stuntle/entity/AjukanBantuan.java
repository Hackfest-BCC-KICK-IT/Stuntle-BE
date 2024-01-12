package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("ajukan_bantuan")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class AjukanBantuan {

    @Id
    private Long id;

    @Column("judul_ajuan")
    private String judul;

    @Column("deskripsi")
    private String deskripsi;

    @Column("status")
    private String status;

    @Column("pesan_tambahan")
    private String pesanTambahan;

    @Column("fk_ortu_id")
    private Long fkOrtuId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate deletedAt;
}
