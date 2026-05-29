# 安全加固修复方案（与代码变更配套）

本文档对应仓库内已落地的代码与配置修改，以及运维侧需配合的动作。

## 1. 已实现的代码/配置变更

| 项 | 说明 |
|----|------|
| 密码重置接口 | 新增 **`POST /sys/user/passwordChange`**，请求体为 JSON（`PasswordResetRequest`），敏感字段不再出现在 URL。 |
| GET 兼容与关闭 | 保留 **`GET /sys/user/passwordChange`** 仅作兼容；**`jeecg.security.allow-password-change-get`** 为 **`false`** 时直接拒绝 GET（生产推荐）。 |
| JWT 从 URL 读取 | 由 **`jeecg.security.allow-token-in-query`** 控制（默认 **`true`** 兼容旧集成；**生产建议 `false`**，见 `application-prod.yml`）。 |
| CORS | **`jeecg.security.cors-allowed-origins`** 为逗号分隔的 Origin 白名单；**为空**时保持旧行为（反射请求 `Origin`），生产应配置为实际前端地址。 |
| 前端 | `jeecgboot-vue3`：`passwordChange`、`updateUserPassword` 改为 **POST**；`jeecg-uniapp` 忘记密码改为 **POST**。 |

## 2. 配置说明（后端 `application-*.yml`）

生产示例（环境变量注入，勿把真实密钥写入仓库）：

```yaml
jeecg:
  security:
    allow-password-change-get: false
    allow-token-in-query: ${JEECG_SECURITY_ALLOW_TOKEN_IN_QUERY:false}
    cors-allowed-origins: ${CORS_ALLOWED_ORIGINS:}
```

环境变量建议：

| 变量 | 含义 |
|------|------|
| `JWT_SECRET` | JWT 签名密钥，至少 32 字符随机串 |
| `JEECG_SIGNATURE_SECRET` | 与前端 `VITE_GLOB_SIGNATURE_SECRET` 一致 |
| `CORS_ALLOWED_ORIGINS` | 例如 `https://hr.example.com,https://www.example.com` |
| `JEECG_SECURITY_ALLOW_TOKEN_IN_QUERY` | 生产建议 `false` |

## 3. 修复脚本（自检）

在项目根目录执行（Windows PowerShell）：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\security-remediation-verify.ps1
```

开发环境可使用仓库根目录的 `local-dev.env`（已加入 `.gitignore`，不要提交真实密钥）：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\security-remediation-verify.ps1 -EnvFile .\local-dev.env
```

严格模式（未设置或长度过短即失败）：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\security-remediation-verify.ps1 -Strict
```

脚本仅检查本机/进程可见的环境变量，**不**替代渗透测试与 WAF/网关策略。

## 4. 本分支已补充的项（2025-05 续）

- **富文本**：`XgsPositionInputSanitizer`（Jsoup relaxed）在岗位 `save/update` 入库前执行；前端列表仍用 `sanitizeRichTextHtml`。
- **用户枚举**：`checkOnlyUser` 统一返回「校验完成」+ 布尔 `result`；`phoneVerification` 统一「验证失败」文案并加 IP 限流。
- **SSRF**：`SsrfUrlGuard` 用于 `ImageDownloadUtil`、`JsoupHtmlContent` 出站 URL。
- **JWT / 安全开关**：`JeecgSecurityProperties` 绑定 `jeecg.security.*`（token 过期、allow-token-in-query、passwordChange GET、CORS 白名单）。
- **Actuator**：`CustomHttpTraceEndpoint` 仅当 `jeecg.actuator.jeecg-http-trace-enabled=true` 时注册（生产默认 false）。

仍建议黑盒验证：CSP、JimuReport `testConnection`、生产 `CORS_ALLOWED_ORIGINS` 环境变量是否已配置。

## 5. 升级与回滚注意

- 部署顺序建议：**先发布后端**（支持 POST），再发布前端；若先发布前端而后端未更新，会 **404/405** 于旧接口。
- 若必须临时兼容仅支持 GET 的旧客户端：在对应环境将 **`allow-password-change-get`** 设为 **`true`**（**缩短窗口期**并尽快升级客户端）。
