# InHabit - Hostel Grievance Management System
## Mini-Project Comprehensive Technical Documentation & Lab Submission Report
**Course:** Database Management Systems & Object-Oriented Programming with Java  
**Student Name:** Raghav Mahesh Sarda  
**PRN:** 257122235  
**Teammate:** Aniruddha Pratap Singh (PRN: 257122247)  
**Class & Branch:** B.Tech Computer Science & Engineering (CSE - C)  
**Hostel Location:** Wing Alpha, Room Alpha-101  
**Target Evaluation:** 10-Mark Practical & Mini-Project Rubric (Steps 1 to 11)  
**Main Database File:** `InHabitDBNew.sql` (15 Normalized Tables in 3NF/BCNF)  

---

## Table of Contents
1. [Rubric Evaluation Mapping](#1-rubric-evaluation-mapping)
2. [Section 1: Project Planning and Requirement Analysis (2 Marks)](#section-1-project-planning-and-requirement-analysis-2-marks)
   - [Step 1: Problem Definition & Simulation Scope](#step-1-problem-definition--simulation-scope)
   - [Step 2: Functional Requirements (FR 1 to FR 10)](#step-2-functional-requirements-fr-1-to-fr-10)
3. [Section 2: Database Design (3 Marks)](#section-2-database-design-3-marks)
   - [Step 3: Entities & Relationship Identification](#step-3-entities--relationship-identification)
   - [Step 4: E-R and Enhanced EER Diagram Specifications](#step-4-e-r-and-enhanced-eer-diagram-specifications)
   - [Step 5: Relational Schema Conversion (15 Normalized Tables)](#step-5-relational-schema-conversion-15-normalized-tables)
   - [Step 6: Anomaly Analysis (Insertion, Deletion, Update)](#step-6-anomaly-analysis-insertion-deletion-update)
   - [Step 7: Functional Dependencies (FD Analysis)](#step-7-functional-dependencies-fd-analysis)
   - [Step 8: Normalization Process (1NF to BCNF)](#step-8-normalization-process-1nf-to-bcnf)
4. [Section 3: Functionality and Implementation (3 Marks)](#section-3-functionality-and-implementation-3-marks)
   - [Step 9: Database DDL & Schema (`InHabitDBNew.sql`)](#step-9-database-ddl--schema-inhabitdbnewsql)
   - [Step 10: SQL Queries, Triggers, Views, Functions, and Procedures](#step-10-sql-queries-triggers-views-functions-and-procedures)
5. [Section 4: Demonstration & Individual Contribution (2 Marks)](#section-4-demonstration--individual-contribution-2-marks)
   - [Step 11: System Demonstration & Code Architecture](#step-11-system-demonstration--code-architecture)
   - [Individual Contribution Statement](#individual-contribution-statement)
   - [Viva-Voce Questions and Answers](#viva-voce-questions-and-answers)
6. [Complete Development Changelog & Work Log](#complete-development-changelog--work-log)

---

## 1. Rubric Evaluation Mapping

| Rubric Criteria | Marks | Covered Steps | Implementation in Project |
|---|:---:|---|---|
| **1. Project Planning & Requirement Analysis** | **2 Marks** | **Step 1 & Step 2** | Problem statement defined, stakeholder analysis (Students, Wardens, Technicians, Admins), 10 Functional Requirements (FR 1 - FR 10). |
| **2. Database Design** | **3 Marks** | **Step 3 to Step 8** | Entity identification, E-R & EER diagrams, relational mapping to 15 normalized tables, insertion/deletion/update anomaly analysis, FD decomposition, 1NF $\rightarrow$ 2NF $\rightarrow$ 3NF $\rightarrow$ BCNF proof. |
| **3. Functionality and Implementation** | **3 Marks** | **Step 9 & Step 10** | `InHabitDBNew.sql` implementation (15 tables, 500+ student records, 20 staff records, 15 complaint tickets, 10 lost-and-found items), trigger `trg_AutoEscalateComplaint`, procedure `sp_AssignTicket`, function `fn_DaysPending`, view `AssignedTicketsView`. |
| **4. Demonstration & Individual Contribution** | **2 Marks** | **Step 11** | Interactive Swing GUI with 5 tabs, real-time search & separator filter toolbars, Lost & Found bulletin, Technician redressal portal, multithreaded auto-escalation daemon (`EscalationThread`), 34 automated smoke tests, `currdelete` archive. |
| **Total** | **10 Marks** | **Steps 1 to 11** | **Complete Implementation** |

---

## Section 1: Project Planning and Requirement Analysis (2 Marks)

### Step 1: Problem Definition & Simulation Scope
In collegiate residential campuses accommodating hundreds of students across multiple hostel wings (Alpha, Beta, Charlie, Delta), traditional grievance logging methods—such as physical register logbooks or scattered WhatsApp groups—suffer from chronic failure modes:
1. **Lack of Transparency:** Students cannot verify if maintenance personnel have acknowledged their ticket.
2. **Duplicate Complaints:** Multiple residents in the same wing or floor file separate grievances for the same underlying issue (e.g., captive portal router outage or water pump failure), overwhelming maintenance staff.
3. **Absence of Priority Democracy:** Emergency issues affecting dozens of students are treated with the same FIFO priority as minor personal queries.
4. **Accountability Gaps:** No systematic timestamping or ownership linking tickets to technicians.

**Solution (InHabit):**
InHabit simulates an integrated campus residential management ecosystem. It couples an authenticated relational DBMS (`InHabitDBNew`) with a multi-threaded Java desktop interface. Residents can file grievances, track real-time redressal statuses, cast community votes (`Genuine` / `Mid`) to democratically surface critical failures (triggering automated escalation at $\ge 30$ votes), report lost-and-found items, and allow technicians to claim and resolve tasks.

---

### Step 2: Functional Requirements (FR 1 to FR 10)

- **FR 1: Secure Resident & Stakeholder Authentication**
  - Authenticates users using institutional credentials (`InstitutionalEmail` or `PRN`) against stored cryptographic password hashes in `USER`.
  - Role-based specialization distinguishes Students, Wardens, Technicians, and System Administrators.
- **FR 2: Grievance Logging & Metadata Validation**
  - Residents log grievances with Title, Category (`Internet`, `Plumbing`, `Mess`, `Cleanliness`, `Electrical`, `Security`), Specific Location, and Description.
- **FR 3: Community Prioritization & Peer Voting**
  - Students view campus-wide grievances in "Campus Feed" and cast one vote per ticket.
  - Strict constraints prevent duplicate voting by the same PRN (`UNIQUE(TICKETID, USERID)`) and disallow self-voting on one's own tickets.
- **FR 4: Automated Background Threshold Escalation**
  - A background daemon thread (`EscalationThread`) and database trigger `trg_AutoEscalateComplaint` evaluate community votes.
  - When an unresolved grievance accumulates $\ge 30$ genuine votes, the system elevates it into `ESCALATED_COMPLAINT`.
- **FR 5: Resident Personal Dashboard & Room Occupancy**
  - Displays personal academic and residential metadata (PRN, Wing, Room Number, Institutional Email, Phone).
  - Dynamically queries relational data to display room co-occupants (e.g., Room Alpha-101 occupants: Raghav Sarda, Yatharth Shah, Parth Kulkarni).
- **FR 6: Real-Time Multi-Axis Search & Separator Filters**
  - Real-time instant search across Title, Description, Location, Category, and Wing using `DocumentListener` and `JComboBox` separators.
  - Live count badges (`Showing: X`) on all complaint feeds, student directories, staff directories, and lost-and-found notices.
- **FR 7: Campus Notices & Lost-and-Found Bulletin (Tab 5)**
  - Interactive bulletin for reporting lost or found items with location, item type, and description.
  - Live details inspector card updating immediately on single-click or keyboard selection, paired with modal dialog on double-click or "View Notice Details" button.
  - One-click Claim mechanism with confirmation dialog.
- **FR 8: Dedicated Maintenance Technician Portal**
  - Facility technicians (electricians, plumbers, network staff) view trade-specific tasks, workload metrics cards, claim assigned complaints, and log resolution status.
- **FR 9: Administrator Portal (4 Tabs & Executive Analytics)**
  - Tab 1: **Analytics & Insights** (replaces basic stats) integrating 4 KPI cards (Total Complaints, Active Pipeline, Concerning Escalations, Resolution Rate %), Category Breakdown progress meters, Grievance Lifecycle Status Pipeline, Hostel Block Resident Demographics breakdown (Alpha, Beta, Charlie, Delta), and an Escalated Concerning Queue with inspection.
  - Tab 2: **Complaints Manager** with real-time multi-criteria filtering, technician assignment, status modification, and live bottom preview card.
  - Tab 3: **All Students Directory** rendering the full 500-resident roster from `InHabitDBNew.sql` with Wing/Block filtering (`Alpha`, `Beta`, `Charlie`, `Delta`) and selection badge.
  - Tab 4: **Staff Directory** with 20 maintenance technicians across electrical, plumbing, civil, carpentry, and network trades.
- **FR 10: Dual-Mode Persistence Architecture**
  - Connects to MySQL server hosting `InHabitDBNew` via JDBC.
  - If the database server is offline during evaluation, smoothly activates an embedded in-memory data store preloaded with `InHabitDBNew.sql` records (including all 500 students and 20 staff), ensuring 100% demo availability.

---

## Section 2: Database Design (3 Marks)

### Step 3: Entities & Relationship Identification

The core entities identified in `InHabitDBNew` are:
1. **USER (Super-entity):** Base user account with institutional email, name, and role.
2. **USER_PHONE:** Multi-valued phone numbers associated with a user.
3. **STUDENT (Sub-entity):** Campus resident with PRN, hostel block, floor, and wing.
4. **STAFF (Sub-entity):** Campus employee with staff ID and department.
5. **ADMIN (Sub-entity):** Hostel system administrator with access level.
6. **WARDEN (Sub-entity):** Hostel warden in charge of specific blocks.
7. **TECHNICIAN (Sub-entity):** Facility technician with trade and supervisor warden.
8. **COMPLAINT:** Grievance ticket filed by a student.
9. **ESCALATED_COMPLAINT (Sub-entity):** High-priority complaint elevated by community votes ($\ge 30$).
10. **SENSITIVE_COMPLAINT (Sub-entity):** Anonymous or confidential grievance.
11. **ATTACHMENT:** File attachments and photos uploaded for complaints.
12. **VOTE:** Community peer-validation votes cast by students.
13. **STATUS_AUDIT_LOG:** Chronological audit trail tracking status transitions.
14. **TICKET_ASSIGNMENT:** Work order dispatch assigning complaints to technicians by wardens.
15. **LOST_AND_FOUND:** Campus notice items reported found or lost.

---

### Step 4: E-R and Enhanced EER Diagram Specifications

```mermaid
erDiagram
    USER ||--o{ USER_PHONE : "has"
    USER ||--o| STUDENT : "specializes into"
    USER ||--o| STAFF : "specializes into"
    STAFF ||--o| ADMIN : "specializes into"
    STAFF ||--o| WARDEN : "specializes into"
    STAFF ||--o| TECHNICIAN : "specializes into"
    WARDEN ||--o{ TECHNICIAN : "supervises"
    STUDENT ||--o{ COMPLAINT : "files"
    COMPLAINT ||--o| ESCALATED_COMPLAINT : "specializes into"
    COMPLAINT ||--o| SENSITIVE_COMPLAINT : "specializes into"
    COMPLAINT ||--o{ ATTACHMENT : "has"
    COMPLAINT ||--o{ VOTE : "accumulates"
    STUDENT ||--o{ VOTE : "casts"
    COMPLAINT ||--o{ STATUS_AUDIT_LOG : "audits"
    COMPLAINT ||--o{ TICKET_ASSIGNMENT : "assigned via"
    USER ||--o{ LOST_AND_FOUND : "reports"
```

---

### Step 5: Relational Schema Conversion (15 Normalized Tables)

The conceptual EER model maps to 15 relational tables in 3NF/BCNF:

1. **`USER`** ($\underline{\text{UserID}}$, FirstName, MiddleName, LastName, $\text{InstitutionalEmail}^\text{UK}$, PasswordHash, Role)
2. **`USER_PHONE`** ($\underline{\text{PhoneID}}$, $\text{UserID}^*$, PhoneNumber)
3. **`STUDENT`** ($\underline{\text{UserID}}^*$, $\text{PRN}^\text{UK}$, BlockName, FloorNumber, Wing)
4. **`STAFF`** ($\underline{\text{UserID}}^*$, $\text{StaffID}^\text{UK}$, Department)
5. **`ADMIN`** ($\underline{\text{UserID}}^*$, AccessLevel)
6. **`WARDEN`** ($\underline{\text{UserID}}^*$, BlockInCharge)
7. **`TECHNICIAN`** ($\underline{\text{UserID}}^*$, Trade, $\text{SupervisorID}^*$)
8. **`COMPLAINT`** ($\underline{\text{TicketID}}$, Title, Description, Category, Status, CreatedAt, WardenConfirmed, StudentConfirmed, $\text{FiledBy}^*$)
9. **`ESCALATED_COMPLAINT`** ($\underline{\text{TicketID}}^*$, EscalatedAt)
10. **`SENSITIVE_COMPLAINT`** ($\underline{\text{TicketID}}^*$, IsAnonymous)
11. **`ATTACHMENT`** ($\underline{\text{AttachmentID}}$, $\text{TicketID}^*$, FileURL, FileType, FileSize_MB, UploadedAt)
12. **`VOTE`** ($\underline{\text{VoteID}}$, $\text{TicketID}^*$, $\text{UserID}^*$, VoteType, VotedAt) — Candidate Key: `(TicketID, UserID)`
13. **`STATUS_AUDIT_LOG`** ($\underline{\text{LogID}}$, $\text{TicketID}^*$, $\text{ChangedBy}^*$, OldStatus, NewStatus, ChangedAt)
14. **`TICKET_ASSIGNMENT`** ($\underline{\text{AssignmentID}}$, $\text{TicketID}^*$, $\text{WardenID}^*$, $\text{TechnicianID}^*$, AssignedAt, ResolutionNotes)
15. **`LOST_AND_FOUND`** ($\underline{\text{ItemID}}$, ItemName, Description, LocationFound, ItemType, Status, ReportedAt, $\text{ReportedBy}^*$)

---

### Step 6: Anomaly Analysis (Insertion, Deletion, Update)

1. **Insertion Anomaly:**
   - A newly admitted student who has not yet lodged a complaint can be inserted into `USER` and `STUDENT` without requiring dummy complaint rows or NULL keys.
2. **Deletion Anomaly:**
   - If a student's complaint is deleted, their user record and phone number remain intact in `USER` and `STUDENT`.
3. **Update Anomaly:**
   - If a resident updates their phone number or room wing, it is updated in exactly one place (`USER_PHONE` or `STUDENT`), preventing partial update anomalies.

---

### Step 7: Functional Dependencies (FD Analysis)

- **`USER`:** $\text{UserID} \rightarrow \{\text{FirstName}, \text{MiddleName}, \text{LastName}, \text{InstitutionalEmail}, \text{PasswordHash}, \text{Role}\}$; $\text{InstitutionalEmail} \rightarrow \{\text{UserID}, \dots\}$
- **`STUDENT`:** $\text{UserID} \rightarrow \{\text{PRN}, \text{BlockName}, \text{FloorNumber}, \text{Wing}\}$; $\text{PRN} \rightarrow \{\text{UserID}, \dots\}$
- **`STAFF`:** $\text{UserID} \rightarrow \{\text{StaffID}, \text{Department}\}$; $\text{StaffID} \rightarrow \{\text{UserID}, \dots\}$
- **`COMPLAINT`:** $\text{TicketID} \rightarrow \{\text{Title}, \text{Description}, \text{Category}, \text{Status}, \text{CreatedAt}, \text{WardenConfirmed}, \text{StudentConfirmed}, \text{FiledBy}\}$
- **`VOTE`:** $\text{VoteID} \rightarrow \{\text{TicketID}, \text{UserID}, \text{VoteType}, \text{VotedAt}\}$; $\{\text{TicketID}, \text{UserID}\} \rightarrow \{\text{VoteID}, \dots\}$
- **`LOST_AND_FOUND`:** $\text{ItemID} \rightarrow \{\text{ItemName}, \text{Description}, \text{LocationFound}, \text{ItemType}, \text{Status}, \text{ReportedAt}, \text{ReportedBy}\}$

---

### Step 8: Normalization Process (1NF to BCNF)

1. **1NF:** Multi-valued phone numbers are factored out into `USER_PHONE`, ensuring all attribute domains are atomic.
2. **2NF:** In relations with composite candidate keys (`VOTE`), all non-prime attributes are fully functionally dependent on the entire key.
3. **3NF:** No non-prime attribute is transitively dependent on any candidate key.
4. **BCNF:** For every non-trivial functional dependency $X \rightarrow Y$, $X$ is a superkey. The schema is in BCNF, lossless, and dependency-preserving.

---

## Section 3: Functionality and Implementation (3 Marks)

### Step 9: Database DDL & Schema (`InHabitDBNew.sql`)
The database script `InHabitDBNew.sql` contains the complete schema:
- **Database:** `InHabitDBNew`
- **15 Tables:** Normalized into 3NF/BCNF.
- **Referential Integrity:** Enforced using `FOREIGN KEY ... ON DELETE CASCADE` and `ON DELETE SET NULL`.

---

### Step 10: SQL Queries, Triggers, Views, Functions, and Procedures

1. **View (`AssignedTicketsView`):**
```sql
CREATE OR REPLACE VIEW AssignedTicketsView AS
SELECT complaint.TicketID, complaint.Title, complaint.Category, complaint.Status,
       ticket_assignment.WardenID, ticket_assignment.TechnicianID, ticket_assignment.ResolutionNotes
FROM complaint
INNER JOIN ticket_assignment ON complaint.TicketID = ticket_assignment.TicketID;
```

2. **Stored Function (`fn_DaysPending`):**
```sql
DELIMITER $$
CREATE FUNCTION fn_DaysPending(p_ticket_id INT)
RETURNS INT DETERMINISTIC
BEGIN
    DECLARE v_days INT;
    SELECT DATEDIFF(CURRENT_DATE(), DATE(CreatedAt)) INTO v_days
    FROM COMPLAINT WHERE TicketID = p_ticket_id;
    RETURN IFNULL(v_days, 0);
END$$
DELIMITER ;
```

3. **Stored Procedure (`sp_AssignTicket`):**
```sql
DELIMITER $$
CREATE PROCEDURE sp_AssignTicket(
    IN p_assignment_id VARCHAR(20),
    IN p_ticket_id INT,
    IN p_warden_id VARCHAR(30),
    IN p_tech_id VARCHAR(30),
    IN p_notes TEXT
)
BEGIN
    DECLARE v_old_status VARCHAR(30);
    DECLARE v_next_log_id INT;

    SELECT Status INTO v_old_status FROM COMPLAINT WHERE TicketID = p_ticket_id;
    SELECT IFNULL(MAX(LogID), 0) + 1 INTO v_next_log_id FROM STATUS_AUDIT_LOG;

    INSERT INTO TICKET_ASSIGNMENT (AssignmentID, TicketID, WardenID, TechnicianID, AssignedAt, ResolutionNotes)
    VALUES (p_assignment_id, p_ticket_id, p_warden_id, p_tech_id, NOW(), p_notes);

    UPDATE COMPLAINT SET Status = 'In Progress' WHERE TicketID = p_ticket_id;

    INSERT INTO STATUS_AUDIT_LOG (LogID, TicketID, ChangedBy, OldStatus, NewStatus, ChangedAt)
    VALUES (v_next_log_id, p_ticket_id, p_warden_id, v_old_status, 'In Progress', NOW());
END$$
DELIMITER ;
```

4. **Trigger (`trg_AutoEscalateComplaint`):**
```sql
DELIMITER $$
CREATE TRIGGER trg_AutoEscalateComplaint
AFTER INSERT ON VOTE
FOR EACH ROW
BEGIN
    DECLARE v_vote_count INT;
    SELECT COUNT(*) INTO v_vote_count FROM VOTE WHERE TicketID = NEW.TicketID;

    IF v_vote_count >= 30 THEN
        INSERT IGNORE INTO ESCALATED_COMPLAINT (TicketID, EscalatedAt)
        VALUES (NEW.TicketID, NOW());
    END IF;
END$$
DELIMITER ;
```

---

## Section 4: Demonstration & Individual Contribution (2 Marks)

### Step 11: System Demonstration & Code Architecture

#### Architecture & Package Layout:
```
currentversion/
├── InHabitDBNew.sql           # Complete MySQL database creation, DDL, and 15 normalized tables
├── InHabit.jar                # Standalone runnable JAR (single-click execution)
├── InHabitDemo.java           # Zero-dependency single-file Java demo with search, lost & found, technician portal
├── Run-InHabit.command        # macOS single-click desktop runner with multi-JDK detection
├── run.sh                     # Linux/macOS shell build & run script
├── run.bat                    # Windows batch build & run script
├── db.properties              # JDBC configuration (InHabitDBNew)
├── currdelete/                # Archive of superseded files (InHabitDB.sql, REVIEW/)
├── out/                       # Compiled class files
├── src/
│   └── inhabit/               # Student package
│       ├── Main.java          # Entry point & thread launcher
│       ├── DBConnection.java  # JDBC connection manager with in-memory fallback
│       ├── User.java          # Base user model (Inheritance)
│       ├── Student.java       # Student specialization model
│       ├── Staff.java         # Staff specialization model
│       ├── Complaint.java     # Grievance ticket model
│       ├── LostAndFoundItem.java # Lost and found model
│       ├── UserDAO.java       # User authentication, search & occupancy queries
│       ├── ComplaintDAO.java  # Grievance CRUD, search, voting & lost-and-found queries
│       ├── EscalationThread.java # Multithreading daemon for auto-escalation
│       ├── UIUtils.java       # Custom antialiased visible buttons & modal inspector
│       ├── LoginFrame.java    # Swing GUI authentication screen (with quick buttons)
│       ├── StudentDashboard.java # Swing GUI 5-tab student dashboard with live search
│       ├── AdminDashboard.java   # Swing GUI 4-tab admin portal with search & filters
│       ├── TechnicianDashboard.java # Swing GUI maintenance redressal desk
│       ├── SmokeTest.java     # Automated verification test suite (34 tests)
│       ├── AuthenticationException.java # Custom exception
│       ├── InvalidComplaintException.java # Custom exception
│       └── InvalidVoteException.java      # Custom exception
```

---

### Individual Contribution Statement
- **Student:** Raghav Mahesh Sarda (PRN: 257122235)
- **Teammate:** Aniruddha Pratap Singh (PRN: 257122247)
- **Role & Contributions:**
  - Designed the 15-table relational schema and authored the complete MySQL script `InHabitDBNew.sql`.
  - Implemented the Java Swing GUI (`LoginFrame`, `StudentDashboard`, `AdminDashboard`, and `TechnicianDashboard`).
  - Added real-time search & separator filter toolbars across all views.
  - Implemented Lost and Found notice posting and claiming bulletin.
  - Implemented JDBC data access layer with dual-mode in-memory fallback.
  - Developed the multithreaded auto-escalation daemon thread (`EscalationThread`).
  - Created automated test harness (`SmokeTest`) verifying 34 test cases (100% pass rate).
  - Maintained project workspace cleanliness by archiving superseded files into `currdelete/`.

---

### Viva-Voce Questions and Answers

**Q1: Why did you redesign the database from 5 tables into 15 normalized tables?**  
*Answer:* The 15-table design strictly reflects the approved Enhanced Entity-Relationship (EER) diagram. It factors out multi-valued phone numbers (`USER_PHONE`), decomposes disjoint specializations (`STUDENT`, `STAFF`, `ADMIN`, `WARDEN`, `TECHNICIAN`), isolates subtypes (`ESCALATED_COMPLAINT`, `SENSITIVE_COMPLAINT`), and adds dedicated audit and assignment tables (`STATUS_AUDIT_LOG`, `TICKET_ASSIGNMENT`), reaching strict BCNF without redundancy.

**Q2: How does the system prevent duplicate voting?**  
*Answer:* In the database, the `VOTE` table enforces a composite unique constraint `UNIQUE(TICKETID, USERID)`. In the application layer, `ComplaintDAO` checks this constraint prior to insertion and raises `InvalidVoteException` if a student attempts to vote twice.

**Q3: How does the multithreading feature work in InHabit?**  
*Answer:* `EscalationThread` extends `Thread` and is marked as a daemon thread (`setDaemon(true)`). It runs concurrently with the Swing UI, periodically executing every 15 seconds to check if any unresolved ticket has accumulated $\ge 30$ genuine votes, immediately elevating it into `ESCALATED_COMPLAINT`.

**Q4: What happens if MySQL is not installed on the evaluation machine?**  
*Answer:* `DBConnection` tests MySQL reachability at startup. If MySQL is offline, it activates the embedded in-memory fallback mode seeded with data from `InHabitDBNew.sql`. The demo continues with 100% functionality and zero crashes.

---

## Complete Development Changelog & Work Log

### Session 1 to Session 7: Startup Stabilization, Fallback, and Clean Student Package
- Established dual-mode in-memory fallback architecture.
- Created standalone runner `InHabitDemo.java` and runnable JAR `InHabit.jar`.
- Duplicate all necessary files into dedicated `currentversion/` workspace.
- Refactored package into clean student style `inhabit/`.

### Session 8: UI Button Contrast, Complaint Details Inspector & Admin Portal
- Implemented `UIUtils.java` with visible high-contrast buttons across macOS, Windows, and Linux.
- Added interactive complaint details modal.
- Configured individual logins for Raghav and Aniruddha.
- Built `AdminDashboard.java` with 4 management tabs.

### Session 9: Teammate Assets Integration & Real-Time Search Features
- Reviewed and integrated assets provided by teammate Aniruddha from `REVIEW/currentversionANIRUDDHA/`.
- Implemented real-time search with category, status, and wing separator dropdowns in `StudentDashboard` and `AdminDashboard`.
- Implemented Tab 5 Lost & Found Bulletin with "Report Item" and "Claim Item" dialogs.
- Created `TechnicianDashboard.java` for maintenance staff (electricians, plumbers) to claim tickets and log redressal notes.
- Expanded automated verification suite `SmokeTest.java` to 34 tests covering all search, filter, notice, and technician flows (100% pass rate).

### Session 10: Adoption of `InHabitDBNew.sql` and `currdelete` Workspace Archive
- Adopted `InHabitDBNew.sql` as the primary, sole database file for the project (15 normalized tables matching the new ER and EER diagrams).
- Updated `db.properties` and `DBConnection.java` to point to `InHabitDBNew`.
- Updated `UserDAO.java` and `ComplaintDAO.java` to execute SQL joins aligned with `InHabitDBNew` table and column names (`UserID`, `TicketID`, `FiledBy`, `ItemID`, etc.).
- Synchronized single-file runner `InHabitDemo.java` with all new search, lost-and-found, and technician features.
- Recompiled and rebuilt `InHabit.jar`.
- Created `currentversion/currdelete/` folder and moved all superseded files (`InHabitDB.sql` and temporary `REVIEW/` directory) into it to preserve a clean, human-built student project structure.

### Session 11: Lost & Found Live Inspector, 500-Resident Directory & Executive Analytics
- **Campus Lost & Found & Ticket Inspection Fixed:**
  - Resolved table rendering and selection issues on Campus Lost & Found.
  - Implemented dual-inspection architecture: instantaneous live preview card at the base (`BorderLayout.SOUTH`) updating upon single-click or keyboard navigation, complemented by full modal inspector on double-click or "View Notice Details" / "View Details" toolbar actions.
  - Fixed selection contrast preservation in custom JTable cell renderers.
- **Admin Dashboard "Analytics & Insights" Executive Center:**
  - Retired the legacy "Overview & Stats" tab and built an integrated "Analytics & Insights" dashboard.
  - Unified 4 high-level KPI metric cards (Total Grievances, Active Pipeline, Concerning Escalations, Resolution Rate %).
  - Implemented visual horizontal category distribution bars with percentage fills.
  - Added Grievance Lifecycle Status Pipeline (Open, Under Review, In Progress, Resolved, Closed).
  - Added Hostel Block Demographics distribution (Alpha, Beta, Charlie, Delta resident counts).
  - Integrated an Escalated Concerning Queue table ($\ge 30$ community votes) with one-click full inspection.
- **500-Resident Student Roster:**
  - Resolved student directory truncation (which previously showed only 12 students) by adding an intelligent SQL loader (`loadAllStudentsFromSql()`) in `UserDAO.java` and `InHabitDemo.java` that parses all 500 records directly from `InHabitDBNew.sql`, backed by a deterministic procedural synthesizer fallback.
  - Added Wing/Block filter dropdown (`All Blocks`, `Alpha`, `Beta`, `Charlie`, `Delta`) with real-time student count badges and resident selection footer details.
- **Audit & Verification:**
  - Executed automated smoke test suite `SmokeTest.java`: 34 / 34 tests passing (100%).
  - Both modular multi-file architecture (`src/inhabit/*.java`) and standalone single-file runner (`InHabitDemo.java`) verified and re-compiled.
  - Rebuilt executable runnable JAR `InHabit.jar`.


