# Cursor 技能：xgsrms 正式环境构建与部署

技能正文（Agent 自动加载）：

```
.cursor/skills/xgsrms-prod-deploy/SKILL.md
```

## 触发场景

用户说「构建并部署到正式环境」「上线 xgs-server」「发布生产」等。

## 快速命令（仓库根，需 VPN 连通 xgs-server）

```powershell
Set-Location "I:\cursorProjects\xgsrms"
powershell -ExecutionPolicy Bypass -File .\scripts\build-release.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\deploy-backend-xgs-server.ps1
Set-Location "I:\cursorProjects\xgsrms\jeecgboot-vue3"
powershell -ExecutionPolicy Bypass -File .\scripts\deploy-frontend-xgs-server.ps1
```

## 详细说明

见 [[08-部署与运维]]、`docs/obsidian/08-部署与运维.md`
