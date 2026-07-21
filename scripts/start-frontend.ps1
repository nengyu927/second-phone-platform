[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Find-ProjectDirectory([string]$Root, [string]$Marker) {
    $matches = @(Get-ChildItem -LiteralPath $Root -Directory | Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName $Marker) })
    if ($matches.Count -ne 1) { throw "無法唯一辨識包含 $Marker 的前端資料夾。" }
    return $matches[0].FullName
}

try {
    $projectRoot = Split-Path -Parent $PSScriptRoot
    $frontendDir = Find-ProjectDirectory $projectRoot 'package.json'
    $node = Get-Command 'node.exe' -ErrorAction SilentlyContinue
    $npm = Get-Command 'npm.cmd' -ErrorAction SilentlyContinue
    if (-not $node) { throw '找不到 Node.js，請先安裝 Node.js 20 以上版本。' }
    if (-not $npm) { throw '找不到 npm，請重新安裝 Node.js。' }

    $viteConfig = Join-Path $frontendDir 'vite.config.js'
    $port = '5173'
    if (Test-Path -LiteralPath $viteConfig) {
        $match = [regex]::Match((Get-Content -LiteralPath $viteConfig -Raw -Encoding UTF8), 'port\s*:\s*(\d+)')
        if ($match.Success) { $port = $match.Groups[1].Value }
    }

    Push-Location $frontendDir
    try {
        if (-not (Test-Path -LiteralPath (Join-Path $frontendDir 'node_modules'))) {
            Write-Host '尚未安裝前端套件，現在開始安裝。' -ForegroundColor Yellow
            if (Test-Path -LiteralPath (Join-Path $frontendDir 'package-lock.json')) { & $npm.Source ci } else { & $npm.Source install }
            if ($LASTEXITCODE -ne 0) { throw '前端套件安裝失敗。' }
        }
        Write-Host "前端資料夾：$frontendDir" -ForegroundColor Cyan
        Write-Host "前端網址：http://localhost:$port" -ForegroundColor Green
        & $npm.Source run dev
        if ($LASTEXITCODE -ne 0) { throw "前端程序結束，錯誤代碼：$LASTEXITCODE" }
    } finally { Pop-Location }
} catch {
    Write-Error "前端啟動失敗：$($_.Exception.Message)"
    exit 1
}
