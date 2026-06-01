# -*- coding: utf-8 -*-
import sys
sys.stdout.reconfigure(encoding="utf-8")
from docx import Document
from docx.enum.text import WD_PARAGRAPH_ALIGNMENT
path = r"I:\cursorProjects\xgsrms\Fw_ 招聘系统漏洞报告及测试记录\招聘系统-漏洞记录及修复情况记录表-汇总-补充条.docx"

def anchor(doc):
    for p in doc.paragraphs:
        if p.text.strip().startswith("【总体修复策略"):
            return p
    raise SystemExit("no anchor")

def fill(t, rows, title="漏洞基本信息"):
    t.rows[0].cells[0].text = title
    t.rows[0].cells[1].text = ""
    for a,b in rows[1:]:
        r = t.add_row()
        r.cells[0].text = a
        r.cells[1].text = b

def br(v, sid, se, desc, f, pr, c):
    return [("漏洞名称", v),("漏洞编号", sid + " (补充登记，不占用VULN-001起主序)"),("影响主机", "10.28.12.15"),("漏洞路径", "见原报告与主表体例"),("业务系统", "信工所招聘管理系统"),("漏洞等级", se),("漏洞描述", desc),("修复建议", f),("漏洞证明", pr),("修复与验证信息", ""),("系统负责人", "刘彦良"),("研发厂商", "中科同为（北京）有限责任公司"),("修复负责人", "刘彦良"),("修复方案说明", "与当前状态、主文同类加固一致"),("涉及修改的文件/代码", c),("修复版本/补丁号", "xgszp-boot / jeecgboot-vue3 当前分支"),("修复时间", "待补录"),("上线时间", "待补录"),("修复方自测说明", "建议安全复测后签字"),("修复确认和闭环", ""),("复测结果", "建议安全签字"),("漏洞发现人确认", "待填写"),("安全团队确认", "待填写"),("系统负责人确认", "待填写"),("最终确认意见", "待勾选"),("完成时间", "待填写")]

SUPP = [
( "补充-02", "信工所招聘管理系统 — 漏洞记录补充条 (报告名: 岗位发布界面 XSS; 补充-02)", "岗位发布/富文本 XSS 风险", "中危", "【已修复】与刘彦良沟通确认; 富文本消毒、CSP 见仓库 SECURITY-REMEDIATION; 见补充分节正文。", "白名单/消毒; CSP 可选", "原测试/确认", "岗位相关 Vue/富文本与后端" ),
( "补充-03", "信工所 — 补充条 (用户名枚举漏洞1; 补充-03)", "用户枚举 (报告条目1)", "中危", "【已修复】与刘彦良确认; 统一错误文案/锁/验证码/限流; checkOnlyUser 等", "同左", "同左", "SysUser 等、firewall" ),
( "补充-04", "信工所 — 补充条 (用户名枚举漏洞2; 补充-04)", "用户枚举 (报告条目2)", "中危", "同补充-03; 与报告分两条叙述", "同补充-03", "同左", "同左" ),
( "补充-05", "信工所 — 补充条 (手机号枚举; 补充-05)", "手机号可枚举/推断", "中危", "【已修复】与刘彦良沟通确认; 短信/发码/绑定侧限流与无注册态提示", "防刷/限流", "同左", "短信/验证码/用户" ),
( "补充-06", "信工所 — 补充条 (报名系统登录验证码绕过; 补充-06)", "验证码可绕过/配置关闭", "中危", "【已修复】与确认; 生产须启用 enableLoginCaptcha、LoginController 校验", "生产强制开启验证码", "同左", "LoginController, yml, 登录页" ),
]
doc = Document(path)
an = anchor(doc)
for d in reversed(SUPP):
    sid,hd,v,se,de,fx,pf,cd = d
    t = doc.add_table(1,2)
    fill(t, br(v,sid,se,de,fx,pf,cd), "漏洞基本信息")
    e = doc.add_paragraph()
    h = doc.add_paragraph(hd)
    h.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    an = anchor(doc)
    an._p.addprevious(t._tbl)
    an = anchor(doc)
    an._p.addprevious(e._p)
    an = anchor(doc)
    an._p.addprevious(h._p)
# stats + list
for p in doc.paragraphs:
    t = p.text
    if "本汇总表条目" in t and "已修复：37" in t:
        p.clear()
        p.add_run("本汇总主表 37 条; 文首 5 处以文末 补充-02~06 单页补充登记 合计书面 42 条(主与补充 编号体系 并列、不互占序号)")
    if t.strip().startswith("【已修复】编号：") and "补充-02" not in t:
        p.add_run("，补充-02，补充-03，补充-04，补充-05，补充-06")
doc.save(path)
print("ok")