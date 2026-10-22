# How to Push InHabit to Your GitHub Repository

This folder (`pushtogithub`) is a clean, production-ready Git repository initialized with all 113 essential project files. All generated caches, 20,000+ `node_modules` files, local database storage, and OS temporary files have been strictly filtered out via `.gitignore`.

---

## ⚡ Option 1: Using Terminal (Recommended - 2 Commands)

1. Open your Terminal and navigate to this folder:
   ```bash
   cd "/Users/thegharkalaptop/Documents/Antigravity/InHabit/pushtogithub"
   ```

2. Link your GitHub remote repository (replace with your repository link):
   ```bash
   git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/<YOUR_REPO_NAME>.git
   ```

3. Push to GitHub:
   ```bash
   git branch -M main
   git push -u origin main
   ```

*(If you ever make more code changes, simply run `git add .`, `git commit -m "update"`, and `git push`.)*

---

## 🖥️ Option 2: Using GitHub Desktop

1. Open **GitHub Desktop**.
2. Click **File** > **Add Local Repository...** (or press `Cmd + O`).
3. Choose the path:
   `/Users/thegharkalaptop/Documents/Antigravity/InHabit/pushtogithub`
4. Click **Publish repository** to push it straight to your GitHub account!

---

## 📂 Repository Contents Summary

| Directory / File | Description |
|---|---|
| `client/` | React 19 + Vite 8 frontend (light & dark mode, role-segregated UI) |
| `server/` | Express 5 REST API + MongoDB Mongoose models, validation, and JWT auth |
| `database/` | `InHabitDBNew.sql` (Master SQL dump with all 520 users, 15 complaints, 30 votes) |
| `dependencies/` | Dependency catalog, documentation, JDBC driver, 17 offline `.tgz` bundles |
| `java-bridge/` | Java DAO + WebBridge + Original Swing source code and runnable JAR |
| `docs/` | SIT Pune FCC Syllabus mapping (CO1 to CO5) |
| `postman/` | Ready-to-import Postman API collection |
| `START-InHabit-FCC.*` | One-click cross-platform launchers (macOS, Linux, Windows) |
| `.gitignore` | Prevents 22,000+ `node_modules`, `.mongo_data`, and build caches from ever bloating Git |
