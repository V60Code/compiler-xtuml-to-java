@echo off
echo ==========================================
echo      xtUML Generated System - Runner
echo ==========================================

:: Only Run (Assumes 'bin' exists from run_compiler)
if not exist "bin" (
    echo [ERROR] 'bin' folder not found. Please run 'run_compiler.bat' first.
    pause
    exit /b
)

java -cp "bin;lib/*" RunSystem
pause
