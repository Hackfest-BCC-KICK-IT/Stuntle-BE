package bcc.stuntle.constant;

public class OpenApiConstant {
    public static final String MODEL = "gpt-3.5-turbo";
    public static final String URL = "https://api.openai.com/v1/chat/completions";
    public static final String ROLE = "user";

    public static final String NOTES = """ 
    
    Keyword untuk pertanyaan kehamilan:
    1.Muntah
    2.Demam
    3.Pendarahan
    4.Ketuban Pecah
    5.Mual Berat/Mual Parah
    6.Nyeri/Kram Perut Berlebih
    7.Kontraksi Berlebih
    8.Jatuh/Terbentur
    9.Janin pasif
    10.Lemas/Lesu
        
    \n
    Keyword untuk pertanyaan Tumbuh Kembang Anak:
    1.Terlambat bicara/telat bicara/susah berbicara/belum bisa bicara
    2.Terlambat berjalan/telat jalan/susah berjalan/belum bisa berjalan
    3.Tantrum
    4.Autisme
    5.Lumpuh
    6.Down syndrome
    7.Cerebral Palsy
    8.Tinggi dibawah normal/Tinggi dibawah rata-rata 
    \n
        
    Keyword umum:
    1.Stunting
    2.Nutrisi
    3.MPASI
    4.ASI
    5.Gizi Anak
    6.Tinggi Anak
    7.Berat Anak
    
    SELALU Berikan jawaban mu dalam bentuk JSON dengan format UNTUK SETIAP PERTANYAAN
    
    {
        "message": String,
        "isKeywordExist": boolean,
        "keywordType": String
    }
    
    dengan message merupakan response Anda(dengan syarat pertanyaan nya harus mengandung poin yang diberikan di atas) dan 
    isKeywordExist menunjukkan apakah pertanyaan yang diberikan memuat keyword yang aku berikan di atas.
    keywordType memberitahu apakah keywordType ini bernilai "keywordBayi" atau "keywordAnak"(hanya bisa 2 value itu) berdasarkan keyword yang aku berikan di atas
    """;
}
