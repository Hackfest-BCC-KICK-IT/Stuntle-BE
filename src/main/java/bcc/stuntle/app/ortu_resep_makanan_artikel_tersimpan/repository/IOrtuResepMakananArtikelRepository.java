package bcc.stuntle.app.ortu_resep_makanan_artikel_tersimpan.repository;

import bcc.stuntle.entity.ResepMakananArtikelTersimpan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface IOrtuResepMakananArtikelRepository {

    String CREATE = """
            INSERT INTO resep_makanan_artikel_tersimpan(fk_artikel_id, fk_ortu_id, fk_resep_makanan_id, jenis)
            VALUES
            ($1, $2, $3, $4)
            """;

    Mono<Long> create(ResepMakananArtikelTersimpan resepMakananArtikelTersimpan);
    Mono<ResepMakananArtikelTersimpan> get(Long ortuId, Long resepMakananId, Long artikelId, String jenis);
    Mono<Long> delete(Long ortuId, Long resepMakananId, Long artikelId, String jenis);
    Mono<Long> activate(Long ortuId, Long resepMakananId, Long artikelId, String jenis);
    Mono<Page<ResepMakananArtikelTersimpan>> getList(Long ortuId, Pageable pageable);
}
