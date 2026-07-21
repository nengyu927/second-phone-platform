[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Find-ProjectDirectory([string]$Root, [string]$Marker) {
    $matches = @(Get-ChildItem -LiteralPath $Root -Directory | Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName $Marker) })
    if ($matches.Count -ne 1) { throw "無法唯一辨識包含 $Marker 的後端資料夾。" }
    return $matches[0].FullName
}

function Import-LocalEnvironment([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path)) { throw '找不到本機 .env，請先執行 scripts\setup.ps1。' }
    foreach ($line in Get-Content -LiteralPath $Path -Encoding UTF8) {
        $valueLine = $line.Trim().TrimStart([char]0xFEFF)
        if (-not $valueLine -or $valueLine.StartsWith('#') -or -not $valueLine.Contains('=')) { continue }
        $parts = $valueLine.Split('=', 2)
        $key = $parts[0].Trim()
        $value = $parts[1].Trim().Trim('"').Trim("'")
        if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($key, 'Process'))) {
            [Environment]::SetEnvironmentVariable($key, $value, 'Process')
        }
    }
}

try {
    $projectRoot = Split-Path -Parent $PSScriptRoot
    $backendDir = Find-ProjectDirectory $projectRoot 'pom.xml'
    Import-LocalEnvironment (Join-Path $projectRoot '.env')
    $jwtSecret = [Environment]::GetEnvironmentVariable('APP_JWT_SECRET', 'Process')
    if ([string]::IsNullOrWhiteSpace($jwtSecret) -or $jwtSecret -eq 'GENERATE_A_LOCAL_SECRET_DURING_SETUP') {
        throw 'APP_JWT_SECRET 尚未設定，請先執行 scripts\setup.ps1。'
    }
    $configuredPort = [Environment]::GetEnvironmentVariable('SERVER_PORT', 'Process')
    $port = if ($configuredPort) { $configuredPort } else { '8080' }
    Write-Host "後端資料夾：$backendDir" -ForegroundColor Cyan
    Write-Host "後端網址：http://localhost:$port" -ForegroundColor Green
    Write-Host "Swagger：http://localhost:$port/swagger-ui/index.html" -ForegroundColor Green
    Push-Location $backendDir
    try {
        if (Test-Path -LiteralPath (Join-Path $backendDir 'mvnw.cmd')) {
            & (Join-Path $backendDir 'mvnw.cmd') spring-boot:run
        } else {
            $maven = Get-Command 'mvn.cmd' -ErrorAction SilentlyContinue
            if (-not $maven) { throw '找不到 Maven，且專案沒有 mvnw.cmd。請先安裝 Maven。' }
            & $maven.Source spring-boot:run
        }
        if ($LASTEXITCODE -ne 0) { throw "後端程序結束，錯誤代碼：$LASTEXITCODE" }
    } finally { Pop-Location }
} catch {
    Write-Error "後端啟動失敗：$($_.Exception.Message)"
    exit 1
}
