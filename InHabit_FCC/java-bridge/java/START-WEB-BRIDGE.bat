@echo off
setlocal
cd /d "%~dp0"
title InHabit Java Web Bridge
where javac >nul 2>nul || (echo ERROR: Install a JDK 17 or newer and add its bin folder to PATH. & pause & exit /b 1)
if not exist out-web mkdir out-web
if not exist lib mkdir lib
if not exist "out-web\inhabit\WebBridge.class" (
  echo Compiling the InHabit web bridge and existing DAO/model classes...
  javac -encoding UTF-8 -sourcepath src -d out-web src\inhabit\WebBridge.java
  if errorlevel 1 (echo Compilation failed. & pause & exit /b 1)
)
echo Starting Java HTTP bridge at http://localhost:8090
java -cp "out-web;lib/*" inhabit.WebBridge
if errorlevel 1 pause
