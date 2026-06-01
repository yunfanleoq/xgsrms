#!/bin/bash
# 将 ~/rms_backend_build/jeecg-system-start-3.9.1.jar 安装到生产并重启（需 sudo 或 JAR 目录可写）
set -eu
STAMP=$(date +%Y%m%d-%H%M%S)
TMP_JAR="/home/xgs/rms_backend_build/jeecg-system-start-3.9.1.jar"
REMOTE_JAR="/www/wwwroot/rms/backend/jeecg-system-start-3.9.1.jar"
RESTART="/home/xgs/restart-rms-backend.sh"

if [ ! -f "$TMP_JAR" ]; then
  echo "ERROR: missing $TMP_JAR — 请先在本地运行 deploy-backend-xgs-server.ps1 上传 JAR"
  exit 1
fi

cp "$REMOTE_JAR" "${REMOTE_JAR}.bak.${STAMP}" 2>/dev/null || true
cp "$TMP_JAR" "$REMOTE_JAR"
chown www:www "$REMOTE_JAR" 2>/dev/null || true
echo "Installed $(ls -lh "$REMOTE_JAR" | awk '{print $5, $9}')"
bash "$RESTART"
