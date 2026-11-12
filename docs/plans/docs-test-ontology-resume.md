---
name: Docs Test Ontology Resume
overview: 先对齐招聘主流程与现有文档资产，补齐「操作文档→测试用例→测试报告→正式环境实测 PDF」；同步把本体论挂到后台菜单；再简化投递页；PDF 自动填表默认 Apache Tika 本地抽文本+规则（智谱可选）。
todos:
  - id: doc-master
    content: 写操作总册 + 修正 obsidian 业务域状态图与三分册角色称呼对齐
    status: completed
  - id: testcases
    content: 编写主流程测试用例与测试计划；补全 full-chain 面试结果步骤
    status: completed
  - id: prod-pdf
    content: 正式环境跑三角色全链路并生成 PDF 测试报告（云端无 VPN：仅交付 PDF 脚本+样例；需本机实测）
    status: cancelled
  - id: ontology-menu
    content: sys_permission 菜单种子 + 授权 + 冒烟本体论/工作流入口
    status: completed
  - id: resume-pdf-local
    content: 精简投递页；PDF 默认 Tika 抽文本+规则填表（智谱改为可选开关）
    status: completed
isProject: false
---

# 招聘系统文档、实测与投递能力改造计划

## 现状结论（已摸清）

### 角色与主流程（以代码/手册为准）

三角色对应关系：

| 业务称呼 | 系统角色/账号 | 典型菜单 |
|---------|--------------|---------|
| 应聘者 | 求职者 `test-qz` | 门户投递、我的申请、我的面试 |
| 部门招聘主管 | 部门岗聘主管 `test-bm` | 简历审核→部门审核；岗位发布申请 |
| 人事处招聘主管/专员 | 人事处 `test-rl` | 人力处收集/人力处审核、面试邀请与结果 |

主流程（与 [docs/standard-manual/03-人力处审核操作手册.md](docs/standard-manual/03-人力处审核操作手册.md)、[jeecgboot-vue3/tests/e2e/flows/recruitment-full-chain.spec.ts](jeecgboot-vue3/tests/e2e/flows/recruitment-full-chain.spec.ts) 一致）：

```mermaid
flowchart LR
  apply[应聘者投递] --> hrCollect[人力处收集]
  hrCollect --> dept[部门审核]
  dept --> hrFinal[人力处审核]
  hrFinal --> invite[面试邀请]
  invite --> accept[应聘者接受]
  accept --> result[面试通过或未通过]
```

注意：[docs/obsidian/04-招聘业务域.md](docs/obsidian/04-招聘业务域.md) 里的状态图把「部门审核」写在「人力收集」之前，与真实菜单链路不一致，文档修订时要改掉。

### 操作文档：已有，但偏散、需收敛

已有资产（不是从零写）：

- 四分册操作手册：[docs/standard-manual/01~04](docs/standard-manual/)
- 角色/账号：[docs/系统角色及测试计划.md](docs/系统角色及测试计划.md)、[docs/测试账号预置指南.md](docs/测试账号预置指南.md)
- 业务/页面索引：[docs/obsidian/04](docs/obsidian/04-招聘业务域.md)、[06](docs/obsidian/06-前端页面映射.md)、[07](docs/obsidian/07-角色与权限.md)
- E2E 采集手册：[docs/e2e-manual/](docs/e2e-manual/)

缺口：缺少一份「总册」把角色×页面×状态流转钉死；测试用例与「按用例出的测试报告 PDF」未与正式环境发版后的现状对齐。

### 本体论：后端+页面已有，后台菜单需补强

- 后端目录/工作流：`jeecg-boot/.../recruitment/ontology/`（`RmsHireOntologyCatalog`、`RmsAgentWorkflowService`）
- 前端：`jeecgboot-vue3/src/views/rmsAgent/RmsAgentWorkflow.vue`（含「本体」Tab），并已嵌进部门/人力审核列表、面试邀请页
- 静态路由：`/rms/agent-workflow`（[staticRouter.ts](jeecgboot-vue3/src/router/routes/staticRouter.ts)）
- 缺口：未见稳定的 `sys_permission` SQL 菜单种子；需给 admin/人事补「招聘智能体 / 本体论」可见菜单与权限码

### PDF 自动填表：当前是智谱云，不是 DeepSeek

实现：[`XgsUserResumeFileServiceImpl.analysisResume`](jeecg-boot/jeecg-module-recruitment/src/main/java/org/jeecg/modules/recruitment/resume/service/impl/XgsUserResumeFileServiceImpl.java) —— 上传文件到 **智谱 ClientV4**，再用 **GLM-4-Plus** 按 JSON 模板抽取。未配置则直接抛「智谱 AI 未配置」。

投递页复杂点：门户/申请弹窗复用了偏「后台表单模板」的组件（如 `home/position/XgsPositionApplyForm.vue`、`XgsPositionApplyFormNew.vue`、`XgsUserPositionApplyModal.vue`），字段多、侧栏模块导航重。

