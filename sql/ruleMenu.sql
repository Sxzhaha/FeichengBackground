-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警模板规则', '2006', '1', 'rule', 'wr/rule/index', 1, 0, 'C', '0', '0', 'wr:rule:list', '#', 'admin', sysdate(), '', null, '预警模板规则菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警模板规则查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'wr:rule:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警模板规则新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'wr:rule:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警模板规则修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'wr:rule:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警模板规则删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'wr:rule:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('预警模板规则导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'wr:rule:export',       '#', 'admin', sysdate(), '', null, '');