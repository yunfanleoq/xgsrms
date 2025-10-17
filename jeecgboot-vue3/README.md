企业级低代码开发平台
===============
当前最新版本： 3.7.2（发布时间：2024-12-12）

Playwright E2E 测试
-------------------

快速开始：

1) 安装依赖与浏览器

```bash
npm i -D @playwright/test
npx playwright install
```

2) 运行用例（默认 baseURL=http://localhost:3100）

```bash
npx playwright test tests/e2e/resume-submit.spec.ts
```

3) 自定义地址

```bash
E2E_BASE_URL="http://localhost:3100" npx playwright test tests/e2e/resume-submit.spec.ts
```

可选：在 `playwright.config.ts` 中调整超时、报告与设备配置；在 `tests/e2e/resume-submit.spec.ts` 中完善表单字段选择器。