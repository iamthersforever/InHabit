#!/usr/bin/env bash
# Compile and run just the Java API bridge.
set -u
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
chmod +x ./run-web-bridge.sh 2>/dev/null || true
exec ./run-web-bridge.sh
