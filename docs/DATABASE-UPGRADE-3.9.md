# MySQL 数据库升级（JeecgBoot 3.7.x → 3.9.x）

本仓库在 `jeecg-system-start` 中通过 **`FlywayConfig`**（非 Spring 默认的 `FlywayAutoConfiguration`）对 **master 且 URL 含 `mysql`** 的数据源执行 `classpath:flyway/sql/mysql` 下的增量脚本。`spring.flyway.enabled` 为 `false` 时不会执行任何迁移。

## 1. 升级前必做

1. **全量备份**（或至少逻辑备份）目标库，并确认可恢复。
2. 确认应用连接的 **库名、账号** 与备份一致；多数据源场景下仅 **名为 `master` 的 MySQL 数据源** 会跑 Flyway。
3. 若库中曾手工执行过部分增量，再次执行可能出现「列已存在」「主键/唯一键冲突」等，需按报错跳过已执行语句或联系 DBA 比对 `flyway_schema_history`。

## 2. 推荐方式：附加 profile `dbupgrade` 启动一次

在 **不改** 日常 `dev` / `prod` 中 `flyway.enabled: false` 的前提下，仅本次启动打开 Flyway：

```text
--spring.profiles.active=dev,dbupgrade
```

或生产（维护窗口、已备份）：

```text
--spring.profiles.active=prod,dbupgrade
```

`application-dbupgrade.yml` 中仅将 `spring.flyway.enabled` 设为 `true`；其余 Flyway 参数仍沿用各环境 yml 与 `FlywayConfig` 默认值（含 `baseline-on-migrate: true`，适用于已有数据的老库首次接入 Flyway）。

**迁移成功后**，下次启动请 **去掉 `dbupgrade`**，避免重复尝试执行已记录版本（一般无影响，但保持习惯更安全）。

### 2.1 观察日志

- 成功：日志含 `【数据库升级】平台集成了MySQL库的Flyway，数据库版本自动升级!`
- 失败：`【数据库升级】flyway执行sql脚本失败` 及具体 SQL 异常；当前实现会 **捕获异常且不阻断启动**，请务必查看日志，**不要**在未修复前认为库已升级成功。

### 2.2 在应用服务器上执行（SSH，与 `application-prod.yml` 一致）

生产库连接由环境变量注入（见 `application-prod.yml` 中 `MYSQL_JDBC_URL` / `MYSQL_USERNAME` / `MYSQL_PASSWORD`），**勿**把口令写入仓库或聊天工具；若口令曾泄露须先轮换。

1. SSH 登录到 **应用所在主机**（MySQL 与 Java 同机时常用 `127.0.0.1` 作为 JDBC 主机名）。
2. 若 MySQL 与本机 Java 同机，JDBC 一般为 `127.0.0.1:3306`；若 MySQL 在其它机器，把 URL 改为实际可达地址与库名（例如 `xgsrms`）。
3. 在 **当前 shell 会话** 或 **systemd `EnvironmentFile`** 中设置上述三个变量（示例仅作键名说明，值请用你自己的）：

```bash
export MYSQL_JDBC_URL='jdbc:mysql://127.0.0.1:3306/xgsrms?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai'
export MYSQL_USERNAME='数据库账号'
export MYSQL_PASSWORD='数据库口令'
```

4. 使用已打包的 `jeecg-system-start` 可执行 jar，**仅本次**带 `dbupgrade`：

```bash
java -jar jeecg-system-start-*.jar --spring.profiles.active=prod,dbupgrade
```

5. 确认日志中出现成功提示后，**正常启动**时去掉 `dbupgrade`，并保留环境变量注入方式不变。

## 3. 增量脚本清单（MySQL，按 Flyway 版本序）

路径：`jeecg-module-system/jeecg-system-start/src/main/resources/flyway/sql/mysql/`

| 脚本文件 |
|----------|
| `V3.8.0_1__airag_add_menu.sql` |
| `V3.8.0_2__airag_init_db.sql` |
| `V3.8.1_1__all_upgrade.sql` |
| `V3.8.1_2__openapi.sql` |
| `V3.8.2_1__all_upgrade.sql` |
| `V3.8.3_0__all_upgrade.sql` |
| `V3.8.3_1__upgrade_jimubi.sql` |
| `V3.9.0_0__all_upgrade.sql` |
| `V3.9.0_1__mcp_demo.sql` |
| `V3.9.0_2__upd_dep_category.sql` |
| `V3.9.0_3__add_aiflow_permission.sql` |
| `V3.9.0_4__add_onlineuser_perms.sql` |
| `V3.9.1_0__all_upgrade.sql` |
| `V3.9.1_1__add_aiapp_img_gen.sql` |

说明：同目录下 `README.md`、`V3.8.0_0__clear_flyway_sql.md` 为运维说明，不是 Flyway 迁移脚本。

## 4. 备选：手工执行 SQL

若无法通过应用启动跑 Flyway，可用 MySQL 客户端 **按上表顺序** 逐个执行上述 `.sql` 文件。若库中已有 `flyway_schema_history`，手工执行后需与 Flyway 记录保持一致，否则后续用 Flyway 会校验失败；无 Flyway 历史的库建议仍优先用 **2** 节方式由 Flyway 统一管理。

## 5. 常见问题

- **老版本 Flyway 历史冲突**：官方在 `V3.8.0_0__clear_flyway_sql.md` 中说明过清理/合并 `flyway_schema_history` 的做法，升级前若从极老 Jeecg 迁来请先阅读该文件。
- **非 MySQL 主库**：`FlywayConfig` 仅对 master + MySQL 执行；其它库需手工执行对应方言脚本（若有）。
- **招聘二开**：`jeecg-module-recruitment` 业务表若独立于 Jeecg 主脚本，仍以业务与 Git 历史为准；本清单为平台主库增量。

## 6. 与本仓库其它文档的关系

总览见 `docs/UPGRADE-JeecgBoot-3.9.md`；数据库细节以本文为准。
