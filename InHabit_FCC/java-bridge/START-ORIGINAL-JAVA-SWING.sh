#!/usr/bin/env bash
# Launch the original packaged Java Swing desktop application on Linux.
set -u
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR/java"

command -v java >/dev/null 2>&1 || { echo "ERROR: Install Java first."; exit 1; }
JAVA_MAJOR="$(java -version 2>&1 | sed -n '1s/.*version "\([0-9]*\).*/\1/p')"
if [ -n "$JAVA_MAJOR" ] && [ "$JAVA_MAJOR" -lt 25 ]; then
  echo "WARNING: InHabit.jar was built with Java 25 bytecode; detected Java $JAVA_MAJOR."
  echo "Use Java 25+ for the most reliable compatibility."
fi
java -jar InHabit.jar
status=$?
if [ "$status" -ne 0 ] && [ -t 0 ]; then
  echo "The desktop app exited with code $status."
  read -r -p "Press Enter to close... " _ || true
fi
exit "$status"
