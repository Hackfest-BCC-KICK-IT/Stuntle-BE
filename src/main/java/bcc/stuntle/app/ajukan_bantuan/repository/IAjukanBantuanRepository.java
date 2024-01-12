package bcc.stuntle.app.ajukan_bantuan.repository;

import bcc.stuntle.entity.AjukanBantuan;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAjukanBantuanRepository extends R2dbcRepository<AjukanBantuan, Long> {}
