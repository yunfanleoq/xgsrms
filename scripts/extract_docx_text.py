"""
从 .docx 提取纯文本段落（需: pip install -r scripts/requirements-docx.txt）
用法: python scripts/extract_docx_text.py "信工所招聘管理系统操作手册.docx"
"""
import sys
from pathlib import Path

if hasattr(sys.stdout, "reconfigure"):
    try:
        sys.stdout.reconfigure(encoding="utf-8")
    except Exception:
        pass


def main() -> None:
    if len(sys.argv) < 2:
        print("用法: python extract_docx_text.py <docx路径>", file=sys.stderr)
        sys.exit(1)
    path = Path(sys.argv[1]).resolve()
    if not path.is_file():
        print(f"文件不存在: {path}", file=sys.stderr)
        sys.exit(1)
    try:
        from docx import Document
    except ImportError:
        print("请先安装: pip install -r scripts/requirements-docx.txt", file=sys.stderr)
        sys.exit(1)
    doc = Document(str(path))
    for i, p in enumerate(doc.paragraphs):
        text = (p.text or "").strip()
        if text:
            print(f"[{i}] {text}")


if __name__ == "__main__":
    main()
