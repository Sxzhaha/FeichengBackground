-- xwq_ai_disease.disease_a -> create_time migration
-- Execute the upgrade section once. The backup table is intentionally retained.

CREATE TABLE IF NOT EXISTS `xwq_ai_disease_a_backup_20260717` (
  `disease_id` bigint NOT NULL,
  `disease_a` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`disease_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='xwq_ai_disease disease_a迁移备份';

INSERT INTO `xwq_ai_disease_a_backup_20260717` (`disease_id`, `disease_a`)
SELECT `disease_id`, `disease_a` FROM `xwq_ai_disease`
ON DUPLICATE KEY UPDATE `disease_a` = VALUES(`disease_a`);

UPDATE `xwq_ai_disease` d
LEFT JOIN `xwq_ai_record` r ON r.record_id = d.record_id
LEFT JOIN `xwq_ai_image` i ON i.image_id = d.image_id
SET d.disease_a = DATE_FORMAT(
  COALESCE(
    CASE
      WHEN TRIM(d.disease_a) REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$'
      THEN STR_TO_DATE(TRIM(d.disease_a), '%Y-%m-%d %H:%i:%s')
      WHEN TRIM(d.disease_a) REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}[0-9]{2}:[0-9]{2}:[0-9]{2}$'
      THEN STR_TO_DATE(TRIM(d.disease_a), '%Y-%m-%d%H:%i:%s')
      ELSE NULL
    END,
    r.create_time,
    i.create_time
  ),
  '%Y-%m-%d %H:%i:%s'
);

ALTER TABLE `xwq_ai_disease`
  CHANGE COLUMN `disease_a` `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '识别结果创建时间';

CREATE INDEX `idx_disease_create_time` ON `xwq_ai_disease` (`create_time`);

-- Verification
SELECT COUNT(*) AS total_count,
       SUM(create_time IS NOT NULL) AS timed_count,
       SUM(create_time IS NULL) AS unknown_time_count
FROM xwq_ai_disease;

-- Rollback (execute manually only when needed)
-- DROP INDEX `idx_disease_create_time` ON `xwq_ai_disease`;
-- ALTER TABLE `xwq_ai_disease`
--   CHANGE COLUMN `create_time` `disease_a` varchar(500) NULL DEFAULT NULL COMMENT '备用字段1';
-- UPDATE `xwq_ai_disease` d
-- INNER JOIN `xwq_ai_disease_a_backup_20260717` b ON b.disease_id = d.disease_id
-- SET d.disease_a = b.disease_a;
