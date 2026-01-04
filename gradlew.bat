@echo off
:: Gradle wrapper script for Windows
set DIRNAME=%~dp0
set APP_HOME=%DIRNAME:~0,-1%
set WRAPPER_JAR=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
if not exist "%WRAPPER_JAR%" (
  echo Gradle wrapper JAR not found: %WRAPPER_JAR%
  echo If you have Gradle installed, run: gradle wrapper
  echo Alternatively, build from Android Studio which will generate the wrapper.
)

java -jar "%WRAPPER_JAR%" %*
