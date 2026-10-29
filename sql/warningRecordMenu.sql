-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警记录', '2006', '1', 'warningRecord', 'wr/warningRecord/index', 1, 0, 'C', '0', '0', 'wr:warningRecord:list', '#', 'admin', sysdate(), '', null, '预警记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'wr:warningRecord:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'wr:warningRecord:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'wr:warningRecord:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'wr:warningRecord:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'wr:warningRecord:export',       '#', 'admin', sysdate(), '', null, '');