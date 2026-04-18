# Infection & Non-Infection Expert System (Clean Architecture)

Aplikasi Sistem Pakar Diagnosa Medis berbasis Desktop (JavaFX) untuk mengklasifikasikan penyakit infeksi (Influenza, DBD) dan non-infeksi (Diabetes, Hipertensi). Dibangun menggunakan ekosistem **Java 21+**, **AtlantaFX (Modern UI)**, dan **Strategy Design Pattern** untuk membandingkan algoritma Rule-Based vs Hierarchical Weighting.

## Cara Menjalankan Aplikasi (How to Run)
Anda tidak perlu meng-install Gradle secara manual di komputer Anda. Repositori ini sudah menyertakan **Gradle Wrapper**.

**Prasyarat:**
- Java Development Kit (JDK) minimal versi 21 (Disarankan OpenJDK 21).

**Langkah-langkah:**
1. Clone repositori ini:
   ```bash
   git clone [https://github.com/username-lu/nama-repo-lu.git](https://github.com/username-lu/infectiones.git)
   cd infectiones
   ```
2. Run dengan gradle
   ```bash
   # Linux/Macos
   ./gradlew run
   # Windows
   gradlew.bat run
   ```
   Aplikasi akan secara otomatis mengunduh dependencies (GSON, AtlantaFX, dll) pada saat pertama kali dijalankan, lalu membuka antarmuka.

### Struktur Knowledge Base
Semua aturan dan bobot penyakit tidak di-hardcode di dalam kode, melainkan dapat dikonfigurasi melalui berkas JSON di:
`src/main/resources/knowledge_base.json`
