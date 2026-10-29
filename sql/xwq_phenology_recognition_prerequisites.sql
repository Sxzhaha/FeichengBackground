-- 2.5.5 物候期识别运行前置数据
-- 升级内容：作物类型字典、物候期识别按钮权限。
-- 本脚本幂等，不修改已有字典和菜单记录。

START TRANSACTION;

INSERT INTO sys_dict_type
    (dict_name, dict_type, status, create_by, create_time, remark)
SELECT
    '作物类型', 'xwq_crop_type', '0', 'admin', NOW(),
    '物候期识别工作台作物类型'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_type WHERE dict_type = 'xwq_crop_type'
);

INSERT INTO sys_dict_data
    (dict_sort, dict_label, dict_value, dict_type, css_class,
     list_class, is_default, status, create_by, create_time, remark)
SELECT
    1, '小麦', 'wheat', 'xwq_crop_type', '', 'primary',
    'Y', '0', 'admin', NOW(), '物候期识别作物'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_data
    WHERE dict_type = 'xwq_crop_type' AND dict_value = 'wheat'
);

INSERT INTO sys_dict_data
    (dict_sort, dict_label, dict_value, dict_type, css_class,
     list_class, is_default, status, create_by, create_time, remark)
SELECT
    2, '玉米', 'corn', 'xwq_crop_type', '', 'success',
    'N', '0', 'admin', NOW(), '物候期识别作物'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_data
    WHERE dict_type = 'xwq_crop_type' AND dict_value = 'corn'
);

INSERT INTO sys_dict_data
    (dict_sort, dict_label, dict_value, dict_type, css_class,
     list_class, is_default, status, create_by, create_time, remark)
SELECT
    3, '水稻', 'rice', 'xwq_crop_type', '', 'warning',
    'N', '0', 'admin', NOW(), '物候期识别作物'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_data
    WHERE dict_type = 'xwq_crop_type' AND dict_value = 'rice'
);

INSERT INTO sys_menu
    (menu_name, parent_id, order_num, path, component, query,
     route_name, is_frame, is_cache, menu_type, visible, status,
     perms, icon, create_by, create_time, remark)
SELECT
    '物候期识别执行', 2143, 1, '', NULL, NULL,
    '', 1, 0, 'F', '0', '0',
    'xwq:phenology:predict', '#', 'admin', NOW(),
    '执行单张或批量物候期识别'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE perms = 'xwq:phenology:predict'
);

COMMIT;

-- 验证 SQL
SELECT dict_type, status
FROM sys_dict_type
WHERE dict_type = 'xwq_crop_type';

SELECT dict_label, dict_value, is_default, status
FROM sys_dict_data
WHERE dict_type = 'xwq_crop_type'
ORDER BY dict_sort;

SELECT menu_id, parent_id, menu_name, menu_type, perms, status
FROM sys_menu
WHERE perms = 'xwq:phenology:predict';

-- 回滚 SQL（需要回滚时单独执行，保持以下顺序）
-- START TRANSACTION;
-- DELETE FROM sys_role_menu
-- WHERE menu_id IN (
--     SELECT menu_id FROM sys_menu
--     WHERE perms = 'xwq:phenology:predict'
-- );
-- DELETE FROM sys_menu WHERE perms = 'xwq:phenology:predict';
-- DELETE FROM sys_dict_data WHERE dict_type = 'xwq_crop_type';
-- DELETE FROM sys_dict_type WHERE dict_type = 'xwq_crop_type';
-- COMMIT;
