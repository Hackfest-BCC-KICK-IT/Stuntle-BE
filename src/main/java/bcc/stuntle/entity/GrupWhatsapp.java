package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("grup_whatsapp")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class GrupWhatsapp {

    @Id
    private Long id;

    @Column("nama_grup_whatsapp")
    private String namaGrup;

    @Column("link_grup_whatsapp")
    private String linkGrup;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate deletedAt;

    @Column("fk_faskes_id")
    private Long fkFaskesId;
}
