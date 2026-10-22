# InHabit — Comprehensive Full Stack Dependency Catalog & Academic Mapping
**Course Name:** Full Stack Development (Course Code: F0002)  
**Institution:** Symbiosis Institute of Technology (SIT), Pune — Symbiosis International (Deemed University)  
**Students:** Raghav Mahesh Sarda (PRN: 257122235) & Aniruddha Pratap Singh (PRN: 257122247)  

---

## 1. Course Outcomes (COs) Mapping

| Course Outcome ID | Description in Syllabus | Project Implementation & Packages |
|---|---|---|
| **CO1** | Design and develop responsive web pages using HTML, CSS, and JavaScript. | Semantic HTML5 structure (`index.html`), custom CSS grid/flexbox (`styles.css`), responsive media queries at 1250px, 920px, 650px. Uses `lucide-react` for clean iconography. |
| **CO2** | Build dynamic frontend applications using ReactJS with components and hooks. | React 19 (`react`, `react-dom`, `@vitejs/plugin-react`). Modular components (`AuthScreen`, `Sidebar`, `Header`, `ComplaintCard`, `LostFoundBoard`, `Modal`). State management via `useState`, `useEffect`, `useMemo`, `useCallback`. |
| **CO3** | Develop RESTful backend services using Node.js and Express.js and test them using Postman. | Node.js v24 + Express 5 (`express`, `cors`, `helmet`, `express-rate-limit`). 16 structured REST endpoints tested with included `InHabit-FCC.postman_collection.json`. |
| **CO4** | Implement full stack applications with MongoDB integration for CRUD operations. | MongoDB + Mongoose (`mongoose`, `mongodb-memory-server`). Demonstrates 1:1 relationship (`UserProfile`), 1:N relationship (`Complaint`), N:M bridge (`Vote`), unique compound indexes, and `$group` aggregation pipeline for real-time dashboard analytics. |
| **CO5** | Deploy and demonstrate a complete full stack application using Render. | Environment variables (`dotenv`), multi-process script runner (`concurrently`), Jenkins CI/CD pipeline (`Jenkinsfile`), and Render cloud blueprint (`render.yaml`). |

---

## 2. Unit-by-Unit Syllabus Topic Coverage

### Unit 1: HTML5 Fundamentals & Forms
- **Syllabus Topics:** HTML document structure, Semantic elements (`<header>`, `<section>`, `<article>`, `<footer>`, `<nav>`, `<main>`), HTML Forms & Inputs, native validation attributes (`required`, `type="email"`, `minLength`, `pattern`).
- **Where Implemented:** 
  - `InHabit-FCC/client/index.html`
  - `InHabit-FCC/client/src/App.jsx` forms for student sign-up, complaint filing, ticket status redressal, and lost & found posting.

### Unit 2: CSS Styling Basics & Advanced Layouts
- **Syllabus Topics:** CSS Selectors (class, id, element), Box model (margin, padding, border), Flexbox & CSS Grid, Responsive Web Design with mobile-first media queries, browser DevTools inspection.
- **Where Implemented:**
  - `InHabit-FCC/client/src/styles.css`
  - Dual-layout CSS Grid for Admin Analytics, Flexbox for Complaint Cards, Responsive Breakpoints at 1250px, 920px, and 650px.

### Unit 3: Advanced JS, DOM, Security & Node.js Core
- **Syllabus Topics:** RegExp, DOM manipulation, secure input sanitization (preventing script injection / XSS), Node.js core HTTP module, Node File System (`node:fs`), URL module (`node:url`), Event Emitter (`node:events`), file uploads, automated email dispatch.
- **Where Implemented & Packages:**
  - Input sanitization & regex escaping: `InHabit-FCC/server/src/utils/validation.js`
  - File uploads: `multer` (`InHabit-FCC/server/src/routes/complaints.routes.js` accepts images up to 5 MB into `uploads/`)
  - Event Emitter & File System logging: `InHabit-FCC/server/src/services/audit.js` using `node:events` and `node:fs/promises` writing to `logs/audit.jsonl`
  - Node URL module: normalizes incoming request paths in `server.js`
  - Automated Email: `nodemailer` script in `InHabit-FCC/server/scripts/sendDemoEmail.js`

### Unit 4: Database Intro & MongoDB CRUD, Modeling & Aggregation
- **Syllabus Topics:** SQL vs NoSQL, MongoDB setup, CRUD operations, One-to-One and Many-to-Many relationships, Aggregation pipelines, Indexes, MongoDB Atlas cloud cluster.
- **Where Implemented & Packages:**
  - `mongoose` & `mongodb-memory-server`
  - **1:1 Relationship:** `User.js` references `UserProfile.js` (unique student hostel room, block, and wing).
  - **1:N Relationship:** `User.js` to `Complaint.js` (`createdBy` reference).
  - **N:M Relationship:** `Vote.js` joining `User` and `Complaint` with compound index preventing duplicate votes.
  - **Aggregation Pipeline:** `InHabit-FCC/server/src/routes/dashboard.routes.js` using `$group`, `$sum`, and `$facet` to compute live category breakdown, status percentages, and urgent tickets.

### Unit 5: DevOps: Git, Jenkins & Tomcat
- **Syllabus Topics:** Version control with Git, Jenkins pipeline automation, Tomcat setup.
- **Where Implemented:**
  - `InHabit-FCC/Jenkinsfile`: Declarative Jenkins pipeline automating dependency installation, security audit, Node test suite execution (`node --test`), and React client build (`vite build`).

### Unit 6: Deployment and Mini Project
- **Syllabus Topics:** Login and registration flow, JWT token sessions, environment variables, hosting on Render.
- **Where Implemented & Packages:**
  - `bcryptjs` for salted password hashing.
  - `jsonwebtoken` for expiring Bearer tokens and role-based route authorization.
  - `dotenv` for configuration isolation.
  - `InHabit-FCC/render.yaml` declaring full-stack deployment on Render (Express Web Service + Static Site).

---

## 3. Java Mini Project Integration (FCC + Java Synergy)

Per the faculty notification:
> *"Flexi Credit Course Mini Project will be combined with the Java Mini Project... Students must ensure that the concepts and requirements of the Flexi Credit Course are properly implemented and can be demonstrated and explained during the evaluation process keeping in mind the scope of Java Mini project as well."*

To satisfy both criteria with 100% confidence, we maintain two complementary approaches:
1. **`InHabit-FCC` (Standalone Full MERN Application):**
   - Implements the exact F0002 syllabus: Node.js, Express, MongoDB, React, Vite, Postman, Jenkins, Render.
2. **`InHabit-FCC-Jugaad` (Integrated Java-to-Web Architecture):**
   - Shows direct technical continuity with the Java Mini Project.
   - Preserves all 15 tables from `InHabitDBNew.sql`, the multi-threaded `EscalationThread`, and existing `ComplaintDAO`/`UserDAO` classes.
   - `WebBridge.java` acts as a lightweight HTTP server bridging the Java DAO backend with the modern React frontend.
