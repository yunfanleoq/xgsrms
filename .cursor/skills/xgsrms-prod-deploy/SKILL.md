---
name: xgsrms-prod-deploy
description: >-
  xgsrms 正式环境发版部署：先登 VPN，再经 SSH Host xgs-server（10.28.12.15）
  用 stage-no-client-sign.sh 与 finish-rms-deploy.sh 发布前后端。在用户说部署、
  发版、上线、正式环境、prod、10.28.12.15、xgs-server 时使用。
---

# xgsrms 正式环境发版部署

## 硬性前置：必须先登录 VPN（aTrust）

正式机 `10.28.12.15` 在内网，**未连 VPN 时 SSH 会超时**。本项目单位 VPN 用 **aTrust**（不是 EasyConnect 当主入口）。

| 项 | 值 |
|---|---|
| 门户 | `https://159.226.95.71/portal/#!/login` |
| 客户端 | macOS `aTrust.app`；接入地址填 `https://159.226.95.71` |
| 登录方式 | 登录窗口下方小字切到 **本地登录**（本地密码认证），不要走「统一身份认证」除非用户指定 |
| 账号 | `liuyanliang` |
| 密码 | **只从钥匙串取**，禁止写入仓库 / 规则正文 / commit |

取密（不要 `echo` 到日志）：

```bash
security find-internet-password -s '159.226.95.71' -a 'liuyanliang' -w
# 或
security find-generic-password -s 'iie-atrust-vpn' -a 'liuyanliang' -w
```

发版开始前先确认隧道：

```bash
ssh -o ConnectTimeout=8 -o BatchMode=yes xgs-server 'echo OK && hostname'
```

- 成功打印 `OK` → 继续构建与发布。
- `Operation timed out` / 无响应 → **立刻停下**，请用户在 aTrust / 浏览器门户用 **本地登录** 登 VPN（必要时再用 AweSun 作跳板）。不要盲重试、不要改部署脚本绕过网络；不要用脚本狂刷登录接口（易触发剩余次数锁定）。

`~/.ssh/config` 中应有：

```sshconfig
Host xgs-server
  HostName 10.28.12.15
  User xgs
  IdentityFile ~/.ssh/id_ed25519_xgs
```

服务器登录/sudo（`xgs`）：**只从钥匙串取**，禁止写入仓库。

```bash
security find-internet-password -s '10.28.12.15' -a 'xgs' -w
# 或
security find-generic-password -s 'xgs-server-sudo' -a 'xgs' -w
```

`finish-rms-deploy.sh` 需 sudo 写 `/www/wwwroot/rms/`；无交互时可用：

```bash
SSHPASS="$(security find-internet-password -s '10.28.12.15' -a 'xgs' -w)"
ssh -o IPQoS=none xgs-server "echo \"$SSHPASS\" | sudo -S bash ~/finish-rms-deploy.sh"
```

（注意引号与历史记录；不要把密码打印到对话或日志。）

## 正式机路径与入口

| 项 | 值 |
|---|---|
| SSH | `xgs-server` → `10.28.12.15` |
| 前端静态 | `/www/wwwroot/rms/frontend/dist` |
| 后端 | `/www/wwwroot/rms/backend`（JAR + `rms.env`） |
| 对外 | `https://10.28.12.15/`（API 前缀 `/xgszp-boot`） |
| 暂存目录 | `/tmp/rms-stage/`（脚本创建） |

前端生产环境：`jeecgboot-vue3/.env.production` 中 `VITE_GLOB_API_URL=/xgszp-boot`，`VITE_GLOB_DOMAIN_URL=https://10.28.12.15/xgszp-boot`，签名密钥留空（无客户端签名）。

## 标准发版流程

仓库根目录：`/Users/yunfanleo/cursorProjects/xgsrms`（或本机等价路径）。

### 1. 确认代码已提交（按用户要求）

用户明确说提交/推送时再 commit；推送按仓库规则双推 `origin` + `cursor`。

### 2. 本地构建（产物必须是本次要发的版本）

前端（内存给足，避免 OOM）：

```bash
cd jeecgboot-vue3
NODE_OPTIONS="--max-old-space-size=8192" npm run build
```

后端：

```bash
cd jeecg-boot
# 按仓库惯用模块打包，产物为 system-start 可执行 JAR
mvn -pl jeecg-module-system/jeecg-system-start -am -DskipTests package
```

构建后核对 JAR / `dist` 时间戳晚于本次改动。

### 3. 连通性闸（再验一次 VPN）

```bash
ssh -o ConnectTimeout=8 xgs-server 'echo OK'
```

不通则停，请用户登 VPN。

### 4. 推暂存（无客户端签名）

```bash
bash scripts/stage-no-client-sign.sh
```

该脚本会 rsync 前端 `dist`、后端 JAR 等到服务器 `/tmp/rms-stage/`，并处理无签名场景。

### 5. 正式切换

```bash
bash scripts/finish-rms-deploy.sh
```

在服务器上备份旧产物、切换到新包、按需重启后端（以脚本实际逻辑为准）。**不要**手工乱改正式目录覆盖顺序。

### 6. 安全环境变量（按需）

若本次含弱口令/CORS/Token 查询等安全项，在服务器执行（脚本可先 scp 到 `~/`）：

```bash
bash ~/ensure-rms-security-env.sh
# 有变更则：
sudo bash ~/restart-rms-backend.sh
```

仓库内源文件：`scripts/ensure-rms-security-env.sh`。

### 7. 冒烟

浏览器或 curl 检查：

- `https://10.28.12.15/` 门户首页与导航（招聘信息 / 职业发展 / 薪酬福利 / 联系我们）
- 登录与核心 API（`/xgszp-boot`）

失败时先看后端日志与 Nginx，不要立刻二次全量覆盖除非用户要求回滚。

## Agent 行为要求

1. **先 VPN，再任何 SSH/rsync**；超时 = 网络未通，不是缺密钥（本机已有可用密钥时）。
2. 未通网时不要空转重试；明确告诉用户「请登录 VPN」。
3. 构建 → stage → finish 顺序固定；不要跳过 stage 直接覆盖正式目录。
4. 仅在用户要求时 commit/push；push 必须 `origin` 与 `cursor` 都推同一分支。
5. 禁止对 `master`/`main` 做 `git push --force`，除非用户明确授权。
6. 不要改全局/`本仓库 git config`；不要擅自改正式机 `rms.env` 里已有密钥类配置。

## 相关文件

- `scripts/stage-no-client-sign.sh` — 推暂存
- `scripts/finish-rms-deploy.sh` — 正式切换
- `scripts/ensure-rms-security-env.sh` — 安全 env 幂等补全
- `jeecgboot-vue3/.env.production` — 正式前端环境
- 项目规则：`.cursor/rules/xgsrms-prod-deploy.mdc`
