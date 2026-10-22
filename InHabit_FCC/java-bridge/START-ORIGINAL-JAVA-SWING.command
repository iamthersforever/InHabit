#!/bin/bash
set -u
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR/java"
command -v java >/dev/null 2>&1 || { echo "Install Java 25 or a compatible runtime first."; read -r -p "Press Return to close..." _; exit 1; }
java -jar InHabit.jar
status=$?
if [ "$status" -ne 0 ]; then echo "Java app exited with code $status."; read -r -p "Press Return to close..." _; fi
exit "$status"
