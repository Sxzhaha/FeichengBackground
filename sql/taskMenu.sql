-- 菜单 SQL（合并说明：此处原本是 ylm「农事任务」与 wr「预警任务」两套菜单的合并冲突，
-- 两套菜单均需保留，且各自按钮挂在各自的父菜单下，故重排为两组先后执行）

-- ============ 农事任务菜单（ylm） ============
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('农事任务', '3', '1', 'task', 'ylm/task/index', 1, 0, 'C', '0', '0', 'ylm:task:list', '#', 'admin', sysdate(), '', null, '农事任务菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('农事任务查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ylm:task:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('农事任务新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ylm:task:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('农事任务修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ylm:task:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('农事任务删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ylm:task:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('农事任务导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ylm:task:export',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('农事任务提交', @parentId, '6',  '#', '', 1, 0, 'F', '0', '0', 'ylm:task:submit',       '#', 'admin', sysdate(), '', null, '');

-- ============ 预警任务菜单（wr） ============
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警任务', '2006', '1', 'task', 'wr/task/index', 1, 0, 'C', '0', '0', 'wr:task:list', '#', 'admin', sysdate(), '', null, '预警任务菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警任务查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'wr:task:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警任务新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'wr:task:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警任务修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'wr:task:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警任务删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'wr:task:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警任务导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'wr:task:export',       '#', 'admin', sysdate(), '', null, '');
