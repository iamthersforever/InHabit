@echo off
setlocal
cd /d "%~dp0"
title InHabit FCC Jugaad - One-click start

echo ==============================================
echo        InHabit FCC Jugaad - One-click start
echo ==============================================
where java >nul 2>nul || (echo ERROR: Install a Java JDK 17 or newer first. & goto :fail)
where javac >nul 2>nul || (echo ERROR: javac was not found. Install a JDK 17 or newer and set PATH. & goto :fail)
where node >nul 2>nul || (echo ERROR: Install Node.js 22.12+ first. & goto :fail)
where npm >nul 2>nul || (echo ERROR: npm was not found. Reinstall Node.js 22.12+. & goto :fail)
node -e "const [a,b]=process.versions.node.split('.').map(Number);process.exit(a>22||(a===22&&b>=12)?0:1)"
if errorlevel 1 (echo ERROR: Node.js 22.12 or newer is required. Detected: & node -v & goto :fail)

if not exist "frontend\node_modules\.bin\vite.cmd" (
  echo.
  echo Installing React/Vite frontend dependencies (first run only; internet required)...
  pushd frontend
  call npm install --no-audit --no-fund
  if errorlevel 1 (popd & echo ERROR: npm install failed. & goto :fail)
  popd
)

if not exist "java\lib" mkdir "java\lib"
if not exist "java\out-web" mkdir "java\out-web"
echo.
echo Starting Java web bridge in a separate console...
start "InHabit Java Web Bridge" "%~dp0java\START-WEB-BRIDGE.bat"

echo.
echo Starting React frontend at http://localhost:5173
start "" /b cmd /c "timeout /t 4 /nobreak ^>nul ^& start http://localhost:5173"
pushd frontend
call npm run dev -- --host 127.0.0.1 --port 5173 --strictPort
popd
echo.
echo Frontend stopped. Close the separate Java Bridge window when finished.
pause
goto :eof

:fail
echo.
echo See README.md for setup details.
pause
exit /b 1
