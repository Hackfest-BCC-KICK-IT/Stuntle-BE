package bcc.stuntle.constant;

public class OpenApiConstant {
    public static final String MODEL = "gpt-3.5-turbo";
    public static final String URL = "https://api.openai.com/v1/chat/completions";
    public static final String ROLE = "user";

    public static final String NOTES = """   
    \n
    Pastikan pertanyaan yang diberikan dalam berkaitan dengan 
    `Stunting`, `Nutrisi`, `MPASI`, `ASI`, `Gizi Anak`, `Tinggi Anak`, dan `Berat Anak`
    
    Utamakan pernyataan di bawah ini:
    Jika pertanyaan yang diberikan diluar konteks, kembalikan response 
    `Pertanyaan di luar konteks.` Jangan berikan response tambahan apapun!
    """;
}
