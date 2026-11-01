const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
        Header, Footer, AlignmentType, HeadingLevel, BorderStyle, WidthType,
        ShadingType, VerticalAlign, PageNumber, LevelFormat } = require("docx");
const fs = require("fs");
const path = require("path");

const FONT_SONG = { ascii: "Times New Roman", eastAsia: "宋体", hAnsi: "Times New Roman" };
const FONT_HEI = { ascii: "Times New Roman", eastAsia: "黑体", hAnsi: "Times New Roman" };
const FONT_FANG = { ascii: "Times New Roman", eastAsia: "仿宋", hAnsi: "Times New Roman" };
const FONT_KAI = { ascii: "Times New Roman", eastAsia: "楷体", hAnsi: "Times New Roman" };

const PAGE_W = 11906;
const PAGE_H = 16838;
const MARGIN = { top: 1440, right: 1440, bottom: 1440, left: 1440 };
const TW = PAGE_W - MARGIN.left - MARGIN.right; // 9026

const thin = { style: BorderStyle.SINGLE, size: 4, color: "666666" };
const borders = { top: thin, bottom: thin, left: thin, right: thin };
const headerFill = "F2F2F2";

function run(text, opts = {}) {
  return new TextRun({
    text,
    font: opts.font || FONT_FANG,
    size: opts.size || 24,
    bold: !!opts.bold,
    italics: !!opts.italics,
    color: opts.color,
  });
}

function p(text, extra = {}) {
  return new Paragraph({
    alignment: extra.align || AlignmentType.JUSTIFIED,
    spacing: { line: 360, after: extra.after ?? 120, before: extra.before ?? 0 },
    indent: extra.indent !== false ? { firstLine: 480 } : undefined,
    children: [run(text, extra.run || {})],
  });
}

function pRuns(children, extra = {}) {
  return new Paragraph({
    alignment: extra.align || AlignmentType.JUSTIFIED,
    spacing: { line: 360, after: extra.after ?? 120, before: extra.before ?? 0 },
    indent: extra.indent !== false ? { firstLine: 480 } : undefined,
    children,
  });
}

function heading(text, level = 1) {
  return new Paragraph({
    heading: level === 1 ? HeadingLevel.HEADING_1 : HeadingLevel.HEADING_2,
    spacing: { before: level === 1 ? 360 : 280, after: 160 },
    children: [run(text, { font: FONT_HEI, size: level === 1 ? 32 : 28, bold: true })],
  });
}

function cell(text, width, opts = {}) {
  const paras = (Array.isArray(text) ? text : [text]).map((t) =>
    new Paragraph({
      alignment: opts.align || AlignmentType.LEFT,
      spacing: { after: 40, before: 40, line: 276 },
      children: [run(String(t), {
        font: opts.header ? FONT_HEI : FONT_SONG,
        size: opts.size || 21,
        bold: !!opts.header,
      })],
    })
  );
  return new TableCell({
    borders,
    width: { size: width, type: WidthType.DXA },
    shading: opts.fill ? { fill: opts.fill, type: ShadingType.CLEAR } : undefined,
    verticalAlign: VerticalAlign.CENTER,
    margins: { top: 60, bottom: 60, left: 80, right: 80 },
    columnSpan: opts.span,
    children: paras,
  });
}

function table(colWidths, rows) {
  return new Table({
    width: { size: TW, type: WidthType.DXA },
    columnWidths: colWidths,
    rows: rows.map((r, i) => new TableRow({
      children: r,
      tableHeader: i === 0,
    })),
  });
}

function headerRow(labels, widths) {
  return labels.map((lab, i) => cell(lab, widths[i], { header: true, fill: headerFill, align: AlignmentType.CENTER }));
}

const w1 = [700, 2200, 4526, 1600];
const w2 = [2200, 1400, 5426];
const w3 = [2400, 6626];

