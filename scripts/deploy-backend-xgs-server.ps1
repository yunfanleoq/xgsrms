# Deploy jeecg-system-start JAR to xgs-server and restart (loads rms.env).
# Usage: powershell -ExecutionPolicy Bypass -File .\scripts\deploy-backend-xgs-server.ps1

$ErrorActionPreference = 'Stop'
$root = Resolve-Path (Join-Path $PSScriptRoot '..')
$jarDir = Join-Path $root 'jeecg-boot\jeecg-module-system\jeecg-system-start\target'
$jar = Get-ChildItem (Join-Path $jarDir 'jeecg-system-start-*.jar') | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $jar) { throw "JAR not found under $jarDir — run build-release.ps1 first." }

$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$remoteTmp = '~/rms_backend_build'

Write-Host "Uploading $($jar.Name) ($([math]::Round($jar.Length/1MB, 1)) MB)..."
ssh xgs-server "mkdir -p ${remoteTmp}"
scp $jar.FullName "xgs-server:${remoteTmp}/jeecg-system-start-3.9.1.jar"
scp (Join-Path $PSScriptRoot 'deploy-backend-xgs-server.sh') "xgs-server:${remoteTmp}/deploy.sh"
if ($LASTEXITCODE -ne 0) { throw "scp failed (exit $LASTEXITCODE)" }

scp (Join-Path $PSScriptRoot 'finish-rms-deploy.sh') "xgs-server:~/finish-rms-deploy.sh"
if ($LASTEXITCODE -ne 0) { throw "scp finish script failed (exit $LASTEXITCODE)" }

Write-Host 'Installing JAR and restarting backend...'
ssh xgs-server "sed -i 's/\r$//' ${remoteTmp}/deploy.sh; chmod +x ${remoteTmp}/deploy.sh; bash ${remoteTmp}/deploy.sh ${stamp}"
if ($LASTEXITCODE -ne 0) { throw "backend deploy failed (exit $LASTEXITCODE)" }

Write-Host 'Done. Backend deployed to /www/wwwroot/rms/backend/jeecg-system-start-3.9.1.jar'
