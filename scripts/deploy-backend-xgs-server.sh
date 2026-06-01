#!/bin/bash
set -euo pipefail

STAMP="${1:?stamp required}"
REMOTE_JAR="/www/wwwroot/rms/backend/jeecg-system-start-3.9.1.jar"
TMP_JAR="$HOME/rms_backend_build/jeecg-system-start-3.9.1.jar"
LOG="/www/wwwroot/rms/backend/logs/restart-${STAMP}.log"
JAVA="/www/server/java/jdk-17.0.18+8/bin/java"

sudo cp "${REMOTE_JAR}" "${REMOTE_JAR}.bak.${STAMP}"
sudo cp "${TMP_JAR}" "${REMOTE_JAR}"
sudo chown www:www "${REMOTE_JAR}"
sudo mkdir -p /www/wwwroot/rms/backend/logs

PID="$(pgrep -f 'jeecg-system-start-3.9.1.jar' || true)"
if [ -n "${PID}" ]; then
  echo "Stopping PID ${PID}..."
  sudo kill "${PID}"
  sleep 5
fi

sudo bash -c "
set -a
source /www/wwwroot/rms/backend/rms.env
set +a
cd /www/wwwroot/rms/backend
nohup runuser -u www -m -- ${JAVA} -jar -Xmx1024M -Xms256M jeecg-system-start-3.9.1.jar \
  --server.address=127.0.0.1 --server.port=8080 \
  >> ${LOG} 2>&1 &
"

for i in $(seq 1 30); do
  if curl -sf "http://127.0.0.1:8080/xgszp-boot/sys/randomImage/${STAMP}" | grep -q '"success":true'; then
    echo "health OK"
    exit 0
  fi
  sleep 2
done

echo "health check failed — tail ${LOG}:"
tail -50 "${LOG}" || sudo tail -50 "${LOG}"
exit 1
