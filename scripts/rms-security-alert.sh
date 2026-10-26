#!/bin/bash
# 每 10 分钟由 /etc/cron.d/rms-security 调用。
# 只把接口日志里的失败和敏感路径追加到 security-alert.log，不记录口令。
set -eu
ACCESS_LOG="/www/wwwlogs/xgszp-boot.access.log"
STATE="/www/wwwroot/rms/backend/logs/security-alert.offset"
OUT="/www/wwwroot/rms/backend/logs/security-alert.log"
mkdir -p "$(dirname "$OUT")"
touch "$ACCESS_LOG" "$OUT"

python3 - "$ACCESS_LOG" "$STATE" "$OUT" << 'PY'
import os, sys
path, state, out = sys.argv[1], sys.argv[2], sys.argv[3]
size = os.path.getsize(path)
offset = 0
if os.path.exists(state):
    try:
        offset = int(open(state).read().strip() or "0")
    except ValueError:
        offset = 0
if offset <= 0 or offset > size:
    # 首次只记当前位置，不把历史访问全部打成告警
    open(state, "w").write(str(size))
    sys.exit(0)
needles_always = ("/graphql", "/actuator", "/druid", ".php", ".jsp", ".asp", "webshell")
needles_fail = ("getdictitems/", "/sys/login", "/sys/mlogin", "/sys/sms")
fail_marks = (" 401 ", " 403 ", " 429 ", " 500 ")
hits = []
with open(path, "rb") as fh:
    fh.seek(offset)
    for raw in fh:
        line = raw.decode("utf-8", "replace")
        low = line.lower()
        if any(n in low for n in needles_always) or (
            any(n in low for n in needles_fail) and any(m in line for m in fail_marks)
        ):
            hits.append(line[:500].rstrip())
with open(state, "w") as fh:
    fh.write(str(size))
if not hits:
    sys.exit(0)
with open(out, "a", encoding="utf-8") as fh:
    fh.write("----- %d hits -----\n" % len(hits))
    for line in hits[:200]:
        fh.write(line + "\n")
PY
