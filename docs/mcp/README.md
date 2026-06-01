# 项目级 MCP 配置（每仓库一份）

原则：**文档与 MCP 根路径都在本仓库内**，不要用全局 `~/.cursor/mcp.json` 指向其他项目的 `docs`。

## xgsrms 约定

| MCP 服务名 | 作用 | 允许访问路径 |
|------------|------|----------------|
| `codegraph` | 代码符号/调用/影响面 | 仓库根（索引在 `.codegraph/`） |
| `obsidian-vault` | 读写项目 Obsidian 笔记 | `docs/obsidian/` |

生效文件：**`.cursor/mcp.json`**（已纳入 Git，换机克隆后即有）。

## 新机器 / 克隆后

1. 将 `docs/mcp/cursor-mcp.template.json` 中的 `{{REPO_ROOT}}` 替换为本机仓库绝对路径（Windows 用反斜杠，如 `I:\\cursorProjects\\xgsrms`）
2. 保存为 `.cursor/mcp.json`，或运行：

```powershell
$root = (Resolve-Path (Join-Path $PSScriptRoot '..\\..')).Path
$tpl = Get-Content (Join-Path $PSScriptRoot 'cursor-mcp.template.json') -Raw
$tpl = $tpl -replace '\{\{REPO_ROOT\}\}', $root.Replace('\', '\\')
$outDir = Join-Path $root '.cursor'
New-Item -ItemType Directory -Force -Path $outDir | Out-Null
$tpl | Set-Content (Join-Path $outDir 'mcp.json') -Encoding UTF8
Write-Host "Wrote $outDir\mcp.json"
```

3. 若未建 CodeGraph 索引：`codegraph init --index`（在仓库根）
4. **重启 Cursor**，在 MCP 中启用本项目的 `obsidian-vault`、`codegraph`

## 与全局 MCP 的关系

`~/.cursor/mcp.json` 里若有 `twrpa-obsidian-vault`、`minicpm-obsidian-vault` 等**其他项目**路径，在打开 xgsrms 时应在 Cursor MCP 设置中**关闭**它们，只保留本项目 `.cursor/mcp.json` 中的 `obsidian-vault`，避免 Agent 写到错误目录。

## 其他 Jeecg/业务项目复用

每个仓库建议同样结构：

```text
your-project/
├── .cursor/mcp.json          # codegraph --path + obsidian-vault → docs/obsidian
├── docs/obsidian/            # Obsidian 知识库（可提交）
├── docs/mcp/                 # 模板与说明（可提交）
└── .codegraph/               # 本地索引（gitignore）
```

复制本目录 `docs/mcp/cursor-mcp.template.json` 与 `docs/obsidian/README.md` 到新项目后改项目名即可。
