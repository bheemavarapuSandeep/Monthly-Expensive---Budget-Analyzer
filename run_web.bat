@echo off
echo ===================================================
echo   Starting Monthly Expense Analyzer (Web Server)
echo ===================================================
javac Server.java
start http://localhost:8080
java Server
pause
