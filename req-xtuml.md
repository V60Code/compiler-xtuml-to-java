# xtUML JSON Model Specification (`req-xtuml.md`)

Panduan ini menjelaskan struktur standar `xtuml.json` yang kompatibel dengan Compiler xtUML custom ini.

## 1. Root Object
Root JSON harus memiliki struktur berikut:

```json
{
  "sub_name": "NamaSubsistem",
  "sub_id": "UnikID",
  "type": "subsystem",
  "model": [ ... ]  // Array berisi Class dan Association
}
```

## 2. Model Elements
Array `model` berisi campuran antara **Class** dan **Association**.

### A. Class Definition
Setiap class merepresentasikan objek dalam sistem.

```json
{
  "type": "class",
  "class_name": "NamaClass",  // PaschalCase (e.g., Customer)
  "class_id": "UnikID",
  "KL": "KEY",                // Key Letters (Singkatan)
  "attributes": [ ... ],
  "states": [ ... ]
}
```

#### Attributes
Mendefinisikan data member dari class.

```json
{
  "attribute_name": "nama_atribut", // snake_case (e.g., phone_number)
  "data_type": "String",            // Tipe: String, Integer, Real, Boolean
  "default_value": "default",       // (Opsional)
  "attribute_id": "UnikID"
}
```

#### States (State Machine)
Mendefinisikan perilaku dinamis class.

```json
{
  "state_name": "NamaState",        // UPPER_CASE (e.g., WAITING_PAYMENT)
  "state_type": "active",           // "active" atau "final"
  "state_event": [ "event_name" ],  // Daftar event yang memicu transisi ke / dari sini
  "action": "OAL Code...",          // Logika kode saat masuk state ini
  "state_id": "UnikID"
}
```

### B. Association Definition
Mendefinisikan hubungan antar class.

```json
{
  "type": "association",
  "name": "R1",                     // Nama Relasi (R1, R2, dst)
  "class": [
    {
      "class_name": "ClassA",
      "class_multiplicity": "1"     // "1" atau "*"
    },
    {
      "class_name": "ClassB",
      "class_multiplicity": "*"
    }
  ]
}
```

## 3. Object Action Language (OAL) Syntax
Compiler ini mendukung subset OAL berikut di dalam property `action`:

| Fitur | Syntax | Contoh |
| :--- | :--- | :--- |
| **Assignment** | `var = value` | `count = 10` |
| **Selection** | `select any param from instances of Class where cond` | `select any c from instances of Customer where name == "Bob"` |
| **Relate** | `relate src to target across Ric` | `relate this to rental across R1` |
| **Unrelate** | `unrelate src from target across Ric` | `unrelate this from rental across R1` |
| **Navigate** | `select one var related by src->Target[Ric]` | `select one r related by this->Rental[R1]` |
| **Delete** | `delete var` | `delete r` |
| **If Condition** | `if (cond)` ... `end if` | `if (count > 5) ... end if` |
| **Property Access** | `var.attribute` | `c.name` |
| **Timer** | `create timer t event evt to target delay ms` | `create timer t event timeout to self delay 1000` |
| **Debug** | `Bridge::log("msg")` | `Bridge::log("Processing...")` |

## 4. Generic Features
Compiler ini bersifat **Generic**. Artinya:
*   Tidak ada hardcoding nama atribut di compiler.
*   Anda bebas membuat atribut apa saja (misal: `saldo`, `umur`, `status`).
*   ActionTranslator otomatis mengubah `saldo` menjadi `getSaldo()`.

## 5. Constraint
*   **Unique IDs**: Setiap ID (`class_id`, `state_id`) harus unik.
*   **Valid Java Identification**: Nama class dan atribut harus valid sebagai identifier Java (jangan pakai spasi atau simbol aneh).
