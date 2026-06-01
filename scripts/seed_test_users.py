#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
信工所招聘系统 — 预置 / 修复 E2E 与 IDOR 测试账号。

用法（在项目根目录）:
  py -3 scripts/seed_test_users.py                    # 生成 SQL 到 stdout
  py -3 scripts/seed_test_users.py -o scripts/sql/mysql/xgs_seed_test_users.generated.sql
  py -3 scripts/seed_test_users.py --print-hashes     # 仅打印密码哈希（调试）

依赖: 本机已安装 JDK（javac/java），用于 Jeecg PBEWithMD5AndDES 密码加密。

说明:
  - test-qz-b  克隆 test-qz  的角色（求职者，IDOR 对照 B）
  - test-bm-b  克隆 test-bm  的角色，但绑定「不同于 test-bm 的第二个部门」
  - test-qz    若已存在则重置密码为 test-qz@2026（修复登录失败）
"""

from __future__ import annotations

import argparse
import os
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA_DIR = ROOT / "scripts" / "java"
JAVA_SRC = JAVA_DIR / "JeecgPasswordHash.java"

# 固定 ID，便于重复执行（INSERT 前会先 DELETE 同 username 的关联）
USER_IDS = {
    "test-qz-b": "2000326100000000001",
    "test-bm-b": "2000326100000000002",
}

ACCOUNTS = [
    {
        "username": "test-qz-b",
        "realname": "求职者测试账号B",
        "password": "test-qz-b@2026",
        "template": "test-qz",
        "dept_mode": "same_as_template",  # 求职者通常无后台部门
    },
    {
        "username": "test-bm-b",
        "realname": "部门测试账号B",
        "password": "test-bm-b@2026",
        "template": "test-bm",
        "dept_mode": "different_from_template",
    },
]

FIX_ACCOUNTS = [
    {"username": "test-qz", "password": "test-qz@2026", "realname": "求职者测试账号"},
]


def ensure_java_hasher() -> Path:
    class_file = JAVA_DIR / "JeecgPasswordHash.class"
    if not JAVA_SRC.exists():
        sys.exit(f"缺少 {JAVA_SRC}")
    if not class_file.exists() or class_file.stat().st_mtime < JAVA_SRC.stat().st_mtime:
        subprocess.run(
            ["javac", str(JAVA_SRC.name)],
            cwd=str(JAVA_DIR),
            check=True,
        )
    return JAVA_DIR


def jeecg_password_hash(username: str, password: str, salt: str | None = None) -> tuple[str, str]:
    """返回 (salt, encoded_password)。"""
    java_dir = ensure_java_hasher()
    cmd = ["java", "JeecgPasswordHash", username, password]
    if salt:
        if len(salt.encode("utf-8")) != 8:
            raise ValueError(f"salt 必须为 8 字节 UTF-8，当前: {salt!r}")
        cmd.append(salt)
    out = subprocess.check_output(cmd, cwd=str(java_dir), text=True).strip()
    s, encoded = out.split("|", 1)
    return s, encoded


def sql_escape(s: str) -> str:
    return s.replace("\\", "\\\\").replace("'", "''")


def build_fix_sql() -> list[str]:
    lines = ["-- ========== 修复已有 test-qz 密码 =========="]
    for acc in FIX_ACCOUNTS:
        salt, pwd = jeecg_password_hash(acc["username"], acc["password"])
        u = sql_escape(acc["username"])
        lines.append(
            f"""
UPDATE sys_user
SET password = '{pwd}', salt = '{salt}', status = 1, del_flag = 0,
    realname = COALESCE(NULLIF(realname, ''), '{sql_escape(acc["realname"])}'),
    update_by = 'admin', update_time = NOW()
WHERE username = '{u}';

-- 若 test-qz 不存在，下面 SELECT 结果为空，请改用界面新建或取消注释 INSERT（需手工指定角色）
SELECT CONCAT('test-qz 存在性: ', IF(COUNT(*)>0,'OK','缺失，请界面新建')) AS check_qz
FROM sys_user WHERE username = '{u}' AND del_flag = 0;
""".strip()
        )
    return lines


def build_seed_user_sql(acc: dict) -> list[str]:
    username = acc["username"]
    template = acc["template"]
    uid = USER_IDS[username]
    salt, pwd = jeecg_password_hash(username, acc["password"])
    u = sql_escape(username)
    t = sql_escape(template)
    rn = sql_escape(acc["realname"])

    lines = [f"-- ========== 预置 {username}（模板 {template}）=========="]

    # 清理旧数据（按 username，避免重复）
    lines.append(
        f"""
DELETE sur FROM sys_user_role sur
INNER JOIN sys_user su ON su.id = sur.user_id
WHERE su.username = '{u}';

DELETE sud FROM sys_user_depart sud
INNER JOIN sys_user su ON su.id = sud.user_id
WHERE su.username = '{u}';

DELETE FROM sys_user WHERE username = '{u}';
""".strip()
    )

    lines.append(
        f"""
