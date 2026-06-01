#!/bin/bash
set -euo pipefail
FRONT_SRC="$HOME/rms_frontend_build"
FRONT_DEST="/www/wwwroot/rms/frontend/dist"
if [ ! -f "${FRONT_SRC}/index.html" ]; then echo "Missing ${FRONT_SRC}/index.html"; exit 1; fi
sudo rsync -a --delete "${FRONT_SRC}/" "${FRONT_DEST}/"
sudo chown -R www:www "${FRONT_DEST}"
rm -rf "${FRONT_SRC}"
echo "Frontend deployed to ${FRONT_DEST}"
