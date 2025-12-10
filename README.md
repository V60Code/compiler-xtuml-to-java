# Generic xtUML to Java Compiler

Build powerful simulations from simple JSON models.

This project is a **Generic xtUML (Executable UML) Compiler** that translates design models (JSON) into production-ready **Java 17** code. It features a smart **Auto-Simulation Generator** that creates extensive test scenarios (`RunSystem.java`) without you lifting a finger.

## ✨ Key Features

*   **Generic Architecture**: Not tied to any specific domain. Works for Rentals, Libraries, Schools, IoT systems, etc.
*   **Smart OAL Translator**: Automatically detects attributes and converts them to Java getters (e.g., `count > 5` \u2192 `candidate.getCount() > 5`).
*   **Context-Aware**: Distinguishes between `Select` filters (using `candidate`) and `If` conditions (using `this`).
*   **Auto-Simulation**: Automatically generates a test runner that:
    1.  Creates object instances with dummy data.
    2.  **Auto-Links** related objects based on Associations defined in JSON.
    3.  **Auto-Fires** events to test State Machine transitions.
*   **Runtime Library**: Includes a robust event dispatcher, timer service, and object broker.

## 📂 Project Structure

*   `xtuml.json` \u2192 **Input Model**. Define your classes, states, and associations here. (See [req-xtuml.md](req-xtuml.md) for spec).
*   `src/com/xtuml/compiler` \u2192 **The Compiler**. (Parses JSON, translates OAL, generates Java).
*   `src/com/xtuml/runtime` \u2192 **The Runtime**. (BaseModel, EventDispatcher, RelationshipManager, TimerService).
*   `src-gen/` \u2192 **Output Folder**. Contains the generated Java code and the auto-generated `RunSystem.java`.

## 🚀 How to Run

### Step 1: Compile & Generate
Run the **`run_compiler.bat`** script.
```cmd
.\run_compiler.bat
```
What it does:
1.  Compiles the compiler source code.
2.  Runs the compiler against `xtuml.json`.
3.  Generates `src-gen` files (Classes + `RunSystem.java`).
4.  **Compiles** the generated code into `bin/`.

### Step 2: Run Simulation
Run the **`run_simulation.bat`** script.
```cmd
.\run_simulation.bat
```
What it does:
1.  Executes the pre-compiled `RunSystem` class.
2.  Shows you the Smoke Test results (Object creation, Attribute setting, Relations, State transitions).

## 🛠 Dependencies

1.  **Java JDK 17+**
2.  **Jackson Parsers** (Already configured in `lib/` folder):
    *   `jackson-core-2.15.2.jar`
    *   `jackson-databind-2.15.2.jar`
    *   `jackson-annotations-2.15.2.jar`

## 📖 Model Specification
To learn how to write your own `xtuml.json` model, please read the **[req-xtuml.md](req-xtuml.md)** file.
