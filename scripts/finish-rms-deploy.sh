#!/bin/bash
# Run on xgs-server: bash ~/finish-rms-deploy.sh  （内部 sudo 仅用于写 www 目录）
# v4: 脚本路径与 xgs 家目录不依赖 $HOME（兼容 sudo 调用 stop/restart）
set -eu

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_USER="${SUDO_USER:-${USER:-xgs}}"
DEPLOY_HOME="$(getent passwd "${DEPLOY_USER}" 2>/dev/null | cut -d: -f6)"
DEPLOY_HOME="${DEPLOY_HOME:-/home/xgs}"
STOP_SCRIPT="${SCRIPT_DIR}/stop-rms-backend.sh"

STAMP="$(date +%Y%m%d-%H%M%S)"
FRONT_SRC="${DEPLOY_HOME}/rms_frontend_build"
FRONT_DEST="/www/wwwroot/rms/frontend/dist"
REMOTE_JAR="/www/wwwroot/rms/backend/jeecg-system-start-3.9.1.jar"
TMP_JAR="${DEPLOY_HOME}/rms_backend_build/jeecg-system-start-3.9.1.jar"
LOG="/www/wwwroot/rms/backend/logs/restart-${STAMP}.log"
JAVA="/www/server/java/jdk-17.0.18+8/bin/java"

wait_for_backend() {
  local i
  for i in $(seq 1 30); do
    if curl -sf "http://127.0.0.1:8080/xgszp-boot/sys/randomImage/${STAMP}" | grep -q '"success":true'; then
      echo "Backend health OK (randomImage)"
      return 0
    fi
    sleep 2
  done
  echo "Backend health failed — see ${LOG}"
  tail -50 "${LOG}" 2>/dev/null || sudo tail -50 "${LOG}"
  return 1
}

stop_backend() {
  if [ ! -f "${STOP_SCRIPT}" ]; then
    echo "ERROR: missing ${STOP_SCRIPT}"
    exit 1
  fi
  bash "${STOP_SCRIPT}"
}

echo "==> finish-rms-deploy.sh v4 | stamp ${STAMP} | deploy_home=${DEPLOY_HOME}"

DEPLOY_BACKEND=0
if [ "${DEPLOY_FRONTEND_ONLY:-0}" != "1" ] && [ -f "${TMP_JAR}" ]; then
  DEPLOY_BACKEND=1
fi

if [ "${DEPLOY_BACKEND}" -eq 1 ]; then
  echo "==> Backend: install JAR"
  cp "${REMOTE_JAR}" "${REMOTE_JAR}.bak.${STAMP}" 2>/dev/null || sudo cp "${REMOTE_JAR}" "${REMOTE_JAR}.bak.${STAMP}"
  sudo cp "${TMP_JAR}" "${REMOTE_JAR}"
  sudo chown www:www "${REMOTE_JAR}"
  stop_backend
  echo "==> Starting backend via restart-rms-backend.sh"
  if [ "$(id -u)" -eq 0 ]; then
    bash "${SCRIPT_DIR}/restart-rms-backend.sh"
  else
    sudo bash "${SCRIPT_DIR}/restart-rms-backend.sh"
  fi
else
  if [ -f "${TMP_JAR}" ] && [ "${DEPLOY_FRONTEND_ONLY:-0}" = "1" ]; then
    echo "==> DEPLOY_FRONTEND_ONLY=1 — skip backend"
  else
    echo "==> Backend JAR not in ${DEPLOY_HOME}/rms_backend_build — skip restart"
  fi
fi

if [ -d "${FRONT_SRC}" ] && [ -f "${FRONT_SRC}/index.html" ]; then
  echo "==> Frontend: rsync to ${FRONT_DEST}"
  sudo rsync -a --delete "${FRONT_SRC}/" "${FRONT_DEST}/"
  sudo chown -R www:www "${FRONT_DEST}"
else
  echo "==> Frontend build not found at ${FRONT_SRC} — skip"
fi

rm -rf "${FRONT_SRC}" "${DEPLOY_HOME}/rms_backend_build" 2>/dev/null || true
echo "Deploy finished."
