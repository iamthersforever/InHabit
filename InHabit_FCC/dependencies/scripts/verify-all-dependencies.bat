@echo off
setlocal enabledelayedexpansion
echo ==========================================================
echo       InHabit System & Dependency Verification Suite      
echo ==========================================================
echo Checking dependencies across FCC, Jugaad, and Java versions...
echo.

set FAILED=0

where java >nul 2>&1
if %errorlevel% equ 0 (
  echo   [PASS] Java Runtime detected.
) else (
  echo   [FAIL] Java Runtime not found.
  set /a FAILED+=1
)

where javac >nul 2>&1
if %errorlevel% equ 0 (
  echo   [PASS] Java Compiler (javac) detected.
) else (
  echo   [FAIL] Java Compiler (javac) not found.
  set /a FAILED+=1
)

where node >nul 2>&1
if %errorlevel% equ 0 (
  echo   [PASS] Node.js detected.
) else (
  echo   [FAIL] Node.js not found.
  set /a FAILED+=1
)

where npm >nul 2>&1
if %errorlevel% equ 0 (
  echo   [PASS] npm detected.
) else (
  echo   [FAIL] npm not found.
  set /a FAILED+=1
)

if exist "%~dp0..\java\mysql-connector-j-26.7.0.jar" (
  echo   [PASS] MySQL JDBC Connector JAR present in Dependencies_InHabit.
) else (
  echo   [FAIL] MySQL JDBC Connector missing.
  set /a FAILED+=1
)

if exist "%~dp0..\node_modules" (
  echo   [PASS] Central node_modules installed in Dependencies_InHabit.
) else (
  echo   [FAIL] Central node_modules missing.
  set /a FAILED+=1
)

echo.
if %FAILED% equ 0 (
  echo SUCCESS: All dependencies verified!
) else (
  echo WARNING: %FAILED% checks failed.
)
pause
