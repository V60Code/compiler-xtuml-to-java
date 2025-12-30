# Analisis Kriteria Compiler xtUML - Status Checklist

Berikut adalah tabel status implementasi untuk 8 kriteria utama project.

|   No   | Kriteria              | Status | Lokasi File & Kode                                             | Keterangan / Analisis                                                                               |
| :----: | :-------------------- | :----: | :------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------- |
| **1**  | **Handle Class**      |   ✅   | `JavaSourceGenerator.java`<br>Method: `generateClass` (L32-58) | Translasi JSON Class ke File `.java` dengan `extends BaseModel`.                                    |
| **1b** | **Type Data**         |   ✅   | `JavaSourceGenerator.java`<br>Method: `mapType` (L252)         | Mapping: `integer`\u2192`int`, `string`\u2192`String`, `id`\u2192`UUID`.                            |
| **2**  | **Relasi (Simple)**   |   ✅   | `RelationshipManager.java`<br>`ActionTranslator.java` (L16)    | Support `relate` & `unrelate` untuk relasi 1-1 dan 1-N.                                             |
| **3**  | **Asosiasi (Review)** |   ✅   | `RelationshipManager.java`<br>Param ke-4 `linkObj`             | Support `relate ... using ...` untuk asosiasi M-N dengan Link Object.                               |
| **4**  | **Generalisasi**      |   ✅   | `JavaSourceGenerator.java`<br>Lines 51-54                      | Support `extends [Supertype]` jika di JSON didefinisikan field `supertype`.                         |
| **5**  | **State Translation** |   ✅   | `JavaSourceGenerator.java`<br>Enum & Switch-Case Logic         | State menjadi `enum`, Transisi menjadi `switch(EventID)`. Logic action di-inject ke method private. |
| **6**  | **Handle Event**      |   ✅   | `EventDispatcher.java`<br>Method `enqueue` & `processQueue`    | Menggunakan Queue global. Event diproses secara sequential (FIFO).                                  |
| **7**  | **Event Parameter**   |   ✅   | `XtUmlEvent.java`<br>Field `Object data`                       | Payload data bisa diakses dengan `result.getData("key")`.                                           |
| **8**  | **Mapping OAL Code**  |   ✅   | `ActionTranslator.java`                                        | Regex Parser menerjemahkan 20+ pattern OAL ke Java (Assign, If, Select, Loop).                      |

## Detail Mapping Tipe Data (Kriteria 8)

| Tipe OAL (xtUML)     | Tipe Java (Target)        | Notes                                  |
| :------------------- | :------------------------ | :------------------------------------- |
| `integer`            | `int`                     | Primitif (Efisien)                     |
| `real` / `decimal`   | `double`                  | Presisi Ganda                          |
| `string`             | `String`                  | Object String                          |
| `boolean`            | `boolean`                 | Primitif                               |
| `id` / `unique_id`   | `java.util.UUID`          | Standar Unique ID 128-bit              |
| `date` / `timestamp` | `java.time.LocalDateTime` | Modern Java Date API                   |
| `state`              | `String` / `Enum`         | Enum untuk Logic, String untuk Storage |
