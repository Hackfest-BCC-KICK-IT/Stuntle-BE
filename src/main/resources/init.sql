-- Custom Type

DROP TABLE IF EXISTS "orang_tua";
CREATE TABLE IF NOT EXISTS "orang_tua"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    nama_ibu VARCHAR(255) NOT NULL DEFAULT '',
    nama_ayah VARCHAR(255) NOT NULL DEFAULT '',
    email VARCHAR(255) NOT NULL DEFAULT '',
    password VARCHAR(255) NOT NULL DEFAULT '',
    is_connect_faskes BOOLEAN NOT NULL DEFAULT false,

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS "data_kehamilan";
CREATE TABLE IF NOT EXISTS "data_kehamilan"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    nama_calon_bayi VARCHAR(255) NOT NULL DEFAULT '',
    tanggal_pertama_haid TIMESTAMP,
    fk_ortu_id BIGINT,

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS "data_anak";
CREATE TABLE IF NOT EXISTS "data_anak"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    nama_anak VARCHAR(255) NOT NULL DEFAULT '',
    tanggal_lahir_anak DATE,
    jenis_kelamin VARCHAR(10) NOT NULL DEFAULT '',
    kondisi_lahir VARCHAR(10) NOT NULL DEFAULT '',
    berat_badan_lahir FLOAT NOT NULL DEFAULT 0,
    panjang_badan_lahir FLOAT NOT NULL DEFAULT 0,
    lingkar_kepala FLOAT NOT NULL DEFAULT 0,
    fk_ortu_id BIGINT,

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS "orang_tua_faskes";
CREATE TABLE IF NOT EXISTS "orang_tua_faskes"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    fk_ortu_id BIGINT NOT NULL,
    fk_faskes_id BIGINT NOT NULL,

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS "ajukan_bantuan";
CREATE TABLE IF NOT EXISTS "ajukan_bantuan"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    judul VARCHAR(255) NOT NULL DEFAULT '',
    deskripsi VARCHAR(255) NOT NULL DEFAULT '',
    status VARCHAR(10) NOT NULL DEFAULT '',
    pesan_tambahan VARCHAR(255) NOT NULL DEFAULT '',
    fk_ortu_id BIGINT NOT NULL,
    fk_faskes_id BIGINT NOT NULL,

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS "data_pemeriksaan_kehamilan";
CREATE TABLE IF NOT EXISTS "data_pemeriksaan_kehamilan"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    tanggal_pemeriksaan DATE,
    tempat_pemeriksaan VARCHAR(255) NOT NULL DEFAULT '',
    nama_pemeriksa VARCHAR(255) NOT NULL DEFAULT '',
    usia_kandungan INT NOT NULL DEFAULT 0,
    tekanan_darah VARCHAR(255) NOT NULL DEFAULT '',
    berat_badan_ibu FLOAT NOT NULL DEFAULT 0,
    status_kehamilan VARCHAR(10) NOT NULL DEFAULT '',
    pesan_tambahan VARCHAR(255) NOT NULL DEFAULT '',
    fk_ortu_id BIGINT NOT NULL,
    fk_faskes_id BIGINT NOT NULL,

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS "grup_whatsapp";
CREATE TABLE IF NOT EXISTS "grup_whatsapp"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    nama_grup_whatsapp VARCHAR(255) NOT NULL DEFAULT '',
    link_grup_whatsapp VARCHAR(255) NOT NULL DEFAULT '',
    fk_faskes_id BIGINT NOT NULL,

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS "resep_makanan";
CREATE TABLE IF NOT EXISTS "resep_makanan"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    public_id VARCHAR(255) NOT NULL DEFAULT '',
    url_gambar VARCHAR(255) NOT NULL DEFAULT '',
    judul_resep VARCHAR(255) NOT NULL DEFAULT '',
    target_resep VARCHAR(255) NOT NULL DEFAULT '',
    target_usia_resep VARCHAR(255) NOT NULL DEFAULT '',
    jenis VARCHAR(255) NOT NULL DEFAULT '',
    bahan_utama VARCHAR(255) NOT NULL DEFAULT '',
    durasi_memasak VARCHAR(255) NOT NULL DEFAULT '',
    bahan_text TEXT,
    cara_membuat_text TEXT,
    nilai_gizi_text TEXT,
    fk_faskes_id BIGINT NOT NULL,

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS "fasilitas_kesehatan";
CREATE TABLE IF NOT EXISTS "fasilitas_kesehatan"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL DEFAULT '',
    password VARCHAR(255) NOT NULL DEFAULT '',
    username VARCHAR(255) NOT NULL DEFAULT '',
    kode_unik VARCHAR(255) NOT NULL DEFAULT '',
    nomor_telepon VARCHAR(255) NOT NULL DEFAULT '',
    alamat_faskes VARCHAR(255) NOT NULL DEFAULT '',

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

