# 病虫害自动识别任务企业级实施计划

> 实施方式：当前会话按任务逐项执行，每一阶段均先写可验证检查，再实现最小闭环。未经用户要求不提交、不推送 Git。

**目标：** 建成面向农技人员的病虫害自动识别工作台，支持可靠的定时任务配置、自动识别执行、结果追溯和运行记录查看。

**架构：** 以 `sxz_disease_auto_task` 保存配置，以 `sxz_disease_auto_task_run` 保存每次执行，以 `sxz_disease_auto_task_item` 建立设备图片、AI图片和识别结果之间的唯一关系。识别结果继续写入公共 `xwq_ai_record` 与 `xwq_ai_disease`，不改变其字段结构。

**技术栈：** Vue 3、Vite、Element Plus、Spring Boot、MyBatis、MySQL 5.7、RuoYi 3.9.2。

## 全局约束

- 除已单独批准停用的旧 `xwq` 调度注解外，只修改或新增路径中包含 `sxz` 的文件。
- 不修改公共请求封装、Vite 配置、环境变量、数据库连接配置和公共工具类。
- 不改变手动上传识别流程以及 `xwq_ai_record`、`xwq_ai_disease` 的现有结构。
- 自动识别主记录统一写入 `record_type=病虫害`、`record_mode=自动定时`。
- 三张新表均包含五个备用字段 `reserve_a` 至 `reserve_e`，所有字段和表均提供中文备注。
- 页面视觉沿用病虫害分析页的导航、绿色强调色、白色卡片、浅灰背景和紧凑信息密度。
- 任何删除任务操作只删除或停用任务配置，不删除历史识别结果。

---

### 任务一：建立数据库契约与回滚脚本

**文件：**
- 新增：`ruoyi-binzhou/src/main/resources/mapper/binZhou/sxz/sql/DiseaseAutoTaskSchema.sql`

**产出：**
- 三张 InnoDB/utf8mb4 表及必要索引、唯一键、字段注释。
- 建表验证 SQL、现有数据检查 SQL和回滚 SQL。
- 唯一约束 `uk_task_source_image(task_id, source_image_id)` 防止同一任务重复处理同一图片。

**验证：**
- 在目标数据库执行 DDL。
- 执行 `SHOW CREATE TABLE`、索引检查和五个备用字段检查。
- 不执行回滚 SQL，仅验证语法和回滚范围。

### 任务二：实现调度规则与领域模型

**文件：**
- 修改：`domain/DiseaseAutoTask.java`
- 新增：`domain/DiseaseAutoTaskRun.java`
- 新增：`domain/DiseaseAutoTaskItem.java`
- 新增：`domain/DiseaseAutoTaskResult.java`
- 新增：`domain/DiseaseAutoTaskSummary.java`
- 修改：`domain/DiseaseAutoTaskQuery.java`
- 新增：`domain/DiseaseAutoResultQuery.java`
- 新增：`service/support/DiseaseAutoScheduleCalculator.java`
- 新增测试：`src/test/java/com/ruoyi/binZhou/sxz/service/support/DiseaseAutoScheduleCalculatorTest.java`

**产出接口：**
- `Date calculateNextRun(DiseaseAutoTask task, Date baseTime)`。
- 支持 `fixed` 固定时刻和 `interval` 间隔执行。
- 支持每日生效时间范围、下次执行时间预览和非法配置拒绝。

**验证：**
- 测试固定时刻跨天、间隔执行、每日时间窗和非法计划。

### 任务三：实现任务配置、选项与概况接口

**文件：**
- 修改：`controller/DiseaseAutoTaskController.java`
- 修改：`mapper/DiseaseAutoTaskMapper.java`
- 修改：`service/IDiseaseAutoTaskService.java`
- 修改：`service/impl/DiseaseAutoTaskServiceImpl.java`
- 修改：`resources/mapper/binZhou/sxz/DiseaseAutoTaskMapper.xml`

**产出接口：**
- 任务增删改查、启停、任务概况、园区/设备/模型选项。
- 校验园区设备归属、模型类型/状态/API地址/密钥、重复任务和执行时间。
- 返回下次执行时间，不向前端暴露 Cron。

**验证：**
- MyBatis XML 解析、后端编译和数据库 CRUD 查询。

### 任务四：实现执行批次、图片明细与调度引擎

