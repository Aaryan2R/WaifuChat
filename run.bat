@echo off
title WaifuChat

:: check if java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo.
    echo  ERROR: Java is not installed or not in PATH!
    echo  Download Java 17+ from: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

:: find the jar
set "JAR=%~dp0WaifuChat.jar"
if not exist "%JAR%" (
    set "JAR=%~dp0target\anime-assistant-1.0-SNAPSHOT.jar"
)
if not exist "%JAR%" (
    echo.
    echo  ERROR: Can't find the jar file!
    echo  Make sure WaifuChat.jar is in the same folder as this bat file.
    echo.
    pause
    exit /b 1
)

echo  Starting WaifuChat...
echo.
java -jar "%JAR%"

if errorlevel 1 (
    echo.
    echo  Something went wrong. Check if your config is set up properly.
    echo  Config file: %USERPROFILE%\.animeassistant\config.properties
    echo.
    pause
)
