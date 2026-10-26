#!/usr/bin/env python3
# 在生产机执行。核对后台账号口令复用和异常登录，并重置后台口令。
# 应聘者（仅 test / DEF_INDEX_ALL 角色）不改。摘要和口令明文不写进本脚本输出以外的仓库文件。
import base64
import json
import os
import secrets
import socket
import string
import subprocess
import tempfile
from collections import defaultdict
from pathlib import Path

ENV_PATH = os.environ.get("RMS_ENV", "/www/wwwroot/rms/backend/rms.env")
PW_CLASSES = os.environ.get("PW_CLASSES", str(Path.home() / "rms-followup/pwtool"))
OUT_FILE = os.environ.get(
    "PW_OUT",
    str(Path.home() / "rms-backend-new-passwords.txt"),
)
LEAKED_PREFIX = "93D1DE04"
APPLICANT_ROLES = {"test", "DEF_INDEX_ALL"}
MAX_BACKEND = 100


def load_env(path):
    data = {}
    for raw in Path(path).read_text(encoding="utf-8", errors="replace").splitlines():
        line = raw.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        k, v = line.split("=", 1)
        data[k.strip()] = v.strip().strip('"').strip("'")
    return data


def jdbc_parts(url):
    # jdbc:mysql://host:port/db?...
    rest = url.split("jdbc:mysql://", 1)[1]
    hostport, db = rest.split("/", 1)
    db = db.split("?", 1)[0]
    if ":" in hostport:
        host, port = hostport.split(":", 1)
    else:
        host, port = hostport, "3306"
    return host, port, db


def mysql_query(env, sql):
    host, port, db = jdbc_parts(env["MYSQL_JDBC_URL"])
    fd, cnf = tempfile.mkstemp(prefix="rms-my-")
    os.close(fd)
    os.chmod(cnf, 0o600)
    Path(cnf).write_text(
        "[client]\nuser=%s\npassword=%s\nhost=%s\nport=%s\ndatabase=%s\n"
        % (env["MYSQL_USERNAME"], env["MYSQL_PASSWORD"], host, port, db),
        encoding="utf-8",
    )
    try:
        proc = subprocess.run(
            ["mysql", "--defaults-extra-file=" + cnf, "--batch", "--raw", "--skip-column-names", "-e", sql],
            check=True,
            capture_output=True,
            text=True,
        )
        return proc.stdout
    finally:
        os.remove(cnf)


def encrypt(username, password, salt):
    proc = subprocess.run(
        [os.environ.get("JAVA_BIN", "java"), "-cp", PW_CLASSES, "org.jeecg.common.util.PasswordTool", username, password, salt],
        check=True,
        capture_output=True,
        text=True,
    )
    return proc.stdout.strip()


def new_password():
    alphabet = string.ascii_letters + string.digits
    body = "".join(secrets.choice(alphabet) for _ in range(18))
    return "Rms#" + body


def jwt_username(token):
    parts = token.split(".")
    if len(parts) < 2:
        return ""
    pad = parts[1] + "=" * (-len(parts[1]) % 4)
    try:
        payload = json.loads(base64.urlsafe_b64decode(pad.encode()))
    except Exception:
        return ""
    return str(payload.get("username") or "")


class RedisConn:
    def __init__(self, host, port, password):
        self.sock = socket.create_connection((host, int(port)), 8)
        self.fh = self.sock.makefile("rb")
        if password:
            reply = self.call("AUTH", password)
            if reply != b"OK" and reply != "OK":
                raise RuntimeError("redis auth failed")

    def call(self, *args):
        chunks = [f"*{len(args)}\r\n".encode()]
        for arg in args:
            raw = arg.encode() if isinstance(arg, str) else arg
            chunks.append(f"${len(raw)}\r\n".encode())
            chunks.append(raw + b"\r\n")
        self.sock.sendall(b"".join(chunks))
        return self._read()

    def _read(self):
        line = self.fh.readline()
        if not line:
            raise RuntimeError("redis closed")
        kind, rest = line[:1], line[1:-2]
        if kind == b"+":
            return rest
        if kind == b"-":
            raise RuntimeError(rest.decode("utf-8", "replace"))
        if kind == b":":
            return int(rest)
        if kind == b"$":
            n = int(rest)
            if n < 0:
                return None
            data = self.fh.read(n)
            self.fh.read(2)
            return data
        if kind == b"*":
            n = int(rest)
            return [self._read() for _ in range(n)]
        raise RuntimeError("redis bad reply")


def clear_tokens(env, usernames):
    if not usernames:
        return 0
    host = env.get("REDIS_HOST", "127.0.0.1")
    port = env.get("REDIS_PORT", "6379")
    password = env.get("REDIS_PASSWORD", "")
    try:
        conn = RedisConn(host, port, password)
    except Exception:
        print("REDIS_SCAN_SKIP")
        return 0
    removed = 0
    wanted = set(usernames)
    cursor = "0"
    while True:
        cursor_raw, keys = conn.call("SCAN", cursor, "MATCH", "prefix_user_token:*", "COUNT", "200")
        cursor = cursor_raw.decode() if isinstance(cursor_raw, bytes) else str(cursor_raw)
        for key in keys or []:
            key_s = key.decode() if isinstance(key, bytes) else str(key)
            drop = False
            if key_s.startswith("prefix_user_token:single_login:"):
                drop = key_s.rsplit(":", 1)[-1] in wanted
            elif key_s.startswith("prefix_user_token:"):
                drop = jwt_username(key_s[len("prefix_user_token:") :]) in wanted
            if drop:
                conn.call("DEL", key_s)
                removed += 1
        if cursor == "0":
            break
    return removed


