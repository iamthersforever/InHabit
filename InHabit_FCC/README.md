# InHabit — Full Stack Development Flexi Credit Course Edition (`InHabit_FCC`)
**Course Name:** Full Stack Development (Course Code: F0002)  
**Institution:** Symbiosis Institute of Technology (SIT), Pune — Symbiosis International (Deemed University)  
**Students:** Raghav Mahesh Sarda (PRN: 257122235) & Aniruddha Pratap Singh (PRN: 257122247)  

A modern, production-grade full-stack web application for hostel grievances and campus lost-and-found. Built directly to satisfy all requirements of the SIT Pune Full Stack Development syllabus (CO1 through CO5), while maintaining 100% data and business logic parity with the approved Java & DBMS Mini Project (`InHabit_Approved`).

---

## 🌟 Key Highlights & Enhancements

1. **Strict Role-Segregated Portals (Direct Java Parity):**
   - **Student Dashboard (5 Dedicated Tabs):**
     1. `My Profile` — Displays room allocation, branch (B.Tech CSE - C), institutional email, contact, and verified room co-occupants (roommates).
     2. `Campus Feed` — Public campus grievances with real-time search, category/status filters, and community +1 support upvoting.
     3. `File Grievance` — Dedicated in-page form to lodge new complaints with category, location, evidence upload, and anonymity controls.
     4. `My Grievances` — Strictly filters and displays *only* the complaints filed by the currently logged-in student (e.g. Raghav sees only Raghav's tickets; Aniruddha sees only Aniruddha's tickets).
     5. `Campus Notices & Lost-Found` — Noticeboard for reporting and claiming lost or found campus belongings.
   - **Chief Warden Portal:** Executive analytics & KPI cards, full grievance operations desk (status progression & technician dispatch), 15+ student resident directory, and maintenance staff roster.
   - **Technician Portal:** Dedicated facility desk for assigned trade work orders, claiming tickets, and recording redressal notes.
2. **Dual Theme Architecture (Light & Dark Mode):**
   - Seamless one-click theme switcher in topbar and sidebar.
   - Smooth CSS transitions and persistent theme state stored in `localStorage`.
   - Rich dark palette (`#0c130f` canvas, `#17241e` surfaces, `#2ea67d` emerald accents) and crisp light palette.
3. **Optimized Typography & Clean Enterprise UI:**
   - Base font-size scaled up to 16px with clear hierarchy (no micro-fonts).
   - High legibility across all form fields (48px-50px height), table rows, modal dialogs, and stats cards.
   - 100% free of AI marketing slop, decorative orbs, or fake placeholders.
4. **Approved Campus Data Parity:**
   - 100% seeded with approved records from `InHabit_Approved/InHabitDBNew.sql`.
   - 15 real complaints (Tickets 1001–1015), including Ticket 1003 with 30 votes and automatic **Urgent Concern** priority escalation.
   - 10 real lost & found notices (LF101–LF110).
   - Official credentials: Raghav Sarda (`257122235`), Aniruddha Singh (`257122247`), Chief Warden (`admin@college.edu.in`), and Technician Tukaram Gawade (`electrician1@college.edu.in`).
5. **Zero-Setup Database Resiliency:**
   - Connects seamlessly to MongoDB Atlas or local MongoDB daemon.
   - If neither is running, it automatically boots an embedded standalone MongoDB engine with persistent WiredTiger storage in `server/.mongo_data/`. Works completely offline with zero configuration!
6. **Centralized Dependencies Hub:**
   - Located right inside `InHabit_FCC/dependencies/` with `mysql-connector-j-26.7.0.jar`, 190+ audited packages, 17 offline `.tgz` bundles, and verification scripts.
7. **Integrated Java Web Bridge:**
   - Located inside `InHabit_FCC/java-bridge/` allowing live demonstration of the Java DAO + React hybrid architecture.

---

## 🔑 Official Evaluation Credentials

| Role | Name & Room | Login (Email or PRN) | Password | Key Portal Capabilities |
|---|---|---|---|---|
| **Student 1** | Raghav Mahesh Sarda (Alpha-101) | `257122235` *(or `raghav.sarda.btech25@college.edu.in`)* | `student123` | Submit complaints with attachments, support others (+1 vote), Lost & Found, view Alpha-101 room allocation |
| **Student 2** | Aniruddha Pratap Singh (Alpha-102) | `257122247` *(or `aniruddha.singh.btech25@college.edu.in`)* | `student123` | Submit complaints, upvote, file notices, view Alpha-102 room allocation |
| **Chief Warden** | Arvind Kulkarni (Admin) | `admin@college.edu.in` *(or `chief.warden@college.edu.in`)* | `admin123` *(or `Staff@101`)* | Executive dashboard, MongoDB aggregation analytics, technician allocation, status pipeline overrides |

