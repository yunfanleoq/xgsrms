# 升级前逻辑备份（需本机已安装 mysqldump，且在 PATH 中）
# 用法示例（参数名避免使用 Host，因与 PowerShell 内置 $Host 冲突）：
#   powershell -ExecutionPolicy Bypass -File .\scripts\backup-mysql-before-upgrade.ps1 -DbHost 127.0.0.1 -Port 3306 -User root -Database jeecg-boot
# 密码通过环境变量 MYSQL_PWD 传入（避免出现在命令行历史），例如：
#   $env:MYSQL_PWD = 'your-secret'; powershell -ExecutionPolicy Bypass -File .\scripts\backup-mysql-before-upgrade.ps1 -DbHost 127.0.0.1 -User root -Database jeecg-boot

param(
    [string] $DbHost = "127.0.0.1",
    [int] $Port = 3306,
    [Parameter(Mandatory = $true)][string] $User,
    [Parameter(Mandatory = $true)][string] $Database,
    [string] $OutDir = "."
)

$ErrorActionPreference = "Stop"
$ts = Get-Date -Format "yyyyMMdd-HHmmss"
$outFile = Join-Path $OutDir "mysql-backup-$Database-$ts.sql"

if (-not $env:MYSQL_PWD) {
    Write-Warning "未设置环境变量 MYSQL_PWD，mysqldump 可能提示输入密码。"
}

$mysqldump = Get-Command mysqldump -ErrorAction SilentlyContinue
if (-not $mysqldump) {
    throw "未找到 mysqldump，请安装 MySQL 客户端并加入 PATH。"
}

& mysqldump -h $DbHost -P $Port -u $User --single-transaction --routines --triggers --events $Database -r $outFile
if ($LASTEXITCODE -ne 0) { throw "mysqldump 失败，退出码 $LASTEXITCODE" }

Write-Host "已备份到: $outFile"
