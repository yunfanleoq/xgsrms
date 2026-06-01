# Frontend-only finish (when backend already OK). Run: ssh -t xgs-server "bash ~/finish-rms-frontend.sh"
$ErrorActionPreference = 'Stop'
$remoteScript = @'
#!/bin/bash
set -euo pipefail
FRONT_SRC="$HOME/rms_frontend_build"
FRONT_DEST="/www/wwwroot/rms/frontend/dist"
if [ ! -f "${FRONT_SRC}/index.html" ]; then echo "Missing ${FRONT_SRC}/index.html"; exit 1; fi
sudo rsync -a --delete "${FRONT_SRC}/" "${FRONT_DEST}/"
sudo chown -R www:www "${FRONT_DEST}"
rm -rf "${FRONT_SRC}"
echo "Frontend deployed to ${FRONT_DEST}"
'@

$local = Join-Path $PSScriptRoot 'finish-rms-frontend.sh'
Set-Content -Path $local -Value $remoteScript -NoNewline -Encoding utf8NoBOM
scp $local xgs-server:~/
ssh xgs-server "sed -i 's/\r$//' ~/finish-rms-frontend.sh; chmod +x ~/finish-rms-frontend.sh"
Write-Host 'Uploaded ~/finish-rms-frontend.sh — run: ssh -t xgs-server "bash ~/finish-rms-frontend.sh"'