**已拍板：默认本地，抽文本用 Apache Tika。**

- `resume.parse.mode` 默认 = `tika`（Tika 抽文本 + 规则/正则映射到现有简历 JSON 模板）
- 智谱 GLM 路径保留为可选 `zhipu`，默认不启用
- 扫描件 OCR / 本地 Ollama 本期不做，需要时再加

实现落点：在 recruitment 模块新增本地解析服务（依赖或抽取 Tika，与 `airag` 已有 `tika-parser-pdf-module` 对齐），改写 [`XgsUserResumeFileServiceImpl.analysisResume`](jeecg-boot/jeecg-module-recruitment/src/main/java/org/jeecg/modules/recruitment/resume/service/impl/XgsUserResumeFileServiceImpl.java)：`tika` 模式下不再要求 `ClientV4` Bean。

### 本地 PDF 分析方法（决策）

| 档位 | 做法 | 本期 |
|------|------|------|
| L1 Tika 抽文本 | 复用/引入 Apache Tika PDF parser | **默认启用** |
| L2 规则结构化 | 手机/邮箱/身份证/教育·工作分段 → JSON 模板 | **默认启用** |
| L3 OCR | Tesseract 等 | 不做 |
| L4 本地 LLM | Ollama | 不做 |
| L5 智谱云 | 现有 GLM 链路 | 可选开关，默认关 |

---

## 阶段安排

### 阶段 A — 系统操作总册（文档收敛）

新增一份总册（建议路径 `docs/standard-manual/00-招聘系统操作总册.md`），内容固定：

1. 角色定义与账号矩阵（对齐三分册称呼）
2. 页面地图（门户 / 求职者后台 / 部门 / 人事）
3. 主流程图 + 各节点菜单路径 + 状态含义
4. 岗位发布子流程（部门申请→人事审核→发布）与简历审核子流程分开写清
5. 链到现有 01~04 分册，避免重复长文

同步修正 [docs/obsidian/04-招聘业务域.md](docs/obsidian/04-招聘业务域.md) 状态图顺序。

### 阶段 B — 测试用例 + 测试文档

在 `docs/` 下新增：

- `docs/testcases/招聘主流程测试用例.md`：用例编号 TC-APPLY / TC-HR-COLLECT / TC-DEPT / TC-HR-FINAL / TC-INVITE / TC-ACCEPT / TC-RESULT，含前置、步骤、期望、账号
- `docs/testcases/招聘主流程测试计划.md`：环境 `https://10.28.12.15`、账号、数据策略（尽量用未投递岗位；已投递则跳过投递步并在报告标注）

复用已有 Playwright 全链路 [recruitment-full-chain.spec.ts](jeecgboot-vue3/tests/e2e/flows/recruitment-full-chain.spec.ts)，并补「面试结果（通过/未通过）」步骤（当前停在接受邀请）。

### 阶段 C — 按用例在正式环境实测并出 PDF

1. VPN + `.env.e2e` 指向 `https://10.28.12.15`
2. `setup`（OCR 登录三角色）→ `full-chain`
3. 用截图+manifest 生成 **PDF 测试报告**（reportlab，目录如 `docs/e2e-runs/<runId>/招聘主流程测试报告.pdf`）
4. 弱口令策略若挡登录：用 admin 重置或按强制改密处理，并记入报告

### 阶段 D — 本体论后台菜单

1. 写 `scripts/sql/mysql/` 菜单种子：父菜单「招聘智能体」、子菜单「工作流/本体」→ component `rmsAgent/RmsAgentWorkflow`，授权 admin + 人事角色
2. 生产执行 SQL 或后台菜单管理导入；冒烟打开 `/rms/agent-workflow` 与审核页内嵌本体 Tab
3. 总册中增加「智能体/本体」一小节说明

### 阶段 E — 投递页简化 + PDF 本地化

1. **投递 UX**：为门户「在线申请」做精简版表单（只保留应聘必填：基本信息、教育、工作、登记照、附件）；高级/冷门字段折叠或后置；去掉后台模板式多余导航噪音
2. **解析链路（默认 Tika）**：
   - Apache Tika 抽取 PDF 纯文本
   - 规则引擎映射到现有 JSON 模板字段并回填表单
   - 配置：`resume.parse.mode=tika|zhipu`，**默认 `tika`**
   - 前端「自动填充」按本地模式缩短超时，并提示已填/未识别字段
3. 不依赖 DeepSeek；智谱仅作可选；相关说明文档后续改为「默认本地 Tika、云端可选」

---

## 建议执行顺序

A → B → C（先交付可读操作总册 + 用例 + 正式环境实测 PDF）→ D → E。

阶段 C 不阻塞 D/E 的设计，但简历页大改建议在 C 基线报告之后做，避免「测的还是旧复杂页」。
