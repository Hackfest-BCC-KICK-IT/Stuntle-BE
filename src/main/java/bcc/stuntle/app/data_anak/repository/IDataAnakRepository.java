package bcc.stuntle.app.data_anak.repository;

import bcc.stuntle.entity.DataAnak;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
interface IDataAnakRepository extends R2dbcRepository<DataAnak, Long> {
    String sqlCreate = """
       INSERT INTO data_anak (nama_anak, jenis_kelamin, tanggal_lahir_anak, kondisi_lahir,
       berat_badan_lahir, panjang_badan_lahir, lingkar_kepala, created_at, updated_at, fk_ortu_id) 
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)      
    """;
}
