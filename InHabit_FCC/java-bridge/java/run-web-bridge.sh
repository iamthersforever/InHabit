#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p out-web lib
if ! command -v javac >/dev/null 2>&1; then
  echo "ERROR: A JDK is required (javac was not found). Install Java 17 or newer."
  exit 1
fi
echo "Compiling the InHabit web bridge and the existing Java DAO/model classes..."
javac -encoding UTF-8 -sourcepath src -d out-web src/inhabit/WebBridge.java
echo "Starting the Java HTTP bridge at http://localhost:8090"
exec java -cp "out-web:lib/*" inhabit.WebBridge
