@echo off
echo ==========================================
echo      xtUML Compiler - Library Setup
echo ==========================================

if not exist lib (
    echo Creating 'lib' folder...
    mkdir lib
)

echo [1/3] Downloading jackson-core-2.15.2.jar...
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar -OutFile lib\jackson-core-2.15.2.jar"

echo [2/3] Downloading jackson-annotations-2.15.2.jar...
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar -OutFile lib\jackson-annotations-2.15.2.jar"

echo [3/3] Downloading jackson-databind-2.15.2.jar...
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar -OutFile lib\jackson-databind-2.15.2.jar"

echo.
echo ==========================================
echo Libraries downloaded successfully.
echo You can now run 'run_compiler.bat'.
echo ==========================================
pause
