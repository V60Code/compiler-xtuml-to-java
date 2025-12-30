# Panduan Demo Lengkap: Rental PS System (End-to-End)

Dokumen ini memandu Anda dari kondisi **Database Kosong** hingga transaksi sewa selesai.

**Mulai Aplikasi:** `.\run_interactive.bat`

---

## Tahap 1: Setup Data Awal (Wajib Dilakukan)

Karena sistem dimulai dengan data kosong, kita harus "membeli" console dan menentukan "harga" sewa dulu.

1.  **Masuk Login Admin**

    - Menu Utama: Ketik **`4`** (Admin Mode).

2.  **Membuat Data Harga (Rate)**

    - Menu Admin: Pilih **`3`** (Rate).
    - Pilih **`1`** (Create New Instance).
    - Input Price: Ketik **`5000`** (misal Rp 5000/jam).
    - _Output_: "Success! Rate instance created."
    - Kembali: Pilih **`3`** (Back).

3.  **Mendaftarkan Console Baru**
    - Menu Admin: Pilih **`2`** (PSConsole).
    - Pilih **`1`** (Create New Instance).
    - Input Model: Ketik **`PS5-Pro`**.
    - Input Availability: Ketik **`available`**.
    - _Output_: "Success! PSConsole 'PS5-Pro' has been registered."
    - _(Opsional: Ulangi langkah ini untuk menambah 'PS4-Slim')_
    - Kembali: Pilih **`3`** (Back) -> **`5`** (Back) hingga ke Menu Utama.

---

## Tahap 2: Transaksi Sewa (Skenario User)

1.  **Validasi Stok**

    - Menu Utama: Pilih **`3`** (Cek Stok).
    - _Hasil_: Harusnya muncul `PS5-Pro: available`.

2.  **Pelanggan Datang & Sewa**

    - Menu Utama: Pilih **`1`** (Sewa Baru).
    - Nama Customer: Ketik **`Andi`**.
    - Pilih Console: Ketik **`1`** (PS5-Pro).
    - Durasi: Ketik **`2`** (2 jam).
    - _Hasil_: "Success! Andi is renting PS5-Pro...".

3.  **Cek Stok Lagi**
    - Menu Utama: Pilih **`3`** (Cek Stok).
    - _Hasil_: `PS5-Pro: rented` (Stok berkurang/status berubah).

---

## Tahap 3: Simulasi & Pengembalian

1.  **Simulasi Waktu Berjalan (Admin Override)**

    - _Catatan: Kita tidak menunggu 2 jam beneran, kita "paksa" event terjadi._
    - Menu Utama: **`4`** (Admin) -> **`4`** (Rental).
    - Pilih **`2`** (Select Existing).
    - Pilih Rental ID (paling bawah).
    - Pilih Event **`3`** (`end_time_passed`) -> "Event fired".
    - Kembali ke Menu Utama.

2.  **Pengembalian Unit**

    - Menu Utama: Pilih **`2`** (Pengembalian).
    - Pilih Rental Andi (No 1).
    - _Hasil_: "Unit return processed".

3.  **Final Check**
    - Menu Utama: Pilih **`3`** (Cek Stok).
    - _Hasil_: `PS5-Pro: available` (Siap disewa lagi).
