@echo off
echo ==========================================
echo      xtUML Interactive Console
echo ==========================================

:: Ensure compiled files exist
if not exist bin\com\xtuml\generated\InteractiveApp.class (
    echo [ERROR] Compiled classes not found. Please run run_compiler.bat first.
    pause
    exit /b 1
)

:: Run the app
java -cp "bin;lib/*" com.xtuml.generated.InteractiveApp
pause
