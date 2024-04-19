@echo off
set "folderPath=%~dp0"

rem Check if folder path is already in PATH
echo %PATH% | findstr /C:"%folderPath%;" >nul
if %errorlevel% equ 0 (
    echo Folder path is already in PATH.
) else (
    echo Adding folder path to PATH...
    set "PATH=%folderPath%;%PATH%"
    echo Folder path has been added to PATH.
)

rem Reload the PATH variable
for %%I in ("%folderPath%") do (
    setx PATH "%%~I;%PATH%" >nul
)

echo Current PATH: %PATH%
