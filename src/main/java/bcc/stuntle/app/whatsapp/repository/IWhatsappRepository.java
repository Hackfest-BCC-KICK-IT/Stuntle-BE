package bcc.stuntle.app.whatsapp.repository;

import bcc.stuntle.entity.GrupWhatsapp;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWhatsappRepository extends R2dbcRepository<GrupWhatsapp, Long> { }
