# 部署 dist 到 xgs-server:/www/wwwroot/rms/frontend
# 用法：在 jeecgboot-vue3 目录执行  .\scripts\deploy-frontend-xgs-server.ps1
# 依赖：SSH Host xgs-server；远端 xgs 用户有 sudo（用于写入 www 目录）

$ErrorActionPreference = "Stop"
$jeecgRoot = Split-Path $PSScriptRoot -Parent
Set-Location $jeecgRoot

if (-not (Test-Path "dist\index.html")) {
    Write-Host "dist 不存在，正在执行 pnpm run build ..."
    pnpm run build
}

$remoteTmp = "~/rms_frontend_build"
$remoteDest = "/www/wwwroot/rms/frontend"

Write-Host "上传到 xgs-server:$remoteTmp ..."
scp -r dist/. "xgs-server:${remoteTmp}/"

Write-Host "sudo 同步到 $remoteDest ..."
ssh xgs-server "sudo rsync -a --delete ${remoteTmp}/ ${remoteDest}/ && sudo chown -R www:www ${remoteDest} && rm -rf ${remoteTmp}"

Write-Host "部署完成。"
