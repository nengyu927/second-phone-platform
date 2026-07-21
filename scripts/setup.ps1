[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Find-ProjectDirectory([string]$Root, [string]$Marker) {
    $matches = @(Get-ChildItem -LiteralPath $Root -Directory | Where-Object {
        Test-Path -LiteralPath (Join-Path $_.FullName $Marker)
    })
    if ($matches.Count -ne 1) {
        throw "無法唯一辨識包含 $Marker 的專案資料夾，請檢查專案結構。"
    }
    return $matches[0].FullName
}

function Require-Command([string]$Name, [string]$DisplayName) {
    $command = Get-Command $Name -ErrorAction SilentlyContinue
    if (-not $command) {
        throw "找不到 $DisplayName，請先安裝並重新開啟 PowerShell。"
    }
    return $command.Source
}

try {
    $projectRoot = Split-Path -Parent $PSScriptRoot
    $frontendDir = Find-ProjectDirectory $projectRoot 'package.json'
    $backendDir = Find-ProjectDirectory $projectRoot 'pom.xml'

    $java = Require-Command 'java.exe' 'Java 21'
    Write-Host 'Java 版本：' -ForegroundColor Cyan
    $javaVersion = & $java --version
    $javaExitCode = $LASTEXITCODE
    $javaVersion | ForEach-Object { Write-Host $_ }
    if ($javaExitCode -ne 0) { throw '無法讀取 Java 版本。' }

    $node = Require-Command 'node.exe' 'Node.js'
    Write-Host "Node.js 版本：$(& $node --version)" -ForegroundColor Cyan

    $npm = Require-Command 'npm.cmd' 'npm'
    Write-Host "npm 版本：$(& $npm --version)" -ForegroundColor Cyan

    $envPath = Join-Path $projectRoot '.env'
    $envExamplePath = Join-Path $projectRoot '.env.example'
    if (-not (Test-Path -LiteralPath $envPath)) {
        if (-not (Test-Path -LiteralPath $envExamplePath)) {
            throw '找不到 .env.example，無法建立本機環境設定。'
        }
        $bytes = New-Object byte[] 48
        [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
        $jwtSecret = [Convert]::ToBase64String($bytes)
        $content = (Get-Content -LiteralPath $envExamplePath -Raw -Encoding UTF8).Replace('GENERATE_A_LOCAL_SECRET_DURING_SETUP', $jwtSecret)
        Set-Content -LiteralPath $envPath -Value $content -Encoding UTF8
        Write-Host '已建立本機 .env，並產生隨機 JWT 密鑰。請依需要填入本機資料庫與管理員密碼。' -ForegroundColor Yellow
    }

    Write-Host "安裝前端套件：$frontendDir" -ForegroundColor Cyan
    Push-Location $frontendDir
    try {
        if (Test-Path -LiteralPath (Join-Path $frontendDir 'package-lock.json')) {
            & $npm ci
        } else {
            & $npm install
        }
        if ($LASTEXITCODE -ne 0) { throw '前端套件安裝失敗。' }
    } finally { Pop-Location }

    Write-Host "安裝後端套件：$backendDir" -ForegroundColor Cyan
    Push-Location $backendDir
    try {
        if (Test-Path -LiteralPath (Join-Path $backendDir 'mvnw.cmd')) {
            & (Join-Path $backendDir 'mvnw.cmd') clean install -DskipTests
        } else {
            $maven = Require-Command 'mvn.cmd' 'Maven'
            & $maven clean install -DskipTests
        }
        if ($LASTEXITCODE -ne 0) { throw '後端 Maven 安裝失敗。' }
    } finally { Pop-Location }

    Write-Host '環境準備完成。下一步請執行：' -ForegroundColor Green
    Write-Host 'powershell -ExecutionPolicy Bypass -File .\scripts\start-all.ps1'
} catch {
    Write-Error "設定失敗：$($_.Exception.Message)"
    exit 1
}
