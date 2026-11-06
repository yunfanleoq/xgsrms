#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从 E2E manifest.json + screenshots 生成《招聘主流程测试报告》PDF（reportlab）。

用法:
  pip install reportlab
  python3 scripts/generate_e2e_pdf_report.py
  python3 scripts/generate_e2e_pdf_report.py --run-dir docs/e2e-runs/<runId>

说明:
  - 正式环境实测需本机 VPN 访问 https://10.28.12.15 后先跑 Playwright full-chain。
  - 云端若连不上正式机：仅用本脚本从已有 manifest 出 PDF，勿伪造步骤成功。
"""
from __future__ import annotations

import argparse
import json
import sys
from datetime import datetime, timezone, timedelta
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RUNS = ROOT / "docs" / "e2e-runs"
TZ_CN = timezone(timedelta(hours=8))


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


def try_register_cjk_font():
    """优先注册常见中文字体，失败则退回 Helvetica（中文会缺字）。"""
    from reportlab.pdfbase import pdfmetrics
    from reportlab.pdfbase.ttfonts import TTFont

    candidates = [
        "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc",
        "/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc",
        "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc",
        "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc",
        "/System/Library/Fonts/PingFang.ttc",
        "C:/Windows/Fonts/msyh.ttc",
        "C:/Windows/Fonts/simhei.ttf",
    ]
    for path in candidates:
        fp = Path(path)
        if not fp.exists():
            continue
        try:
            pdfmetrics.registerFont(TTFont("E2EReportCJK", str(fp), subfontIndex=0))
            return "E2EReportCJK"
        except Exception:
            continue
    return "Helvetica"


def build_pdf(manifest: dict, run_dir: Path, out_path: Path) -> None:
    try:
        from reportlab.lib.pagesizes import A4
        from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
        from reportlab.lib.units import mm
        from reportlab.platypus import (
            Image,
            PageBreak,
            Paragraph,
            SimpleDocTemplate,
            Spacer,
        )
    except ImportError:
        raise SystemExit(
            "缺少 reportlab。请执行: pip install reportlab\n"
            "或: python3 -m pip install --user reportlab"
        )

    font = try_register_cjk_font()
    styles = getSampleStyleSheet()
    title_style = ParagraphStyle(
        "TitleCN",
        parent=styles["Title"],
        fontName=font,
        fontSize=18,
        leading=24,
        spaceAfter=12,
    )
    h2 = ParagraphStyle(
        "H2CN",
        parent=styles["Heading2"],
        fontName=font,
        fontSize=13,
        leading=18,
        spaceBefore=10,
        spaceAfter=6,
    )
    body = ParagraphStyle(
        "BodyCN",
        parent=styles["Normal"],
        fontName=font,
        fontSize=10,
        leading=14,
        spaceAfter=4,
    )
    meta = ParagraphStyle(
        "MetaCN",
        parent=body,
        textColor="#444444",
    )

    steps = manifest.get("steps") or []
    ok = sum(1 for s in steps if s.get("status") == "ok")
    fail = sum(1 for s in steps if s.get("status") == "fail")
    skip_like = len(steps) - ok - fail
    now_cn = datetime.now(TZ_CN).strftime("%Y-%m-%d %H:%M:%S Asia/Shanghai")

    doc = SimpleDocTemplate(
        str(out_path),
        pagesize=A4,
        leftMargin=18 * mm,
        rightMargin=18 * mm,
        topMargin=16 * mm,
        bottomMargin=16 * mm,
        title="招聘主流程测试报告",
    )
    story = []
    name = manifest.get("softwareName") or "信工所招聘管理系统"
    story.append(Paragraph(f"{name} — 招聘主流程测试报告", title_style))
    story.append(Paragraph(f"生成时间（Asia/Shanghai）：{now_cn}", meta))
    story.append(Paragraph(f"运行 ID：{manifest.get('runId', '')}", meta))
    story.append(Paragraph(f"环境：{manifest.get('baseURL', '')}", meta))
    story.append(Paragraph(f"开始：{manifest.get('startedAt', '')}", meta))
    story.append(Paragraph(f"结束：{manifest.get('finishedAt', '')}", meta))
    story.append(
        Paragraph(
            f"步骤统计：共 {len(steps)} 步，成功 {ok}，失败 {fail}，其他 {skip_like}",
            meta,
        )
    )
    story.append(
        Paragraph(
            "覆盖用例：TC-APPLY / TC-HR-COLLECT / TC-DEPT / TC-HR-FINAL / "
            "TC-INVITE / TC-ACCEPT / TC-RESULT（以 manifest 实际步骤为准）。",
            body,
        )
    )
    story.append(Spacer(1, 8))

    if not steps:
        story.append(
            Paragraph(
                "本 manifest 无步骤。若正式环境未实测，请本机 VPN 跑 Playwright 后再生成 PDF。",
                body,
            )
        )

    for s in steps:
        sid = s.get("id") or ""
        title = s.get("title") or ""
        status = s.get("status") or ""
        story.append(Paragraph(f"{sid} · {title} [{status}]", h2))
        story.append(Paragraph(f"角色：{s.get('roleLabel') or s.get('role') or ''}", body))
        if s.get("route"):
            story.append(Paragraph(f"路由：{s.get('route')}", body))
        story.append(Paragraph(f"操作：{s.get('action') or ''}", body))
        story.append(Paragraph(f"页面说明：{s.get('pageDesc') or ''}", body))
        if s.get("error"):
            story.append(Paragraph(f"错误：{s.get('error')}", body))
        shot = s.get("screenshot")
        if shot:
            img_path = run_dir / shot
            if img_path.exists():
                max_w, max_h = 160 * mm, 100 * mm
                try:
                    img = Image(str(img_path))
                    iw, ih = float(img.imageWidth), float(img.imageHeight)
                    scale = min(max_w / iw, max_h / ih, 1.0)
                    img.drawWidth = iw * scale
                    img.drawHeight = ih * scale
                    story.append(img)
                except Exception as exc:
                    story.append(Paragraph(f"（截图无法嵌入：{shot} — {exc}）", meta))
            else:
                story.append(Paragraph(f"（截图缺失：{shot}）", meta))
        story.append(Spacer(1, 6))

    story.append(PageBreak())
    story.append(Paragraph("附录：网络与执行约束", h2))
    story.append(
        Paragraph(
            "正式机 10.28.12.15 位于内网。云端 Agent 若无法连通，不得伪造实测通过；"
            "应在本机 VPN 执行 full-chain 后再用本脚本出 PDF。",
            body,
        )
    )

    doc.build(story)


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description="从 E2E manifest 生成招聘主流程 PDF 报告")
    parser.add_argument("--run-dir", type=str, default="", help="docs/e2e-runs/<runId>")
    parser.add_argument(
        "--out",
        type=str,
        default="",
        help="输出 PDF 路径（默认 run-dir/招聘主流程测试报告.pdf）",
    )
    args = parser.parse_args(argv)
    run_dir = Path(args.run_dir) if args.run_dir else find_latest_run()
    if not run_dir.is_absolute():
        run_dir = (ROOT / run_dir).resolve()
    manifest = load_manifest(run_dir)
    out = Path(args.out) if args.out else run_dir / "招聘主流程测试报告.pdf"
    if not out.is_absolute():
        out = (ROOT / out).resolve()
    out.parent.mkdir(parents=True, exist_ok=True)
    build_pdf(manifest, run_dir, out)
    print(f"Wrote {out}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
