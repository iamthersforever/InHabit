# Porting Notes — InHabit Jugaad Version

## Original project preserved

The original Java source files are copied into `java/src/inhabit/` without restructuring the DAO/database layer.

Key retained classes:

- `ComplaintDAO`
- `UserDAO`
- `DBConnection`
- `Complaint`
- `User`, `Student`, `Staff`
- `LostAndFoundItem`
- `InvalidComplaintException`
- `InvalidVoteException`
- `AuthenticationException`
- `EscalationThread`
- Original Swing frames and utility classes

## Only additions

### 1. `WebBridge.java`

A small Java HTTP adapter using the JDK's built-in `com.sun.net.httpserver.HttpServer`.

It calls the same existing DAO methods instead of implementing a second database layer.

### 2. React frontend

`frontend/` replaces the Swing screens with a browser UI.

Implemented screens/features:

- Login
- Dashboard
- Community grievance feed
- Search
- File grievance
- Voting
- Status changes for administrative roles
- Lost & Found
- Claiming items
- Responsive layout
- Online/fallback indicator

## Important honesty point for evaluation

If asked, do **not** say this is a pure Node/Express/MongoDB FCC implementation. It is the backup port that keeps the original Java/MySQL application logic and changes the presentation layer.

The technically accurate explanation is:

> "We retained the original Java DAO and MySQL layer and added a small HTTP adapter so a React frontend can communicate with it. The React frontend replaces the Swing GUI."

This is why this version is useful as a last-minute backup, while the separate MERN-style version is the proper FCC submission.
