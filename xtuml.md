# Dokumentasi `xtuml.json`

Dokumen ini menjelaskan struktur, kegunaan, dan isi dari file `xtuml.json` yang digunakan sebagai input utama untuk compiler xtUML ke Java.

## 1. Kegunaan (Purpose)
File `xtuml.json` berfungsi sebagai **Source Model** yang berisi definisi lengkap sistem yang akan dibangun. Compiler akan membaca file ini untuk men-generate kode Java yang mencakup:
*   **Entity Classes**: Struktur data (Pojo).
*   **Relationships**: Logika relasi antar objek (One-to-Many, dll).
*   **State Machines**: Logika siklus hidup objek (Status & Transisi).
*   **Business Logic**: Kode operasional yang ditulis dalam OAL (Object Action Language).

## 2. Struktur File (Structure)
Format file ini adalah JSON standar dengan hierarki sebagai berikut:

```json
{
  "type": "subsystem",          // Tipe root element
  "sub_name": "NamaSubsystem",  // Nama sistem
  "model": [                    // Array berisi semua elemen model
    { "type": "class", ... },
    { "type": "association", ... }
  ]
}
```

### Komponen Utama:
1.  **Class**: Mendefinisikan objek/tabel.
    *   `attributes`: Daftar kolom data (ID, String, State, dll).
    *   `states`: (Opsional) Definisi state machine (status, event, action).
    *   `operations`: (Opsional) Method tambahan.
2.  **Association**: Mendefinisikan hubungan antar Class.
    *   Berisi array 2 Class yang terhubung beserta kardinalitasnya (`1..1`, `1..*`).

---

## 3. Isi Model (`xtuml.json`)
File `xtuml.json` saat ini berisi model untuk **Sistem Rental PlayStation (`rental_ps`)**.

### A. Subsystem
*   **Name**: `rental_ps`
*   **ID**: `s1`

### B. Classes
Berikut adalah kelas-kelas yang didefinisikan dalam model:

#### 1. Customer
Merepresentasikan pelanggan yang menyewa.
*   **Atribut**:
    *   `customer_id` (ID)
    *   `name` (String)
    *   `contact` (String)
    *   `email` (String)

#### 2. PSConsole
Merepresentasikan unit konsol PlayStation.
*   **Atribut**:
    *   `console_id` (ID)
    *   `model` (String)
    *   `availability` (State: `available`, `rented`, `maintenance`)
    *   `rate_id` (ID - Referensi ke Rate)
*   **State Machine Lifecycle**:
    *   **Available**: Status siap sewa.
    *   **Rented**: Status sedang disewa (memiliki timer otomatis).
    *   **Maintenance**: Status perbaikan.

#### 3. Rate
Konfigurasi harga sewa.
*   **Atribut**:
    *   `rate_id` (ID)
    *   `price_per_hour` (Decimal)
*   **Operations**:
    *   `calculate_tax()`: Menghitung pajak dari harga dasar.

#### 4. Rental
Transaksi penyewaan utama.
*   **Atribut**:
    *   `rental_id` (ID)
    *   `customer_id` (Ref Customer)
    *   `console_id` (Ref Console)
    *   `start_time`, `end_time` (Datetime)
    *   `status` (State: `Pending`, `Rented`, `Expired`, `Completed`, `Canceled`)
*   **State Machine Lifecycle**:
    *   Mengatur alur tranksaksi mulai dari *Pending* -> *Rented* (Unit aktif) -> *Completed* (Selesai) atau *Expired*.

### C. Associations (Relasi)
Hubungan antar kelas yang didefinisikan:

| Nama Relasi | Class A (Source) | Class B (Target) | Deskripsi |
| :--- | :--- | :--- | :--- |
| **R1** | `Customer` (1..*) | `Rental` (0..*) | Satu Customer bisa memiliki banyak Rental History. |
| **R2** | `Rental` (1..1) | `PSConsole` (1..1) | Satu sesi Rental terkait dengan satu unit Console. |
| **R3** | `PSConsole` (1..1) | `Rate` (1..*) | Console mengacu pada Rate harga tertentu. |

---

## 4. Tipe Data (Data Types)
Tipe data xtUML yang digunakan dalam file ini dipetakan ke Java sebagai berikut:

| xtUML Type | Java Type | Keterangan |
| :--- | :--- | :--- |
| `id` | `int` / `UUID` | Primary Key atau Foreign Key. |
| `string` | `String` | Teks. |
| `integer` | `int` | Bilangan bulat. |
| `decimal/real`| `double` | Bilangan desimal. |
| `datetime` | `LocalDateTime`| Tanggal dan waktu. |
| `state` | `String` | Disimpan sebagai string nama state di database/objek. |
