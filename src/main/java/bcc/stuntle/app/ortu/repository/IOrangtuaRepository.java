package bcc.stuntle.app.ortu.repository;

import bcc.stuntle.entity.Orangtua;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
interface IOrangtuaRepository extends R2dbcRepository<Orangtua, Long> {

    String createSql = """
    INSERT INTO orang_tua(nama_ibu, nama_ayah, email, password, is_connect_faskes, created_at)
    VALUES
    ($1, $2, $3, $4, $5, $6)        
    """;

    @Query("SELECT * FROM \"orang_tua\" WHERE email = $1")
    Mono<Orangtua> findByEmail(String email);
}
