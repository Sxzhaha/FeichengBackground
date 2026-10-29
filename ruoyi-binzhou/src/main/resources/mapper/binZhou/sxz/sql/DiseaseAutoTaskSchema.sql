-- 病虫害自动识别任务数据库结构（MySQL 5.7）
-- 仅新增 sxz 业务表，不修改 xwq_ai_record、xwq_ai_disease 等公共表。

CREATE TABLE IF NOT EXISTS `sxz_disease_auto_task` (
  `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务主键ID',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `park_id` bigint DEFAULT NULL COMMENT '园区ID，关联park.park_id',
  `device_id` bigint NOT NULL COMMENT '摄像头设备ID，关联iot_device.device_id',
  `model_id` bigint NOT NULL COMMENT '病虫害识别模型ID，关联xwq_ai_model.model_id',
  `crop_type` varchar(100) NOT NULL DEFAULT '枣树' COMMENT '作物类型',
  `schedule_type` varchar(20) NOT NULL COMMENT '调度方式：fixed固定时刻，interval间隔执行',
  `fixed_times` varchar(255) DEFAULT NULL COMMENT '每日固定执行时刻，英文逗号分隔，如08:00,14:00,20:00',
  `interval_minutes` int DEFAULT NULL COMMENT '间隔执行分钟数，schedule_type为interval时使用',
  `active_start_time` time DEFAULT NULL COMMENT '每日允许执行开始时间',
  `active_end_time` time DEFAULT NULL COMMENT '每日允许执行结束时间',
  `batch_limit` int NOT NULL DEFAULT 20 COMMENT '单次最多识别图片数量，范围1至100',
  `task_status` char(1) NOT NULL DEFAULT '0' COMMENT '任务状态：0启用，1停用',
  `run_state` varchar(20) NOT NULL DEFAULT 'IDLE' COMMENT '运行状态：IDLE空闲，RUNNING执行中',
  `execution_token` varchar(64) DEFAULT NULL COMMENT '任务执行锁令牌，防止并发重复执行',
  `execution_lock_time` datetime DEFAULT NULL COMMENT '任务执行锁定时间',
  `last_run_time` datetime DEFAULT NULL COMMENT '最近一次执行结束时间',
  `next_run_time` datetime DEFAULT NULL COMMENT '下一次计划执行时间',
  `last_run_status` varchar(20) DEFAULT NULL COMMENT '最近执行状态：SUCCESS、PARTIAL、FAILED、SKIPPED',
  `last_run_message` varchar(1000) DEFAULT NULL COMMENT '最近执行结果或失败原因',
  `version` int NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志：0正常，2已删除',
  `remark` varchar(500) DEFAULT NULL COMMENT '任务备注',
  `reserve_a` varchar(255) DEFAULT NULL COMMENT '备用字段A',
  `reserve_b` varchar(255) DEFAULT NULL COMMENT '备用字段B',
  `reserve_c` varchar(255) DEFAULT NULL COMMENT '备用字段C',
  `reserve_d` varchar(255) DEFAULT NULL COMMENT '备用字段D',
  `reserve_e` varchar(255) DEFAULT NULL COMMENT '备用字段E',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`task_id`),
  KEY `idx_auto_task_status_next` (`task_status`,`del_flag`,`next_run_time`),
  KEY `idx_auto_task_park_device` (`park_id`,`device_id`),
  KEY `idx_auto_task_model` (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病虫害自动识别任务配置表';

CREATE TABLE IF NOT EXISTS `sxz_disease_auto_task_run` (
  `run_id` bigint NOT NULL AUTO_INCREMENT COMMENT '执行批次主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID，逻辑关联sxz_disease_auto_task.task_id',
  `task_name` varchar(100) NOT NULL COMMENT '执行时任务名称快照',
  `trigger_type` varchar(20) NOT NULL COMMENT '触发方式：AUTO自动调度，MANUAL手动触发',
  `run_status` varchar(20) NOT NULL COMMENT '执行状态：RUNNING、SUCCESS、PARTIAL、FAILED、SKIPPED、INTERRUPTED',
  `start_time` datetime NOT NULL COMMENT '执行开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `source_count` int NOT NULL DEFAULT 0 COMMENT '本次发现的设备图片数量',
  `recognize_count` int NOT NULL DEFAULT 0 COMMENT '本次实际发起识别数量',
  `success_count` int NOT NULL DEFAULT 0 COMMENT '识别成功数量',
  `fail_count` int NOT NULL DEFAULT 0 COMMENT '识别失败数量',
  `skip_count` int NOT NULL DEFAULT 0 COMMENT '跳过数量',
  `error_message` varchar(2000) DEFAULT NULL COMMENT '批次失败或异常摘要',
  `reserve_a` varchar(255) DEFAULT NULL COMMENT '备用字段A',
  `reserve_b` varchar(255) DEFAULT NULL COMMENT '备用字段B',
  `reserve_c` varchar(255) DEFAULT NULL COMMENT '备用字段C',
  `reserve_d` varchar(255) DEFAULT NULL COMMENT '备用字段D',
  `reserve_e` varchar(255) DEFAULT NULL COMMENT '备用字段E',
  `create_by` varchar(64) DEFAULT NULL COMMENT '触发人，自动调度时为system',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`run_id`),
  KEY `idx_auto_run_task_time` (`task_id`,`start_time`),
  KEY `idx_auto_run_status_time` (`run_status`,`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病虫害自动识别任务执行批次表';

CREATE TABLE IF NOT EXISTS `sxz_disease_auto_task_item` (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务图片明细主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID，逻辑关联sxz_disease_auto_task.task_id',
  `run_id` bigint NOT NULL COMMENT '执行批次ID，逻辑关联sxz_disease_auto_task_run.run_id',
  `source_image_id` bigint NOT NULL COMMENT '设备原始图片ID，关联image_record.id',
  `ai_image_id` bigint DEFAULT NULL COMMENT 'AI图片ID，关联xwq_ai_image.image_id',
  `record_id` bigint DEFAULT NULL COMMENT '识别主记录ID，关联xwq_ai_record.record_id',
  `disease_id` bigint DEFAULT NULL COMMENT '病害结果ID，关联xwq_ai_disease.disease_id',
  `item_status` varchar(20) NOT NULL COMMENT '处理状态：PENDING、RUNNING、SUCCESS、FAILED、SKIPPED',
  `attempt_count` int NOT NULL DEFAULT 0 COMMENT '模型调用尝试次数',
  `start_time` datetime DEFAULT NULL COMMENT '单图识别开始时间',
  `finish_time` datetime DEFAULT NULL COMMENT '单图识别完成时间',
  `error_message` varchar(1000) DEFAULT NULL COMMENT '单图识别失败原因',
  `reserve_a` varchar(255) DEFAULT NULL COMMENT '备用字段A',
  `reserve_b` varchar(255) DEFAULT NULL COMMENT '备用字段B',
  `reserve_c` varchar(255) DEFAULT NULL COMMENT '备用字段C',
  `reserve_d` varchar(255) DEFAULT NULL COMMENT '备用字段D',
  `reserve_e` varchar(255) DEFAULT NULL COMMENT '备用字段E',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `uk_auto_item_task_source` (`task_id`,`source_image_id`),
  KEY `idx_auto_item_run_status` (`run_id`,`item_status`),
  KEY `idx_auto_item_record` (`record_id`),
  KEY `idx_auto_item_disease` (`disease_id`),
  KEY `idx_auto_item_ai_image` (`ai_image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病虫害自动识别任务图片明细表';

-- 验证SQL（建表后执行）
SELECT TABLE_NAME, TABLE_COMMENT
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('sxz_disease_auto_task', 'sxz_disease_auto_task_run', 'sxz_disease_auto_task_item')
ORDER BY TABLE_NAME;

SELECT TABLE_NAME, COUNT(*) AS reserve_field_count
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('sxz_disease_auto_task', 'sxz_disease_auto_task_run', 'sxz_disease_auto_task_item')
  AND COLUMN_NAME IN ('reserve_a', 'reserve_b', 'reserve_c', 'reserve_d', 'reserve_e')
GROUP BY TABLE_NAME
ORDER BY TABLE_NAME;

-- 回滚SQL（仅在明确需要撤销本模块且已备份数据后执行，当前禁止自动执行）
-- DROP TABLE IF EXISTS `sxz_disease_auto_task_item`;
-- DROP TABLE IF EXISTS `sxz_disease_auto_task_run`;
-- DROP TABLE IF EXISTS `sxz_disease_auto_task`;
