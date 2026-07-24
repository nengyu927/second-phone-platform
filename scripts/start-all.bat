@echo off
chcp 65001 > nul
cd /d "%~dp0.."

echo ================================================
echo   二手手機平台  啟動中
echo ================================================
echo.

echo [1/2] 開啟後端 Spring Boot 視窗...
start "後端 Spring Boot :8080" powershell.exe -NoExit -ExecutionPolicy Bypass -File "%~dp0start-backend.ps1"

echo [2/2] 開啟前端 Vite 視窗...
start "前端 Vite :5173" powershell.exe -NoExit -ExecutionPolicy Bypass -File "%~dp0start-frontend.ps1"

echo.
echo ------------------------------------------------
echo   前端    http://localhost:5173
echo   後端    http://localhost:8080
echo   Swagger http://localhost:8080/swagger-ui/index.html
echo ------------------------------------------------
echo.
echo 等待前端啟動後自動開啟瀏覽器（約 15 秒）...
timeout /t 15 /nobreak > nul
start http://localhost:5173
echo.
pause
