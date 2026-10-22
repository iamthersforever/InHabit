# FCC Jugaad — Viva Talking Points

### Why React?
The Java Swing UI is desktop-only. React provides the new browser-based presentation layer while keeping the application logic underneath.

### Why do we need WebBridge?
A browser cannot directly instantiate Java Swing classes or call JDBC DAO methods. The bridge exposes selected DAO operations over HTTP.

### Did we rewrite the database layer?
No. The existing Java DAO classes and `InHabitDBNew` MySQL schema are retained.

### Is this the proper FCC Node/Express/MongoDB implementation?
No. This is the backup/Jugaad port. The proper FCC-stack version is the separate MERN-style project.

### What is the architecture?
React frontend → Java WebBridge → existing Java DAO → JDBC/MySQL.

### What happens if MySQL is unavailable?
The original Java project has an in-memory fallback mode, so the web frontend can still demonstrate the application flow.
