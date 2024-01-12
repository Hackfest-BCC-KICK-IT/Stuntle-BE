package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("resep_makanan_artikel_tersimpan")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class ResepMakananArtikelTersimpan {

    @Id
    @Column("fk_artikel_id")
    private Long fkArtikelId;

    @Column("fk_ortu_id")
    private Long fkOrtuId;

    @Column("fk_resep_makanan_id")
    private Long fkResepMakananId;

    @Column("jenis")
    private String jenis;

    @Column("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createdAt;

    @Column("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate updatedAt;

    @Column("deleted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate deletedAt;
}
