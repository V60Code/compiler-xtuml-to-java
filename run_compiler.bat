@echo off
echo ==========================================
echo      xtUML Compiler - Build and Run
echo ==========================================

echo [1/4] Cleaning build directory...
if exist out rd /s /q out
mkdir out
if exist sources.txt del sources.txt

echo [2/4] Listing source files...
dir /s /B src\*.java > sources.txt

echo [3/4] Compiling...
:: Ensure you have the 'lib' folder with Jackson JARs
if not exist lib (
    echo [ERROR] 'lib' folder not found! Please run setup_libs.bat first to download dependencies.
    del sources.txt
    pause
    exit /b 1
)

javac -cp "lib/*" -d out @sources.txt
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    del sources.txt
    
    echo.
    echo hint: If errors are about Jackson, make sure you ran setup_libs.bat
    pause
    exit /b %errorlevel%
)
del sources.txt
echo Compilation successful.

echo [4/4] Running Compiler (Phase 1-7)...
echo ------------------------------------------
java -cp "out;lib/*" com.xtuml.compiler.Main
echo ------------------------------------------

echo.
echo [5/5] Compiling Generated Code (Phase 8)...
dir /s /b src\com\xtuml\runtime\*.java > sources_gen.txt
dir /s /b src-gen\com\xtuml\generated\*.java >> sources_gen.txt
if exist src-gen\RunSystem.java echo src-gen\RunSystem.java >> sources_gen.txt

javac -d bin -cp "lib/*;." @sources_gen.txt
if %errorlevel% neq 0 (
    echo [ERROR] Failed to compile generated code!
    del sources_gen.txt
    pause
    exit /b 1
)
del sources_gen.txt
echo Success! Generated code is ready in 'bin' folder.
echo You can now run 'run_simulation.bat' directly.

pause
