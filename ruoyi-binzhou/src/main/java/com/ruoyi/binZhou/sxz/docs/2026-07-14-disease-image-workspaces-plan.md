# 识别图片双工作区实施计划

## 目标

在现有 `/disease-images` 路由内增加“手动识别图片”和“自动识别图片”两个工作区，默认展示手动识别图片，并以 `diseaseId` 表示一次独立识别结果。

## 修改范围

- 前端 `sxz/diseaseImage_front/index.vue`：工作区切换、差异化筛选、差异化卡片。
- 后端 `DiseaseImageQuery.java`：增加识别方式、任务名称、设备名称查询条件。
- 后端 `DiseaseImageVO.java`：增加结果、来源、设备、任务及批次字段。
- 后端 `DiseaseImageMapper.xml`：从“每张图片最新结果”改为“每次成功识别结果”，并关联自动任务来源。

## 约束

- 不修改路由、公共配置、数据库结构和 `sxz` 以外的业务代码。
- 不删除或覆盖工作区现有修改。
- 手动结果限定 `record_mode = '手动上传'`。
- 自动结果限定 `record_mode = '自动定时'`，并关联任务、批次和设备。
- 自动执行失败、跳过及运行中记录继续由自动任务页面负责。

## 验证

1. 查询与返回对象契约测试。
2. Mapper XML 解析检查。
3. Maven 编译。
4. Vue 单文件组件编译。
5. 前端生产构建；若被其他模块阻塞，单独报告阻塞文件。

## 回滚

将 `D:\BinzhouSystem\_codex_backups\20260714-disease-image-workspaces` 中的四个原文件复制回原路径，并删除本次新增测试与计划文件。
