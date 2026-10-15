@echo off
echo ==================================================
echo   Pushing Monthly Expense Analyzer to GitHub
echo ==================================================
echo.
echo Target Repository:
echo https://github.com/bheemavarapuSandeep/Monthly-Expensive---Budget-Analyzer
echo.
git push -u origin main
echo.
if %errorlevel% equ 0 (
    echo [SUCCESS] Code successfully pushed to GitHub!
) else (
    echo [ERROR] Push encountered an issue. Please check your GitHub login or permissions.
)
echo.
pause
