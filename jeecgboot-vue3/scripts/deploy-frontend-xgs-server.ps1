# Deploy dist to xgs-server:/www/wwwroot/rms/frontend/dist (nginx root)
# Usage: from jeecgboot-vue3: .\scripts\deploy-frontend-xgs-server.ps1
# Needs: SSH host "xgs-server" and remote sudo to write www

$ErrorActionPreference = "Stop"
$jeecgRoot = Split-Path $PSScriptRoot -Parent
Set-Location $jeecgRoot

if (-not (Test-Path "dist\index.html")) {
    Write-Host "dist not found, running pnpm run build..."
    pnpm run build
}

$remoteTmp = "~/rms_frontend_build"
$remoteDest = "/www/wwwroot/rms/frontend/dist"

Write-Host "Uploading to xgs-server:$remoteTmp ..."
ssh xgs-server "mkdir -p ${remoteTmp}"
scp -r dist/. "xgs-server:${remoteTmp}/"
if ($LASTEXITCODE -ne 0) { throw "scp failed (exit $LASTEXITCODE)" }

$finishScript = Resolve-Path (Join-Path $PSScriptRoot '..\..\scripts\finish-rms-deploy.sh')
$stopScript = Resolve-Path (Join-Path $PSScriptRoot '..\..\scripts\stop-rms-backend.sh')
scp $finishScript "xgs-server:~/finish-rms-deploy.sh"
scp $stopScript "xgs-server:~/stop-rms-backend.sh"
if ($LASTEXITCODE -ne 0) { throw "scp finish/stop script failed (exit $LASTEXITCODE)" }
ssh xgs-server "sed -i 's/\r$//' ~/finish-rms-deploy.sh ~/stop-rms-backend.sh"

Write-Host "rsync to $remoteDest (sudo) ..."
ssh xgs-server "sed -i 's/\r$//' ~/finish-rms-deploy.sh ~/stop-rms-backend.sh; bash ~/finish-rms-deploy.sh"
if ($LASTEXITCODE -ne 0) { throw "ssh/finish-rms-deploy failed (exit $LASTEXITCODE)" }

Write-Host "Done. Frontend deployed."
