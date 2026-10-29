-- 用户默认配置（MySQL 5.7+ / RuoYi 代码生成器）
-- 园区、地块、设备属于用户默认监测范围；模型属于全系统公共资源。
-- 用户模型表只保存公共模型 ID，不复制模型名称、地址或参数。

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `xwq_user_default_scope` (
  `scope_id` bigint NOT NULL AUTO_INCREMENT COMMENT '默认范围主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `park_id` bigint DEFAULT NULL COMMENT '默认园区ID',
  `area_id` bigint DEFAULT NULL COMMENT '默认地块ID',
  `device_id` bigint DEFAULT NULL COMMENT '默认设备ID',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`scope_id`),
  UNIQUE KEY `uk_xwq_default_scope_user` (`user_id`),
  KEY `idx_xwq_default_scope_park_area` (`park_id`, `area_id`),
  KEY `idx_xwq_default_scope_device` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户默认监测范围';

CREATE TABLE IF NOT EXISTS `xwq_user_default_model` (
  `preference_id` bigint NOT NULL AUTO_INCREMENT COMMENT '默认模型主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `scene_code` varchar(32) NOT NULL COMMENT '识别场景（disease病虫害 pest虫情 phenology物候）',
  `model_id` bigint DEFAULT NULL COMMENT '公共模型ID（空表示跟随系统）',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`preference_id`),
  UNIQUE KEY `uk_xwq_default_model_user_scene` (`user_id`, `scene_code`),
  KEY `idx_xwq_default_model_model` (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户默认公共模型';

-- 不建立数据库外键，保存时由业务层校验用户权限、园区地块归属、设备归属和模型状态。
-- user_id 必须从当前登录 Token 获取；没有设备或选择跟随系统时对应字段允许为 NULL。
