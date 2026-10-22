#!/usr/bin/env bash
# Linux one-click launcher for InHabit FCC (React + Express + MongoDB).
set -u
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

pause_exit() {
  echo
  if [ -t 0 ]; then read -r -p "Press Enter to close... " _ || true; fi
}
fail() { echo; echo "ERROR: $1"; pause_exit; exit 1; }

cat <<'BANNER'
============================================
        InHabit FCC - One-click start
============================================
BANNER

command -v node >/dev/null 2>&1 || fail "Node.js is missing. Install Node.js 22.12+ and rerun."
command -v npm  >/dev/null 2>&1 || fail "npm is missing. Reinstall Node.js 22.12+ (npm is included)."
node -e "const [a,b]=process.versions.node.split('.').map(Number);process.exit(a>22||(a===22&&b>=12)?0:1)" \
  || fail "Detected Node $(node -v). InHabit FCC requires Node.js 22.12 or newer."

node scripts/prepare-local.mjs || fail "Could not prepare local configuration."
if [ ! -x "node_modules/.bin/vite" ] || [ ! -x "node_modules/.bin/concurrently" ]; then
  echo
  echo "Installing project dependencies (first run only; internet required)..."
  npm install --no-audit --no-fund || fail "npm install failed. Check your internet connection and rerun this launcher."
fi

echo
echo "Preparing demo accounts and sample records in MongoDB..."
if ! npm run seed; then
  echo
  echo "MongoDB setup failed. The app needs a working MongoDB connection."
  echo "1) Start your local MongoDB service, OR"
  echo "2) Edit server/.env and set MONGODB_URI to your MongoDB Atlas URI."
  echo "Then rerun this launcher. This launcher does not intentionally drop any database."
  pause_exit
  exit 1
fi

echo
echo "Starting Express API + React frontend."
echo "Frontend: http://localhost:5173"
echo "API:      http://localhost:5000/api/health"
echo "Stop both with Ctrl+C in this terminal."
if command -v xdg-open >/dev/null 2>&1; then
  (sleep 4; xdg-open "http://localhost:5173" >/dev/null 2>&1 || true) &
elif command -v gio >/dev/null 2>&1; then
  (sleep 4; gio open "http://localhost:5173" >/dev/null 2>&1 || true) &
else
  echo "No desktop browser opener found; open http://localhost:5173 manually."
fi
npm run dev
