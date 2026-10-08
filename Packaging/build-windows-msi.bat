@echo off
set RELEASE_TYPE=%1
if "%RELEASE_TYPE%"=="" set RELEASE_TYPE=release

rem packages the image from build-windows-image.bat (--app-image is in args-installer-*). jpackage
rem copies it verbatim, so any signatures added to the launchers in between survive
jpackage.exe @version @platform/common/args-meta @platform/common/args-%RELEASE_TYPE% @platform/common/args-installer @platform/windows/args-%RELEASE_TYPE% @platform/windows/args-installer @platform/windows/args-installer-%RELEASE_TYPE% --type msi
