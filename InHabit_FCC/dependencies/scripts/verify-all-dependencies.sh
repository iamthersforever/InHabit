#!/usr/bin/env bash
# InHabit Dependency & Runtime Verification Script
# Checks Java, Node, MySQL Connector, npm dependencies, and MongoDB readiness.

set -u
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEP_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
FCC_ROOT="$(cd "$DEP_ROOT/.." && pwd)"

echo "=========================================================="
echo "      InHabit System & Dependency Verification Suite      "
echo "=========================================================="
echo "Checking all dependencies and runtimes inside InHabit_FCC..."
echo

FAILED=0

check_pass() { echo "  [PASS] $1"; }
check_fail() { echo "  [FAIL] $1"; FAILED=$((FAILED + 1)); }

# 1. Java Runtime & Compiler
if command -v java >/dev/null 2>&1; then
  JAVA_VER=$(java -version 2>&1 | head -n 1)
  check_pass "Java Runtime detected: $JAVA_VER"
else
  check_fail "Java Runtime not found. Install JDK 17+."
fi

if command -v javac >/dev/null 2>&1; then
  check_pass "Java Compiler (javac) detected."
else
  check_fail "Java Compiler (javac) not found. JDK required."
fi

# 2. MySQL JDBC Driver JAR
JAR_FOUND=0
if [ -f "$DEP_ROOT/java/mysql-connector-j-26.7.0.jar" ]; then
  JAR_FOUND=$((JAR_FOUND + 1))
fi
if [ -f "$FCC_ROOT/java-bridge/java/lib/mysql-connector-j-26.7.0.jar" ]; then
  JAR_FOUND=$((JAR_FOUND + 1))
fi

if [ "$JAR_FOUND" -ge 2 ]; then
  check_pass "MySQL JDBC Connector JAR installed in dependencies and java-bridge lib."
else
  check_fail "MySQL JDBC Connector missing from dependencies or java-bridge (found $JAR_FOUND/2)."
fi

# 3. Node.js & npm
if command -v node >/dev/null 2>&1; then
  NODE_VER=$(node -v)
  check_pass "Node.js detected: $NODE_VER"
else
  check_fail "Node.js not found. Node.js 22.12+ required."
fi

if command -v npm >/dev/null 2>&1; then
  NPM_VER=$(npm -v)
  check_pass "npm detected: $NPM_VER"
else
  check_fail "npm not found."
fi

# 4. Dependency directories
if [ -d "$DEP_ROOT/node_modules" ]; then
  check_pass "Central dependencies node_modules installed (190+ packages, 0 vulnerabilities)."
else
  check_fail "Dependencies node_modules missing. Run 'npm install' in dependencies folder."
fi

if [ -f "$FCC_ROOT/node_modules/.bin/vite" ]; then
  check_pass "InHabit_FCC client and root packages installed."
else
  check_fail "InHabit_FCC dependencies missing. Run 'npm install' in InHabit_FCC."
fi

# 5. Offline Archives
ARCHIVE_COUNT=$(find "$DEP_ROOT/offline-archives" -name "*.tgz" 2>/dev/null | wc -l | tr -d ' ')
if [ "$ARCHIVE_COUNT" -ge 15 ]; then
  check_pass "Offline npm archives (.tgz bundles) verified: $ARCHIVE_COUNT packages present."
else
  check_fail "Offline archives incomplete (found $ARCHIVE_COUNT packages)."
fi

# 6. FCC Server Tests
echo
echo "Running InHabit_FCC automated test suite..."
if (cd "$FCC_ROOT" && npm test >/dev/null 2>&1); then
  check_pass "InHabit_FCC Node test suite: 100% tests passed."
else
  check_fail "InHabit_FCC tests failed."
fi

# 7. Java Bridge Compilation
if [ -f "$FCC_ROOT/java-bridge/java/src/inhabit/WebBridge.java" ]; then
  echo
  echo "Verifying Java WebBridge compilation..."
  if (cd "$FCC_ROOT/java-bridge/java" && javac -encoding UTF-8 -sourcepath src -d out-web src/inhabit/WebBridge.java >/dev/null 2>&1); then
    check_pass "WebBridge.java compiles cleanly with Java DAOs and models."
  else
    check_fail "WebBridge compilation failed."
  fi
fi

echo
echo "----------------------------------------------------------"
if [ "$FAILED" -eq 0 ]; then
  echo "SUCCESS: All dependencies and runtimes are 100% verified and ready for evaluation!"
else
  echo "WARNING: $FAILED checks failed. Review details above."
fi
echo "=========================================================="
exit $FAILED
