-- =============================================================================
-- 招聘智能体 / 工作流·本体 菜单种子（幂等）
-- 父菜单：招聘智能体
-- 子菜单：工作流/本体 → component rmsAgent/RmsAgentWorkflow
-- 授权：admin + 人事相关角色（role_code/role_name 含人事、人力、hr、rl 等）
-- 执行后用户需重新登录以刷新菜单。
-- =============================================================================

SET NAMES utf8mb4;

SET @parent_id := '2000327000000000001';
SET @child_id  := '2000327000000000002';

-- 1) 父菜单（一级目录 menu_type=0）
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`,
  `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`,
  `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`,
  `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`
)
SELECT
  @parent_id,
  NULL,
  '招聘智能体',
  '/rms/agent',
  'layouts/default/index',
  1,
  'RmsAgent',
  '/rms/agent-workflow',
  0,
  NULL,
  '1',
  85.00,
  0,
  'ant-design:robot-outlined',
  0,
  0,
  0,
  0,
  '招聘本体与智能体工作流父菜单',
  'admin',
  NOW(),
  NULL,
  NULL,
  0,
  0,
  '1',
  0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = @parent_id OR `name` = '招聘智能体');

-- 若已存在同名但 id 不同，锚定已有父 id
SET @parent_id := COALESCE(
  (SELECT `id` FROM `sys_permission` WHERE `id` = '2000327000000000001' LIMIT 1),
  (SELECT `id` FROM `sys_permission` WHERE `name` = '招聘智能体' AND `menu_type` = 0 LIMIT 1),
  @parent_id
);

-- 2) 子菜单（工作流/本体）
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`,
  `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`,
  `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`,
  `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`
)
SELECT
  @child_id,
  @parent_id,
  '工作流/本体',
  '/rms/agent-workflow',
  'rmsAgent/RmsAgentWorkflow',
  1,
  'RmsAgentWorkflow',
  NULL,
  1,
  'rms:agent:workflow',
  '1',
  1.00,
  0,
  'ant-design:partition-outlined',
  1,
  0,
  0,
  0,
  '招聘智能体工作流与本体论看板',
  'admin',
  NOW(),
  NULL,
  NULL,
  0,
  0,
  '1',
  0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission`
  WHERE `id` = @child_id
     OR `component` = 'rmsAgent/RmsAgentWorkflow'
     OR `perms` = 'rms:agent:workflow'
);

SET @child_id := COALESCE(
  (SELECT `id` FROM `sys_permission` WHERE `id` = '2000327000000000002' LIMIT 1),
  (SELECT `id` FROM `sys_permission` WHERE `perms` = 'rms:agent:workflow' LIMIT 1),
  (SELECT `id` FROM `sys_permission` WHERE `component` = 'rmsAgent/RmsAgentWorkflow' LIMIT 1),
  @child_id
);

-- 保证父挂接正确
UPDATE `sys_permission`
SET `parent_id` = @parent_id
WHERE `id` = @child_id
  AND (`parent_id` IS NULL OR `parent_id` = '' OR `parent_id` <> @parent_id);

-- 3) 授权 admin
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), r.`id`, @parent_id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
WHERE r.`role_code` = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = r.`id` AND x.`permission_id` = @parent_id
  );

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), r.`id`, @child_id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
WHERE r.`role_code` = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = r.`id` AND x.`permission_id` = @child_id
  );

-- 4) 授权人事处相关角色（按名称/编码模糊匹配，避免环境差异）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), r.`id`, @parent_id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
WHERE (
    r.`role_code` IN ('admin', 'hr', 'rl', 'renshi', '人事处')
    OR r.`role_name` LIKE '%人事%'
    OR r.`role_name` LIKE '%人力%'
    OR r.`role_code` LIKE '%hr%'
    OR r.`role_code` LIKE '%rl%'
  )
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = r.`id` AND x.`permission_id` = @parent_id
  );

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), r.`id`, @child_id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
WHERE (
    r.`role_code` IN ('admin', 'hr', 'rl', 'renshi', '人事处')
    OR r.`role_name` LIKE '%人事%'
    OR r.`role_name` LIKE '%人力%'
    OR r.`role_code` LIKE '%hr%'
    OR r.`role_code` LIKE '%rl%'
  )
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = r.`id` AND x.`permission_id` = @child_id
  );

-- 5) 给已有面试邀请 list 权限的角色补挂（人事侧常见）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, @child_id, srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM (
  SELECT DISTINCT srp.`role_id`, srp.`data_rule_ids`
  FROM `sys_role_permission` srp
  INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
  WHERE p.`perms` LIKE 'xgsInviteToInterview:%'
     OR p.`perms` LIKE '%invite_to_interview%'
) srp
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` x
  WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = @child_id
);

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), srp.`role_id`, @parent_id, srp.`data_rule_ids`, NOW(), '127.0.0.1'
FROM (
  SELECT DISTINCT srp.`role_id`, srp.`data_rule_ids`
  FROM `sys_role_permission` srp
  INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
  WHERE p.`perms` LIKE 'xgsInviteToInterview:%'
     OR p.`perms` LIKE '%invite_to_interview%'
) srp
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` x
  WHERE x.`role_id` = srp.`role_id` AND x.`permission_id` = @parent_id
);

-- 自检
SELECT 'parent' AS t, id, name, url, component FROM `sys_permission` WHERE id = @parent_id OR name = '招聘智能体'
UNION ALL
SELECT 'child', id, name, url, component FROM `sys_permission` WHERE id = @child_id OR perms = 'rms:agent:workflow';

SELECT 'role_grant_parent' AS t, COUNT(*) AS c FROM `sys_role_permission` WHERE permission_id = @parent_id
UNION ALL
SELECT 'role_grant_child', COUNT(*) FROM `sys_role_permission` WHERE permission_id = @child_id;
