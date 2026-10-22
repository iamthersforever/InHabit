# InHabit - Hostel Grievance Management System
## Mini Project: Java Swing (GUI) + Multi-Threading + JDBC + InHabitDBNew

**Student Name:** Raghav Mahesh Sarda (PRN: 257122235 | Room: Alpha-101 | B.Tech CSE - C)  
**Teammate:** Aniruddha Pratap Singh (PRN: 257122247 | Room: Alpha-102 | B.Tech CSE - C)  
**Main Database File:** `InHabitDBNew.sql` (15 Normalized Tables matching EER Diagram)  

---

## 1. Quick Start (Zero Setup Required)

The application includes an embedded in-memory fallback engine pre-loaded with data from `InHabitDBNew.sql`. It runs immediately whether MySQL is installed or offline:

### Option A: Double-Click Runnable JAR (Recommended)
Double-click `InHabit.jar` or run:
```bash
java -jar InHabit.jar
```

### Option B: macOS Single-Click Script
Double-click `Run-InHabit.command` or run:
```bash
./Run-InHabit.command
```

### Option C: Standalone Single-File Runner
```bash
java InHabitDemo.java
```

### Option D: Build and Run from Source
- **macOS / Linux:**
  ```bash
  ./run.sh
  ```
- **Windows:**
  ```cmd
  run.bat
  ```

### Automated Verification Test Suite (Headless):
```bash
java -cp out inhabit.SmokeTest
```
*(Runs 34 automated tests verifying Raghav & Aniruddha authentication, Technician and Admin portals, live search across categories/wings, Lost & Found notice posting/claiming, room occupancy, voting constraints, and auto-escalation).*

---

## 2. Demo Evaluation Credentials

| Role | Email / Username | Password | Profile & Features |
|---|---|---|---|
| **Raghav Sarda** | `raghav.sarda.btech25@college.edu.in` (or PRN `257122235`) | `raghav_sarda@235` (or `student123`) | **Alpha-101** (CSE - C). Roommates: Parth Kulkarni, Yatharth Shah. Filed: Ticket 1001 (Wi-Fi). |
| **Aniruddha Singh** | `aniruddha.singh.btech25@college.edu.in` (or PRN `257122247`) | `aniruddha_singh@247` (or `student123`) | **Alpha-102** (CSE - C). Roommate: Vishrut Chinta. Filed: Ticket 1002 (Solar hot water). |
| **Administrator** | `admin@college.edu.in` (or `admin`) | `admin123` | **Chief Warden & Admin**. Accesses Admin Dashboard with all students, staff, complaints, technician assignments & status management. |
| **Technician (Electrician)** | `electrician1@college.edu.in` | `Staff@106` | **Tukaram Gawade** (Electrical Dept). Accesses dedicated Facility Redressal Desk to claim tickets, update redressal notes, and view workload stats. |

> **Tip:** The Login screen contains 4 quick-switch buttons (`[Raghav (101)]`, `[Aniruddha (102)]`, `[Admin Portal]`, `[Tech (Electrician)]`) for instant 1-click credential filling!

---

## 3. Key Features

1. **Normalized 15-Table Database (`InHabitDBNew.sql`):**
   - Engineered strictly as per approved ER & EER diagrams with 3NF relations: `USER`, `USER_PHONE`, `STUDENT`, `STAFF`, `ADMIN`, `WARDEN`, `TECHNICIAN`, `COMPLAINT`, `ESCALATED_COMPLAINT`, `SENSITIVE_COMPLAINT`, `ATTACHMENT`, `VOTE`, `STATUS_AUDIT_LOG`, `TICKET_ASSIGNMENT`, `LOST_AND_FOUND`.
   - Includes stored view `AssignedTicketsView`, stored function `fn_DaysPending`, stored procedure `sp_AssignTicket`, and trigger `trg_AutoEscalateComplaint`.
2. **Real-Time Search & Separator Filters:**
   - Real-time search across Title, Description, Location, PRN, Category, and Wing using `DocumentListener` and `JComboBox` filter separators.
   - Live result count badges (`Showing: X`) on all complaint feeds, student directories, staff directories, and lost-and-found listings.
3. **Interactive Grievance & Notice Details Inspector:**
   - Double-click any row or select with single-click/arrow-keys to instantly inspect full details via live bottom preview cards and modal dialogs.
   - Added dedicated "View Details" / "View Notice Details" action buttons on all toolbars.
   - Allows peer upvoting directly from the details view for students.
   - Allows status updates (`Open`, `Under Review`, `In Progress`, `Resolved`, `Closed`) for Admins and Technicians.
4. **Campus Notices & Lost-and-Found Bulletin (Tab 5):**
   - Interactive bulletin for reporting lost or found items with location, item type, and description.
   - Instant live preview card showing notice details, contact info, and status.
   - One-click Claim mechanism with confirmation dialog.
5. **Executive Analytics & Insights Tab (Admin Dashboard):**
   - Replaced basic overview/stats with a unified Analytics & Insights command center.
   - Includes real-time KPI metric cards (Total Complaints, Active Pipeline, Concerning Escalations, Resolution Rate %).
   - Visual Category Breakdown with color-coded horizontal progress bars.
   - Grievance Lifecycle Status Pipeline.
   - Hostel Block Demographics distribution (Alpha, Beta, Charlie, Delta).
   - High-Priority / Concerning Incident Queue with one-click full inspection.
6. **Full 500-Resident Directory with Wing/Block Filtering:**
   - Admin student directory displays all 500 resident students directly loaded from `InHabitDBNew.sql`.
   - Filter directory instantly by Wing/Block (`All Blocks`, `Alpha`, `Beta`, `Charlie`, `Delta`) with real-time student count badges.
7. **Dedicated Maintenance Technician Portal (`TechnicianDashboard`):**
   - Facility technicians (electricians, plumbers, network staff) can view trade-specific tasks, workload metrics cards, claim assigned complaints, and log resolution status.
8. **Community Voting & Multithreaded Auto-Escalation:**
   - Students can upvote genuine community issues.
   - Duplicate voting and voting on own tickets are strictly prevented.
   - Tickets with $\ge 30$ genuine votes are automatically elevated to `CONCERNING`.
9. **Clean Project Structure & `currdelete` Archive:**
   - All superseded and temporary review files have been archived into `currentversion/currdelete/` to maintain a pristine, student-grade workspace.