def main():
    env = load_env(ENV_PATH)
    rows = mysql_query(
        env,
        """
        SELECT u.id, u.username, IFNULL(u.salt,''), UPPER(IFNULL(u.password,'')),
               IFNULL(GROUP_CONCAT(DISTINCT r.role_code ORDER BY r.role_code SEPARATOR ','), '')
        FROM sys_user u
        LEFT JOIN sys_user_role ur ON ur.user_id = u.id
        LEFT JOIN sys_role r ON r.id = ur.role_id
        WHERE u.del_flag = 0
        GROUP BY u.id, u.username, u.salt, u.password
        """,
    )
    users = []
    for line in rows.splitlines():
        if not line.strip():
            continue
        parts = line.split("\t")
        if len(parts) < 5:
            continue
        users.append(
            {
                "id": parts[0],
                "username": parts[1],
                "salt": parts[2],
                "password": parts[3],
                "roles": [r for r in parts[4].split(",") if r],
            }
        )

    def is_backend(u):
        if u["username"] == "admin":
            return True
        return any(r not in APPLICANT_ROLES for r in u["roles"])

    backend = [u for u in users if is_backend(u)]
    print("ACTIVE_USERS %d" % len(users))
    print("BACKEND_USERS %d" % len(backend))
    role_names = sorted({r for u in backend for r in u["roles"]})
    print("BACKEND_ROLES %s" % ",".join(role_names))
    if len(backend) > MAX_BACKEND:
        print("ABORT_TOO_MANY_BACKEND")
        raise SystemExit(2)

    by_hash = defaultdict(list)
    for u in users:
        if u["password"]:
            by_hash[u["password"]].append(u["username"])
    reuse_groups = [names for names in by_hash.values() if len(names) > 1]
    backend_names = {u["username"] for u in backend}
    reuse_backend = [names for names in reuse_groups if backend_names.intersection(names)]
    print("REUSE_GROUPS %d" % len(reuse_groups))
    print("REUSE_GROUPS_WITH_BACKEND %d" % len(reuse_backend))
    for names in reuse_backend:
        shown = [n for n in names if n in backend_names]
        print("REUSE_BACKEND_USER " + ",".join(shown))

    leaked = [u["username"] for u in backend if u["password"].startswith(LEAKED_PREFIX)]
    print("BACKEND_MATCH_EXPOSED_PREFIX %d" % len(leaked))

    weak = []
    for u in backend:
        salt = u["salt"] or "R8s4kP2m"
        try:
            digest = encrypt(u["username"], "123456", salt).upper()
        except subprocess.CalledProcessError:
            print("ENCRYPT_FAIL " + u["username"])
            raise
        if digest == u["password"]:
            weak.append(u["username"])
    print("BACKEND_DEFAULT_PASSWORD %d" % len(weak))
    for name in weak:
        print("WEAK_USER " + name)

    logs = mysql_query(
        env,
        """
        SELECT IFNULL(username,''), IFNULL(ip,''), IFNULL(DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%s'),''),
               LEFT(IFNULL(log_content,''), 80)
        FROM sys_log
        WHERE log_type = 1
          AND ip IN ('111.47.56.204','182.105.188.5')
          AND log_content LIKE '%登录成功%'
        ORDER BY create_time DESC
        LIMIT 50
        """,
    )
    hit = [ln for ln in logs.splitlines() if ln.strip()]
    print("SUSPICIOUS_LOGIN_SUCCESS %d" % len(hit))
    for ln in hit:
        print("SUSPICIOUS " + ln.replace("\t", " | "))

    lines = []
    updates = []
    for u in backend:
        salt = u["salt"]
        if len(salt) < 8:
            salt = "".join(secrets.choice(string.ascii_letters + string.digits) for _ in range(8))
        pw = new_password()
        digest = encrypt(u["username"], pw, salt)
        safe_user = u["username"].replace("\\", "\\\\").replace("'", "''")
        safe_id = u["id"].replace("'", "")
        updates.append(
            "UPDATE sys_user SET password='%s', salt='%s', update_time=NOW() WHERE id='%s' AND username='%s';"
            % (digest, salt, safe_id, safe_user)
        )
        lines.append("%s\t%s\t%s" % (u["username"], ",".join(u["roles"]), pw))

    sql = "START TRANSACTION;\n" + "\n".join(updates) + "\nCOMMIT;\n"
    mysql_query(env, sql)
    removed = clear_tokens(env, [u["username"] for u in backend])
    Path(OUT_FILE).write_text(
        "username\troles\tpassword\n" + "\n".join(lines) + "\n",
        encoding="utf-8",
    )
    os.chmod(OUT_FILE, 0o600)
    print("RESET_DONE %d" % len(backend))
    print("REDIS_TOKEN_REMOVED %d" % removed)
    print("PASSWORD_FILE " + OUT_FILE)


if __name__ == "__main__":
    main()
