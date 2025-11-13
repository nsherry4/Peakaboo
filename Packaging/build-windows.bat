@echo off
set RELEASE_TYPE=%1
if "%RELEASE_TYPE%"=="" set RELEASE_TYPE=release

jpackage.exe @version @platform/common/args @platform/common/args-%RELEASE_TYPE% @platform/windows/args @platform/windows/args-%RELEASE_TYPE%
