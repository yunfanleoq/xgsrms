# 单体 + MySQL：落地 JeecgBoot 3.9.1（Spring Boot 3）

当前仓库原为 **3.7.2（Spring Boot 2.7 + JDK 8）**。官方 **3.9.x** 使用 **Spring Boot 3.5.x、`org.jeecgframework.boot3`、JDK 17**。

## 1. 环境

- **JDK 17**（或官方支持的更高 LTS）
- **Git**、**Maven 3.8+**
- **MySQL**：升级后按官方 SQL 说明执行增量脚本（若有）；先备份数据库

## 2. 一键替换后端（推荐）

在项目根目录执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\upgrade-to-jeecg391.ps1
```

脚本会：

- **优先**使用与本仓库**同级**目录下的 `JeecgBoot` 源码（例如 `I:\cursorProjects\JeecgBoot`，需存在 `jeecg-boot\pom.xml`）；否则在 `.upgrade-cache` 下 **shallow clone** `v3.9.1`
- 备份 `jeecg-module-recruitment` 与 `jeecg-system-start` 的 `resources`
- 将当前 `jeecg-boot` 重命名为 `jeecg-boot.backup-时间戳`
- 用官方 `jeecg-boot` 覆盖，并恢复招聘模块、改父 `pom`、改 `system-start` 依赖
- 将招聘模块内 **`javax.servlet` / `javax.annotation` / `javax.websocket`** 替换为 **`jakarta.*`**

若 clone 较慢，可事先手动克隆：

```powershell
git clone --depth 1 --branch v3.9.1 https://github.com/jeecgboot/JeecgBoot.git .upgrade-cache\JeecgBoot-3.9.1
```

再执行脚本；或显式指定官方仓库根目录（需含 `jeecg-boot` 子目录）：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\upgrade-to-jeecg391.ps1 -OfficialJeecgBootRoot "I:\cursorProjects\JeecgBoot"
```

## 3. 必须手工合并的项

### 3.1 配置文件（MySQL / Redis / 签名等）

脚本会把旧环境的 `jeecg-system-start` 下 **resources** 拷到 `backup/system-start-resources-时间戳/`。

请逐项合并到新目录：

`jeecg-boot/jeecg-module-system/jeecg-system-start/src/main/resources/`

重点：`application-dev.yml`、`application-prod.yml` 中的：

- `spring.datasource`（MySQL 地址、库名、账号）
- `spring.redis`
- `jeecg.signatureSecret` / 环境变量
- `jeecg.path.upload`、`jeecg.uploadType` 等

Spring Boot 3 部分配置键已变更，可借助官方自带的 **`spring-boot-properties-migrator`**（已在官方 `system-start` 依赖中）在启动时提示。

### 3.2 Shiro 门户匿名访问（招聘门户）

官方新库的 `ShiroConfig` **不含**你们二开的门户路径。请从备份的 `jeecg-boot.backup-*` 中打开：

`jeecg-boot-base-core/.../ShiroConfig.java`

将类似下列配置 **合并回** 新工程的 `ShiroConfig`（在 `filterChainDefinitionMap` 中 `anon` 段，注意顺序与注释）：

- `/xgsHome/**`、`/xgsJournalism/**`、`/positions/xgsPositions/list`、`/positions/xgsPositions/getDeptList` 等（以你们备份为准）
- 以及此前对 **Swagger/Druid/jmreport**、**密码重置 POST** 等安全相关修改

### 3.3 其它二开文件

对比备份目录与 Git 历史，合并例如：

- `JwtFilter`、`SysUserController` 密码策略、`CommonController` 上传限制
- `jeecg-module-recruitment` 内除 jakarta 以外的业务改动（脚本已覆盖模块目录，**以备份与 Git 为准再核对**）

## 4. 编译与启动

```powershell
cd jeecg-boot
mvn -pl jeecg-module-system/jeecg-system-start -am package -DskipTests
```

主类一般为 `org.jeecg.JeecgSystemApplication`（以官方 3.9.1 为准）。

## 5. 前端 `jeecgboot-vue3`

官方 3.9.1 仓库根目录下带有新版 `jeecgboot-vue3`，**不要**整目录覆盖你们已改业务。

建议：

1. 记录当前 `package.json` 中业务无关变更；
2. 对照官方 `v3.9.1` 的 `package.json` 升级 **Vue3 / vite / @jeecg/online** 等版本；
3. 合并路由、环境变量与 `src/views` 下招聘相关页面。

或使用官方前端工程为基线，把本仓库 `src/views` 中 `xgs*`、`home` 等业务目录拷入并修编译错误。

## 6. 数据库

- **操作步骤、Flyway 开关方式、增量 SQL 清单与排错**：见 **`docs/DATABASE-UPGRADE-3.9.md`**。
- 升级前请 **全量备份**；可用仓库脚本 `scripts/backup-mysql-before-upgrade.ps1` 做逻辑备份（需本机 `mysqldump`）。
- 推荐在启动参数中附加 **`dbupgrade`** profile，一次性开启 Flyway（详见上文文档），避免长期把 `application-dev.yml` / `application-prod.yml` 里的 `spring.flyway.enabled` 改为 `true`。

## 7. 回滚

保留目录 `jeecg-boot.backup-时间戳` 可直接改回旧后端；数据库请先备份再执行升级脚本。
