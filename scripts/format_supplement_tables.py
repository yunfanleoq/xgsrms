# -*- coding: utf-8 -*-
"""与主表一致：2列表格首行两列+左侧列底纹 D0CECE；补充分节与总体前分页。"""
import sys
from typing import Optional

from docx import Document
from docx.oxml import OxmlElement
from docx.oxml.ns import qn

FILL = "D0CECE"


def set_cell_shading(cell, fill: Optional[str]) -> None:
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    for shd in list(tcPr.findall(qn("w:shd"))):
        tcPr.remove(shd)
    if not fill:
        return
    shd = OxmlElement("w:shd")
    shd.set(qn("w:val"), "clear")
    shd.set(qn("w:color"), "auto")
    shd.set(qn("w:fill"), fill)
    tcPr.append(shd)


def style_table_like_template(t) -> None:
    for ri, row in enumerate(t.rows):
        for ci, c in enumerate(row.cells):
            if ri == 0:
                set_cell_shading(c, FILL)
            else:
                set_cell_shading(c, FILL if ci == 0 else None)


def is_supplement_table(t) -> bool:
    try:
        n = min(4, len(t.rows))
        blob = "".join(
            t.rows[i].cells[j].text
            for i in range(n)
            for j in range(len(t.rows[i].cells))
        )
    except (IndexError, ValueError, AttributeError, Exception):
        return False
    return (
        "补充-0" in blob
        and "补充登记" in blob
        and "漏洞基本信息" in blob
    )


def main() -> None:
    path = r"I:\cursorProjects\xgsrms\Fw_ 招聘系统漏洞报告及测试记录\招聘系统-漏洞记录及修复情况记录表-汇总.docx"
    if len(sys.argv) >= 2:
        path = sys.argv[1]
    doc = Document(path)
    n = 0
    for t in doc.tables:
        try:
            if is_supplement_table(t):
                style_table_like_template(t)
                n += 1
        except Exception:
            continue

    def pb(p) -> None:
        p.paragraph_format.page_break_before = True

    for p in doc.paragraphs:
        t = p.text.strip()
        if not t:
            continue
        if "岗位发布界面 XSS" in t and "补充" in t:
            pb(p)
            continue
        if "用户名枚举漏洞1" in t and "信工所" in t:
            pb(p)
            continue
        if "用户名枚举漏洞2" in t and "信工所" in t:
            pb(p)
            continue
        if t.startswith("信工所 补充条（手机号枚举）"):
            pb(p)
            continue
        if "报名系统登录验证码" in t and "信工所" in t:
            pb(p)
            continue
        if t.startswith("【总体修复策略"):
            pb(p)
            continue

    doc.save(path)
    print(f"styled_tables={n} file={path}", file=sys.stderr)


if __name__ == "__main__":
    main()
