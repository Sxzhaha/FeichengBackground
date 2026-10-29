-- 农事管理：任务表与基础字典初始化

CREATE TABLE IF NOT EXISTS `ylm_agri_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_no` varchar(64) NOT NULL COMMENT '任务编码',
  `work_type_id` bigint NULL DEFAULT NULL COMMENT '农事类型（字典值）',
  `work_method_id` bigint NULL DEFAULT NULL COMMENT '作业方式（字典值）',
  `area_id` bigint NULL DEFAULT NULL COMMENT '区域ID',
  `description` varchar(1000) NULL DEFAULT NULL COMMENT '任务描述',
  `photo_urls` text NULL COMMENT '照片地址，多个用英文逗号分隔',
  `create_by_id` bigint NULL DEFAULT NULL COMMENT '任务创建人ID',
  `operator_id` varchar(64) NULL DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `operate_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `remark` varchar(500) NULL DEFAULT NULL COMMENT '备注',
  `agri_task_a` varchar(255) NULL DEFAULT NULL COMMENT '备用字段A',
  `agri_task_b` varchar(255) NULL DEFAULT NULL COMMENT '备用字段B',
  `agri_task_c` varchar(255) NULL DEFAULT NULL COMMENT '备用字段C',
  `agri_task_d` varchar(255) NULL DEFAULT NULL COMMENT '备用字段D',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ylm_agri_task_no` (`task_no`),
  KEY `idx_ylm_agri_operator` (`operator_id`),
  KEY `idx_ylm_agri_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农事任务表';

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '农事类型', 'ylm_agri_work_type', '0', 'admin', sysdate(), '农事管理农事类型'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'ylm_agri_work_type');

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '农事作业方式', 'ylm_agri_work_method', '0', 'admin', sysdate(), '农事管理作业方式'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'ylm_agri_work_method');

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '农事操作人', 'ylm_agri_operator', '0', 'admin', sysdate(), '农事管理操作人，dict_value 应与用户ID一致'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'ylm_agri_operator');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 1, '施肥', '1', 'ylm_agri_work_type', '', 'primary', 'Y', '0', 'admin', sysdate(), '农事类型'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'ylm_agri_work_type' AND dict_value = '1');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 2, '灌溉', '2', 'ylm_agri_work_type', '', 'success', 'N', '0', 'admin', sysdate(), '农事类型'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'ylm_agri_work_type' AND dict_value = '2');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 3, '修剪', '3', 'ylm_agri_work_type', '', 'warning', 'N', '0', 'admin', sysdate(), '农事类型'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'ylm_agri_work_type' AND dict_value = '3');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 4, '病虫害防治', '4', 'ylm_agri_work_type', '', 'danger', 'N', '0', 'admin', sysdate(), '农事类型'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'ylm_agri_work_type' AND dict_value = '4');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 1, '人工', '1', 'ylm_agri_work_method', '', 'primary', 'Y', '0', 'admin', sysdate(), '作业方式'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'ylm_agri_work_method' AND dict_value = '1');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 2, '机械', '2', 'ylm_agri_work_method', '', 'success', 'N', '0', 'admin', sysdate(), '作业方式'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'ylm_agri_work_method' AND dict_value = '2');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 3, '无人机', '3', 'ylm_agri_work_method', '', 'warning', 'N', '0', 'admin', sysdate(), '作业方式'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'ylm_agri_work_method' AND dict_value = '3');
