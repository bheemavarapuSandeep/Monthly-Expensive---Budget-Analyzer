@echo off
setlocal
set "PATH=C:\Users\mudhi\AppData\Local\Microsoft\WinGet\Packages\Git.MinGit_Microsoft.Winget.Source_8wekyb3d8bbwe\cmd;C:\Users\mudhi\AppData\Local\Microsoft\WinGet\Packages\Git.MinGit_Microsoft.Winget.Source_8wekyb3d8bbwe\ucrt64\bin;%PATH%"

echo ==================================================
echo   Pushing Monthly Expense Analyzer to GitHub
echo ==================================================
echo.
echo Target Repository:
echo https://github.com/bheemavarapuSandeep/Monthly-Expensive---Budget-Analyzer
echo.
echo Pushing commits to GitHub...
echo (If a browser window or login prompt opens, authorize it to complete)
echo.

git push -u origin main

echo.
if %errorlevel% equ 0 (
    echo ==================================================
    echo [SUCCESS] Code successfully pushed to GitHub!
    echo ==================================================
) else (
    echo ==================================================
    echo [ERROR] Push encountered an issue.
    echo ==================================================
)
echo.
pause