const children = [
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 200, after: 80 },
    children: [run("中国科学院信息工程研究所", { font: FONT_HEI, size: 28, bold: true })],
  }),
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 80, after: 200 },
    children: [run("信工所招聘管理系统弱口令风险整改报告", { font: FONT_HEI, size: 36, bold: true })],
  }),
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { after: 80 },
    children: [run("（对照《“弱口令”风险系统整改整治清单》附件2）", { font: FONT_KAI, size: 22 })],
  }),
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { after: 360 },
    children: [run("2026年11月1日", { font: FONT_SONG, size: 22 })],
  }),

  pRuns([
    run("填报单位：", { font: FONT_HEI, size: 24, bold: true }),
    run("中国科学院信息工程研究所", { font: FONT_FANG, size: 24 }),
    run("　　系统负责人：", { font: FONT_HEI, size: 24, bold: true }),
    run("　　　　　　（请补填）", { font: FONT_FANG, size: 24 }),
  ], { indent: false, align: AlignmentType.LEFT }),

  p("本报告对应上级下发的《“弱口令”风险系统整改整治清单》（附件2）。整改对象是信息工程研究所在用的招聘管理系统，对外访问地址为 https://10.28.12.15。能改的都已改完。附件3是“改不了才填”的说明，本系统不用交。"),

  heading("一、系统基本情况"),
  p("系统用于岗位发布、应聘报名、部门与人事审核、面试安排等招聘业务。管理端部署在所内服务器，报名入口已做 HTTPS 和访问控制。扫描记录显示外网能够打到登录接口，因此按互联网系统、业务生产系统类填报。系统功能简述：支撑研究所公开招聘与内部用人流程，包括岗位发布、在线报名、材料审核、面试邀请和录用相关办理。"),

  heading("二、整改前问题认定"),
  p("附件2把弱口令风险分成六类。对照本系统整改前的实现，六类均存在。登录主要是账号加密码，再配图形验证码，没有接科技云通行证。新建用户时前端只要求 8 位，后端不校验，弱口令可以入库。登录不检查是否超过 90 天。失败锁定只有 10 分钟，不按 IP 累计。库内是可逆加盐算法。生产未强制拒绝明文登录。跨境只封过两个扫描来源 IP。管理员重置成默认弱口令后可以一直用。"),

  heading("三、问题与整改总表"),
  table(w1, [
    headerRow(["序号", "原问题", "整改措施", "状态"], w1),
    [cell("1", w1[0], { align: AlignmentType.CENTER }), cell("口令过短，规则只在前端", w1[1]), cell("不少于 12 位，须含大小写、数字、特殊字符，且不能包含用户名。后端统一校验", w1[2]), cell("已整改", w1[3], { align: AlignmentType.CENTER })],
    [cell("2", w1[0], { align: AlignmentType.CENTER }), cell("没有弱口令字典", w1[1]), cell("后端黑名单拦截 123456、admin 等常见弱口令和变形", w1[2]), cell("已整改", w1[3], { align: AlignmentType.CENTER })],
    [cell("3", w1[0], { align: AlignmentType.CENTER }), cell("不检查、不强制换密", w1[1]), cell("生产 90 天过期；从未改过或到期，登录后必须改密", w1[2]), cell("已整改", w1[3], { align: AlignmentType.CENTER })],
    [cell("4", w1[0], { align: AlignmentType.CENTER }), cell("失败锁定偏短，不按 IP 累计", w1[1]), cell("账号和 IP 各计 5 次，锁定 30 分钟；Nginx 登录每分钟 10 次", w1[2]), cell("已整改", w1[3], { align: AlignmentType.CENTER })],
    [cell("5", w1[0], { align: AlignmentType.CENTER }), cell("主要是账号加密码", w1[1]), cell("密码 + 图形验证码 + RSA；生产对已绑手机再验短信", w1[2]), cell("已整改", w1[3], { align: AlignmentType.CENTER })],
    [cell("6", w1[0], { align: AlignmentType.CENTER }), cell("库内可逆加盐算法", w1[1]), cell("新口令改为 PBKDF2 不可逆哈希；旧口令登录成功后自动升级", w1[2]), cell("已整改", w1[3], { align: AlignmentType.CENTER })],
    [cell("7", w1[0], { align: AlignmentType.CENTER }), cell("生产未强制加密登录", w1[1]), cell("生产拒绝明文登录，必须走一次性 RSA；全站 HTTPS", w1[2]), cell("已整改", w1[3], { align: AlignmentType.CENTER })],
    [cell("8", w1[0], { align: AlignmentType.CENTER }), cell("跨境只封了两个扫描 IP", w1[1]), cell("应用内按国家码或国内 IPv4 段拦截；通报扫描源继续封禁", w1[2]), cell("已整改", w1[3], { align: AlignmentType.CENTER })],
    [cell("9", w1[0], { align: AlignmentType.CENTER }), cell("管理员重置写成默认弱口令", w1[1]), cell("重置后清空改密时间，下次登录必须改成合规口令", w1[2]), cell("已整改", w1[3], { align: AlignmentType.CENTER })],
    [cell("10", w1[0], { align: AlignmentType.CENTER }), cell("未接科技云通行证", w1[1]), cell("用户含所外应聘人员。用第 5 项多因子替代，不走附件3", w1[2]), cell("替代覆盖", w1[3], { align: AlignmentType.CENTER })],
  ]),

  new Paragraph({ spacing: { after: 80 } }),
  p("附件2“整改技术措施”勾选：多因子认证；限制复杂度并设置更新；弱口令校验；防暴破；加密存储传输；访问控制；限制境外 IP 登录。不勾选：隔离境内外账号数据。"),

  heading("四、技术落实说明"),
  p("新建用户、管理员改密、个人改密、注册、忘记密码，都走同一条后端规则，只改前端绕不开。弱口令命中直接拒绝。登录不因“旧口令偏弱”把人锁在门外，这类账号进系统后必须立刻改密。"),
  p("防暴破按账号和 IP 分别累计。已绑手机的账号，密码和验证码通过后，后台往绑定手机发登录短信，登录页出现验证码框。未绑手机的账号仍用密码加图形验证码加 RSA。开发环境短信二次认证默认关。"),
  p("库里新口令使用 PBKDF2 不可逆哈希。登录时先认新算法，认不出再按原来的加盐算法核对，对上了就改写成新哈希。跨境不依赖 Nginx 是否装了 GeoIP，应用自己带国内 IPv4 段；网关如果已经带了国家码，以国家码为准。"),

  heading("五、附件2填表口径"),
  table(w3, [
    headerRow(["栏目", "填写内容"], w3),
    [cell("系统归属单位", w3[0]), cell("中国科学院信息工程研究所", w3[1])],
    [cell("统一社会信用代码", w3[0]), cell("（请按所里统一口径填写）", w3[1])],
    [cell("系统名称", w3[0]), cell("信工所招聘管理系统", w3[1])],
    [cell("IP地址 / 域名", w3[0]), cell("10.28.12.15　/　https://10.28.12.15", w3[1])],
    [cell("系统类别", w3[0]), cell("业务生产系统类", w3[1])],
    [cell("是否互联网系统", w3[0]), cell("是", w3[1])],
    [cell("整改状态", w3[0]), cell("已整改", w3[1])],
    [cell("问题分类", w3[0]), cell("六类均可勾选（整改前均存在）", w3[1])],
  ]),

  heading("六、上线与复测"),
  p("发布顺序是先发后端，再发前端。用短口令新建或改密，应提示至少 12 位且含四类字符。用 123456、admin 应提示常见弱口令。连续输错 5 次，第 6 次应提示约 30 分钟后再试。从未改过密的测试号登录应弹出改密窗。生产用明文调登录应被拒绝。已绑手机的账号，密码通过后应出现二次认证框。从所内地址登录应成功。用境外或非国内公网地址访问登录应被拒绝。本轮未发现新的恶意样本。"),

  heading("七、结论"),
  p("信工所招聘管理系统已按附件2完成弱口令风险整改。口令复杂度、弱口令字典、90 天更换、防暴破、多因子认证、不可逆存储、加密传输、跨境登录控制均已落地。未接入科技云通行证的原因是用户中包含所外应聘人员，已用密码、图形验证码、RSA 和短信二次认证覆盖，不另行填报附件3。"),
  p("如因本系统口令问题发生网络安全事件，本单位按院所规定承担责任，并继续按 90 天更换和失败锁定策略维护。"),

  new Paragraph({ spacing: { before: 400, after: 80 }, indent: { firstLine: 0 }, children: [] }),
  new Paragraph({
    alignment: AlignmentType.RIGHT,
    spacing: { after: 80 },
    children: [run("填报单位（盖章）：中国科学院信息工程研究所", { font: FONT_FANG, size: 24 })],
  }),
  new Paragraph({
    alignment: AlignmentType.RIGHT,
    spacing: { after: 80 },
    children: [run("日期：2026年11月1日", { font: FONT_FANG, size: 24 })],
  }),
];

