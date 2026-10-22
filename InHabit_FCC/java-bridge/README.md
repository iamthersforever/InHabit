# InHabit — FCC Jugaad Frontend Port

This is the **backup / safety-net version** of the finished InHabit Java + MySQL project.

## What changed?

Almost nothing in the original application logic was rewritten.

- Existing Java model classes are retained.
- Existing `ComplaintDAO`, `UserDAO`, `DBConnection`, validation and escalation logic are retained.
- Existing `InHabitDBNew.sql` schema is retained.
- Existing Java Swing screens are retained in the Java source tree.
- **New:** `inhabit.WebBridge` exposes a small HTTP API around the existing DAO layer.
- **New:** `frontend/` is a React/Vite web UI replacing Swing as the presentation layer.

Architecture:

```text
React / HTML / CSS / JavaScript
              ↓
      WebBridge.java (tiny adapter)
              ↓
      Existing Java DAO classes
              ↓
       Existing MySQL schema
              ↓
        InHabitDBNew
```

If MySQL is unavailable, the existing Java fallback mode is still available, so the web UI can be demonstrated locally.

## Why the bridge?

A browser cannot directly call Java Swing classes or JDBC DAO methods. Swing is a desktop UI toolkit, not a browser backend. Therefore a **minimal HTTP adapter** is required if the goal is to keep the original Java logic while replacing only the UI.

The adapter does not replace the DAO layer; it simply exposes selected existing operations as HTTP endpoints.


## One-click launchers (quick evaluation setup)

- **Windows — web port:** double-click `START-InHabit-FCC-Jugaad.bat`. It starts the Java bridge in a separate console and runs the React frontend in the current console. Close both when finished.
- **macOS — web port:** double-click `START-InHabit-FCC-Jugaad.command`. If macOS blocks it, run `chmod +x START-InHabit-FCC-Jugaad.command` once from Terminal, then launch again. It compiles/starts the Java bridge, installs React dependencies if needed, launches Vite, and opens the browser. Stop with **Ctrl+C** in the Terminal; the launcher attempts to stop the bridge it started.
- **Linux — web port:** open a terminal here and run `./START-InHabit-FCC-Jugaad.sh`. If needed, run `chmod +x START-InHabit-FCC-Jugaad.sh` once first. It starts the Java bridge and React frontend, and opens the browser using `xdg-open`/`gio` if available.
- **Original desktop app:** use `START-ORIGINAL-JAVA-SWING.bat` on Windows, `START-ORIGINAL-JAVA-SWING.command` on macOS, or `./START-ORIGINAL-JAVA-SWING.sh` on Linux. The supplied JAR was built with Java 25 bytecode; Java 25+ is recommended for it.
- **Java bridge only:** `java/START-WEB-BRIDGE.bat` on Windows, `java/START-WEB-BRIDGE.command` on macOS, or `java/START-WEB-BRIDGE.sh` on Linux.

The web-port launcher requires Java/JDK 17+ (the supplied original Swing JAR was built using Java 25, so use Java 25 for the most reliable compatibility), Node.js 22.12+, and npm. npm dependency installation needs internet on the first run. The bridge uses the existing `java/db.properties` and falls back to its in-memory mode if MySQL/JDBC is unavailable. For live MySQL, start MySQL, configure `java/db.properties`, and place MySQL Connector/J in `java/lib/` (instructions are in `java/lib/README.txt`). The launcher does not create/drop the MySQL database.

## Run it

### 1. Start the Java bridge

From `java/`:

```bash
mkdir -p out
javac -encoding UTF-8 -d out src/inhabit/*.java
java -cp out inhabit.WebBridge
```

Or use:

```bash
./run-web-bridge.sh
```

The API runs at `http://localhost:8090`.

### 2. Start the React frontend

In another terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the Vite URL shown by the terminal (normally `http://localhost:5173`).

### 3. MySQL

The bridge uses the same `db.properties` and the same `InHabitDBNew` schema as the Java project. If the MySQL Connector/J driver is available on the Java classpath, the DAO layer will use MySQL. Otherwise the original in-memory fallback mode is used.

For the actual MySQL path, keep your existing Java/JDBC setup and Connector/J configuration exactly as used for the final Java project.

## Demo login

Student:

- PRN: `257122235`
- Password: `student123`

The frontend also contains a demo-mode login so the UI can still be shown if the Java bridge is not running.

## Endpoints exposed by the bridge

- `GET /api/health`
- `POST /api/login`
- `GET /api/complaints`
- `POST /api/complaints`
- `POST /api/vote`
- `POST /api/status`
- `POST /api/assign`
- `GET /api/lostfound`
- `POST /api/lostfound`
- `POST /api/claim`

## FCC positioning

This version is intentionally a **Jugaad / backup port**, not the primary FCC submission.

It demonstrates:

- HTML structure through the React-rendered application
- CSS styling and responsive layout
- JavaScript event handling and form validation
- React components and state
- A browser-facing API integration
- The original Java + JDBC + MySQL implementation underneath
- Git-friendly separation of frontend and Java application code

It does **not** replace the FCC course's preferred Node.js + Express + MongoDB stack. The separate `InHabit-FCC-Full-Stack-Project.zip` is the proper FCC-stack version.
