# 病虫害自动识别跨模块数据契约

## 1. 模块边界

- 摄像头模块负责配置 `camera_task`、执行抓拍并写入 `image_record`。
- SXZ 自动识别模块只消费抓拍图片，不配置第二套抓拍时间，不修改摄像头模块数据。
- XWQ AI 模块负责模型配置、模型调用以及公共识别结果落库。
- 病虫害分析、识别图片和病虫害报告只读取公共 AI 结果，不直接读取任务执行过程表。

## 2. 主数据关系

```text
park.park_id
  -> area.park_id
  -> location.area_id
  -> iot_device.location_id
  -> camera_preset.device_id
  -> camera_task.preset_id
  -> image_record.task_id
```

自动识别策略通过 `sxz_disease_auto_task.camera_task_id` 逻辑关联
`camera_task.task_id`。不建立物理外键，避免跨团队模块删除和部署互相阻塞。

## 3. 图片与识别结果关系

```text
image_record.id
  -> sxz_disease_auto_task_item.source_image_id
  -> xwq_ai_image.image_id
  -> xwq_ai_record.record_id
  -> xwq_ai_disease.disease_id
```

`sxz_disease_auto_task_run` 保存一次消费批次，
`sxz_disease_auto_task_item` 保存每张图片的处理状态和结果主键。

## 4. 公共 AI 字段规范

| 目标字段 | 固定值或来源 |
| --- | --- |
| `xwq_ai_image.image_source` | `摄像头` |
| `xwq_ai_image.image_source_id` | `image_record.id` |
| `xwq_ai_image.device_id` | `image_record.device_id` |
| `xwq_ai_image.plot_id` | `location.area_id` |
| `xwq_ai_image.park_id` | `area.park_id` |
| `xwq_ai_record.record_type` | `病虫害` |
| `xwq_ai_record.record_mode` | `自动定时` |
| `xwq_ai_record.device_id` | 抓拍设备 ID |
| `xwq_ai_record.plot_id` | 区域 ID |
| `xwq_ai_record.park_id` | 园区 ID |
| `xwq_ai_disease.create_time` | 病虫害识别结果创建时间，类型为 `DATETIME` |

`xwq_ai_disease.create_time` 由识别服务在结果落库时写入；实际抓拍时间继续保存在
`image_record.capture_time` 和自动识别任务明细中，不再占用病害结果备用字段。

## 5. 图片消费规则

自动识别只消费满足以下条件的图片：

1. `image_record.task_id = sxz_disease_auto_task.camera_task_id`。
2. 图片地址不为空。
3. 当前策略尚未处理该 `image_record.id`。
4. 策略、抓拍任务和模型均处于可用状态。

`image_record.task_id IS NULL` 的手动抓拍图片不得进入自动识别。

## 6. 状态规范

- 策略状态：`ENABLED`、`DISABLED`、`INVALID`。
- 批次状态：`RUNNING`、`SUCCESS`、`PARTIAL`、`FAILED`、`SKIPPED`、`INTERRUPTED`。
- 单图状态：`PENDING`、`RUNNING`、`SUCCESS`、`RETRY_WAIT`、`FAILED`、`SKIPPED`。

没有新图片属于正常等待，不应记为失败。

## 7. 幂等与恢复

- `(task_id, source_image_id)` 保证同一策略不重复消费同一图片。
- `(source_image_id, model_id_snapshot)` 防止同一图片被同一模型重复识别。
- 外部模型调用不持有数据库长事务，每张图片独立提交结果。
- 超时的 `RUNNING` 明细在服务重启后转入 `RETRY_WAIT`。
- 公共 AI 结果已存在但明细未回写时，应优先重新关联，不再次调用模型。

## 8. 下游使用规则

- 识别图片页面通过公共 AI 表展示结果，并按 `record_mode` 区分手动和自动。
- 环境相关性只统计 `record_type='病虫害'` 且 `record_mode='自动定时'` 的记录。
- 环境数据按实际 `park_id` 和 `xwq_ai_disease.create_time` 关联 `ld_agri_weather_live`。
- 病虫害报告可按全部、手动上传、自动定时三种范围统计。
