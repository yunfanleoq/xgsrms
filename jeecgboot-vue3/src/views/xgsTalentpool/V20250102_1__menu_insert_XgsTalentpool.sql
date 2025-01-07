-- 注意：该页面对应的前台目录为views/xgsTalentpool文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2025010201408150510', NULL, '人才库', '/xgsTalentpool/xgsTalentpoolList', 'xgsTalentpool/XgsTalentpoolList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2025-01-02 13:40:51', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025010201408150511', '2025010201408150510', '添加人才库', NULL, NULL, 0, NULL, NULL, 2, 'xgsTalentpool:xgs_talentpool:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-02 13:40:51', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025010201408160512', '2025010201408150510', '编辑人才库', NULL, NULL, 0, NULL, NULL, 2, 'xgsTalentpool:xgs_talentpool:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-02 13:40:51', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025010201408160513', '2025010201408150510', '删除人才库', NULL, NULL, 0, NULL, NULL, 2, 'xgsTalentpool:xgs_talentpool:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-02 13:40:51', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025010201408160514', '2025010201408150510', '批量删除人才库', NULL, NULL, 0, NULL, NULL, 2, 'xgsTalentpool:xgs_talentpool:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-02 13:40:51', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025010201408160515', '2025010201408150510', '导出excel_人才库', NULL, NULL, 0, NULL, NULL, 2, 'xgsTalentpool:xgs_talentpool:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-02 13:40:51', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025010201408160516', '2025010201408150510', '导入excel_人才库', NULL, NULL, 0, NULL, NULL, 2, 'xgsTalentpool:xgs_talentpool:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-01-02 13:40:51', NULL, NULL, 0, 0, '1', 0);