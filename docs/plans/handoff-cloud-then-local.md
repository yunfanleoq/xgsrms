# 云端 → 本机接力（合上电脑后回来用）

云端 Agent：`bc-e5637168-4219-4807-ac77-e658f69ade0f`  
基线 commit：`d5b3592d`（含计划 `docs/plans/docs-test-ontology-resume.md`）

## 合上电脑期间

- 云端 VM 会继续跑，不依赖本机开盖。
- 本机不要指望自动拉分支 / 自动测正式环境（需 VPN + 本机会话）。

## 回来后对 Cursor 说

> 拉回来跑

Agent 应执行：

1. 查云端 Agent 分支 / PR / [Review](bc-e5637168-4219-4807-ac77-e658f69ade0f#changes)
2. `git fetch`（`origin` + `cursor`）并检出/合并该分支到本地
3. `ssh xgs-server 'echo OK'` 确认 VPN
4. 跑三角色主流程 E2E → 生成测试 PDF
5. 若有需上线的代码：按 `.cursor/skills/xgsrms-prod-deploy/SKILL.md` 发版（钥匙串取 xgs sudo）

## 本机专属（云端做不了）

- aTrust VPN + 正式机 `10.28.12.15`
- 钥匙串：`159.226.95.71`（VPN）、`10.28.12.15`（xgs）
