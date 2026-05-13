-- XGS 安全：Quartz 列表/查询/导入导出、多数据源写操作等按钮权限，与 Controller 上 @RequiresPermissions 一致
-- 将新权限授予已拥有「定时任务-添加」「多数据源-列表」的角色，避免管理员入口 403

-- ===================== Quartz =====================
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000301', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:quartzJob:add' ORDER BY `id` LIMIT 1), '定时任务分页列表', NULL, NULL, 0, NULL, NULL, 2, 'system:quartzJob:list', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000301' OR `perms` = 'system:quartzJob:list');

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000302', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:quartzJob:add' ORDER BY `id` LIMIT 1), '定时任务通过id查询', NULL, NULL, 0, NULL, NULL, 2, 'system:quartzJob:queryById', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000302' OR `perms` = 'system:quartzJob:queryById');

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000303', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:quartzJob:add' ORDER BY `id` LIMIT 1), '定时任务导出Excel', NULL, NULL, 0, NULL, NULL, 2, 'system:quartzJob:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000303' OR `perms` = 'system:quartzJob:exportXls');

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000304', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:quartzJob:add' ORDER BY `id` LIMIT 1), '定时任务导入Excel', NULL, NULL, 0, NULL, NULL, 2, 'system:quartzJob:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000304' OR `perms` = 'system:quartzJob:importExcel');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000301', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:quartzJob:add'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000301');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000302', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:quartzJob:add'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000302');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000303', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:quartzJob:add'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000303');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000304', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:quartzJob:add'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000304');

-- ===================== 多数据源 =====================
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000311', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:datasource:list' ORDER BY `id` LIMIT 1), '多数据源添加', NULL, NULL, 0, NULL, NULL, 2, 'system:datasource:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000311' OR `perms` = 'system:datasource:add');

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000312', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:datasource:list' ORDER BY `id` LIMIT 1), '多数据源编辑', NULL, NULL, 0, NULL, NULL, 2, 'system:datasource:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000312' OR `perms` = 'system:datasource:edit');

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000313', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:datasource:list' ORDER BY `id` LIMIT 1), '多数据源删除', NULL, NULL, 0, NULL, NULL, 2, 'system:datasource:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000313' OR `perms` = 'system:datasource:delete');

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000314', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:datasource:list' ORDER BY `id` LIMIT 1), '多数据源批量删除', NULL, NULL, 0, NULL, NULL, 2, 'system:datasource:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000314' OR `perms` = 'system:datasource:deleteBatch');

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000315', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:datasource:list' ORDER BY `id` LIMIT 1), '多数据源按id查询', NULL, NULL, 0, NULL, NULL, 2, 'system:datasource:queryById', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000315' OR `perms` = 'system:datasource:queryById');

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000316', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:datasource:list' ORDER BY `id` LIMIT 1), '多数据源导出Excel', NULL, NULL, 0, NULL, NULL, 2, 'system:datasource:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000316' OR `perms` = 'system:datasource:exportXls');

INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000317', (SELECT `id` FROM `sys_permission` WHERE `perms` = 'system:datasource:list' ORDER BY `id` LIMIT 1), '多数据源导入Excel', NULL, NULL, 0, NULL, NULL, 2, 'system:datasource:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000317' OR `perms` = 'system:datasource:importExcel');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000311', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:datasource:list'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000311');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000312', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:datasource:list'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000312');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000313', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:datasource:list'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000313');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000314', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:datasource:list'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000314');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000315', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:datasource:list'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000315');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000316', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:datasource:list'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000316');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000317', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id` AND p.`perms` = 'system:datasource:list'
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000317');
