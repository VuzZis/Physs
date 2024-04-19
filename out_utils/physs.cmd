@echo off
set "batchPath=%~dp0"
set "folderPath=%batchPath:~0,-1%"
set "jarPath=%folderPath%\Physs.jar"
java -jar "%jarPath%" %*