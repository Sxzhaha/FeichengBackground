-- 病虫害自动识别与摄像头抓拍任务集成升级脚本（MySQL 5.7）
-- 当前文件仅供审批，未经确认不得直接执行。

-- ============================================================
-- 一、升级前检查
-- ============================================================

SELECT task_id, task_name, device_id, model_id, task_status
FROM sxz_disease_auto_task
ORDER BY task_id;

-- 每条旧策略能够匹配多少个摄像头抓拍任务。只有唯一匹配时才允许自动迁移。
SELECT t.task_id,
       t.task_name,
       t.device_id,
       COUNT(DISTINCT ct.task_id) AS camera_task_count,
       GROUP_CONCAT(DISTINCT ct.task_id ORDER BY ct.task_id) AS camera_task_ids
FROM sxz_disease_auto_task t
LEFT JOIN camera_preset cp ON cp.device_id = t.device_id
LEFT JOIN camera_task ct ON ct.preset_id = cp.id
GROUP BY t.task_id, t.task_name, t.device_id
ORDER BY t.task_id;

-- 检查未来唯一索引是否可能冲突。
SELECT i.source_image_id,
       t.model_id,
       COUNT(*) AS duplicate_count
FROM sxz_disease_auto_task_item i
INNER JOIN sxz_disease_auto_task t ON t.task_id = i.task_id
GROUP BY i.source_image_id, t.model_id
HAVING COUNT(*) > 1;

-- ============================================================
-- 二、升级 SQL
-- ============================================================

ALTER TABLE sxz_disease_auto_task
    ADD COLUMN camera_task_id BIGINT NULL COMMENT '关联摄像头抓拍任务camera_task.task_id' AFTER task_name,
    ADD COLUMN consume_mode VARCHAR(20) NOT NULL DEFAULT 'CAPTURE_TASK' COMMENT '图片消费方式：CAPTURE_TASK按抓拍任务消费' AFTER camera_task_id,
    ADD COLUMN max_retry_count INT NOT NULL DEFAULT 2 COMMENT '单张图片最大重试次数' AFTER batch_limit,
    ADD COLUMN last_source_image_id BIGINT NULL COMMENT '最近成功发现的image_record.id，仅用于扫描加速' AFTER last_run_message,
    ADD COLUMN invalid_reason VARCHAR(500) NULL COMMENT '抓拍任务、设备或模型失效原因' AFTER last_source_image_id,
    MODIFY COLUMN schedule_type VARCHAR(20) NULL COMMENT '兼容旧字段，新流程不再使用',
    ADD INDEX idx_auto_task_camera_status (camera_task_id, task_status, del_flag);

ALTER TABLE sxz_disease_auto_task_run
    ADD COLUMN camera_task_id BIGINT NULL COMMENT '执行时抓拍任务ID快照' AFTER task_id,
    ADD COLUMN device_id BIGINT NULL COMMENT '执行时设备ID快照' AFTER camera_task_id,
    ADD COLUMN park_id BIGINT NULL COMMENT '执行时园区ID快照' AFTER device_id,
    ADD COLUMN model_id BIGINT NULL COMMENT '执行时模型ID快照' AFTER park_id,
    ADD INDEX idx_auto_run_camera_time (camera_task_id, start_time);

ALTER TABLE sxz_disease_auto_task_item
    ADD COLUMN model_id_snapshot BIGINT NULL COMMENT '执行时模型ID快照' AFTER source_image_id,
    ADD COLUMN capture_time DATETIME NULL COMMENT '原始图片抓拍时间' AFTER model_id_snapshot,
    ADD COLUMN next_retry_time DATETIME NULL COMMENT '下次允许重试时间' AFTER attempt_count,
    ADD INDEX idx_auto_item_retry (item_status, next_retry_time);

-- 旧策略只有在设备唯一对应一个抓拍任务时才自动绑定，禁止猜测多任务关系。
UPDATE sxz_disease_auto_task t
INNER JOIN (
    SELECT mapped.task_id, MIN(mapped.camera_task_id) AS camera_task_id
    FROM (
        SELECT t0.task_id, ct.task_id AS camera_task_id
        FROM sxz_disease_auto_task t0
        INNER JOIN camera_preset cp ON cp.device_id = t0.device_id
        INNER JOIN camera_task ct ON ct.preset_id = cp.id
    ) mapped
    GROUP BY mapped.task_id
    HAVING COUNT(DISTINCT mapped.camera_task_id) = 1
) unique_mapping ON unique_mapping.task_id = t.task_id
SET t.camera_task_id = unique_mapping.camera_task_id,
    t.consume_mode = 'CAPTURE_TASK';

