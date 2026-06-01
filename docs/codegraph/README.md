# xgsrms CodeGraph 配置说明

[CodeGraph](https://github.com/colbymchenry/codegraph) 为本仓库提供符号索引、调用关系与影响面分析，并与 `docs/obsidian/` 项目文档联动。

## 一键启用（推荐）

在仓库根目录 PowerShell：

```powershell
Set-Location I:\cursorProjects\xgsrms
codegraph init --index
codegraph install -t cursor -l local -y
```

然后 **重启 Cursor**，在 MCP 面板确认 `codegraph` 已连接。

## 生成文件

| 路径 | 说明 | Git |
|------|------|-----|
| `.codegraph/` | SQLite 索引（~119MB） | 已 ignore |
| `.cursor/mcp.json` | MCP 服务配置 | `.cursor/` 已 ignore |
| `.cursor/rules/codegraph.mdc` | Agent 使用规范 | 同上 |

换机克隆后需重新执行「一键启用」。

## MCP 配置片段（手工添加时）

若未运行 `install`，可在项目 `.cursor/mcp.json` 中加入：

```json
{
  "mcpServers": {
    "codegraph": {
      "type": "stdio",
      "command": "codegraph",
      "args": ["serve", "--mcp", "--path", "I:\\cursorProjects\\xgsrms"]
    }
  }
}
```

将 `path` 改为你的仓库绝对路径。Cursor 必须用 `--path`，否则 MCP 找不到 `.codegraph/`。

## 日常命令

```powershell
codegraph status
codegraph sync
codegraph context "岗位申请 XgsPositionApply"
codegraph query "XgsPositionApplyController"
codegraph impact "XgsFlowOpinionsController"
```

## 文档输出

- Obsidian 知识库：`docs/obsidian/`（从 `00-Index.md` 进入）
- 项目 MCP（CodeGraph + Obsidian）：`docs/mcp/README.md`
- Agent 技能：`docs/cursor-skill-codegraph-obsidian.md`
