@echo off
cd %~dp0
cd src
@echo on
javac -encoding utf8 -d ..\build server\MyServer.java
@echo off
cd ..\build
@echo on
java server.MyServer