-- 无法唯一绑定的旧策略先停用，等待人工重新选择抓拍任务。
UPDATE sxz_disease_auto_task
SET task_status = '1',
    invalid_reason = '旧策略未能唯一匹配摄像头抓拍任务，请重新绑定后启用'
WHERE camera_task_id IS NULL
  AND del_flag = '0';

UPDATE sxz_disease_auto_task_run r
INNER JOIN sxz_disease_auto_task t ON t.task_id = r.task_id
SET r.camera_task_id = t.camera_task_id,
    r.device_id = t.device_id,
    r.park_id = t.park_id,
    r.model_id = t.model_id
WHERE r.camera_task_id IS NULL;

UPDATE sxz_disease_auto_task_item i
INNER JOIN sxz_disease_auto_task t ON t.task_id = i.task_id
LEFT JOIN image_record src ON src.id = i.source_image_id
SET i.model_id_snapshot = t.model_id,
    i.capture_time = COALESCE(src.capture_time, src.create_time)
WHERE i.model_id_snapshot IS NULL
   OR i.capture_time IS NULL;

-- 必须确认升级前的重复检查结果为0行后再建立该索引。
ALTER TABLE sxz_disease_auto_task_item
    ADD UNIQUE INDEX uk_auto_item_source_model (source_image_id, model_id_snapshot);

-- ============================================================
-- 三、升级后验证 SQL
-- ============================================================

SELECT task_id, task_name, camera_task_id, consume_mode, max_retry_count,
       task_status, invalid_reason
FROM sxz_disease_auto_task
ORDER BY task_id;

SELECT t.task_id,
       t.task_name,
       ct.task_name AS camera_task_name,
       cp.point_index,
       d.device_id,
       d.device_name,
       a.area_id,
       p.park_id,
       p.park_name
FROM sxz_disease_auto_task t
LEFT JOIN camera_task ct ON ct.task_id = t.camera_task_id
LEFT JOIN camera_preset cp ON cp.id = ct.preset_id
LEFT JOIN iot_device d ON d.device_id = cp.device_id
LEFT JOIN location l ON l.location_id = d.location_id
LEFT JOIN area a ON a.area_id = l.area_id
LEFT JOIN park p ON p.park_id = a.park_id
ORDER BY t.task_id;

SELECT 'task_without_camera' AS check_name, COUNT(*) AS problem_count
FROM sxz_disease_auto_task
WHERE del_flag = '0' AND task_status = '0' AND camera_task_id IS NULL
UNION ALL
SELECT 'run_without_snapshot', COUNT(*)
FROM sxz_disease_auto_task_run
WHERE camera_task_id IS NULL OR model_id IS NULL
UNION ALL
SELECT 'item_without_snapshot', COUNT(*)
FROM sxz_disease_auto_task_item
WHERE model_id_snapshot IS NULL OR capture_time IS NULL;

-- ============================================================
-- 四、回滚 SQL
-- ============================================================

-- 回滚前为新流程产生的空调度字段补兼容值，保证恢复NOT NULL约束成功。
UPDATE sxz_disease_auto_task
SET schedule_type = 'fixed'
WHERE schedule_type IS NULL OR TRIM(schedule_type) = '';

ALTER TABLE sxz_disease_auto_task_item
    DROP INDEX uk_auto_item_source_model,
    DROP INDEX idx_auto_item_retry,
    DROP COLUMN next_retry_time,
    DROP COLUMN capture_time,
    DROP COLUMN model_id_snapshot;

ALTER TABLE sxz_disease_auto_task_run
    DROP INDEX idx_auto_run_camera_time,
    DROP COLUMN model_id,
    DROP COLUMN park_id,
    DROP COLUMN device_id,
    DROP COLUMN camera_task_id;

ALTER TABLE sxz_disease_auto_task
    DROP INDEX idx_auto_task_camera_status,
    DROP COLUMN invalid_reason,
    DROP COLUMN last_source_image_id,
    DROP COLUMN max_retry_count,
    DROP COLUMN consume_mode,
    DROP COLUMN camera_task_id,
    MODIFY COLUMN schedule_type VARCHAR(20) NOT NULL COMMENT '调度方式：fixed固定时刻，interval间隔执行';
