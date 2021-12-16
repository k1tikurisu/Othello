@echo off
cd %~dp0
cd src
@echo on
javac -encoding utf8 -d ..\build client\MyClient.java
@echo off
cd ..\build
@echo on
java client.MyClient