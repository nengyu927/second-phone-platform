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

    Write-Host '已分別開啟前端與後端 PowerShell 視窗。' -ForegroundColor Green
    Write-Host "前端：http://localhost:$frontendPort"
    Write-Host "後端：http://localhost:$backendPort"
    Write-Host "Swagger：http://localhost:$backendPort/swagger-ui/index.html"
} catch {
    Write-Error "整合啟動失敗：$($_.Exception.Message)"
    exit 1
}
