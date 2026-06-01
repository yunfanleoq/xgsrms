#!/bin/bash
# 重启后端：先彻底停进程，再 source rms.env 启动
# 用法：sudo bash /home/xgs/restart-rms-backend.sh  （或 xgs 用户 bash ~/restart-rms-backend.sh）
set -eu

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
STOP_SCRIPT="${SCRIPT_DIR}/stop-rms-backend.sh"
STAMP="$(date +%Y%m%d-%H%M%S)"
LOG="/www/wwwroot/rms/backend/logs/restart-${STAMP}.log"
JAVA="/www/server/java/jdk-17.0.18+8/bin/java"
JAR="/www/wwwroot/rms/backend/jeecg-system-start-3.9.1.jar"
ENV="/www/wwwroot/rms/backend/rms.env"

if [ ! -f "${STOP_SCRIPT}" ]; then
  echo "ERROR: missing ${STOP_SCRIPT}"
  exit 1
fi

bash "${STOP_SCRIPT}"

echo "==> Starting backend with rms.env..."
mkdir -p /www/wwwroot/rms/backend/logs
set -a
# shellcheck source=/dev/null
source "${ENV}"
set +a
cd /www/wwwroot/rms/backend
nohup runuser -u www -m -- "${JAVA}" -jar -Xmx1024M -Xms256M "${JAR}" \
  --server.address=127.0.0.1 --server.port=8080 \
  >> "${LOG}" 2>&1 &

echo "==> Waiting for health..."
i=1
while [ "$i" -le 30 ]; do
  if curl -sf "http://127.0.0.1:8080/xgszp-boot/sys/randomImage/${STAMP}" | grep -q success; then
    echo "OK: backend up, log: ${LOG}"
    pgrep -af jeecg-system-start-3.9.1.jar || true
    exit 0
  fi
  i=$((i + 1))
  sleep 2
done

echo "FAIL: health check timeout, tail log:"
tail -30 "${LOG}" 2>/dev/null || true
exit 1
