echo off
SETLOCAL
rem Java development kit directory. Must be 1.8
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_241

rem Directory where project is.
set SOURCEBUILD=C:\Users\User1\Desktop\Project

cd %SOURCEBUILD%\build\nodesNetwork2\NotaryB
start cmd.exe /c "title NotaryB && java -jar corda.jar"
cd %SOURCEBUILD%\build\nodesNetwork2\PartyB
start cmd.exe /c "title PartyB && java -jar corda.jar"
cd %SOURCEBUILD%\build\nodesNetwork2\Oracle
start cmd.exe /c "title OracleB && java -jar corda.jar"
cd %SOURCEBUILD%\build\nodesNetwork1\NotaryA
start cmd.exe /c "title NotaryA && java -jar corda.jar"
cd %SOURCEBUILD%\build\nodesNetwork1\PartyA
start cmd.exe /c "title PartyA && java -jar corda.jar"
cd %SOURCEBUILD%\build\nodesNetwork1\Oracle
start cmd.exe /c "title OracleA && java -jar corda.jar"
cd %SOURCEBUILD%