**文件：**
- 修改：`service/impl/DiseaseAutoTaskServiceImpl.java`
- 修改：`service/support/DiseaseAutoTaskScheduler.java`
- 修改：`mapper/DiseaseAutoTaskMapper.java`
- 修改：`resources/mapper/binZhou/sxz/DiseaseAutoTaskMapper.xml`

**执行流程：**
- 原子领取到期任务，避免同一任务并发运行。
- 创建执行批次，查询任务设备的新 `image_record`。
- 先插入唯一图片明细，再同步或复用 `xwq_ai_image`。
- 调用现有 `IAiDiseaseService.predictByImage`。
- 回填 `record_id`、`disease_id`，逐图记录成功或失败。
- 无新图片记为“已跳过”，单图失败不阻断整批。
- 汇总批次并更新任务最近状态和下次执行时间。

**验证：**
- 重复图片不能重复创建明细。
- 同一任务不能并发执行。
- 成功、部分成功、失败、已跳过状态统计正确。

### 任务五：实现自动识别结果查询

**文件：**
- 修改：`controller/DiseaseAutoTaskController.java`
- 修改：`service/IDiseaseAutoTaskService.java`
- 修改：`service/impl/DiseaseAutoTaskServiceImpl.java`
- 修改：`mapper/DiseaseAutoTaskMapper.java`
- 修改：`resources/mapper/binZhou/sxz/DiseaseAutoTaskMapper.xml`

**产出接口：**
- 自动识别结果概况、分页列表、单条详情。
- 按时间、园区、设备、健康/病害、病害名称、模型和任务筛选。
- 只查询 `record_mode=自动定时` 且有任务图片明细关联的数据。

**验证：**
- 结果数量与任务明细、`xwq_ai_record`、`xwq_ai_disease` 一致。
- 手动上传记录不会进入自动识别结果。

### 任务六：重构前端自动识别工作台

**文件：**
- 修改：`src/api/binZhou/sxz/diseaseAutoTask.js`
- 修改：`src/views/binZhou/sxz/diseaseAutoTask/index.vue`

**页面结构：**
- 沿用病虫害分析页顶部导航和页面视觉。
- 默认“自动识别结果”页签：今日概况、组合筛选、图片结果列表、详情抽屉、分页。
- “定时任务”页签：任务概况、任务表格、三步新建/编辑、立即执行、运行记录抽屉。
- 固定时间和间隔执行均使用业务表单，不显示 Cron。
- 完整支持加载、空数据、请求失败、图片失败和按钮防重复状态。

**验证：**
- Vue SFC 编译、路由直达、筛选联动、表单校验和窗口尺寸检查。

### 任务七：补齐 `sxz` 页面导航入口

**文件：**
- 修改：`src/views/binZhou/sxz/diseaseAnalysis/index.vue`
- 修改：`src/views/binZhou/sxz/featureDisease/index.vue`
- 修改：`src/views/binZhou/sxz/diseaseImage_front/index.vue`
- 修改：`src/views/binZhou/sxz/diseaseReport/index.vue`

**产出：**
- 四个现有页面导航新增“自动识别”，指向 `/sxz/disease-auto-task`。
- 不修改页面其他布局和业务代码。

**验证：**
- 五个页面导航互通，当前页激活状态正确。

### 任务八：联调、验收与回滚核对

**验证命令：**
- `mvn -pl ruoyi-binzhou -am -DskipTests compile`
- MyBatis XML 解析。
- 调度计算器测试。
- Vue SFC 编译检查。
- `npm run build:prod`，若被现有非 `sxz` 依赖阻塞，单独报告原始错误。

**业务验收：**
- 创建固定时刻任务并看到正确的下次执行时间。
- 创建间隔任务并成功启停。
- 立即执行无新图片时显示“已跳过”。
- 有新设备图片时生成批次、明细、主记录和病害结果。
- 结果页能由任务跳转并筛选本次识别结果。
- 手动上传结果不进入自动结果页。
- 删除任务不删除历史识别记录。

**回滚：**
- 代码使用本次时间戳备份按文件恢复，不使用 `git reset` 或整仓回退。
- 数据库回滚前先导出三张新表；回滚 SQL只删除 `sxz_disease_auto_task_item`、`sxz_disease_auto_task_run`、`sxz_disease_auto_task`。
