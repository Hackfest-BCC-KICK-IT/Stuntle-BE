package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("resep_makanan")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class ResepMakanan {

    @Id
    private Long id;

    @Column("public_id")
    private String publicId;

    @Column("url_gambar")
    private String urlGambar;

    @Column("judul_resep")
    private String judulResep;

    @Column("target_resep")
    private String targetResep;

    @Column("target_usia_resep")
    private String targetUsiaResep;

    @Column("jenis")
    private String jenis;

    @Column("bahan_utama")
    private String bahanUtama;

    @Column("durasi_memasak")
    private String durasiMemasak;

    @Column("bahan_text")
    private String bahanText;

    @Column("cara_membuat_text")
    private String caraMembuatText;

    @Column("nilai_gizi_text")
    private String nilaiGiziText;

    @Column("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createdAt;

    @Column("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate updatedAt;

    @Column("deleted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate deletedAt;

    @Column("fk_faskes_id")
    private Long fkFaskesId;
}