const doc = new Document({
  styles: {
    default: {
      document: { run: { font: FONT_FANG, size: 24 } },
    },
    paragraphStyles: [
      {
        id: "Heading1",
        name: "Heading 1",
        basedOn: "Normal",
        next: "Normal",
        quickFormat: true,
        run: { font: FONT_HEI, size: 32, bold: true },
        paragraph: { spacing: { before: 360, after: 160 }, outlineLevel: 0 },
      },
      {
        id: "Heading2",
        name: "Heading 2",
        basedOn: "Normal",
        next: "Normal",
        quickFormat: true,
        run: { font: FONT_HEI, size: 28, bold: true },
        paragraph: { spacing: { before: 280, after: 160 }, outlineLevel: 1 },
      },
    ],
  },
  sections: [{
    properties: {
      page: {
        size: { width: PAGE_W, height: PAGE_H },
        margin: MARGIN,
      },
    },
    headers: {
      default: new Header({
        children: [
          new Paragraph({
            alignment: AlignmentType.RIGHT,
            border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: "666666", space: 8 } },
            children: [run("信工所招聘管理系统　弱口令风险整改报告", { font: FONT_SONG, size: 18, color: "555555" })],
          }),
        ],
      }),
    },
    footers: {
      default: new Footer({
        children: [
          new Paragraph({
            alignment: AlignmentType.CENTER,
            border: { top: { style: BorderStyle.SINGLE, size: 6, color: "666666", space: 8 } },
            children: [
              run("第 ", { font: FONT_SONG, size: 18 }),
              new TextRun({ children: [PageNumber.CURRENT], font: FONT_SONG, size: 18 }),
              run(" 页　共 ", { font: FONT_SONG, size: 18 }),
              new TextRun({ children: [PageNumber.TOTAL_PAGES], font: FONT_SONG, size: 18 }),
              run(" 页", { font: FONT_SONG, size: 18 }),
            ],
          }),
        ],
      }),
    },
    children,
  }],
});

const out = path.join(__dirname, "弱口令风险系统整改报告-2026-11-01.docx");
Packer.toBuffer(doc).then((buf) => {
  fs.writeFileSync(out, buf);
  console.log("wrote", out);
});
