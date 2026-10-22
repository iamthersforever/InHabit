# InHabit — Dependencies & Runtime Package Repository (`Dependencies_InHabit`)

This directory serves as the **central dependency hub, offline package repository, and runtime verification suite** for the entire **InHabit** project ecosystem:
1. **`InHabit-FCC`** (Full Stack Development MERN Submission — Node.js, Express, MongoDB, React, Vite)
2. **`InHabit-FCC-Jugaad`** (Hybrid Java + React Web Port — Java DAOs, `WebBridge.java`, React UI)
3. **`currentversion`** (Original DBMS / Java Swing Desktop Application)

---

## Directory Structure

```text
Dependencies_InHabit/
├── java/
│   └── mysql-connector-j-26.7.0.jar      # Official MySQL JDBC Driver (Java 17/21/25 compatible)
├── node_modules/                         # 190+ fully installed packages for instant local use
├── offline-archives/                     # 17 offline .tgz npm package bundles (zero-internet install)
│   ├── bcryptjs-3.0.3.tgz
│   ├── concurrently-10.0.6.tgz
│   ├── cors-2.8.6.tgz
│   ├── dotenv-18.0.9.tgz
│   ├── express-5.3.0.tgz
│   ├── express-rate-limit-8.7.1.tgz
│   ├── helmet-8.3.0.tgz
│   ├── jsonwebtoken-9.0.3.tgz
│   ├── lucide-react-1.61.0.tgz
│   ├── mongodb-memory-server-11.3.0.tgz
│   ├── mongoose-9.11.3.tgz
│   ├── multer-2.5.0.tgz
│   ├── nodemailer-10.2.1.tgz
│   ├── react-19.3.1.tgz
│   ├── react-dom-19.3.1.tgz
│   ├── vite-8.3.5.tgz
│   └── vitejs-plugin-react-6.1.2.tgz
├── scripts/
│   ├── verify-all-dependencies.sh        # Automated one-click verification (macOS/Linux)
│   └── verify-all-dependencies.bat       # Automated one-click verification (Windows)
├── docs/
│   └── DEPENDENCY_CATALOG.md             # Full syllabus mapping (Units 1-6 & CO1-CO5)
├── package.json                          # Central npm dependency declaration
└── package-lock.json                     # Deterministic dependency lockfile
```

---

## 1. Quick Verification

To verify that all runtimes, packages, JARs, and test suites are 100% operational:

- **macOS / Linux:**
  ```bash
  ./scripts/verify-all-dependencies.sh
  ```
- **Windows:**
  ```cmd
  scripts\verify-all-dependencies.bat
  ```

---

## 2. Dependency Manifest & Syllabus Mapping

| Dependency | Version | Layer | Course Outcome / Unit | Purpose & Technical Justification |
|---|---|---|---|---|
| **`react` / `react-dom`** | `^19.3.1` | Frontend | CO1, CO2 (Units 1 & 2) | Component-based interactive UI, declarative state, hooks (`useState`, `useEffect`, `useMemo`, `useCallback`). |
| **`vite`** | `^8.3.5` | Frontend Tooling | CO1, CO2 (Unit 2) | Modern, ultra-fast ES module bundler and dev server with Hot Module Replacement (HMR). |
| **`@vitejs/plugin-react`** | `^6.1.2` | Frontend Build | CO2 | Fast Refresh and JSX transformation for React 19 in Vite. |
| **`lucide-react`** | `^1.59.0` | Frontend UI | CO1 (Unit 2) | Accessible, lightweight SVG icon system for grievance status badges and navigation. |
| **`express`** | `^5.3.0` | Backend API | CO3 (Unit 3) | Minimal and flexible Node.js web application framework providing RESTful API routes. |
| **`mongoose`** | `^9.11.2` | Database ORM | CO4 (Unit 4) | Elegant MongoDB object modeling, schema validation, document references, indexes, and aggregation pipelines. |
| **`mongodb-memory-server`** | `^11.3.0` | Standalone DB | CO4 (Unit 4) | Runs official standalone MongoDB engine locally with zero setup, ensuring offline demo resilience. |
| **`jsonwebtoken` (JWT)** | `^9.0.2` | Security / Auth | CO5 (Unit 6) | Stateless token-based user authentication and role-based route guard middleware (`student`, `admin`, `technician`). |
| **`bcryptjs`** | `^2.4.3` | Security | CO5 (Unit 6) | Secure cryptographic password hashing with 10 salt rounds to protect student and admin credentials. |
| **`helmet`** | `^8.0.0` | Security | CO2 (Unit 3) | HTTP response header hardening (CSP, X-Content-Type-Options) preventing clickjacking and script injection. |
| **`cors`** | `^2.8.5` | Networking | CO3 (Unit 3) | Configures Cross-Origin Resource Sharing allowing the Vite client (`:5173`) to communicate with Express (`:5000`). |
| **`express-rate-limit`** | `^7.5.0` | API Protection | CO3 (Unit 3) | Rate limiting to prevent brute-force login attempts and denial-of-service spam. |
| **`multer`** | `^2.4.0` | File System | CO2 (Unit 3) | Handles `multipart/form-data` for grievance and lost-and-found image evidence uploads. |
| **`nodemailer`** | `^10.2.1` | Automation | CO2 (Unit 3) | SMTP email dispatch script (`node scripts/sendDemoEmail.js`) for automated hostel notifications. |
| **`dotenv`** | `^16.4.7` | Config | CO5 (Unit 6) | Loads environmental variables (`PORT`, `MONGODB_URI`, `JWT_SECRET`) from `.env`. |
| **`concurrently`** | `^9.1.2` | Dev Workflow | CO5 (Unit 6) | Concurrently runs both the Node backend and React frontend under a single terminal command. |
| **`mysql-connector-j`** | `26.7.0` | Database Driver | Java Mini Project / DBMS | Official Type 4 JDBC driver for MySQL, connecting Java DAOs to `InHabitDBNew`. |

---

## 3. How to Run Each Project

### A. Full Stack FCC Project (`InHabit-FCC`) — Primary Submission
- **macOS:** Double-click `START-InHabit-FCC.command`
- **Linux:** `./START-InHabit-FCC.sh`
- **Windows:** Double-click `START-InHabit-FCC.bat`
- **Manual CLI:**
  ```bash
  cd InHabit-FCC
  npm run dev
  ```
  - Frontend: `http://localhost:5173`
  - Backend API: `http://localhost:5000`
  - Postman collection: `InHabit-FCC/postman/InHabit-FCC.postman_collection.json`

### B. FCC Jugaad Project (`InHabit-FCC-Jugaad`) — Java Bridge Submission
- **macOS:** Double-click `START-InHabit-FCC-Jugaad.command`
- **Windows:** Double-click `START-InHabit-FCC-Jugaad.bat`
- **Linux:** `./START-InHabit-FCC-Jugaad.sh`
- Bridges React UI (`:5173`) with Java HTTP Bridge (`:8090`) and existing Java DAOs (`InHabitDBNew`).

### C. DBMS & Java Swing Project (`currentversion`) — Original Java Submission
- Double-click `Run-InHabit.command` or run `java -jar InHabit.jar`.
