#!/bin/bash
# 彻底停止信工所招聘后端（java + runuser 包装进程 + 8080 监听）
# 用法：sudo bash ~/stop-rms-backend.sh
set -eu

JAR_MARKER="jeecg-system-start-3.9.1.jar"
PORT="8080"
MAX_ROUNDS=5

kill_matching() {
  local pattern="$1"
  local pids killed=""
  pids="$(pgrep -f "$pattern" 2>/dev/null || true)"
  [ -z "$pids" ] && return 0
  killed="$(echo "$pids" | tr '\n' ' ' | sed 's/ $//')"
  echo "  kill -9 [$pattern] PIDs: ${killed}"
  while IFS= read -r pid; do
    [ -n "$pid" ] || continue
    sudo kill -9 "$pid" 2>/dev/null || kill -9 "$pid" 2>/dev/null || true
  done <<EOF
$pids
EOF
}

kill_port_listeners() {
  if command -v fuser >/dev/null 2>&1; then
    sudo fuser -k "${PORT}/tcp" 2>/dev/null || true
    return 0
  fi
  if command -v lsof >/dev/null 2>&1; then
    local pids
    pids="$(lsof -t -i ":${PORT}" -sTCP:LISTEN 2>/dev/null || true)"
    [ -z "$pids" ] && return 0
    echo "  kill -9 port ${PORT} listeners: $(echo "$pids" | tr '\n' ' ')"
    while IFS= read -r pid; do
      [ -n "$pid" ] || continue
      sudo kill -9 "$pid" 2>/dev/null || kill -9 "$pid" 2>/dev/null || true
    done <<EOF
$pids
EOF
  fi
}

any_backend_process() {
  pgrep -f "$JAR_MARKER" >/dev/null 2>&1
}

port_still_listening() {
  if command -v ss >/dev/null 2>&1; then
    ss -ltn "sport = :${PORT}" 2>/dev/null | grep -q ":${PORT}"
    return $?
  fi
  curl -sf --max-time 2 "http://127.0.0.1:${PORT}/xgszp-boot/" >/dev/null 2>&1
}

echo "==> stop-rms-backend: thorough stop (${JAR_MARKER}, port ${PORT})"

round=1
while [ "$round" -le "$MAX_ROUNDS" ]; do
  echo "-- round ${round}/${MAX_ROUNDS}"
  kill_matching "$JAR_MARKER"
  kill_matching "runuser.*${JAR_MARKER}"
  kill_matching "java.*-jar.*${JAR_MARKER}"
  kill_port_listeners
  sleep 2

  if ! any_backend_process && ! port_still_listening; then
    echo "OK: backend stopped (no process, port ${PORT} free)"
    exit 0
  fi

  if any_backend_process; then
    echo "  still running:"
    pgrep -af "$JAR_MARKER" 2>/dev/null || true
  fi
  if port_still_listening; then
    echo "  port ${PORT} still listening"
    ss -ltnp "sport = :${PORT}" 2>/dev/null || lsof -i ":${PORT}" 2>/dev/null || true
  fi
  round=$((round + 1))
done

echo "ERROR: failed to stop backend after ${MAX_ROUNDS} rounds"
pgrep -af "$JAR_MARKER" 2>/dev/null || true
exit 1
