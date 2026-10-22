@echo off
setlocal
cd /d "%~dp0"
title InHabit FCC - One-click start

echo ============================================
echo         InHabit FCC - One-click start
echo ============================================
where node >nul 2>nul || (echo ERROR: Install Node.js 22.12+ first. & goto :fail)
where npm  >nul 2>nul || (echo ERROR: npm was not found. Reinstall Node.js 22.12+. & goto :fail)
node -e "const [a,b]=process.versions.node.split('.').map(Number);process.exit(a>22||(a===22&&b>=12)?0:1)"
if errorlevel 1 (echo ERROR: Node.js 22.12 or newer is required. Detected: & node -v & goto :fail)

node scripts\prepare-local.mjs
if errorlevel 1 (echo ERROR: Could not prepare local configuration. & goto :fail)
if not exist "node_modules\.bin\vite.cmd" (
  echo.
  echo Installing project dependencies (first run only; internet required)...
  call npm install --no-audit --no-fund
  if errorlevel 1 (echo ERROR: npm install failed. Check internet connection. & goto :fail)
)

echo.
echo Preparing demo accounts and sample records in MongoDB...
call npm run seed
if errorlevel 1 (
  echo.
  echo MongoDB setup failed. The app needs a working MongoDB connection.
  echo 1^) Start your local MongoDB service, OR
  echo 2^) Edit server\.env and set MONGODB_URI to your MongoDB Atlas URI.
  echo Then double-click this launcher again. No database was intentionally dropped.
  goto :fail
)

echo.
echo Starting API + React frontend. Browser will open at http://localhost:5173
start "" /b cmd /c "timeout /t 4 /nobreak ^>nul ^& start http://localhost:5173"
call npm run dev
 echo.
echo Stopped. Close this window when finished.
pause
goto :eof

:fail
echo.
echo See README.md for setup instructions.
pause
exit /b 1
