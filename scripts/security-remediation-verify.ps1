<#
.SYNOPSIS
  Verify security remediation environment variables.

.EXAMPLE
  powershell -ExecutionPolicy Bypass -File .\scripts\security-remediation-verify.ps1
  powershell -ExecutionPolicy Bypass -File .\scripts\security-remediation-verify.ps1 -Strict
  powershell -ExecutionPolicy Bypass -File .\scripts\security-remediation-verify.ps1 -EnvFile .\local-dev.env
#>

param(
    [switch] $Strict,
    [string] $EnvFile = ''
)

$ErrorActionPreference = 'Continue'
$failed = 0

if (-not [string]::IsNullOrWhiteSpace($EnvFile)) {
    if (-not (Test-Path $EnvFile)) {
        Write-Host "FAIL: EnvFile not found: $EnvFile" -ForegroundColor Red
        exit 1
    }
    foreach ($line in Get-Content $EnvFile) {
        $t = $line.Trim()
        if ($t -eq '' -or $t.StartsWith('#')) { continue }
        if ($t -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)\s*$') {
            $name = $matches[1]
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, 'Process')
        }
    }
    Write-Host "Loaded env file: $EnvFile" -ForegroundColor Cyan
}

function Test-Env {
    param([string]$Name, [int]$MinLen = 0, [string]$Hint = '')
    $v = [Environment]::GetEnvironmentVariable($Name, 'Machine')
    if ([string]::IsNullOrEmpty($v)) { $v = [Environment]::GetEnvironmentVariable($Name, 'User') }
    if ([string]::IsNullOrEmpty($v)) { $v = [Environment]::GetEnvironmentVariable($Name, 'Process') }

    if ([string]::IsNullOrWhiteSpace($v)) {
        Write-Host "FAIL: $Name is not set. $Hint" -ForegroundColor Red
        $script:failed++
        return
    }
    if ($MinLen -gt 0 -and $v.Length -lt $MinLen) {
        Write-Host "WARN: $Name is shorter than recommended minimum $MinLen. $Hint" -ForegroundColor Yellow
        if ($Strict) { $script:failed++ }
        return
    }
    Write-Host "OK: $Name is set" -ForegroundColor Green
}

Write-Host "`n=== Jeecg production security environment check ===`n" -ForegroundColor Cyan

Test-Env -Name 'JWT_SECRET' -MinLen 32 -Hint 'Use a strong random value and do not share it with dev.'
Test-Env -Name 'JEECG_SIGNATURE_SECRET' -MinLen 16 -Hint 'Must match frontend VITE_GLOB_SIGNATURE_SECRET.'
Test-Env -Name 'REDIS_PASSWORD' -MinLen 1 -Hint 'Required when JEECG_SECURITY_REDIS_REQUIRE_PASSWORD=true.'

Write-Host "`n--- Optional CORS / token policy ---`n" -ForegroundColor Cyan
$cors = [Environment]::GetEnvironmentVariable('CORS_ALLOWED_ORIGINS', 'Process')
if ([string]::IsNullOrWhiteSpace($cors)) {
    Write-Host "INFO: CORS_ALLOWED_ORIGINS is not set. Configure frontend origins in production." -ForegroundColor Yellow
} else {
    Write-Host "OK: CORS_ALLOWED_ORIGINS is set" -ForegroundColor Green
}

$tok = [Environment]::GetEnvironmentVariable('JEECG_SECURITY_ALLOW_TOKEN_IN_QUERY', 'Process')
if ([string]::IsNullOrWhiteSpace($tok)) {
    Write-Host "INFO: JEECG_SECURITY_ALLOW_TOKEN_IN_QUERY is not set. Prod default is false." -ForegroundColor Gray
}

Write-Host "`n=== Result ===`n" -ForegroundColor Cyan
if ($failed -eq 0) {
    Write-Host "Check completed (Strict=$Strict). Also verify HTTPS, knife4j disabled, upload permissions, and DB least privilege manually." -ForegroundColor Green
    exit 0
} else {
    Write-Host "$($failed) required checks failed. Fix them and retry." -ForegroundColor Red
    exit 1
}
