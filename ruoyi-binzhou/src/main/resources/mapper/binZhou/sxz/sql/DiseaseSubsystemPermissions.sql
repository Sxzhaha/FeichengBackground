-- 肥城桃病虫害子系统权限升级 SQL（MySQL 5.7/8.0）
-- 本文件不会由应用自动执行，需由数据库管理员审核后手工执行。
-- 执行后，请在“角色管理 -> 分配权限”中按管理员、果农、专家职责勾选按钮权限。

-- 1. 给现有页面菜单设置接口所需的基础权限。
UPDATE sys_menu
SET perms = 'sxz:autoTask:list'
WHERE component = 'binZhou/sxz/diseaseAutoTask/index';

UPDATE sys_menu
SET perms = 'sxz:diseaseAnalysis:list'
WHERE component = 'binZhou/sxz/diseaseAnalysis/index';

UPDATE sys_menu
SET perms = 'sxz:diseaseImage:list'
WHERE component IN ('binZhou/sxz/diseaseImage/index', 'binZhou/sxz/diseaseImage_front/index');

UPDATE sys_menu
SET perms = 'sxz:diseaseReport:list'
WHERE component = 'binZhou/sxz/diseaseReport/index';

UPDATE sys_menu
SET perms = 'sxz:diseaseRecognition:execute'
WHERE component = 'binZhou/sxz/featureDisease/index';

-- 2. 自动识别任务需要细分查询、新增、修改、删除和立即执行权限。
SET @auto_task_menu_id = (
    SELECT menu_id FROM sys_menu
    WHERE component = 'binZhou/sxz/diseaseAutoTask/index'
    ORDER BY menu_id DESC LIMIT 1
);
SET @button_id = (SELECT COALESCE(MAX(menu_id), 2000) FROM sys_menu);

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 1, '病害自动任务查询', COALESCE(@auto_task_menu_id, 0), 1, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoTask:query', '#', 'admin', NOW(), '查看任务、批次和识别明细'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoTask:query');

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 2, '病害自动任务新增', COALESCE(@auto_task_menu_id, 0), 2, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoTask:add', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoTask:add');

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 3, '病害自动任务修改', COALESCE(@auto_task_menu_id, 0), 3, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoTask:edit', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoTask:edit');

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 4, '病害自动任务删除', COALESCE(@auto_task_menu_id, 0), 4, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoTask:remove', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoTask:remove');

INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache,
 menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT @button_id + 5, '病害自动任务执行', COALESCE(@auto_task_menu_id, 0), 5, '#', '', NULL, 1, 0,
       'F', '0', '0', 'sxz:autoTask:execute', '#', 'admin', NOW(), '允许手动触发一次识别任务'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'sxz:autoTask:execute');

-- 3. 核验。
SELECT menu_id, menu_name, parent_id, perms, menu_type, status
FROM sys_menu
WHERE perms LIKE 'sxz:autoTask:%'
   OR perms IN (
       'sxz:diseaseAnalysis:list',
       'sxz:diseaseImage:list',
       'sxz:diseaseReport:list',
       'sxz:diseaseRecognition:execute'
   )
ORDER BY parent_id, order_num, menu_id;

-- 回滚时应先删除对应 sys_role_menu 记录，再删除新增按钮；页面原权限请按原值恢复。
