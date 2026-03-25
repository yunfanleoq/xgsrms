-- XGS 安全加固：用户选人组件 / 按部门查用户接口需显式权限，防止低权限账号枚举全站用户敏感信息
-- 权限标识与 SysUserController 上 @RequiresPermissions("system:user:queryUserComponentData") 一致

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000001', '3f915b2769fc80648e92d04e84ca059d', '用户选人组件查询用户列表', NULL, NULL, 0, NULL, NULL, 2, 'system:user:queryUserComponentData', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000001' OR `perms` = 'system:user:queryUserComponentData');

-- 将新权限授予与「通过ID查询用户信息」(system:user:queryById) 相同的角色，避免后台选人、流程等场景失效
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000001', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
WHERE srp.`permission_id` = '15c92115213910765570'
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000001'
  );