ALTER TABLE data_pemeriksaan_kehamilan
ADD COLUMN fk_data_kehamilan INT;

DROP TABLE IF EXISTS "data_pemeriksaan_anak";
CREATE TABLE IF NOT EXISTS "data_pemeriksaan_anak"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    tanggal_pemeriksaan TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tempat_pemeriksaan VARCHAR(255) NOT NULL DEFAULT '',
    nama_pemeriksa VARCHAR(255) NOT NULL DEFAULT '',
    umur_anak INT NOT NULL DEFAULT 0.0,
    tinggi_anak FLOAT NOT NULL DEFAULT 0.0,
    berat_badan_anak FLOAT NOT NULL DEFAULT 0.0,
    status_anak VARCHAR(255) NOT NULL DEFAULT '',
    pesan_tambahan VARCHAR(500) NOT NULL DEFAULT '',
    fk_ortu_id INT,
    fk_faskes_id INT,
    fk_data_anak INT,

    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

alter table resep_makanan
add column public_id VARCHAR(255) not null default '';

DROP TABLE IF EXISTS "artikel";
CREATE TABLE IF NOT EXISTS "artikel"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    public_id VARCHAR(255) NOT NULL DEFAULT '',
    link_gambar VARCHAR(255) NOT NULL DEFAULT '',
    judul_artikel VARCHAR(255) NOT NULL DEFAULT '',
    peninjau VARCHAR(255) NOT NULL DEFAULT '',
    isi_text TEXT NOT NULL DEFAULT '',
    fk_faskes_id INT,
    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

DROP TABLE IF EXISTS "chat_message";
CREATE TABLE IF NOT EXISTS "chat_message"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    message TEXT NOT NULL DEFAULT '',
    fk_ortu_id INT,
    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS "chat_response";
CREATE TABLE IF NOT EXISTS "chat_response"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    response TEXT NOT NULL DEFAULT '',
    fk_ortu_id INT,
    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS "chat_response_usage";
CREATE TABLE IF NOT EXISTS "chat_response_usage"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    total_tokens BIGINT NOT NULL DEFAULT 0,
    fk_ortu_id INT,
    fk_chat_response INT,
    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS "ajukan_bantuan";
CREATE TABLE IF NOT EXISTS "ajukan_bantuan"(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    judul_ajuan VARCHAR(255) NOT NULL DEFAULT '',
    deskripsi TEXT NOT NULL DEFAULT '',
    status VARCHAR(50) NOT NULL DEFAULT '',
    pesan_tambahan TEXT NOT NULL DEFAULT '',
    fk_ortu_id INT,
    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
)

DROP TABLE IF EXISTS "resep_makanan_artikel_tersimpan";
CREATE TABLE IF NOT EXISTS "resep_makanan_artikel_tersimpan"(
    fk_artikel_id INT,
    fk_ortu_id INT,
    fk_resep_makanan_id INT,
    jenis VARCHAR(50),
    -- Helper
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    PRIMARY KEY(fk_artikel_id, fk_ortu_id, fk_resep_makanan_id, jenis)
);

ALTER TABLE "orang_tua"
ADD COLUMN "image_url" VARCHAR(255);