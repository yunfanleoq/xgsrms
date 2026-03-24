-- 方案二：批量刷新「密码最后修改时间」，使密码在 jeecg.security.password.expire-days 策略下重新计算有效期。
-- 执行前请备份 sys_user 表；在业务低峰期执行。
-- 适用：JeecgBoot sys_user 表（字段 password_update_time、del_flag）。

UPDATE sys_user
SET password_update_time = NOW()
WHERE del_flag = 0;

-- 执行后可 SELECT 抽查：
-- SELECT id, username, password_update_time, create_time FROM sys_user WHERE del_flag = 0 LIMIT 20;
