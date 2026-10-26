#!/bin/bash
# 仅更新 Nginx 站点配置并重载（PROJ-006 CSP 等），不重新生成证书。
# 服务器执行：sudo bash ~/apply-rms-nginx-site.sh
set -eu

HOST="10.28.12.15"
NGINX_SITE="/www/server/panel/vhost/nginx/${HOST}.conf"
STAMP="$(date +%Y%m%d-%H%M%S)"

for REPO_CONF in \
  "$(cd "$(dirname "$0")" && pwd)/../deploy/nginx/${HOST}.conf" \
  "/home/xgs/rms_deploy/deploy/nginx/${HOST}.conf" \
  "${HOME}/rms_deploy/deploy/nginx/${HOST}.conf"; do
  if [ -f "$REPO_CONF" ]; then
    break
  fi
done

if [ "$(id -u)" -ne 0 ]; then
  echo "请使用 root 执行: sudo bash $0"
  exit 1
fi

if [ ! -f "$REPO_CONF" ]; then
  echo "ERROR: 未找到 deploy/nginx/${HOST}.conf"
  exit 1
fi

INC_SRC="$(dirname "$REPO_CONF")/rms-api-proxy.inc"
if [ ! -f "$INC_SRC" ]; then
  echo "ERROR: 未找到 rms-api-proxy.inc"
  exit 1
fi
cp "$NGINX_SITE" "${NGINX_SITE}.bak.${STAMP}"
cp "$INC_SRC" /www/server/panel/vhost/nginx/rms-api-proxy.inc
cp "$REPO_CONF" "$NGINX_SITE"
touch /www/wwwlogs/xgszp-boot.access.log
chown www:www /www/wwwlogs/xgszp-boot.access.log || true
nginx -t
/etc/init.d/nginx reload || systemctl reload nginx || nginx -s reload
echo "Nginx 已重载。探测 HTTP 跳转与 CSP："
curl -sI "http://${HOST}/" | head -3 || true
curl -skI "https://${HOST}/" | grep -iE 'content-security-policy|strict-transport' || true

APP_CONFIG="/www/wwwroot/rms/frontend/dist/_app.config.js"
if [ -f "$APP_CONFIG" ] && grep -q '"VITE_GLOB_DOMAIN_URL":"http://10.28.12.15/xgszp-boot"' "$APP_CONFIG"; then
  sed -i 's|"VITE_GLOB_DOMAIN_URL":"http://10.28.12.15/xgszp-boot"|"VITE_GLOB_DOMAIN_URL":"https://10.28.12.15/xgszp-boot"|' "$APP_CONFIG"
  echo "已更新 _app.config.js 中 VITE_GLOB_DOMAIN_URL 为 https"
fi
