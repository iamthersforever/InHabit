# InHabit — Hostel Grievance Management System

**One project. Three academic dimensions: DBMS, Java, and Full Stack Development.**

InHabit is a hostel grievance-management platform designed to make campus maintenance and issue reporting more organized and transparent. Students can report grievances, follow their progress, support shared issues, and post lost-and-found notices. Staff can review and resolve tickets, while administrators oversee assignments, status changes, and operational insights.

> **Implementation note:** InHabit has multiple related implementations developed for different coursework requirements. The original **DBMS/Java edition** uses Java Swing, JDBC, and MySQL. The **Full Stack Development (FCC) edition** is a separate web implementation using React, Node.js, Express, and MongoDB. They share the same project concept, but they do not run as one application or share the same database schema.

## Project at a glance

| Edition | Technologies | What it demonstrates |
|---|---|---|
| **DBMS** | MySQL, ER/EER modelling, relational design, SQL | A normalized relational database, relationships and constraints, views, a stored function, a stored procedure, and a trigger for grievance escalation. |
| **Java application** | Java, Swing, JDBC, DAO pattern, OOP, event handling, multithreading | A desktop application with student, administrator, and technician workflows, grievance management, voting, and lost-and-found. |
| **Full Stack Development (FCC)** | HTML5, CSS3, JavaScript, React, Node.js, Express, MongoDB, Mongoose | A browser-based application backed by REST APIs, authentication, role-protected workflows, database operations, dashboard aggregation, testing, and deployment configuration. |
| **Java-bridge web port (optional)** | React/Vite + Java HTTP bridge + existing Java DAO/JDBC layer | An alternative web UI that keeps the legacy Java backend. This is a compatibility/backup port, **not** the primary Node/Express/MongoDB FCC edition. |

## How the implementations fit together

```text
                         InHabit
                            |
             +--------------+--------------+
             |              |              |
        DBMS edition   Java desktop    FCC web edition
             |              |              |
           MySQL       Swing + JDBC   React + HTML/CSS/JS
                            |              |
                       Java DAO       Node.js + Express
                            |              |
                          MySQL           MongoDB
```

The optional Java-bridge build follows a different path:

```text
React / Vite browser UI
          |
      HTTP requests
          |
    WebBridge.java
          |
 Existing Java DAO / JDBC
          |
  MySQL or local fallback
```

## Core features

- **Grievance reporting:** File categorized issues with descriptions and location details.
- **Status tracking:** Follow issues through stages such as Open, Under Review, In Progress, Resolved, and Closed.
- **Community support:** Students can support shared concerns, with duplicate/self-voting restrictions implemented in the relevant versions.
- **Escalation:** High-support grievances can be flagged for attention; the threshold is set to 30 supports in the project versions that implement this workflow.
- **Role-specific workspaces:** Student, administrator/warden, and technician workflows.
- **Lost and found:** Post notices and manage claim/return workflows.
- **Administration and insights:** Review tickets, assign work, monitor statuses, and view summary statistics.

Feature availability and enforcement details can vary by implementation. Consult the README inside the relevant edition for its exact scope.

## 1. DBMS and Java edition

The original desktop application is built with **Java Swing + JDBC + MySQL**. Its SQL design is maintained in `InHabitDBNew.sql` and includes 15 relational tables, along with the `AssignedTicketsView` view, `fn_DaysPending` function, `sp_AssignTicket` procedure, and `trg_AutoEscalateComplaint` trigger.

### Run the desktop application

From the directory containing the Java edition:

```bash
# macOS / Linux
./run.sh

# Windows
run.bat

# Or, if the packaged JAR is present
java -jar InHabit.jar
```

The project also includes a headless smoke-test entry point in builds where the compiled test classes are supplied:

```bash
java -cp out inhabit.SmokeTest
```

### MySQL notes

- Review `InHabitDBNew.sql` before executing it against an existing database; setup scripts may recreate schema objects or data.
- Configure the JDBC connection and ensure MySQL Connector/J is available when you want to use a live MySQL instance.
- Some packaged builds support an in-memory fallback when MySQL or the JDBC driver is unavailable. Data in that fallback is not a substitute for a persistent MySQL database.

## 2. Full Stack Development (FCC) edition

The FCC edition reimplements the same hostel-grievance concept as a web application with a separate stack:

- **Frontend:** React, HTML5, CSS3, and JavaScript
- **Backend:** Node.js and Express REST API
- **Database:** MongoDB with Mongoose
- **Authentication:** bcrypt password hashes, JWT sessions, and role-protected API routes
- **Development and delivery:** Postman collection, automated tests, Jenkins pipeline file, and Render deployment configuration

It includes grievance CRUD workflows, support/voting, status and technician assignment flows, lost-and-found, and dashboard statistics. It uses MongoDB rather than reusing the original MySQL schema.

### Run the FCC edition

If the FCC source is committed under `InHabit-FCC/`, follow its full setup guide at [`InHabit-FCC/README.md`](InHabit-FCC/README.md). The standard setup requires a recent Node.js/npm installation and a running local MongoDB instance or a MongoDB Atlas URI.

The typical local setup is:

```bash
cd InHabit-FCC
cp server/.env.example server/.env
# Edit server/.env: configure MONGODB_URI and a strong JWT_SECRET
npm install
npm run seed
npm run dev
```

The frontend normally runs at `http://localhost:5173` and the API at `http://localhost:5000`. Run the included tests and production build with:

```bash
npm test
npm run build
```

Use the edition's own README for the exact requirements, demo accounts, environment variables, launcher scripts, API routes, and deployment instructions.

## 3. Optional Java-bridge web port

The Java-bridge version replaces the Swing presentation layer with a React/Vite frontend while retaining the existing Java DAO and database layer behind `WebBridge.java`. It is useful as a transitional or backup implementation when preserving the original Java/MySQL logic is important.

If this version is committed under `InHabit-FCC-Jugaad/`, its platform-specific launch instructions and API endpoints are documented in [`InHabit-FCC-Jugaad/README.md`](InHabit-FCC-Jugaad/README.md).

## Repository layout

The root-level README introduces the overall project. Keep implementation-specific setup instructions with their corresponding source so the differences between the editions stay clear. A combined repository can use a layout such as:

```text
InHabit/
├── README.md                    # Overall project and edition guide
├── InHabitDBNew.sql             # Relational DBMS schema
├── src/ or Java source files    # Original Java desktop app
├── run.sh / run.bat             # Desktop launchers, when supplied
├── InHabit-FCC/                 # React + Node/Express + MongoDB edition
│   ├── client/
│   ├── server/
│   └── README.md
└── InHabit-FCC-Jugaad/          # Optional React-to-Java bridge edition
    ├── frontend/
    ├── java/
    └── README.md
```

Only keep folder links above once the corresponding folders have been committed to this repository. If the versions live in separate archives or repositories, link to those locations instead.

## Team

- **Raghav Sarda**
- **Aniruddha Pratap Singh**

Developed as an academic mini-project and extended to demonstrate database design, Java application development, and full-stack web development.

## Scope and security

InHabit is an educational project, not a production-ready campus service. Before institutional deployment, it would need a security review, approved institutional identity integration, controlled account provisioning, backups, monitoring, secure file storage, and operational policies for handling sensitive grievances. Never commit real `.env` values, database passwords, tokens, or personal credentials to version control.
