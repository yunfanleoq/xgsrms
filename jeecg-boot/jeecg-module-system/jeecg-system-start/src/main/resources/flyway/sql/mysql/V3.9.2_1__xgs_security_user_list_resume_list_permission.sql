-- XGS 安全加固：用户列表、简历主表全量列表需显式权限，防止低权限账号（含求职者 JWT）枚举敏感数据
-- 与 SysUserController @RequiresPermissions("system:user:list")、XgsResumeBaseController @RequiresPermissions("xgsResume:xgs_resume_base:list") 一致

-- 1) 用户分页列表
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000002', '3f915b2769fc80648e92d04e84ca059d', '用户分页列表', NULL, NULL, 0, NULL, NULL, 2, 'system:user:list', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000002' OR `perms` = 'system:user:list');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000002', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
WHERE srp.`permission_id` = '1214376304951664642'
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000002'
  );

-- 已分配「用户管理」菜单（父级）的角色一并授予列表权限，避免仅挂菜单、未勾子按钮的角色无法打开用户页
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000002', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
WHERE srp.`permission_id` = '3f915b2769fc80648e92d04e84ca059d'
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000002'
  );

-- 2) 简历主表分页列表（与后台列表页一致；求职者默认无此权限）
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000003', 'd7d6e2e4e2934f2c9385a623fd98c6f3', '简历主表分页列表', NULL, NULL, 0, NULL, NULL, 2, 'xgsResume:xgs_resume_base:list', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000003' OR `perms` = 'xgsResume:xgs_resume_base:list');

-- 授予与「用户分页列表」相同的角色（通常为具备用户管理能力的后台角色）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000003', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
WHERE srp.`permission_id` = '2000325000000000002'
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000003'
  );

-- 同时授予拥有简历模块增删改导权限的角色（便于仅有招聘权限、无用户编辑权限的 HR）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000003', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
WHERE p.`perms` IN ('xgsResume:xgs_resume_base:edit', 'xgsResume:xgs_resume_base:add', 'xgsResume:xgs_resume_base:exportXls')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000003'
  );
