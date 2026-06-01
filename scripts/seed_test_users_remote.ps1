# 上传并在 xgs-server 执行测试账号预置 SQL
# 用法（仓库根目录，需能 ssh xgs-server）:
#   powershell -ExecutionPolicy Bypass -File scripts\seed_test_users_remote.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

Write-Host "==> 1/3 生成 SQL"
py -3 scripts\seed_test_users.py -o scripts\sql\mysql\xgs_seed_test_users.generated.sql

$sql = Join-Path $Root "scripts\sql\mysql\xgs_seed_test_users.generated.sql"
$sh  = Join-Path $Root "scripts\sql\mysql\run_seed_on_server.sh"

Write-Host "==> 2/3 上传到 xgs-server"
scp $sql xgs-server:/tmp/xgs_seed_test_users.sql
scp $sh  xgs-server:/tmp/run_seed_on_server.sh

Write-Host "==> 3/3 执行预置"
ssh xgs-server "sed -i 's/\r$//' /tmp/run_seed_on_server.sh ; bash /tmp/run_seed_on_server.sh"

Write-Host "Done."
