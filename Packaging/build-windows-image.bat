@echo off
set RELEASE_TYPE=%1
if "%RELEASE_TYPE%"=="" set RELEASE_TYPE=release

rem jpackage won't overwrite an existing app image
if exist image rmdir /s /q image

rem the launchers in this image get signed before build-windows-msi.bat packages it
jpackage.exe @version @platform/common/args @platform/common/args-meta @platform/common/args-%RELEASE_TYPE% @platform/windows/args-%RELEASE_TYPE% --type app-image --dest image
