#!/usr/bin/env bash
# Linux one-click launcher for the React frontend + existing Java DAO/JDBC bridge.
set -u
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BRIDGE_PID=""
cd "$ROOT_DIR"

pause_exit() { echo; if [ -t 0 ]; then read -r -p "Press Enter to close... " _ || true; fi; }
fail() { echo; echo "ERROR: $1"; pause_exit; exit 1; }
cleanup() {
  if [ -n "$BRIDGE_PID" ] && kill -0 "$BRIDGE_PID" 2>/dev/null; then
    kill "$BRIDGE_PID" 2>/dev/null || true
    wait "$BRIDGE_PID" 2>/dev/null || true
  fi
}
trap cleanup EXIT
trap 'exit 130' INT TERM

cat <<'BANNER'
==================================================
       InHabit FCC Jugaad - One-click start
==================================================
BANNER

command -v java >/dev/null 2>&1 || fail "Java runtime is missing. Install JDK 17+ for the web bridge."
command -v javac >/dev/null 2>&1 || fail "javac is missing. Install a JDK 17+ and ensure javac is on PATH."
command -v node >/dev/null 2>&1 || fail "Node.js is missing. Install Node.js 22.12+ first."
command -v npm  >/dev/null 2>&1 || fail "npm is missing. Reinstall Node.js 22.12+ (npm is included)."
node -e "const [a,b]=process.versions.node.split('.').map(Number);process.exit(a>22||(a===22&&b>=12)?0:1)" \
  || fail "Detected Node $(node -v). This Vite version requires Node.js 22.12 or newer."

if [ ! -x "$ROOT_DIR/frontend/node_modules/.bin/vite" ]; then
  echo
  echo "Installing React/Vite dependencies (first run only; internet required)..."
  (cd "$ROOT_DIR/frontend" && npm install --no-audit --no-fund) \
    || fail "npm install failed. Check your internet connection and rerun this launcher."
fi

mkdir -p "$ROOT_DIR/java/out-web" "$ROOT_DIR/java/lib"
chmod +x "$ROOT_DIR/java/run-web-bridge.sh" 2>/dev/null || true

echo
echo "Starting Java WebBridge (recompiles the existing DAO/model source)..."
(cd "$ROOT_DIR/java" && exec ./run-web-bridge.sh) > "$ROOT_DIR/java/webbridge.log" 2>&1 &
BRIDGE_PID=$!
sleep 2
if kill -0 "$BRIDGE_PID" 2>/dev/null; then
  echo "Java bridge process started. Log: java/webbridge.log"
elif node -e "fetch('http://127.0.0.1:8090/api/health').then(r=>process.exit(r.ok?0:1)).catch(()=>process.exit(1))" >/dev/null 2>&1; then
  BRIDGE_PID=""
  echo "A Java bridge is already responding on port 8090; reusing it."
else
  BRIDGE_PID=""
  echo "WARNING: Java bridge did not stay running. See java/webbridge.log."
  echo "The browser UI may still open in demo/fallback mode, but DB-backed actions may not work."
fi

echo
echo "Starting React frontend at http://localhost:5173"
echo "Java API: http://localhost:8090/api/health"
echo "Stop the frontend with Ctrl+C; the launcher will try to stop its Java bridge."
if command -v xdg-open >/dev/null 2>&1; then
  (sleep 4; xdg-open "http://localhost:5173" >/dev/null 2>&1 || true) &
elif command -v gio >/dev/null 2>&1; then
  (sleep 4; gio open "http://localhost:5173" >/dev/null 2>&1 || true) &
else
  echo "No desktop browser opener found; open http://localhost:5173 manually."
fi
cd "$ROOT_DIR/frontend"
npm run dev -- --host 127.0.0.1 --port 5173 --strictPort
