#!/bin/bash
set -u
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
BRIDGE_PID=""

pause_exit() { echo; read -r -p "Press Return to close this window..." _; }
cleanup() {
  if [ -n "$BRIDGE_PID" ] && kill -0 "$BRIDGE_PID" 2>/dev/null; then
    kill "$BRIDGE_PID" 2>/dev/null || true
  fi
}
fail() { echo; echo "ERROR: $1"; pause_exit; exit 1; }
trap cleanup EXIT INT TERM

echo "=============================================="
echo "       InHabit FCC Jugaad - One-click start"
echo "=============================================="
command -v java >/dev/null 2>&1 || fail "Java runtime is missing. Install a JDK 17 or newer."
command -v javac >/dev/null 2>&1 || fail "Java compiler javac is missing. Install a JDK 17 or newer."
command -v node >/dev/null 2>&1 || fail "Node.js is missing. Install Node.js 22.12+ first."
command -v npm >/dev/null 2>&1 || fail "npm is missing. Reinstall Node.js 22.12+."
node -e "const [a,b]=process.versions.node.split('.').map(Number);process.exit(a>22||(a===22&&b>=12)?0:1)" \
  || fail "Detected Node $(node -v). This Vite version requires Node.js 22.12 or newer."

if [ ! -x "$ROOT_DIR/frontend/node_modules/.bin/vite" ]; then
  echo; echo "Installing React/Vite frontend dependencies (first run only; internet required)..."
  (cd "$ROOT_DIR/frontend" && npm install --no-audit --no-fund) || fail "npm install failed. Check internet and rerun."
fi

mkdir -p "$ROOT_DIR/java"
if [ ! -x "$ROOT_DIR/java/run-web-bridge.sh" ]; then chmod +x "$ROOT_DIR/java/run-web-bridge.sh" 2>/dev/null || true; fi
cd "$ROOT_DIR/java"
./run-web-bridge.sh > "$ROOT_DIR/java/webbridge.log" 2>&1 &
BRIDGE_PID=$!
sleep 2
if kill -0 "$BRIDGE_PID" 2>/dev/null; then
  echo "Java bridge started. Log: java/webbridge.log"
else
  BRIDGE_PID=""
  if node -e "fetch('http://127.0.0.1:8090/api/health').then(r=>process.exit(r.ok?0:1)).catch(()=>process.exit(1))" >/dev/null 2>&1; then
    echo "A bridge already responds on port 8090; reusing it."
  else
    echo "WARNING: the Java bridge did not stay running. Details are in java/webbridge.log."
    echo "The React UI can still open in demo/fallback mode."
  fi
fi

cd "$ROOT_DIR/frontend"
echo
echo "Starting React frontend at http://localhost:5173"
(sleep 4; open "http://localhost:5173" >/dev/null 2>&1) &
npm run dev -- --host 127.0.0.1 --port 5173 --strictPort
