#!/bin/bash
# 补全 /www/wwwroot/rms/backend/rms.env 中的安全相关环境变量（幂等）。
# 服务器：bash ~/ensure-rms-security-env.sh
# 修改后须重启后端：sudo bash ~/restart-rms-backend.sh
set -eu

ENV_FILE="/www/wwwroot/rms/backend/rms.env"
HOST="${RMS_HOST:-10.28.12.15}"
STAMP="$(date +%Y%m%d-%H%M%S)"

if [ ! -f "$ENV_FILE" ]; then
  echo "ERROR: 未找到 $ENV_FILE"
  exit 1
fi

cp "$ENV_FILE" "${ENV_FILE}.bak.${STAMP}"

ensure_kv() {
  local key="$1"
  local value="$2"
  if grep -q "^${key}=" "$ENV_FILE"; then
    echo "已有: ${key}"
  else
    echo "${key}=${value}" >> "$ENV_FILE"
    echo "已追加: ${key}=${value}"
  fi
}

ensure_kv "JEECG_SECURITY_ALLOW_TOKEN_IN_QUERY" "false"

CORS_LINE="CORS_ALLOWED_ORIGINS=http://${HOST},https://${HOST}"
if grep -q '^CORS_ALLOWED_ORIGINS=' "$ENV_FILE"; then
  echo "已有: CORS_ALLOWED_ORIGINS"
else
  echo "$CORS_LINE" >> "$ENV_FILE"
  echo "已追加: $CORS_LINE"
fi

echo ""
echo "当前安全相关配置："
grep -E '^(JEECG_SECURITY_ALLOW_TOKEN_IN_QUERY|CORS_ALLOWED_ORIGINS)=' "$ENV_FILE"
echo ""
echo "若值有变更，请执行: sudo bash ~/restart-rms-backend.sh"
