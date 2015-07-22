@echo off
SETLOCAL

:: ~dp0 gets the directory of the script
:: .. is one directory up (moves from \bin up)
SET TMPPATH=%~dp0..

:: Check if the first argument is debug flag
if "%1" == "-debug" (
    set DEBUG=-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y
    shift
)

:: Put all the other arguments into the PARAMS variable
:loop
if [%1]==[] goto afterloop
set PARAMS=%PARAMS% %1
shift
goto loop
:afterloop


:: Resolve relative path by changing directory
:: into relative path, getting the path with CD
:: and changing back to the previous directory.
PUSHD %TMPPATH%
SET BUILDTOOL_HOME=%CD%
POPD

SET BIN=%BUILDTOOL_HOME%\bin
SET LIB=%BUILDTOOL_HOME%\lib

SET CLASSPATH=%BIN%\*;%LIB%\*;


"%JAVA_HOME%\bin\java" -Xmx1g -classpath "%CLASSPATH%" %DEBUG% ch.liquidmind.deflector.Main %PARAMS%