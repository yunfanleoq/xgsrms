# Cursor 技能：JeecgBoot E2E 文档采集与多角色测试

技能正文（Agent 自动加载）：

```
.cursor/skills/jeecg-e2e-doc-capture/SKILL.md
```

## 触发场景

用户说「跑 E2E」「Playwright 测试」「doc-capture」「生成操作说明书」「测试账号预置」「OCR 登录」「e2e-manual」等。

## 快速命令（xgsrms，内网 10.28.12.15）

```powershell
Set-Location "I:\cursorProjects\xgsrms\jeecgboot-vue3"
Copy-Item .env.e2e.example .env.e2e   # 首次
py -3 -m pip install -r ..\scripts\e2e-requirements.txt
npm install --legacy-peer-deps
npx playwright install chromium
npm run test:e2e:full
```

## 产出物

| 路径 | 说明 |
|------|------|
| `docs/e2e-runs/{runId}/测试报告.html` | 逐步截图 + 操作 + 页面说明 |
| `docs/e2e-manual/操作说明书-E2E采集-最新.md` | 软著结构操作手册 |
| `jeecgboot-vue3/.auth/*.json` | 六角色登录态（gitignore） |

## 安装为全局 Skill

```powershell
powershell -ExecutionPolicy Bypass -File .cursor\skills\jeecg-e2e-doc-capture\install-to-user-skills.ps1
```

## 相关文档

- 清单：`.cursor/skills/jeecg-e2e-doc-capture/checklist.md`
- 参考：`.cursor/skills/jeecg-e2e-doc-capture/reference.md`
- `docs/e2e-runs/README.md`
- `docs/自动化测试方案.md`
- `docs/测试账号预置指南.md`
- 安全扫描衔接：`jeecg-webapp-security-scan`、[[12-生产环境安全扫描报告-2026-06-01]]
