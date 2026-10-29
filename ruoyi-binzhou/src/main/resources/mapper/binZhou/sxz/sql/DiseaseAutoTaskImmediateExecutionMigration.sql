-- 病虫害自动识别“立即执行”支持同一历史抓拍图片在不同批次中重新识别。
-- 执行前请确认当前数据库为目标业务库，并先备份 sxz_disease_auto_task_item 表结构。

-- 1. 升级 SQL
ALTER TABLE sxz_disease_auto_task_item
    DROP INDEX uk_auto_item_source_model,
    DROP INDEX uk_auto_item_task_source,
    ADD UNIQUE KEY uk_auto_item_run_source (run_id, source_image_id);

-- 2. 验证 SQL：应只返回 uk_auto_item_run_source，且 non_unique 为 0。
SELECT index_name, non_unique,
       GROUP_CONCAT(column_name ORDER BY seq_in_index) AS columns_in_index
FROM information_schema.statistics
WHERE table_schema = DATABASE()
  AND table_name = 'sxz_disease_auto_task_item'
  AND index_name IN ('uk_auto_item_source_model', 'uk_auto_item_task_source', 'uk_auto_item_run_source')
GROUP BY index_name, non_unique;

-- 3. 回滚前检查：如果返回数据，说明同一任务或同一模型已经对同一来源图片产生多批次记录，
--    必须先由业务人员确认保留哪条记录，不能直接恢复旧唯一索引。
SELECT task_id, source_image_id, COUNT(*) AS duplicate_count
FROM sxz_disease_auto_task_item
GROUP BY task_id, source_image_id
HAVING COUNT(*) > 1;

SELECT source_image_id, model_id_snapshot, COUNT(*) AS duplicate_count
FROM sxz_disease_auto_task_item
GROUP BY source_image_id, model_id_snapshot
HAVING COUNT(*) > 1;

-- 4. 回滚 SQL：仅当上面两条检查均无返回记录时执行。
-- ALTER TABLE sxz_disease_auto_task_item
--     DROP INDEX uk_auto_item_run_source,
--     ADD UNIQUE KEY uk_auto_item_source_model (source_image_id, model_id_snapshot),
--     ADD UNIQUE KEY uk_auto_item_task_source (task_id, source_image_id);
