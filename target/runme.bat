@echo off
color 0A
cls
java -jar spicybot-1.0-jar-with-dependencies.jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
pause