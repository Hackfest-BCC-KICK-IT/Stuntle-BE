package bcc.stuntle.app.faskes.repository;

import bcc.stuntle.entity.FasilitasKesehatan;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
interface IFasilitasKesehatanRepository extends R2dbcRepository<FasilitasKesehatan, Long>{

    String createSql = """
            INSERT INTO fasilitas_kesehatan(email, password, username, kode_unik, nomor_telepon, alamat_faskes, created_at, updated_at) 
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
            """;

    @Query("SELECT * FROM \"fasilitas_kesehatan\" WHERE email = $1 LIMIT 1")
    Mono<FasilitasKesehatan> findByEmail(String email);
}
