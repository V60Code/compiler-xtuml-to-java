# Dokumentasi Batch Files (`.bat`)

Dokumen ini menjelaskan kegunaan dan alur proses dari setiap file batch yang tersedia dalam proyek ini. File-file ini berfungsi untuk mempermudah proses setup, kompilasi, dan eksekusi sistem.

## 1. `setup_libs.bat`

### Tujuan
Mengunduh library eksternal yang dibutuhkan oleh compiler (Jackson JSON Processor) dan menyimpannya ke dalam folder `lib`.

### Proses
1.  **Cek Folder `lib`**: Memeriksa apakah folder `lib` sudah ada. Jika belum, folder akan dibuat.
2.  **Download Library**: Menggunakan PowerShell `Invoke-WebRequest` untuk mengunduh file `.jar` berikut dari Maven Central:
    *   `jackson-core-2.15.2.jar`
    *   `jackson-annotations-2.15.2.jar`
    *   `jackson-databind-2.15.2.jar`
3.  **Selesai**: Menampilkan pesan sukses bahwa library siap digunakan.

> **Penting**: Script ini HARUS dijalankan pertama kali sebelum melakukan kompilasi.

---

## 2. `run_compiler.bat`

### Tujuan
Melakukan proses build lengkap: mengompilasi kode sumber compiler, menjalankan compiler untuk men-generate kode dari `xtuml.json`, dan terakhir mengompilasi kode hasil generasi tersebut.

### Proses
1.  **Clean & Prepare**:
    *   Menghapus folder `out` (hasil build sebelumnya) jika ada.
    *   Membuat ulang folder `out`.
    *   Menghapus file sementara `sources.txt` jika ada.
2.  **List Source Files**: Mencatat semua file `.java` dari folder `src` ke dalam `sources.txt`.
3.  **Compile Compiler**:
    *   Mengecek keberadaan folder `lib`. Jika tidak ada, script akan berhenti dan meminta user menjalankan `setup_libs.bat`.
    *   Menjalankan `javac` untuk mengompilasi kode compiler dengan classpath library Jackson.
    *   Output kompilasi disimpan di folder `out`.
4.  **Run Compiler (Code Generation)**:
    *   Menjalankan `com.xtuml.compiler.Main`.
    *   Compiler membaca `xtuml.json` dan menghasilkan kode Java baru di folder `src-gen`.
5.  **Compile Generated Code**:
    *   Mengumpulkan daftar file dari `src/com/xtuml/runtime` (Runtime Library) dan `src-gen` (Generated Code).
    *   Mengompilasi file-file tersebut ke dalam folder `bin`.
6.  **Selesai**: Jika berhasil, sistem siap dijalankan menggunakan `run_simulation.bat`.

---

## 3. `run_simulation.bat`

### Tujuan
Menjalankan aplikasi hasil generasi (sistem yang telah dicompile) untuk simulasi.

### Proses
1.  **Cek Binary**: Memastikan folder `bin` (hasil dari `run_compiler.bat`) tersedia. Jika tidak, akan meminta user menjalankan `run_compiler.bat`.
2.  **Run System**:
    *   Menjalankan class `RunSystem` (entry point sistem yang digenerate).
    *   Menyertakan `bin` dan `lib/*` dalam classpath.
3.  **Interaksi**: User dapat berinteraksi dengan simulasi melalui console yang muncul.
