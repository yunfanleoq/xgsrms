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

## 5. CERT-585812（字典接口未授权 / 表字典越权）

院数通保通报：`GET /xgszp-boot/sys/dict/getDictItems/sys_user,username,username` 可在伪造 `X-Sign` 后未授权读取用户名。

| 措施 | 状态 |
|------|------|
| 去掉 Shiro `anon`：`/sys/dict/getDictItems/*` | 代码已改 |
| `@IgnoreAuth` 仅放行**普通字典 code**（无逗号）；`table,text,code` 须有效 JWT | 代码已改 |
| **立即轮换**生产 `JEECG_SIGNATURE_SECRET`（通报中的密钥已泄露）并同步前端 `VITE_GLOB_SIGNATURE_SECRET` 后重新构建/发布 | 运维必做 |
| 核查库表 `sys_table_white_list`：生产勿把 `sys_user` 配成可查敏感字段 | 运维核查 |
| 部署后复测：无 Token 访问上述 URL 应 401；门户普通字典（如 `jour_type`、`岗位分类`）仍可用 | 已复测 |
| 后台账号改密、口令复用与异常登录核对 | `scripts/reset-backend-passwords.py`（只改带业务角色的账号，应聘者仅 `test` 角色不改） |
| 打开 `/xgszp-boot/` 访问日志、180 天保留、每 10 分钟告警 | `deploy/nginx/10.28.12.15.conf`、`deploy/logrotate/rms-logs`、`scripts/rms-security-alert.sh` |
| 登录/短信/字典频率限制，封禁 `111.47.56.204`、`182.105.188.5` | 同上 Nginx 配置 |
| 前端包不再携带签名密钥；生产关闭浏览器侧签名，敏感接口只认登录令牌 | 代码已改，须前后端一起发布 |

反馈院方时附：整改说明、复测截图/请求响应、是否检出恶意样本（无则注明未发现）。

## 6. 升级与回滚注意

- 部署顺序建议：**先发布后端**（支持 POST），再发布前端；若先发布前端而后端未更新，会 **404/405** 于旧接口。
- 若必须临时兼容仅支持 GET 的旧客户端：在对应环境将 **`allow-password-change-get`** 设为 **`true`**（**缩短窗口期**并尽快升级客户端）。
