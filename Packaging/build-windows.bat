@echo off
set RELEASE_TYPE=%1
if "%RELEASE_TYPE%"=="" set RELEASE_TYPE=release

rem unsigned local build; release.yml runs the two stages separately so it can sign in between
call build-windows-image.bat %RELEASE_TYPE%
if errorlevel 1 exit /b %errorlevel%
call build-windows-msi.bat %RELEASE_TYPE%
