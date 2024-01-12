package bcc.stuntle.app.orang_tua_faskes.repository;

import bcc.stuntle.entity.OrangtuaFaskes;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
interface IOrangtuaFaskesRepository extends R2dbcRepository<OrangtuaFaskes, Long> {}
