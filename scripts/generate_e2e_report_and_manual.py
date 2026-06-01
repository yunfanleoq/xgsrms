#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从 E2E manifest.json 生成：
  1) HTML 测试报告（每步截图 + 操作说明 + 页面说明）
  2) Markdown 系统操作说明书（软著手册结构，每图配步骤与页面说明）

用法:
  py -3 scripts/generate_e2e_report_and_manual.py
  py -3 scripts/generate_e2e_report_and_manual.py --run-dir docs/e2e-runs/2026-06-01T12-00-00
"""
from __future__ import annotations

import argparse
import html
import json
import shutil
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RUNS = ROOT / "docs" / "e2e-runs"
MANUAL_OUT = ROOT / "docs" / "e2e-manual"


def find_latest_run() -> Path:
    if not RUNS.exists():
        raise SystemExit(f"无运行目录: {RUNS}")
    dirs = [p for p in RUNS.iterdir() if p.is_dir() and (p / "manifest.json").exists()]
    if not dirs:
        raise SystemExit("未找到含 manifest.json 的运行目录")
    return max(dirs, key=lambda p: p.stat().st_mtime)


def load_manifest(run_dir: Path) -> dict:
    p = run_dir / "manifest.json"
    if not p.exists():
        raise SystemExit(f"缺少 {p}")
    return json.loads(p.read_text(encoding="utf-8"))


def group_by_role(steps: list) -> dict[str, list]:
    groups: dict[str, list] = {}
    for s in steps:
        groups.setdefault(s.get("roleLabel") or s.get("role", "其他"), []).append(s)
    return groups


def build_html(manifest: dict, run_dir: Path) -> str:
    steps = manifest.get("steps", [])
    ok = sum(1 for s in steps if s.get("status") == "ok")
    fail = len(steps) - ok
    title = f"{manifest.get('softwareName', '系统')} — E2E 测试报告"
    parts = [
        "<!DOCTYPE html><html lang='zh-CN'><head><meta charset='utf-8'/>",
        f"<title>{html.escape(title)}</title>",
        "<style>",
        "body{font-family:'Microsoft YaHei',sans-serif;max-width:960px;margin:24px auto;line-height:1.7;color:#222}",
        "h1{border-bottom:2px solid #1677ff;padding-bottom:8px}",
        ".meta{background:#f5f7fa;padding:12px 16px;border-radius:8px;margin:16px 0}",
        ".step{border:1px solid #e8e8e8;border-radius:8px;padding:16px;margin:20px 0}",
        ".step h3{margin:0 0 8px;color:#1677ff}",
        ".tag{display:inline-block;background:#e6f4ff;color:#0958d9;padding:2px 8px;border-radius:4px;font-size:12px;margin-right:6px}",
        ".fail{border-color:#ffccc7;background:#fff2f0}",
        "img{max-width:100%;border:1px solid #ddd;border-radius:4px;margin-top:12px}",
        ".label{font-weight:600;color:#555}",
        "</style></head><body>",
        f"<h1>{html.escape(title)}</h1>",
        "<div class='meta'>",
        f"<p><span class='label'>运行 ID：</span>{html.escape(manifest.get('runId',''))}</p>",
        f"<p><span class='label'>环境：</span>{html.escape(manifest.get('baseURL',''))}</p>",
        f"<p><span class='label'>开始：</span>{html.escape(manifest.get('startedAt',''))}</p>",
        f"<p><span class='label'>结束：</span>{html.escape(manifest.get('finishedAt',''))}</p>",
        f"<p><span class='label'>步骤统计：</span>共 {len(steps)} 步，成功 {ok}，失败 {fail}</p>",
        "</div>",
    ]
    for s in steps:
        cls = "step fail" if s.get("status") != "ok" else "step"
        parts.append(f"<div class='{cls}' id='{html.escape(s.get('id',''))}'>")
        parts.append(f"<h3>{html.escape(s.get('id',''))} · {html.escape(s.get('title',''))}</h3>")
        parts.append(f"<span class='tag'>{html.escape(s.get('roleLabel',''))}</span>")
        if s.get("route"):
            parts.append(f"<span class='tag'>{html.escape(s.get('route',''))}</span>")
        parts.append(f"<p><span class='label'>操作步骤：</span>{html.escape(s.get('action',''))}</p>")
        parts.append(f"<p><span class='label'>页面说明：</span>{html.escape(s.get('pageDesc',''))}</p>")
        if s.get("url"):
            parts.append(f"<p><span class='label'>URL：</span><code>{html.escape(s.get('url',''))}</code></p>")
        if s.get("error"):
            parts.append(f"<p style='color:#cf1322'><span class='label'>错误：</span>{html.escape(s.get('error',''))}</p>")
        a = s.get("analysis") or {}
        if a:
            parts.append(
                f"<p><span class='label'>页面分析：</span>"
                f"404={html.escape(str(a.get('is404')))}；"
                f"表格行≈{html.escape(str(a.get('tableRowCount','')))}；"
                f"摘要={html.escape(str(a.get('bodySnippet',''))[:120])}</p>"
            )
        shot = s.get("screenshot")
        if shot:
            parts.append(f"<img src='{html.escape(shot)}' alt='{html.escape(s.get('title',''))}'/>")
        parts.append("</div>")
    parts.append("</body></html>")
    return "\n".join(parts)


def build_manual_md(manifest: dict, run_dir: Path) -> str:
    """按软著操作手册结构：每步「操作步骤 + 页面说明 + 配图」。"""
    name = manifest.get("softwareName", "信工所招聘管理系统")
    ver = manifest.get("softwareVersion", "V1.0")
    base = manifest.get("baseURL", "http://10.28.12.15")
    steps = manifest.get("steps", [])
    groups = group_by_role(steps)
    run_id = manifest.get("runId", "")
    rel_prefix = f"../e2e-runs/{run_dir.name}"

    lines = [
        f"# {name} 用户操作说明书（E2E 实机采集）",
        "",
        "## 文档信息",
        "",
        "| 项目 | 说明 |",
        "|------|------|",
        f"| 软件名称 | {name} |",
        f"| 版本号 | {ver} |",
        f"| 文档类型 | 操作说明书（自动化实机截图） |",
        f"| 采集时间 | {manifest.get('finishedAt') or manifest.get('startedAt', '')} |",
        f"| 运行环境 | {base} |",
        f"| 采集批次 | `{run_id}` |",
        "",
        "---",
        "",
        "## 第 1 章 引言",
        "",
        "### 1.1 编写目的",
        "本文档用于指导用户使用信工所招聘管理系统，完成岗位浏览、在线申请、部门与人事审核、面试邀请等操作。",
        "正文配图来自 Playwright 自动化测试在**真实运行环境**中的全页截图。",
        "",
        "### 1.2 软件概述",
        f"- **软件名称**：{name} {ver}",
        "- **目标用户**：求职者、部门管理员、人事处管理员、系统管理员",
        "- **主要功能**：岗位发布与审核、在线简历投递、多级简历审核、面试邀请与结果查询",
        "",
        "### 1.3 运行环境",
        "",
        "| 组件 | 要求 |",
        "|------|------|",
        "| 浏览器 | Chrome / Edge 最新版 |",
        f"| 访问地址 | {base} |",
        "| 网络 | 单位内网（示例环境） |",
        "",
        "---",
        "",
        "## 第 2 章 系统访问与登录",
        "",
    ]

    login_steps = [s for s in steps if s.get("role") in ("anonymous", "test-qz") and "登录" in s.get("title", "")]
    if not login_steps:
        login_steps = steps[:2]
    sec = 1
    for s in login_steps:
        lines.extend(_step_block(s, rel_prefix, sec))
        sec += 1

    chapter = 3
    role_order = ["求职者 A", "部门管理员 A", "人事处", "系统管理员", "求职者 B", "部门管理员 B"]
    for role_label in role_order:
        role_steps = groups.get(role_label, [])
        if not role_steps:
            continue
        lines.append(f"## 第 {chapter} 章 {role_label}操作")
        lines.append("")
        lines.append(f"### {chapter}.1 功能说明")
        lines.append("")
        lines.append(f"本章说明 **{role_label}** 在系统中的典型菜单与页面。以下步骤均附实机截图。")
        lines.append("")
        sec = 1
        for s in role_steps:
            if s in login_steps:
                continue
            lines.extend(_step_block(s, rel_prefix, sec, prefix=f"{chapter}."))
            sec += 1
        chapter += 1

    lines.extend(
        [
            "---",
            "",
            "## 附录 A 测试步骤索引",
            "",
            "| 编号 | 角色 | 页面 | 状态 |",
            "|------|------|------|------|",
        ]
    )
    for s in steps:
        lines.append(
            f"| {s.get('id','')} | {s.get('roleLabel','')} | {s.get('title','')} | {s.get('status','')} |"
        )
    lines.append("")
    lines.append(
        "> 导出 PDF：可将本 Markdown 与 `docs/e2e-runs/` 下截图一并导入 Word，"
        "按 [软著 pdf-format-spec](https://github.com/yunfanleoq/soft-copyright-application-skill) 设置页眉页脚后导出 PDF。"
    )
    lines.append("")
    return "\n".join(lines)


def _step_block(step: dict, rel_prefix: str, sec: int, prefix: str = "2.") -> list[str]:
    sid = step.get("id", f"S{sec:02d}")
    title = step.get("title", "")
    action = step.get("action", "")
    desc = step.get("pageDesc", "")
    shot = step.get("screenshot", "")
    img = f"{rel_prefix}/{shot}" if shot else ""
    block = [
        f"### {prefix}{sec} {title}",
        "",
        "**页面说明**",
        desc,
        "",
        "**操作步骤**",
        action,
        "",
    ]
    if img:
        block.append(f"![{title}]({img})")
        block.append("")
    block.append("**预期结果**")
    block.append("页面正常加载，无报错提示；当前登录用户仅可见其权限范围内的数据。")
    block.append("")
    return block


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--run-dir", type=Path, help="e2e-runs 子目录")
    args = parser.parse_args()

    run_dir = args.run_dir.resolve() if args.run_dir else find_latest_run()
    manifest = load_manifest(run_dir)

    html_path = run_dir / "测试报告.html"
    html_path.write_text(build_html(manifest, run_dir), encoding="utf-8")

    MANUAL_OUT.mkdir(parents=True, exist_ok=True)
    manual_path = MANUAL_OUT / f"操作说明书-E2E采集-{run_dir.name}.md"
    manual_path.write_text(build_manual_md(manifest, run_dir), encoding="utf-8")

    latest = MANUAL_OUT / "操作说明书-E2E采集-最新.md"
    shutil.copyfile(manual_path, latest)

    summary = run_dir / "README.md"
    summary.write_text(
        "\n".join(
            [
                f"# E2E 运行 {manifest.get('runId')}",
                "",
                f"- 测试报告：[测试报告.html](./测试报告.html)",
                f"- 操作手册：[../e2e-manual/操作说明书-E2E采集-最新.md](../e2e-manual/操作说明书-E2E采集-最新.md)",
                f"- Playwright HTML：`./playwright-report/index.html`（若已生成）",
                f"- 步骤数：{len(manifest.get('steps', []))}",
                "",
            ]
        ),
        encoding="utf-8",
    )

    print(f"运行目录: {run_dir}")
    print(f"测试报告: {html_path}")
    print(f"操作手册: {manual_path}")
    print(f"最新手册: {latest}")


if __name__ == "__main__":
    main()
