@echo off
chcp 65001 > nul
cd /d "%~dp0"

echo ============================================
echo   二手手機平台  快速啟動
echo ============================================

if not exist ".env" (
    echo.
    echo [警告] 找不到 .env，第一次使用請先執行 setup：
    echo.
    echo   powershell -ExecutionPolicy Bypass -File scripts\setup.ps1
    echo.
    pause
    exit /b 1
)

echo 正在開啟前端與後端視窗...
call scripts\start-all.bat
