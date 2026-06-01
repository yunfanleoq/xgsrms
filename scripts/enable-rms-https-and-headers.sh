#!/bin/bash
# PROJ-002/003/006：为 10.28.12.15 生成自签 HTTPS 证书、更新 Nginx（含 CSP）、重载并提示更新 CORS。
# 在服务器上执行：sudo bash enable-rms-https-and-headers.sh
set -eu

HOST="10.28.12.15"
CERT_DIR="/www/server/panel/vhost/cert/${HOST}"
NGINX_SITE="/www/server/panel/vhost/nginx/${HOST}.conf"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_CONF="${SCRIPT_DIR}/../deploy/nginx/${HOST}.conf"
if [ ! -f "$REPO_CONF" ]; then
  REPO_CONF="/home/xgs/rms_deploy/deploy/nginx/${HOST}.conf"
fi
if [ ! -f "$REPO_CONF" ]; then
  REPO_CONF="${HOME}/rms_deploy/deploy/nginx/${HOST}.conf"
fi
ENV_FILE="/www/wwwroot/rms/backend/rms.env"
STAMP="$(date +%Y%m%d-%H%M%S)"

if [ "$(id -u)" -ne 0 ]; then
  echo "请使用 root 执行: sudo bash $0"
  exit 1
fi

echo "==> 1/5 生成自签证书 (${CERT_DIR})"
mkdir -p "$CERT_DIR"
if [ ! -f "${CERT_DIR}/fullchain.pem" ]; then
  openssl req -x509 -nodes -days 3650 -newkey rsa:2048 \
    -keyout "${CERT_DIR}/privkey.pem" \
    -out "${CERT_DIR}/fullchain.pem" \
    -subj "/CN=${HOST}" \
    -addext "subjectAltName=IP:${HOST}"
  chmod 600 "${CERT_DIR}/privkey.pem"
  echo "证书已生成"
else
  echo "证书已存在，跳过生成"
fi

echo "==> 2/5 备份并更新 Nginx 站点配置"
if [ -f "$REPO_CONF" ]; then
  cp "$NGINX_SITE" "${NGINX_SITE}.bak.${STAMP}"
  cp "$REPO_CONF" "$NGINX_SITE"
else
  echo "ERROR: 未找到 ${REPO_CONF}，请先将 deploy/nginx/${HOST}.conf 上传到服务器 scripts/../deploy/nginx/"
  exit 1
fi

echo "==> 3/5 检查并重载 Nginx"
nginx -t
/etc/init.d/nginx reload || systemctl reload nginx || nginx -s reload

echo "==> 4/5 更新 CORS 并重载后端"
CORS_LINE="CORS_ALLOWED_ORIGINS=http://${HOST},https://${HOST}"
if [ -f "$ENV_FILE" ]; then
  if grep -q '^CORS_ALLOWED_ORIGINS=' "$ENV_FILE"; then
    sed -i "s|^CORS_ALLOWED_ORIGINS=.*|${CORS_LINE}|" "$ENV_FILE"
  else
    echo "$CORS_LINE" >> "$ENV_FILE"
  fi
  echo "已写入: $CORS_LINE"
  RESTART="/home/xgs/restart-rms-backend.sh"
  if [ -f "$RESTART" ]; then
    bash "$RESTART"
  else
    echo "WARN: 请手动重启后端: sudo bash /home/xgs/restart-rms-backend.sh"
  fi
fi

echo "==> 5/5 本机探测"
curl -skI "https://${HOST}/" | head -5 || true
curl -sI "http://${HOST}/" | head -5 || true

echo ""
echo "完成。浏览器访问 https://${HOST}/ （自签证书需手动信任一次）。"
echo "HTTP 80 已 301 跳转 HTTPS（Auth-04）。"
