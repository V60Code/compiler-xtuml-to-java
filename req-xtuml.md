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
  "states": [ ... ],
  "operations": [ ... ]       // (Optional) Business Logic Methods
}
```

#### Attributes
Mendefinisikan data member dari class.

```json
{
  "attribute_name": "nama_atribut",       // snake_case
  "attribute_type": "naming_attribute",   // Tipe Atribut:
                                          // - "naming_attribute" (Primary Key)
                                          // - "descriptive_attribute" (Data biasa)
                                          // - "referential_attribute" (Foreign Key)
  "data_type": "string",                  // Pilihan: "id", "string", "integer", "real" (decimal), "datetime", "state"
  "default_value": "default",             // (Opsional) Nilai awal
                                          // Jika type "state", gunakan format: "states.nama_state_kecil"
  "attribute_id": "UnikID"
}
```

#### States (State Machine)
Compiler ini menggunakan model **Global Transitions** dimana event terikat pada **Target State**.

```json
{
  "state_name": "NamaState",        // UPPER_CASE disarankan
  "state_type": "string",           // Biasanya "string"
  "state_value": "state_value",     // lowercase value
  "state_event": [ "evt_trigger" ], // Daftar event yang memicu transisi KE state ini
                                    // (Dari state mana saja)
  "action": "OAL Code...",          // Logika yang dijalankan saat masuk state ini
  "state_id": "UnikID"
}
```

#### Operations (Methods)
Mendefinisikan fungsi/method tambahan dalam class (selain state action).

```json
{
  "name": "calculate_tax",
  "return_type": "decimal",         // void, string, integer, decimal
  "action": "tax = 100 * 0.1;"      // OAL Code body
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
      "class_multiplicity": "1"     // "0..1", "1..1", "0..*", "1..*"
    },
    {
      "class_name": "ClassB",
      "class_multiplicity": "0..*"
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
| **Timer** | `create timer t event evt to target delay ms` | `create timer t event setExpired to self delay 1000` |
| **Date** | `create date var` | `create date now` |
| **Debug** | `Bridge::log("msg")` | `Bridge::log("Processing...")` |

## 4. Generic Features
Compiler ini bersifat **Generic**. Artinya:
*   Tidak ada hardcoding nama atribut di compiler.
*   Anda bebas membuat atribut apa saja (misal: `saldo`, `umur`, `status`).
*   ActionTranslator otomatis mengubah `saldo` menjadi `getSaldo()`.
*   Compiler membedakan konteks:
    *   `select ... where x == y` \u2192 `candidate.getX()`
    *   `if (x == y)` \u2192 `this.getX()`

## 5. Constraint
*   **Unique IDs**: Setiap ID (`class_id`, `state_id`) harus unik.
*   **Valid Java Identification**: Nama class dan atribut harus valid sebagai identifier Java (jangan pakai spasi atau simbol aneh).
