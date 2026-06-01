# Skill: CodeGraph + Obsidian 项目文档

在 **xgsrms** 仓库中，将 CodeGraph 代码图谱与 Obsidian 知识库（`docs/obsidian/`）联动使用。

## 何时触发

- 用户要求：代码分析、架构梳理、调用链、影响面、模块地图、**并**要 Obsidian/项目文档
- 用户提到：CodeGraph、code graph、代码图谱、结合 Obsidian
- 全库分析、招聘模块深挖、前后端映射验证

## 前置条件

1. 仓库根目录已 `codegraph init` 且索引可用（`codegraph status` 显示 OK）
2. 若未初始化：询问用户后执行 `codegraph init --index`（大仓库需数分钟）
3. Cursor：已 `codegraph install -t cursor -l local -y`（生成 `.cursor/mcp.json` + `rules/codegraph.mdc`）

## 工具优先级（必须遵守 CodeGraph 官方意图）

| 意图 | 优先 | 避免 |
|------|------|------|
| 符号定义/搜索 | `codegraph_search` / CLI `codegraph query` | 先 grep 类名 |
| 功能/任务上下文 | `codegraph_context` | 大量 Read 扫目录 |
| 调用关系 | `codegraph_callers` / `callees` | 手工翻 Controller |
| 修改影响 | `codegraph_impact` | 凭猜测列文件 |
| 多符号源码 | `codegraph_explore` | 循环 `codegraph_node` |
| 字面量/注释 | Read / rg | 用 codegraph 搜字符串 |

MCP 不可用时，一律用 **CLI** 同等命令，不要跳过图谱分析。

## 与 Obsidian 的协作流程

```text
1. codegraph status          # 确认索引；有 pending 则 sync
2. codegraph_context "<任务>" # 或 query / impact / callers
3. 必要时 Read 1–2 个关键文件核对业务细节
4. 更新 docs/obsidian/ 对应笔记（wikilink [[05-后端API清单]] 等）
5. 若新建主题：在 [[00-Index]] 增加链接
```

### 应写入 Obsidian 的内容

- 类/方法/表名 + **仓库相对路径**
- 前后端 API 前缀对照（可从 `*.api.ts` 的 `enum Api` 提取）
- `impact` 列出的受影响符号（重构前必读）
- Mermaid 流程图（业务流，非替代 call graph）

### 不要写入 Obsidian 的内容

- 整页粘贴 codegraph 原始输出（过长、易过期）
- `.codegraph/` 数据库文件
- `local-signature.env` 等密钥

## xgsrms 重点查询模板

| 主题 | context / query 示例 |
|------|----------------------|
| 岗位投递 | `XgsPositionApply doPositionApply` |
| 岗位发布 | `XgsPositionsController apply check post` |
| 审核意见 | `XgsFlowOpinions submitByApplyId` |
| 简历分表 | `XgsResumeBase XgsResumeFG XgsResumeTJ` |
| 门户鉴权 | `RecruitmentPortalAuthUtil` |
| 前端审批 | `xgsApplyVerify dep_verify rcl_verify` |

## 输出语言

- 对用户：中文
- Obsidian 笔记：中文标题 + 英文标识符（类名、路径）

## 相关文件

| 路径 | 说明 |
|------|------|
| `docs/obsidian/11-代码图谱与CodeGraph.md` | 图谱统计与实证 |
| `docs/obsidian/00-Index.md` | 总索引 |
| `.cursor/mcp.json` | 本项目 `obsidian-vault` → `docs/obsidian/` |
| `docs/mcp/README.md` | 每仓库项目级 MCP 约定 |
| `.cursor/rules/codegraph.mdc` | Cursor 自动加载的规则 |
| `docs/cursor-skill-jeecg-security-scan.md` | 安全扫描（可与 impact 结合） |

写入 Obsidian 时优先使用 MCP **`obsidian-vault`**（本项目路径），勿使用全局里其他项目的 Obsidian MCP。

## 维护

- 依赖升级或大规模重构后：`codegraph sync`，刷新 [[11-代码图谱与CodeGraph]] 中的统计数据（`codegraph status`）
- 换机：重新 `codegraph init --index` + `codegraph install -t cursor -l local -y`
