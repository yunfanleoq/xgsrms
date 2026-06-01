#!/bin/bash
set -euo pipefail
ENV_FILE="/www/wwwroot/rms/backend/rms.env"
SQL_FILE="/tmp/xgs_seed_test_users.sql"

if [[ ! -f "$SQL_FILE" ]]; then
  echo "Missing $SQL_FILE"
  exit 1
fi

read_env() {
  local key="$1"
  grep -E "^${key}=" "$ENV_FILE" | head -1 | cut -d= -f2- | tr -d '\r'
}

MYSQL_JDBC_URL="$(read_env MYSQL_JDBC_URL)"
MYSQL_USERNAME="$(read_env MYSQL_USERNAME)"
MYSQL_PASSWORD="$(read_env MYSQL_PASSWORD)"

if [[ -z "$MYSQL_JDBC_URL" || -z "$MYSQL_USERNAME" || -z "$MYSQL_PASSWORD" ]]; then
  echo "Failed to read MYSQL_* from $ENV_FILE"
  exit 1
fi
DB="${MYSQL_JDBC_URL#jdbc:mysql://}"
DB_HOST="${DB%%/*}"
DB_NAME="${DB#*/}"
DB_NAME="${DB_NAME%%\?*}"

echo "==> Before seed"
mysql -h"${DB_HOST%%:*}" -P"${DB_HOST##*:}" -u"$MYSQL_USERNAME" -p"$MYSQL_PASSWORD" "$DB_NAME" -e \
  "SELECT username, realname, status FROM sys_user WHERE username IN ('test-qz','test-bm','test-rl','test-qz-b','test-bm-b') AND del_flag=0;"

echo "==> Running seed SQL"
mysql -h"${DB_HOST%%:*}" -P"${DB_HOST##*:}" -u"$MYSQL_USERNAME" -p"$MYSQL_PASSWORD" "$DB_NAME" < "$SQL_FILE"

echo "==> After seed"
mysql -h"${DB_HOST%%:*}" -P"${DB_HOST##*:}" -u"$MYSQL_USERNAME" -p"$MYSQL_PASSWORD" "$DB_NAME" -e \
  "SELECT u.username, u.realname, u.status, GROUP_CONCAT(r.role_name) AS roles
   FROM sys_user u
   LEFT JOIN sys_user_role ur ON ur.user_id = u.id
   LEFT JOIN sys_role r ON r.id = ur.role_id
   WHERE u.username IN ('test-qz','test-qz-b','test-bm','test-bm-b','test-rl') AND u.del_flag=0
   GROUP BY u.username, u.realname, u.status
   ORDER BY u.username;"

echo "==> Department bindings for bm accounts"
mysql -h"${DB_HOST%%:*}" -P"${DB_HOST##*:}" -u"$MYSQL_USERNAME" -p"$MYSQL_PASSWORD" "$DB_NAME" -e \
  "SELECT u.username, d.depart_name
   FROM sys_user u
   JOIN sys_user_depart ud ON ud.user_id = u.id
   JOIN sys_depart d ON d.id = ud.dep_id
   WHERE u.username IN ('test-bm','test-bm-b') AND u.del_flag=0;"

echo "OK: seed completed"
