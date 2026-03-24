<#
.SYNOPSIS
  安全加固项自检：环境变量与配置（与 SECURITY-REMEDIATION.md 配套）

.DESCRIPTION
  在部署机或 CI 上运行，检查生产相关环境变量是否已设置、长度是否达标。
  不读取 .env 文件内容中的密钥，仅提示需人工确认项。

.EXAMPLE
  powershell -ExecutionPolicy Bypass -File .\scripts\security-remediation-verify.ps1
  powershell -ExecutionPolicy Bypass -File .\scripts\security-remediation-verify.ps1 -Strict
#>

param(
    [switch] $Strict
)

$ErrorActionPreference = 'Continue'
$failed = 0

function Test-Env {
    param([string]$Name, [int]$MinLen = 0, [string]$Hint = '')
    $v = [Environment]::GetEnvironmentVariable($Name, 'Machine')
    if ([string]::IsNullOrEmpty($v)) { $v = [Environment]::GetEnvironmentVariable($Name, 'User') }
    if ([string]::IsNullOrEmpty($v)) { $v = [Environment]::GetEnvironmentVariable($Name, 'Process') }

    if ([string]::IsNullOrWhiteSpace($v)) {
        Write-Host "[FAIL] $Name 未设置。$Hint" -ForegroundColor Red
        script:failed++
        return
    }
    if ($MinLen -gt 0 -and $v.Length -lt $MinLen) {
        Write-Host "[WARN] $Name 长度过短（建议至少 $MinLen 字符）。$Hint" -ForegroundColor Yellow
        if ($Strict) { script:failed++ }
        return
    }
    Write-Host "[ OK ] $Name 已设置" -ForegroundColor Green
}

Write-Host "`n=== Jeecg 生产安全环境变量检查 ===`n" -ForegroundColor Cyan

Test-Env -Name 'JWT_SECRET' -MinLen 32 -Hint '须为高强度随机串，与开发环境不得共用。'
Test-Env -Name 'JEECG_SIGNATURE_SECRET' -MinLen 16 -Hint '须与前端生产构建 VITE_GLOB_SIGNATURE_SECRET 一致。'
Test-Env -Name 'REDIS_PASSWORD' -MinLen 1 -Hint 'Redis 若对公网/混网暴露则必填；JEECG_SECURITY_REDIS_REQUIRE_PASSWORD=true 时会校验。'

Write-Host "`n--- 可选：CORS / Token 策略（未设置则使用 application-prod 默认）---`n" -ForegroundColor Cyan
$cors = [Environment]::GetEnvironmentVariable('CORS_ALLOWED_ORIGINS', 'Process')
if ([string]::IsNullOrWhiteSpace($cors)) {
    Write-Host "[INFO] CORS_ALLOWED_ORIGINS 未设置：后端将使用“反射 Origin”旧行为；生产建议配置为前端站点 Origin 列表（逗号分隔）。" -ForegroundColor Yellow
} else {
    Write-Host "[ OK ] CORS_ALLOWED_ORIGINS 已设置" -ForegroundColor Green
}

$tok = [Environment]::GetEnvironmentVariable('JEECG_SECURITY_ALLOW_TOKEN_IN_QUERY', 'Process')
if ([string]::IsNullOrWhiteSpace($tok)) {
    Write-Host "[INFO] JEECG_SECURITY_ALLOW_TOKEN_IN_QUERY 未设置：prod 默认 false（禁止 URL 传 token）。" -ForegroundColor Gray
}

Write-Host "`n=== 结果 ===`n" -ForegroundColor Cyan
if ($failed -eq 0) {
    Write-Host "检查完成（Strict=$Strict）。请仍人工确认：Nginx HTTPS、knife4j 关闭、文件上传目录权限、数据库账号最小权限等。" -ForegroundColor Green
    exit 0
} else {
    Write-Host "存在 $($failed) 项未通过，请修复后重试。" -ForegroundColor Red
    exit 1
}
