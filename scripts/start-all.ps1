[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

try {
    $projectRoot = Split-Path -Parent $PSScriptRoot
    $backendScript = Join-Path $PSScriptRoot 'start-backend.ps1'
    $frontendScript = Join-Path $PSScriptRoot 'start-frontend.ps1'
    if (-not (Test-Path -LiteralPath $backendScript)) { throw '找不到 start-backend.ps1。' }
    if (-not (Test-Path -LiteralPath $frontendScript)) { throw '找不到 start-frontend.ps1。' }

    $backendPort = '8080'
    $envPath = Join-Path $projectRoot '.env'
    if (Test-Path -LiteralPath $envPath) {
        $portLine = Get-Content -LiteralPath $envPath -Encoding UTF8 | Where-Object { $_ -match '^\s*SERVER_PORT\s*=' } | Select-Object -First 1
        if ($portLine) { $backendPort = ($portLine.Split('=', 2)[1]).Trim() }
    }
    $frontendPort = '5173'
    $viteConfig = Get-ChildItem -LiteralPath $projectRoot -Directory | ForEach-Object { Join-Path $_.FullName 'vite.config.js' } | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1
    if ($viteConfig) {
        $match = [regex]::Match((Get-Content -LiteralPath $viteConfig -Raw -Encoding UTF8), 'port\s*:\s*(\d+)')
        if ($match.Success) { $frontendPort = $match.Groups[1].Value }
    }

    Start-Process -FilePath 'powershell.exe' -WorkingDirectory $projectRoot -ArgumentList @('-NoExit','-ExecutionPolicy','Bypass','-File',"`"$backendScript`"")
    Start-Process -FilePath 'powershell.exe' -WorkingDirectory $projectRoot -ArgumentList @('-NoExit','-ExecutionPolicy','Bypass','-File',"`"$frontendScript`"")

    Write-Host ''
    Write-Host '================================================' -ForegroundColor DarkGray
    Write-Host '  已開啟前端與後端視窗，請稍待幾秒讓伺服器啟動。' -ForegroundColor Green
    Write-Host '------------------------------------------------' -ForegroundColor DarkGray
    Write-Host "  前端    http://localhost:$frontendPort"
    Write-Host "  後端    http://localhost:$backendPort"
    Write-Host "  Swagger http://localhost:$backendPort/swagger-ui/index.html"
    Write-Host '================================================' -ForegroundColor DarkGray
    Write-Host ''
    Write-Host '等待前端啟動後自動開啟瀏覽器（約 15 秒）...' -ForegroundColor Yellow
    Start-Sleep -Seconds 15
    Start-Process "http://localhost:$frontendPort"
} catch {
    Write-Error "整合啟動失敗：$($_.Exception.Message)"
    exit 1
}
