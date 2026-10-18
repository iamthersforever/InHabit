@echo off
echo Compiling InHabit application...
if not exist out mkdir out
javac -d out src\inhabit\*.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)
echo Starting InHabit...
java -cp "out;lib\*" inhabit.Main
pause
