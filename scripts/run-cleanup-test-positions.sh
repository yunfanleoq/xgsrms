#!/bin/bash
set -eu
set -a
source /www/wwwroot/rms/backend/rms.env
set +a
MODE="${1:-preview}"
DIR="$(cd "$(dirname "$0")" && pwd)"
if [ "$MODE" = "apply" ]; then
  SQL_FILE="${DIR}/cleanup-test-positions-apply.sql"
else
  SQL_FILE="${DIR}/cleanup-test-positions-preview.sql"
fi
mysql -h127.0.0.1 -u"$MYSQL_USERNAME" -p"$MYSQL_PASSWORD" xgsrms < "$SQL_FILE"