INSERT INTO sys_user (
  id, username, realname, password, salt, avatar, birthday, sex, email, phone,
  org_code, status, del_flag, third_id, activiti_sync, work_no, telephone,
  create_by, create_time, update_by, update_time, user_identity, depart_ids,
  client_id, bpm_status, sign, sign_enable, main_dep_post_id, sort, position_type
)
SELECT
  '{uid}', '{u}', '{rn}', '{pwd}', '{salt}',
  avatar, birthday, sex, NULL, NULL,
  org_code, 1, 0, third_id, activiti_sync, '{u}', telephone,
  'admin', NOW(), NULL, NULL, user_identity, depart_ids,
  client_id, bpm_status, sign, sign_enable, main_dep_post_id, sort, position_type
FROM sys_user
WHERE username = '{t}' AND del_flag = 0
LIMIT 1;
""".strip()
    )

    # 若模板不存在，fallback 最小字段 INSERT
    lines.append(
        f"""
INSERT INTO sys_user (id, username, realname, password, salt, status, del_flag, create_by, create_time)
SELECT '{uid}', '{u}', '{rn}', '{pwd}', '{salt}', 1, 0, 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = '{u}' AND del_flag = 0)
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = '{t}' AND del_flag = 0);
""".strip()
    )

    # 克隆角色
    lines.append(
        f"""
INSERT INTO sys_user_role (id, user_id, role_id)
SELECT REPLACE(UUID(), '-', ''), '{uid}', sur.role_id
FROM sys_user_role sur
INNER JOIN sys_user su ON su.id = sur.user_id
WHERE su.username = '{t}' AND su.del_flag = 0
  AND EXISTS (SELECT 1 FROM sys_user WHERE username = '{u}' AND del_flag = 0);
""".strip()
    )

    if acc["dept_mode"] == "same_as_template":
        lines.append(
            f"""
INSERT INTO sys_user_depart (ID, user_id, dep_id)
SELECT REPLACE(UUID(), '-', ''), '{uid}', sud.dep_id
FROM sys_user_depart sud
INNER JOIN sys_user su ON su.id = sud.user_id
WHERE su.username = '{t}' AND su.del_flag = 0
  AND EXISTS (SELECT 1 FROM sys_user WHERE username = '{u}' AND del_flag = 0);
""".strip()
        )
    else:
        # 绑定与 test-bm 不同的部门（取 sys_depart 中第二个可用部门）
        lines.append(
            f"""
INSERT INTO sys_user_depart (ID, user_id, dep_id)
SELECT REPLACE(UUID(), '-', ''), '{uid}', d.id
FROM sys_depart d
WHERE d.del_flag = '0'
  AND d.id NOT IN (
    SELECT sud.dep_id FROM sys_user_depart sud
    INNER JOIN sys_user su ON su.id = sud.user_id
    WHERE su.username = '{t}' AND su.del_flag = 0
  )
ORDER BY d.org_code
LIMIT 1;

-- 若无第二个部门，请先在【部门管理】新建「测试部门B」后重跑本脚本
SELECT CONCAT('{u} 绑定部门数: ', COUNT(*)) AS dept_check
FROM sys_user_depart sud
INNER JOIN sys_user su ON su.id = sud.user_id
WHERE su.username = '{u}';
""".strip()
        )

    lines.append(
        f"""
SELECT CONCAT('{u} 角色数: ', COUNT(*)) AS role_check
FROM sys_user_role sur
INNER JOIN sys_user su ON su.id = sur.user_id
WHERE su.username = '{u}';
""".strip()
    )

    return lines


def build_full_sql(*, skip_fix: bool = False) -> str:
    parts = [
        "-- 信工所招聘系统 — 测试账号预置 SQL",
        "-- 生成工具: scripts/seed_test_users.py",
        "-- 执行前请备份；建议在测试/预发环境执行",
        "SET NAMES utf8mb4;",
        "",
    ]
    if not skip_fix:
        parts.extend(build_fix_sql())
        parts.append("")
    for acc in ACCOUNTS:
        parts.extend(build_seed_user_sql(acc))
        parts.append("")
    parts.append(
        """
-- ========== 执行后验证 ==========
SELECT username, realname, status, del_flag, salt
FROM sys_user
WHERE username IN ('test-qz','test-qz-b','test-bm','test-bm-b','test-rl')
ORDER BY username;
""".strip()
    )
    return "\n\n".join(parts) + "\n"


def main() -> None:
    parser = argparse.ArgumentParser(description="生成招聘系统测试账号预置 SQL")
    parser.add_argument("-o", "--output", help="写入 SQL 文件路径")
    parser.add_argument("--skip-fix", action="store_true", help="跳过 test-qz 密码修复（已执行过时使用）")
    parser.add_argument("--print-hashes", action="store_true", help="打印密码哈希")
    args = parser.parse_args()

    if args.print_hashes:
        for acc in FIX_ACCOUNTS + ACCOUNTS:
            salt, pwd = jeecg_password_hash(acc["username"], acc["password"])
            print(f"{acc['username']}\t{acc['password']}\tsalt={salt}\tpwd={pwd}")
        return

    sql = build_full_sql(skip_fix=args.skip_fix)
    if args.output:
        out = Path(args.output)
        out.parent.mkdir(parents=True, exist_ok=True)
        out.write_text(sql, encoding="utf-8")
        print(f"已写入: {out}")
    else:
        print(sql)


if __name__ == "__main__":
    main()
