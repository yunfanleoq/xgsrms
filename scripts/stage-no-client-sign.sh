#!/bin/bash
# 把「关闭浏览器签名」的后端包和前端包放到 xgs-server 家目录，并收紧数据源白名单。
# 上传完成后仍需：ssh -t xgs-server 'sudo bash ~/finish-rms-deploy.sh'
set -eu
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
JAR="$ROOT/jeecg-boot/jeecg-module-system/jeecg-system-start/target/jeecg-system-start-3.9.1.jar"
DIST="$ROOT/jeecgboot-vue3/dist"
HOST="${1:-xgs-server}"

if [ ! -f "$JAR" ]; then
  echo "缺少 $JAR"
  exit 1
fi
if [ ! -f "$DIST/index.html" ]; then
  echo "缺少 $DIST/index.html"
  exit 1
fi

ssh -o ConnectTimeout=12 -o BatchMode=yes "$HOST" 'mkdir -p ~/rms_backend_build ~/rms_frontend_build'
rsync -a --delete "$DIST/" "$HOST:rms_frontend_build/"
rsync -a "$JAR" "$HOST:rms_backend_build/jeecg-system-start-3.9.1.jar"
ssh -o BatchMode=yes "$HOST" 'python3 -' << 'PY'
from pathlib import Path
import os, subprocess, tempfile
env = {}
for raw in Path("/www/wwwroot/rms/backend/rms.env").read_text().splitlines():
    line = raw.strip()
    if not line or line.startswith("#") or "=" not in line:
        continue
    k, v = line.split("=", 1)
    env[k.strip()] = v.strip().strip('"').strip("'")
url = env["MYSQL_JDBC_URL"].split("jdbc:mysql://", 1)[1]
hostport, db = url.split("/", 1)
db = db.split("?", 1)[0]
host, port = hostport.split(":") if ":" in hostport else (hostport, "3306")
fd, cnf = tempfile.mkstemp(prefix="rms-my-")
os.close(fd)
os.chmod(cnf, 0o600)
Path(cnf).write_text("[client]\nuser=%s\npassword=%s\nhost=%s\nport=%s\ndatabase=%s\n" % (
    env["MYSQL_USERNAME"], env["MYSQL_PASSWORD"], host, port, db))
sql = """
UPDATE sys_table_white_list
SET field_name='db_driver,code,db_username,name,db_url'
WHERE table_name='sys_data_source';
SELECT table_name, field_name FROM sys_table_white_list
WHERE table_name IN ('sys_user','sys_data_source');
"""
try:
    out = subprocess.check_output(["mysql", "--defaults-extra-file="+cnf, "-N", "-e", sql], text=True)
    print(out.strip())
finally:
    os.remove(cnf)
PY
echo "STAGED. 接下来在本机执行: ssh -t xgs-server 'sudo bash ~/finish-rms-deploy.sh'"
