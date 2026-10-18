#!/bin/bash
# InHabit - Single-Click Launcher for macOS
cd "$(dirname "$0")"

# Configure Java runtime
if ! command -v java &> /dev/null || ! java -version &> /dev/null; then
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

# Launch InHabit (prefer JAR, then out, then compile)
if [ -f "InHabit.jar" ]; then
    java -jar InHabit.jar
elif [ -d "out" ]; then
    java -cp "out:lib/*" inhabit.Main
else
    mkdir -p out
    javac -d out src/inhabit/*.java
    java -cp "out:lib/*" inhabit.Main
fi
