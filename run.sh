#!/bin/bash
# InHabit - Hostel Grievance Management System
# Build and Run Script

# Auto-detect JAVA_HOME if not present
if ! command -v javac &> /dev/null || ! javac -version &> /dev/null; then
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
    elif [ -d "/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"
    elif command -v /usr/libexec/java_home &> /dev/null; then
        export JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null)"
    fi
    if [ -n "$JAVA_HOME" ]; then
        export PATH="$JAVA_HOME/bin:$PATH"
    fi
fi

mkdir -p out
echo "Compiling InHabit application..."
javac -d out src/inhabit/*.java
if [ $? -eq 0 ]; then
    echo "Starting InHabit..."
    java -cp "out:lib/*" inhabit.Main
else
    echo "Compilation failed."
    exit 1
fi
