#!/bin/bash
set -u
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

pause_exit() { echo; read -r -p "Press Return to close this window..." _; }
fail() { echo; echo "ERROR: $1"; pause_exit; exit 1; }

echo "============================================"
echo "        InHabit FCC - One-click start"
echo "============================================"

command -v node >/dev/null 2>&1 || fail "Node.js is missing. Install Node.js 22.12+ and reopen this launcher."
command -v npm  >/dev/null 2>&1 || fail "npm is missing. Reinstall Node.js 22.12+ (npm is included)."
node -e "const [a,b]=process.versions.node.split('.').map(Number);process.exit(a>22||(a===22&&b>=12)?0:1)" \
  || fail "Detected Node $(node -v). InHabit FCC requires Node.js 22.12 or newer."

node scripts/prepare-local.mjs || fail "Could not prepare local configuration."
if [ ! -x "node_modules/.bin/vite" ] || [ ! -x "node_modules/.bin/concurrently" ]; then
  echo; echo "Installing project dependencies (first run only; internet required)..."
  npm install --no-audit --no-fund || fail "npm install failed. Check your internet connection and rerun this launcher."
fi

echo
echo "Preparing demo accounts and sample records in MongoDB..."
if ! npm run seed; then
  echo
  echo "MongoDB setup failed. The app needs a working MongoDB connection."
  echo "1) Start your local MongoDB service, OR"
  echo "2) Edit server/.env and set MONGODB_URI to your MongoDB Atlas URI."
  echo "Then double-click this launcher again. No database was intentionally dropped."
  pause_exit
  exit 1
fi

echo
echo "Starting API + React frontend. Browser will open at http://localhost:5173"
(sleep 4; open "http://localhost:5173" >/dev/null 2>&1) &
npm run dev
