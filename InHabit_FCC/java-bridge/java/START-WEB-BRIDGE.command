#!/bin/bash
set -u
cd "$(dirname "$0")"
./run-web-bridge.sh
status=$?
echo
if [ "$status" -ne 0 ]; then
  echo "The Java bridge stopped with exit code $status. Check the error above."
  read -r -p "Press Return to close..." _
fi
exit "$status"
