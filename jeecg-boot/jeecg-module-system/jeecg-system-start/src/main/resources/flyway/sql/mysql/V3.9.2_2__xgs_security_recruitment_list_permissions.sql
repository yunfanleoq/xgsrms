-- XGS 安全加固：招聘模块各管理端分页列表显式权限（与 Controller @RequiresPermissions 一致）
-- parent_id 优先与同模块已有按钮权限共用父菜单；多锚点 COALESCE 避免单条缺失导致 parent 为空

-- 1) 人才库分页列表 xgsTalentpool:xgs_talentpool:list
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000004',
       COALESCE(
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsTalentpool:xgs_talentpool:add' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsTalentpool:xgs_talentpool:edit' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsTalentpool:xgs_talentpool:exportXls' LIMIT 1)
       ),
       '人才库分页列表', NULL, NULL, 0, NULL, NULL, 2, 'xgsTalentpool:xgs_talentpool:list', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000004' OR `perms` = 'xgsTalentpool:xgs_talentpool:list');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000004', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
WHERE p.`perms` IN ('xgsTalentpool:xgs_talentpool:add', 'xgsTalentpool:xgs_talentpool:edit', 'xgsTalentpool:xgs_talentpool:exportXls', 'xgsTalentpool:xgs_talentpool:delete', 'xgsTalentpool:xgs_talentpool:deleteBatch')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000004'
  );

-- 2) 岗位申请分页列表 positions:xgs_position_apply:list（与 xgsPositionApply、xgsPositionApply2 共用）
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000005',
       COALESCE(
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'positions:xgs_position_apply:edit' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'positions:xgs_position_apply:exportXls' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'positions:xgs_position_apply:delete' LIMIT 1)
       ),
       '岗位申请分页列表', NULL, NULL, 0, NULL, NULL, 2, 'positions:xgs_position_apply:list', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000005' OR `perms` = 'positions:xgs_position_apply:list');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000005', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
WHERE p.`perms` IN ('positions:xgs_position_apply:edit', 'positions:xgs_position_apply:exportXls', 'positions:xgs_position_apply:importExcel', 'positions:xgs_position_apply:delete', 'positions:xgs_position_apply:deleteBatch')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000005'
  );

-- 3) 面试管理分页列表 xgsInterview:xgs_interview:list
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000006',
       COALESCE(
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsInterview:xgs_interview:add' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsInterview:xgs_interview:edit' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsInterview:xgs_interview:exportXls' LIMIT 1)
       ),
       '面试管理分页列表', NULL, NULL, 0, NULL, NULL, 2, 'xgsInterview:xgs_interview:list', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000006' OR `perms` = 'xgsInterview:xgs_interview:list');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000006', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
WHERE p.`perms` IN ('xgsInterview:xgs_interview:add', 'xgsInterview:xgs_interview:edit', 'xgsInterview:xgs_interview:exportXls', 'xgsInterview:xgs_interview:delete', 'xgsInterview:xgs_interview:deleteBatch')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000006'
  );

-- 4) 面试邀请分页列表 xgsInviteToInterview:xgs_invite_to_interview:list
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000007',
       COALESCE(
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsInviteToInterview:xgs_invite_to_interview:add' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsInviteToInterview:xgs_invite_to_interview:delete' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsInviteToInterview:xgs_invite_to_interview:exportXls' LIMIT 1)
       ),
       '面试邀请分页列表', NULL, NULL, 0, NULL, NULL, 2, 'xgsInviteToInterview:xgs_invite_to_interview:list', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000007' OR `perms` = 'xgsInviteToInterview:xgs_invite_to_interview:list');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000007', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
WHERE p.`perms` IN ('xgsInviteToInterview:xgs_invite_to_interview:add', 'xgsInviteToInterview:xgs_invite_to_interview:delete', 'xgsInviteToInterview:xgs_invite_to_interview:exportXls', 'xgsInviteToInterview:xgs_invite_to_interview:deleteBatch')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000007'
  );

-- 5) 审批办理过程分页列表 resume:xgs_flow_opinions:list
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000008',
       COALESCE(
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'resume:xgs_flow_opinions:delete' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'resume:xgs_flow_opinions:exportXls' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'resume:xgs_flow_opinions:importExcel' LIMIT 1)
       ),
       '审批办理过程分页列表', NULL, NULL, 0, NULL, NULL, 2, 'resume:xgs_flow_opinions:list', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000008' OR `perms` = 'resume:xgs_flow_opinions:list');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000008', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
WHERE p.`perms` IN ('resume:xgs_flow_opinions:delete', 'resume:xgs_flow_opinions:deleteBatch', 'resume:xgs_flow_opinions:exportXls', 'resume:xgs_flow_opinions:importExcel')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000008'
  );

-- 6) 用户简历文件分页列表 resume:xgs_user_resume_file:list
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
SELECT '2000325000000000009',
       COALESCE(
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'resume:xgs_user_resume_file:add' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'resume:xgs_user_resume_file:edit' LIMIT 1),
         (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'resume:xgs_user_resume_file:exportXls' LIMIT 1)
       ),
       '用户简历文件分页列表', NULL, NULL, 0, NULL, NULL, 2, 'resume:xgs_user_resume_file:list', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2000325000000000009' OR `perms` = 'resume:xgs_user_resume_file:list');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000009', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
WHERE p.`perms` IN ('resume:xgs_user_resume_file:add', 'resume:xgs_user_resume_file:edit', 'resume:xgs_user_resume_file:exportXls', 'resume:xgs_user_resume_file:delete', 'resume:xgs_user_resume_file:deleteBatch')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000009'
  );

-- 与简历主表列表权限同一批 HR：已拥有 xgsResume:xgs_resume_base:list 的角色同步获得岗位申请列表（常见同一招聘工作台）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, '2000325000000000005', srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM `sys_role_permission` srp
WHERE srp.`permission_id` = '2000325000000000003'
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = '2000325000000000005'
  );
