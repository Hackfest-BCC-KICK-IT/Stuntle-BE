package bcc.stuntle.app.pemeriksaan_kehamilan.repository;

import bcc.stuntle.entity.DataPemeriksaanKehamilan;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPemeriksaanKehamilanRepository extends R2dbcRepository<DataPemeriksaanKehamilan, Long> {}
