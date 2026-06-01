# -*- coding: utf-8 -*-
import sys
from docx import Document
from docx.enum.text import WD_PARAGRAPH_ALIGNMENT
from docx.oxml.ns import qn

def _p_text(p):
    ns = "{http://schemas.openxmlformats.org/wordprocessingml/2006/main}"
    return "".join(t.text for t in p.iter(f"{ns}t") if t.text)

def _tbl_text(tbl):
    return " ".join(_p_text(p) for p in tbl.iter(qn("w:p")))

def find_cz(els):
    for i, e in enumerate(els):
        if e.tag == qn("w:tbl") and "补充-CZ-001" in _tbl_text(e):
            return i
    return -1

def find_zong(els):
    for i, e in enumerate(els):
        if e.tag == qn("w:p") and _p_text(e).strip().startswith("【总体修复策略"):
            return i
    return -1

def fill(t, data, title="漏洞基本信息"):
    t.rows[0].cells[0].text = title
    t.rows[0].cells[1].text = ""
    for a, b in data:
        r = t.add_row()
        r.cells[0].text = a
        r.cells[1].text = b

def br(v, sid, se, desc, fix, pr, c):
    return [("漏洞名称", v),("漏洞编号", sid + "（补充登记，不占用主表 VULN-001 起序）"),("影响主机", "10.28.12.15（同主表）"),("漏洞路径", "见原报告与主表体例"),("业务系统", "信工所招聘管理系统（JeecgBoot / xgszp-boot）"),("漏洞等级", se),("漏洞描述", desc),("修复建议", fix),("漏洞证明", pr),("修复与验证信息", ""),("系统负责人", "刘彦良"),("研发厂商", "中科同为科技（北京）有限责任公司"),("修复负责人", "刘彦良"),("修复方案说明", "与主表同类加固一致，以实际验收为准。"),("涉及修改的文件/代码", c),("修复版本/补丁号", "xgszp-boot / jeecgboot-vue3 当前开发分支"),("修复时间", "待补录"),("上线时间", "待补录"),("修复方自测说明", "建议安全复测后签字。"),("修复确认和闭环", ""),("复测结果", "建议安全签字。"),("漏洞发现人确认", "待填写"),("安全团队确认", "待填写"),("系统负责人确认", "待填写"),("最终确认意见", "待勾选"),("完成时间", "待填写")]

SUPP = [
( "补充-02", "信工所招聘管理系统 漏洞记录补充条（报告名 岗位发布界面 XSS）", "岗位发布/富文本 XSS", "中危", "【已修复】与刘彦良确认；富文本消毒、CSP 见 SECURITY-REMEDIATION。", "白名单/消毒", "原测试/确认", "岗位相关 Vue/富文本与后端" ),
( "补充-03", "信工所 补充条（用户名枚举漏洞1）", "用户枚举(1)", "中危", "【已修复】与刘彦良确认；统一提示、锁、验证码、限流。", "同左", "同左", "SysUser 与 firewall" ),
( "补充-04", "信工所 补充条（用户名枚举漏洞2）", "用户枚举(2)", "中危", "同补充-03；报告分两条故单列。", "同左", "同左", "同左" ),
( "补充-05", "信工所 补充条（手机号枚举）", "手机号枚举/推断", "中危", "【已修复】与刘彦良确认；发码/绑定防刷、不泄注册态。", "防刷/限流", "同左", "短信/验证码/用户" ),
( "补充-06", "信工所 补充条（报名系统登录验证码绕过）", "验证码绕过或配置关闭", "中危", "【已修复】与确认；生产须启用 enableLoginCaptcha、LoginController 校验。", "生产强制验证码", "同左", "LoginController, yml, 登录页" ),
]

def main():
    src = r"I:\cursorProjects\xgsrms\Fw_ 招聘系统漏洞报告及测试记录\招聘系统-漏洞记录及修复情况记录表-汇总-补充条.docx"
    dst = r"I:\cursorProjects\xgsrms\Fw_ 招聘系统漏洞报告及测试记录\招聘系统-漏洞记录及修复情况记录表-汇总.docx"
    doc = Document(src)
    body = doc._element.body
    els = list(body)
    cz = find_cz(els)
    zi = find_zong(els)
    if cz < 0 or zi < 0:
        print("CZ or 总体 not found, try 汇总 with supplements", file=sys.stderr)
        sys.exit(1)
    for k in range(zi - 1, cz, -1):
        body.remove(els[k])
    from docx.oxml import OxmlElement
    els = list(body)
    cz = find_cz(els)
    zi = find_zong(els)
    cz_el = list(body)[cz]
    zong_el = list(body)[zi]
    sp1 = OxmlElement("w:p")
    sp2 = OxmlElement("w:p")
    cz_el.addnext(sp1)
    sp1.addnext(sp2)
    ins = sp2
    for d in SUPP:
        sid, head, v, se, *x = d
        desc, fix, pr, c = x[0], x[1], x[2], x[3]
        h = doc.add_paragraph(head)
        h.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
        e = doc.add_paragraph()
        t = doc.add_table(1, 2)
        fill(t, br(v, sid, se, desc, fix, pr, c))
        b = doc._element.body
        for el in (h._p, e._p, t._tbl):
            b.remove(el)
        ins.addnext(h._p)
        h._p.addnext(e._p)
        e._p.addnext(t._tbl)
        ins = t._tbl
    p_end = OxmlElement("w:p")
    r = OxmlElement("w:r")
    te = OxmlElement("w:t")
    te.text = "（上为补充-02 至 补充-06，顺序自上而下。）"
    r.append(te)
    p_end.append(r)
    ins.addnext(p_end)
    doc.save(dst)
    print("OK", dst)

if __name__ == "__main__":
    main()