> **Pro-Tip for Viva:** The Login screen features **3 Quick-Switch Demo Buttons** to log in as Raghav, Aniruddha, or Chief Warden in 1 click!

---

## 🚀 One-Click Launchers

From inside `InHabit_FCC`:

- **macOS:** Double-click `START-InHabit-FCC.command` (or run `./START-InHabit-FCC.command`)
- **Linux:** Run `./START-InHabit-FCC.sh`
- **Windows:** Double-click `START-InHabit-FCC.bat`

The launcher automatically:
1. Verifies Node.js & npm runtime.
2. Checks configuration and initializes `.env`.
3. Verifies or spins up the standalone MongoDB engine with persistent data.
4. Concurrently starts the Express API (`:5000`) and Vite React client (`:5173`).
5. Opens `http://localhost:5173` in your default browser.

---

## 🛠️ Tech Stack & Syllabus Mapping

| Syllabus Outcome | Technology | Implementation in `InHabit_FCC` |
|---|---|---|
| **CO1** (HTML5 & CSS3) | Semantic HTML5, CSS Grid & Flexbox | `client/index.html`, `client/src/styles.css` with responsive breakpoints at 1250px, 920px, and 650px. |
| **CO2** (React & Hooks) | React 19 + Vite 8 | `client/src/App.jsx` using `useState`, `useEffect`, `useMemo`, `useCallback`, Lucide icons. |
| **CO3** (Node.js & Express REST) | Node.js v24 + Express 5 | `server/src/server.js`, `server/src/routes/*.js`, Postman collection in `postman/`. |
| **CO4** (MongoDB CRUD & Aggregation) | MongoDB + Mongoose 9 | `server/src/models/` (User, UserProfile 1:1, Complaint 1:N, Vote N:M). Aggregation pipeline in `dashboard.routes.js`. |
| **CO5** (Deployment & DevOps) | JWT, bcrypt, Jenkins & Render | `server/src/middleware/auth.js`, `Jenkinsfile`, and `render.yaml`. |

---

## 📦 Directory Structure

```text
InHabit_FCC/
├── client/                             # React 19 + Vite Frontend
│   ├── index.html
│   ├── vite.config.js
│   ├── package.json
│   └── src/
│       ├── App.jsx                     # Modular UI with Light/Dark Mode & 4 Demo Logins
│       ├── styles.css                  # Modern design system (Light & Dark themes, 16px base font)
│       └── api.js                      # REST API client
├── server/                             # Node.js + Express + MongoDB Backend
│   ├── src/
│   │   ├── config/db.js                # Dual MongoDB connection (Atlas / local / standalone engine)
│   │   ├── middleware/auth.js          # JWT & Role authorization guards
│   │   ├── models/                     # Mongoose schemas & indexes
│   │   ├── routes/                     # REST endpoints: auth, complaints, lostfound, dashboard
│   │   ├── services/audit.js           # Domain audit logging via node:events + node:fs
│   │   └── utils/validation.js         # Input sanitization & regex escaping
│   ├── scripts/
│   │   ├── seed.js                     # Seeds 15 approved complaints & 10 lost-found items
│   │   └── sendDemoEmail.js            # Automated email dispatch script
│   └── package.json
├── dependencies/                       # Central dependency hub & offline cache
│   ├── java/mysql-connector-j-26.7.0.jar
│   ├── node_modules/                   # 190+ packages (0 vulnerabilities)
│   ├── offline-archives/               # 17 offline .tgz bundles
│   ├── scripts/verify-all-dependencies.sh
│   └── docs/DEPENDENCY_CATALOG.md
├── java-bridge/                        # Java HTTP Bridge + Swing DAOs (Java Mini Project Synergy)
│   ├── java/
│   │   ├── src/inhabit/WebBridge.java  # HTTP bridge on port 8090
│   │   └── run-web-bridge.sh
│   └── frontend/                       # Web UI for Java bridge
├── postman/
│   └── InHabit-FCC.postman_collection.json # Ready-to-import Postman test collection
├── Jenkinsfile                         # CI/CD pipeline automation
├── render.yaml                         # Cloud deployment blueprint
├── START-InHabit-FCC.command           # macOS single-click runner
├── START-InHabit-FCC.sh                # Linux single-click runner
├── START-InHabit-FCC.bat               # Windows single-click runner
└── README.md
```

---

## 🧪 Testing and Verification

Run the automated verification suite at any time:

```bash
./dependencies/scripts/verify-all-dependencies.sh
```

To run unit tests:
```bash
npm test
```

To build production bundle:
```bash
npm run build
```
