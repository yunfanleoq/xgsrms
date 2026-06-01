#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Jeecg 图形验证码 OCR（ddddocr）。用法: py e2e_captcha_ocr.py <base64字符串或图片路径>"""
from __future__ import annotations

import base64
import re
import sys
from pathlib import Path


def _load_image(arg: str) -> bytes:
    if Path(arg).is_file():
        return Path(arg).read_bytes()
    raw = arg.strip()
    if raw.startswith("data:"):
        raw = raw.split(",", 1)[1]
    return base64.b64decode(raw)


def recognize(data: bytes) -> str:
    try:
        import ddddocr
    except ImportError as exc:
        raise SystemExit("缺少 ddddocr，请执行: py -3 -m pip install ddddocr") from exc

    ocr = ddddocr.DdddOcr(show_ad=False)
    text = ocr.classification(data) or ""
    text = re.sub(r"[^a-zA-Z0-9]", "", text)
    return text[:4]


def main() -> None:
    if len(sys.argv) < 2:
        raise SystemExit("用法: py e2e_captcha_ocr.py <base64或图片路径>")
    text = recognize(_load_image(sys.argv[1]))
    if len(text) < 4:
        raise SystemExit(f"OCR 结果过短: {text!r}")
    print(text)


if __name__ == "__main__":
    main()
