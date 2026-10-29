-- 自动识别任务管理菜单升级 SQL（MySQL 5.7）
-- 本文件不会由应用自动执行，需数据库审批后手工执行。

-- 1. 升级 SQL
SET @parent_id = 2142;
SET @menu_id = (SELECT COALESCE(MAX(menu_id), 2000) + 1 FROM sys_menu);

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @menu_id, '自动识别任务管理', @parent_id, 2, 'diseaseAutoManage',
       'binZhou/sxz/diseaseAutoManage/index', NULL, 1, 0,
       'C', '0', '0', 'sxz:autoManage:list', 'monitor', 'admin', NOW(),
       '管理自动识别任务、执行批次和图片明细'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu
    WHERE component = 'binZhou/sxz/diseaseAutoManage/index'
       OR perms = 'sxz:autoManage:list'
);

SET @menu_id = (
    SELECT menu_id FROM sys_menu
    WHERE component = 'binZhou/sxz/diseaseAutoManage/index'
    ORDER BY menu_id DESC LIMIT 1
);

SET @button_id = (SELECT COALESCE(MAX(menu_id), 2000) FROM sys_menu);

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 1, '自动识别任务查询', @menu_id, 1, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoManage:query', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoManage:query');

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 2, '自动识别任务新增', @menu_id, 2, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoManage:add', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoManage:add');

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 3, '自动识别任务修改', @menu_id, 3, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoManage:edit', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoManage:edit');

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 4, '自动识别任务删除', @menu_id, 4, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoManage:remove', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoManage:remove');

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 5, '自动识别任务执行', @menu_id, 5, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoManage:execute', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoManage:execute');

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 6, '自动识别任务导出', @menu_id, 6, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoManage:export', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoManage:export');

-- 管理员角色通常自动拥有全部菜单。其他角色需要在权限管理中手工勾选本菜单。

-- 2. 验证 SQL
SELECT menu_id, menu_name, parent_id, order_num, path, component, perms, menu_type, status
FROM sys_menu
WHERE component = 'binZhou/sxz/diseaseAutoManage/index'
   OR perms LIKE 'sxz:autoManage:%'
ORDER BY parent_id, order_num, menu_id;

-- 3. 回滚 SQL
-- 先移除角色菜单关联，再删除按钮和页面菜单，只影响本模块权限。
-- DELETE rm FROM sys_role_menu rm
-- JOIN sys_menu m ON m.menu_id = rm.menu_id
-- WHERE m.component = 'binZhou/sxz/diseaseAutoManage/index'
--    OR m.perms LIKE 'sxz:autoManage:%';

-- DELETE FROM sys_menu
-- WHERE component = 'binZhou/sxz/diseaseAutoManage/index'
--    OR perms LIKE 'sxz:autoManage:%';
