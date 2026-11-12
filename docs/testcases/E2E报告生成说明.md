# E2E 测试报告生成说明

Playwright `StepCapture` 产物默认写到（已 gitignore）：

```
docs/e2e-runs/<runId>/
  manifest.json
  screenshots/
  测试报告.html
  招聘主流程测试报告.pdf
```

## 命令

```bash
# HTML + Markdown 手册
python3 scripts/generate_e2e_report_and_manual.py --run-dir docs/e2e-runs/<runId>

# PDF（需 reportlab）
pip install reportlab
python3 scripts/generate_e2e_pdf_report.py --run-dir docs/e2e-runs/<runId>
```

## 正式环境

环境 `https://10.28.12.15`，须本机 aTrust VPN。云端 Agent 通常连不上：只交付脚本与用例，**不要伪造实测通过**。详见 [招聘主流程测试计划.md](招聘主流程测试计划.md)。
