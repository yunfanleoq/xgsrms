-- =============================================================================
-- 招聘：岗位申请列表权限 positions:xgs_position_apply:list
-- 用途：与 XgsPositionApplyController @RequiresPermissions("positions:xgs_position_apply:list") 一致；
--       修复接口 510、前端 v-auth 缺权限；可重复执行（幂等）。
-- 执行：在业务库（如 xgsrms）用 MySQL 客户端执行；执行后用户需重新登录。
-- 说明：本脚本维护「权限数据 + 角色授权」，不创建左侧「子菜单」路由行；
--       子菜单仍须在菜单管理中配置（类型=子菜单、组件路径等）。
-- =============================================================================

SET NAMES utf8mb4;

-- 权限主键（与 Flyway V3.9.2_2 保持一致，避免重复插入）
SET @perm_list_id := '2000325000000000005';

-- 父节点：与已有「岗位申请」类按钮挂在同一菜单下（多锚点回退）
SET @parent_id := COALESCE(
  (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'positions:xgs_position_apply:edit' LIMIT 1),
  (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'positions:xgs_position_apply:exportXls' LIMIT 1),
  (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'positions:xgs_position_apply:delete' LIMIT 1),
  (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'positions:xgs_position_apply:deleteBatch' LIMIT 1),
  (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'resume:xgs_position_apply:add' LIMIT 1),
  (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'xgsUserResume:xgs_position_apply:exportXls' LIMIT 1),
  (SELECT `parent_id` FROM `sys_permission` WHERE `perms` = 'positions:xgs_position_apply:importExcel' LIMIT 1),
  (SELECT `parent_id` FROM `sys_permission` WHERE `perms` LIKE 'positions:xgs_position_apply:%' AND `menu_type` = 2 ORDER BY `sort_no` LIMIT 1)
);

-- 若仍为空，挂到「岗位申请」相关任意一条权限的父级（最后手段：任意含 xgsPositionApply 的菜单）
SET @parent_id := COALESCE(
  @parent_id,
  (SELECT `parent_id` FROM `sys_permission` WHERE `url` LIKE '%xgsPositionApply%' AND `menu_type` IN (0, 1) LIMIT 1),
  (SELECT `parent_id` FROM `sys_permission` WHERE `component` LIKE '%xgsPositionApply%' AND `menu_type` IN (0, 1) LIMIT 1)
);

-- 1) 插入权限定义（按钮权限 menu_type=2）
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`,
  `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`,
  `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`,
  `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`
)
SELECT
  @perm_list_id,
  @parent_id,
  '岗位申请分页列表',
  NULL,
  NULL,
  0,
  NULL,
  NULL,
  2,
  'positions:xgs_position_apply:list',
  '1',
  NULL,
  0,
  NULL,
  1,
  0,
  0,
  0,
  NULL,
  'admin',
  NOW(),
  NULL,
  NULL,
  0,
  0,
  '1',
  0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = @perm_list_id OR `perms` = 'positions:xgs_position_apply:list');
-- 若已存在但 parent 为空，可选手动更新：
-- UPDATE sys_permission SET parent_id = @parent_id WHERE id = @perm_list_id AND (parent_id IS NULL OR parent_id = '');

-- 2) 给「已有岗位申请相关权限」的角色自动补挂 list（与 Flyway 逻辑一致，并扩展 resume / xgsUserResume 前缀）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), r.`role_id`, @perm_list_id, r.`data_rule_ids`, NOW(), '127.0.0.1'
FROM (
  SELECT DISTINCT srp.`role_id`, srp.`data_rule_ids`
  FROM `sys_role_permission` srp
  INNER JOIN `sys_permission` p ON p.`id` = srp.`permission_id`
  WHERE p.`perms` IN (
      'positions:xgs_position_apply:edit',
      'positions:xgs_position_apply:exportXls',
      'positions:xgs_position_apply:importExcel',
      'positions:xgs_position_apply:delete',
      'positions:xgs_position_apply:deleteBatch',
      'resume:xgs_position_apply:add',
      'resume:xgs_position_apply:edit',
      'resume:xgs_position_apply:delete',
      'resume:xgs_position_apply:deleteBatch',
      'xgsUserResume:xgs_position_apply:exportXls'
    )
    OR p.`perms` LIKE 'positions:xgs_position_apply:%'
    OR p.`perms` LIKE 'resume:xgs_position_apply:%'
    OR p.`perms` LIKE 'xgsUserResume:xgs_position_apply:%'
) r
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` x
  WHERE x.`role_id` = r.`role_id` AND x.`permission_id` = @perm_list_id
);

-- 3) 可选：给管理员角色（role_code=admin）强制补挂（若上面无锚点导致 2 未插入）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT REPLACE(UUID(), '-', ''), r.`id`, @perm_list_id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
WHERE r.`role_code` = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` x WHERE x.`role_id` = r.`id` AND x.`permission_id` = @perm_list_id
  );

-- 执行后自检
SELECT 'sys_permission rows' AS t, COUNT(*) AS c FROM `sys_permission` WHERE `perms` = 'positions:xgs_position_apply:list'
UNION ALL
SELECT 'sys_role_permission for list', COUNT(*) FROM `sys_role_permission` WHERE `permission_id` = @perm_list_id;
