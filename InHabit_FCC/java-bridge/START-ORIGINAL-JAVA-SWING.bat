@echo off
cd /d "%~dp0java"
where java >nul 2>nul || (echo Install a compatible Java runtime first. & pause & exit /b 1)
java -jar InHabit.jar
if errorlevel 1 pause
