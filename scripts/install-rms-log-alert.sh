#!/bin/bash
# root：安装日志轮转与每 10 分钟的安全告警。
set -eu
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
if [ "$(id -u)" -ne 0 ]; then
  echo "请使用 root 执行: sudo bash $0"
  exit 1
fi
install -m 644 "$ROOT/deploy/logrotate/rms-logs" /etc/logrotate.d/rms-logs
install -m 755 "$ROOT/scripts/rms-security-alert.sh" /www/wwwroot/rms/backend/rms-security-alert.sh
cat > /etc/cron.d/rms-security << 'EOF'
PATH=/usr/sbin:/usr/bin:/bin
SHELL=/bin/bash
*/10 * * * * root /www/wwwroot/rms/backend/rms-security-alert.sh
EOF
chmod 644 /etc/cron.d/rms-security
mkdir -p /www/wwwroot/rms/backend/logs
logrotate -d /etc/logrotate.d/rms-logs >/tmp/rms-logrotate-check.txt 2>&1 || {
  echo "logrotate 检查失败，见 /tmp/rms-logrotate-check.txt"
  exit 1
}
echo "已安装：接口日志保留 180 天，应用日志保留 90 天，告警每 10 分钟写入 /www/wwwroot/rms/backend/logs/security-alert.log"
