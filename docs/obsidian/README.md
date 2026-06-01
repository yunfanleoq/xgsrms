# xgsrms Obsidian 知识库

本目录为 **信工所招聘管理系统（xgsrms）** 的项目文档，采用 Obsidian 约定（wikilink、`#标签`、Mermaid）。

## Cursor MCP（项目内文档）

本仓库 **Obsidian 文档只写在本目录**，由项目级 MCP 暴露给 Agent：

| 项 | 值 |
|----|-----|
| MCP 服务名 | `obsidian-vault` |
| 配置位置 | `.cursor/mcp.json` |
| 允许路径 | `docs/obsidian/`（本目录） |

安装/换机：见 `docs/mcp/README.md` 或执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\docs\mcp\install-project-mcp.ps1
```

在 Cursor 中请**关闭**全局 MCP 里指向其他项目（如 TWRPA、MiniCPM）的 Obsidian 服务，避免写错 vault。

## 在 Obsidian 中打开

1. Obsidian → **打开文件夹作为仓库** → 选择本目录：`docs/obsidian`
2. 或将本目录软链接到你的主 Vault，例如：
   ```powershell
   New-Item -ItemType Junction -Path "D:\你的Vault\xgsrms" -Target "I:\cursorProjects\xgsrms\docs\obsidian"
   ```

## 文档地图

从 [[00-Index|首页索引]] 开始浏览。

## 与仓库其他文档的关系

| 本库笔记 | 仓库内对应 |
|----------|------------|
| [[07-角色与权限]] | `docs/standard-manual/` |
| [[09-安全与扫描]] | `docs/cursor-skill-jeecg-security-scan.md` |
| [[08-部署与运维]] | `scripts/*.ps1`、`jeecgboot-vue3/scripts/` |

## CodeGraph 联动

本库与 **CodeGraph** 代码图谱配合使用（见 [[11-代码图谱与CodeGraph]]）：

- Agent 技能：`docs/cursor-skill-codegraph-obsidian.md`
- 索引目录：`.codegraph/`（本地生成，不提交 Git）

## 维护说明

- 源码变更后：重点更新 [[05-后端API清单]]、[[06-前端页面映射]]；大改用 `codegraph sync` 后刷新 [[11-代码图谱与CodeGraph]]
- 版本升级：同步 [[02-技术栈与架构]] 与 `docs/UPGRADE-JeecgBoot-3.9.md`

---
tags: #xgsrms #meta
