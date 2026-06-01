# Cursor 技能：Jeecg 类 Web 安全扫描（全项目）

本仓库不重复存放 Skill 正文，避免与全局技能冲突。

## 安装位置（一次即可）

```
%USERPROFILE%\.cursor\skills\jeecg-webapp-security-scan\
```

已在当前机器配置则 **任意 Cursor 项目** 均可使用。

## 用法

```text
@jeecg-webapp-security-scan 对 https://host/xxx-boot 做 quick 扫描，预发，账号 ...
@jeecg-webapp-security-scan 执行 L3 e2e-security 全量回归
```

可选：在项目根生成范围文件

```powershell
python $env:USERPROFILE\.cursor\skills\jeecg-webapp-security-scan\scripts\init-security-scan-scope.py
```

## Skill 文件索引（2026-06-01 扩充）

| 文件 | 用途 |
|------|------|
| `SKILL.md` | 主流程 + L3 Step 5 |
| `checklist.md` | S0～S11（含 E2E 清单） |
| `e2e-automation.md` | **L3 Playwright 自动化**（Authz/File/Rep/Auth-02/Api-03） |
| `fixes-playbook.md` | **经 xgsrms 验证的修复模式** |
| `reference.md` | 路径、grep、响应判定 |
| `examples.md` | 含 xgsrms L3 示例 5/6 |

## 本仓库（xgsrms）附加资料

| 资源 | 路径 |
|------|------|
| 策略 | `Fw_ 招聘系统漏洞报告及测试记录/同类应用系统-安全扫描策略.md` |
| 验证脚本 | `scripts/security-remediation-verify.ps1` |
| **Agent 记忆** | `docs/obsidian/09-安全与扫描.md` |
| 生产报告 | `docs/obsidian/12-生产环境安全扫描报告-2026-06-01.md` |
| E2E 用例 | `jeecgboot-vue3/tests/e2e/security/*.spec.ts` |

## L3 快速命令

```powershell
Set-Location I:\cursorProjects\xgsrms\jeecgboot-vue3
npm run test:e2e:security:full
```

## 换电脑 / 同事

复制整个文件夹 `jeecg-webapp-security-scan` 到对方 `%USERPROFILE%\.cursor\skills\` 即可。
