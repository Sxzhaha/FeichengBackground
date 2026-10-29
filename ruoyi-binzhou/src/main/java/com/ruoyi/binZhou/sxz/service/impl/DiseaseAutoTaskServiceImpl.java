package com.ruoyi.binZhou.sxz.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoResultQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTask;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskItem;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskOption;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskResult;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskRun;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskSummary;
import com.ruoyi.binZhou.sxz.mapper.DiseaseAutoTaskMapper;
import com.ruoyi.binZhou.sxz.service.IDiseaseAutoTaskService;
import com.ruoyi.binZhou.sxz.service.IDiseaseKnowledgeRecognitionService;
import com.ruoyi.binZhou.sxz.service.client.DiseaseModelCallException;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

@Service
public class DiseaseAutoTaskServiceImpl implements IDiseaseAutoTaskService
{
    private static final Logger log = LoggerFactory.getLogger(DiseaseAutoTaskServiceImpl.class);
    private static final String ENABLED = "0";
    private static final String DISABLED = "1";
    private static final String RUNNING = "RUNNING";
    private static final String CAPTURE_TASK_MODE = "CAPTURE_TASK";
    private static final int SOURCE_FETCH_SIZE = 50;

    private final DiseaseAutoTaskMapper mapper;
    private final IDiseaseKnowledgeRecognitionService diseaseRecognitionService;

    public DiseaseAutoTaskServiceImpl(DiseaseAutoTaskMapper mapper,
            IDiseaseKnowledgeRecognitionService diseaseRecognitionService)
    {
        this.mapper = mapper;
        this.diseaseRecognitionService = diseaseRecognitionService;
    }

    @Override
    public List<DiseaseAutoTask> selectTaskList(DiseaseAutoTaskQuery query)
    {
        return mapper.selectTaskList(query == null ? new DiseaseAutoTaskQuery() : query);
    }

    @Override
    public DiseaseAutoTask selectTaskById(Long taskId)
    {
        return mapper.selectTaskById(taskId);
    }

    @Override
    public int insertTask(DiseaseAutoTask task)
    {
        normalizeAndValidate(task, null);
        return mapper.insertTask(task);
    }

    @Override
    public int updateTask(DiseaseAutoTask task)
    {
        if (task == null || task.getTaskId() == null)
        {
            throw new ServiceException("任务ID不能为空");
        }
        normalizeAndValidate(task, task.getTaskId());
        return mapper.updateTask(task);
    }

    @Override
    public int deleteTaskByIds(Long[] taskIds)
    {
        if (taskIds == null || taskIds.length == 0)
        {
            throw new ServiceException("请选择需要删除的任务");
        }
        return mapper.deleteTaskByIds(taskIds);
    }

    @Override
    public int updateTaskStatus(Long taskId, String taskStatus)
    {
        if (!"0".equals(taskStatus) && !"1".equals(taskStatus))
        {
            throw new ServiceException("任务状态只能为启用或停用");
        }
        DiseaseAutoTask task = requireTask(taskId);
        if (ENABLED.equals(taskStatus))
        {
            task.setTaskStatus(taskStatus);
            normalizeAndValidate(task, taskId);
            return mapper.updateTask(task);
        }
        return mapper.updateTaskStatus(taskId, taskStatus);
    }

    @Override
    public DiseaseAutoTaskRun executeTask(Long taskId, String triggerType)
    {
        if (!"AUTO".equals(triggerType))
        {
            return executeImmediately(taskId);
        }
        DiseaseAutoTask task = requireTask(taskId);
        if (!ENABLED.equals(task.getTaskStatus()))
        {
            throw new ServiceException("任务已停用，不能执行");
        }
        normalizeAndValidate(task, taskId);

        String token = UUID.randomUUID().toString().replace("-", "");
        if (mapper.acquireTask(taskId, token) != 1)
        {
            throw new ServiceException("任务正在执行，请勿重复触发");
        }
        task.setExecutionToken(token);

        DiseaseAutoTaskRun run = createRun(task, triggerType);
        try
        {
            List<DiseaseAutoTaskItem> sourceImages = mapper.selectSourceImages(task, SOURCE_FETCH_SIZE);
            if (sourceImages.isEmpty())
            {
                run.setRunStatus("SKIPPED");
                run.setEndTime(new Date());
                run.setErrorMessage("关联抓拍任务暂无新增图片");
                if (!"AUTO".equals(run.getTriggerType()))
                {
                    mapper.insertRun(run);
                    mapper.updateRun(run);
                }
                finishTask(task, run);
                return run;
            }

            mapper.insertRun(run);

            while (!sourceImages.isEmpty())
            {
                run.setSourceCount(run.getSourceCount() + sourceImages.size());
                for (DiseaseAutoTaskItem item : sourceImages)
                {
                    processImage(task, run, item, "自动定时");
                }
                if (sourceImages.size() < SOURCE_FETCH_SIZE) break;
                sourceImages = mapper.selectSourceImages(task, SOURCE_FETCH_SIZE);
            }
            String status = run.getFailCount() == 0 ? "SUCCESS" : run.getSuccessCount() > 0 ? "PARTIAL" : "FAILED";
            finishRun(run, status, buildRunMessage(run));
            finishTask(task, run);
            return run;
        }
        catch (Exception e)
        {
            if (run.getRunId() != null)
            {
                finishRun(run, "FAILED", limit(e.getMessage(), 2000));
            }
            finishTask(task, run);
            throw e instanceof ServiceException ? (ServiceException) e : new ServiceException("自动识别任务执行失败：" + e.getMessage());
        }
    }

    @Override
    public DiseaseAutoTaskRun executeImmediately(Long taskId)
    {
        DiseaseAutoTask task = requireTask(taskId);
        validateImmediateExecution(task);

        String token = UUID.randomUUID().toString().replace("-", "");
        if (mapper.acquireTaskForImmediate(taskId, token) != 1)
        {
            throw new ServiceException("任务正在执行，请勿重复触发");
        }
        task.setExecutionToken(token);

        DiseaseAutoTaskRun run = createRun(task, "MANUAL");
        try
        {
            mapper.insertRun(run);
            DiseaseAutoTaskItem sourceImage = mapper.selectLatestSourceImage(task);
            if (sourceImage == null)
            {
                finishRun(run, "SKIPPED", "关联抓拍任务或摄像头暂无可用历史图片");
                finishTask(task, run);
                return run;
            }

            run.setSourceCount(1);
            processImage(task, run, sourceImage, "自动定时");
            String status = run.getFailCount() == 0 ? "SUCCESS" : "FAILED";
            finishRun(run, status, buildRunMessage(run));
            finishTask(task, run);
            return run;
        }
        catch (Exception e)
        {
            if (run.getRunId() != null)
            {
                finishRun(run, "FAILED", limit(e.getMessage(), 2000));
            }
            finishTask(task, run);
            throw e instanceof ServiceException ? (ServiceException) e
                    : new ServiceException("立即识别执行失败：" + e.getMessage());
        }
    }

    @Override
    public void executeDueTasks()
    {
        for (DiseaseAutoTask task : mapper.selectDueTaskList(50))
        {
            try
            {
                executeTask(task.getTaskId(), "AUTO");
            }
            catch (Exception e)
            {
                log.warn("病虫害自动识别任务执行失败，taskId={}，原因={}", task.getTaskId(), e.getMessage());
            }
        }
    }

    @Override
    public List<DiseaseAutoTaskRun> selectRunList(Long taskId) { return mapper.selectRunList(taskId); }

    @Override
    public List<DiseaseAutoTaskItem> selectRunItems(Long runId) { return mapper.selectRunItems(runId); }

    @Override
    public DiseaseAutoTaskSummary selectSummary() { return mapper.selectSummary(); }

    @Override
    public List<DiseaseAutoTaskResult> selectResultList(DiseaseAutoResultQuery query)
    {
        if (query == null) query = new DiseaseAutoResultQuery();
        if (query.getDays() == null && isBlank(query.getBeginTime())) query.setDays(1);
        return mapper.selectResultList(query);
    }

    @Override
    public DiseaseAutoTaskResult selectResultByItemId(Long itemId)
    {
        return mapper.selectResultByItemId(itemId);
    }

    @Override
    public Map<String, Object> selectOptions(Long parkId)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("parks", mapper.selectParkOptions());
        result.put("devices", mapper.selectDeviceOptions(parkId));
        result.put("cameraTasks", mapper.selectCaptureTaskOptions());
        result.put("models", mapper.selectModelOptions());
        return result;
    }

    private void processImage(DiseaseAutoTask task, DiseaseAutoTaskRun run, DiseaseAutoTaskItem item,
            String recordMode)
    {
        item.setTaskId(task.getTaskId());
        item.setRunId(run.getRunId());
        item.setModelIdSnapshot(task.getModelId());
        item.setItemStatus("PENDING");
        try
        {
            if (mapper.insertItem(item) != 1) return;
        }
        catch (DuplicateKeyException e)
        {
            run.setSkipCount(run.getSkipCount() + 1);
            return;
        }

        run.setRecognizeCount(run.getRecognizeCount() + 1);
        try
        {
            mapper.insertAiImage(task, item);
            Long aiImageId = mapper.selectAiImageId(item.getSourceImageId());
            if (aiImageId == null) throw new ServiceException("设备图片同步失败，sourceImageId=" + item.getSourceImageId());
            item.setAiImageId(aiImageId);
            item.setAttemptCount(0);
            item.setStartTime(new Date());
            mapper.updateAiImageStatus(aiImageId, 1L);
            mapper.updateItemRunning(item);

            Map<String, Object> result = diseaseRecognitionService.predictByImage(
                    aiImageId,
                    task.getModelId(),
                    task.getCropType(),
                    recordMode,
                    task.getMaxRetryCount());
            item.setAttemptCount(asInt(result.get("modelAttemptCount"), 1));
            item.setRecordId(asLong(result.get("recordId")));
            item.setDiseaseId(asLong(result.get("diseaseId")));
            item.setFinishTime(new Date());
            mapper.updateItemSuccess(item);
            run.setSuccessCount(run.getSuccessCount() + 1);
        }
        catch (DiseaseModelCallException e)
        {
            item.setAttemptCount(e.getAttemptCount());
            item.setFinishTime(new Date());
            item.setErrorMessage(limit(e.getMessage(), 1000));
            mapper.updateItemFailed(item);
            if (item.getAiImageId() != null) mapper.updateAiImageStatus(item.getAiImageId(), 3L);
            run.setFailCount(run.getFailCount() + 1);
        }
        catch (Exception e)
        {
            item.setFinishTime(new Date());
            item.setErrorMessage(limit(e.getMessage(), 1000));
            mapper.updateItemFailed(item);
            if (item.getAiImageId() != null) mapper.updateAiImageStatus(item.getAiImageId(), 3L);
            run.setFailCount(run.getFailCount() + 1);
        }
    }

    private DiseaseAutoTaskRun createRun(DiseaseAutoTask task, String triggerType)
    {
        DiseaseAutoTaskRun run = new DiseaseAutoTaskRun();
        run.setTaskId(task.getTaskId());
        run.setCameraTaskId(task.getCameraTaskId());
        run.setDeviceId(task.getDeviceId());
        run.setParkId(task.getParkId());
        run.setModelId(task.getModelId());
        run.setTaskName(task.getTaskName());
        run.setTriggerType("AUTO".equals(triggerType) ? "AUTO" : "MANUAL");
        run.setRunStatus(RUNNING);
        run.setStartTime(new Date());
        run.setSourceCount(0); run.setRecognizeCount(0); run.setSuccessCount(0); run.setFailCount(0); run.setSkipCount(0);
        run.setCreateBy(currentUser());
        return run;
    }

    private void finishRun(DiseaseAutoTaskRun run, String status, String message)
    {
        run.setRunStatus(status);
        run.setEndTime(new Date());
        run.setErrorMessage(limit(message, 2000));
        mapper.updateRun(run);
    }

    private void finishTask(DiseaseAutoTask task, DiseaseAutoTaskRun run)
    {
        task.setLastRunTime(run.getEndTime() == null ? new Date() : run.getEndTime());
        task.setLastRunStatus(run.getRunStatus());
        task.setLastRunMessage(run.getErrorMessage());
        task.setNextRunTime(null);
        if (mapper.releaseTask(task) != 1)
        {
            log.warn("自动识别任务锁释放被忽略，taskId={}，执行令牌已变更", task.getTaskId());
        }
    }

    private void normalizeAndValidate(DiseaseAutoTask task, Long excludeTaskId)
    {
        if (task == null || isBlank(task.getTaskName())) throw new ServiceException("任务名称不能为空");
        if (task.getCameraTaskId() == null) throw new ServiceException("请选择摄像头抓拍任务");
        if (task.getModelId() == null) throw new ServiceException("请选择病虫害识别模型");
        if (isBlank(task.getCropType())) task.setCropType("枣树");
        if (isBlank(task.getTaskStatus())) task.setTaskStatus(ENABLED);
        task.setConsumeMode(CAPTURE_TASK_MODE);
        if (task.getMaxRetryCount() == null) task.setMaxRetryCount(2);
        if (task.getMaxRetryCount() < 0 || task.getMaxRetryCount() > 10) throw new ServiceException("最大重试次数必须在0至10之间");

        DiseaseAutoTaskOption captureTask = mapper.selectCaptureTaskOptionById(task.getCameraTaskId());
        if (captureTask == null) throw new ServiceException("关联的摄像头抓拍任务不存在");
        if (captureTask.getDeviceId() == null) throw new ServiceException("抓拍任务未关联有效摄像头设备");
        if (!ENABLED.equals(captureTask.getStatus()))
        {
            task.setInvalidReason("上游摄像头抓拍任务未启用");
            if (ENABLED.equals(task.getTaskStatus()))
            {
                if (excludeTaskId == null)
                {
                    task.setTaskStatus(DISABLED);
                }
                else
                {
                    throw new ServiceException("摄像头抓拍任务未启用，不能启用自动识别策略");
                }
            }
        }
        else
        {
            task.setInvalidReason(null);
        }

        task.setDeviceId(captureTask.getDeviceId());
        task.setParkId(captureTask.getParkId());
        task.setAreaId(captureTask.getAreaId());
        task.setPresetId(captureTask.getPresetId());
        if (task.getAreaId() == null || task.getParkId() == null)
            throw new ServiceException("抓拍设备未完整关联区域和园区，不能创建自动识别策略");
        if (mapper.countAvailableModel(task.getModelId()) == 0) throw new ServiceException("识别模型不存在、已停用或未配置接口地址");
        task.setTaskId(excludeTaskId);
        if (mapper.countDuplicateTask(task) > 0) throw new ServiceException("该抓拍任务已绑定相同识别模型");
    }

    private void validateImmediateExecution(DiseaseAutoTask task)
    {
        if (task.getCameraTaskId() == null) throw new ServiceException("任务未关联摄像头抓拍任务");
        if (task.getModelId() == null) throw new ServiceException("任务未关联病虫害识别模型");
        if (isBlank(task.getCropType())) task.setCropType("枣树");

        DiseaseAutoTaskOption captureTask = mapper.selectCaptureTaskOptionById(task.getCameraTaskId());
        if (mapper.countAvailableModel(task.getModelId()) == 0)
            throw new ServiceException("识别模型不存在、已停用或未配置接口地址");

        if (captureTask != null)
        {
            task.setDeviceId(captureTask.getDeviceId());
            task.setParkId(captureTask.getParkId());
            task.setAreaId(captureTask.getAreaId());
            task.setPresetId(captureTask.getPresetId());
        }
        if (task.getDeviceId() == null)
            throw new ServiceException("抓拍任务已不存在，且自动识别任务未保留摄像头信息");
    }

    private DiseaseAutoTask requireTask(Long taskId)
    {
        DiseaseAutoTask task = taskId == null ? null : mapper.selectTaskById(taskId);
        if (task == null) throw new ServiceException("自动识别任务不存在");
        return task;
    }

    private Long asLong(Object value) { return value == null ? null : Long.valueOf(value.toString()); }
    private int asInt(Object value, int defaultValue)
    {
        if (value == null) return defaultValue;
        try { return Integer.parseInt(value.toString()); }
        catch (NumberFormatException e) { return defaultValue; }
    }
    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    private String limit(String value, int max) { return value == null ? null : value.length() > max ? value.substring(0, max) : value; }
    private String buildRunMessage(DiseaseAutoTaskRun run) { return "识别成功" + run.getSuccessCount() + "张，失败" + run.getFailCount() + "张"; }
    private String currentUser()
    {
        try { return SecurityUtils.getUsername(); }
        catch (Exception e) { return "system"; }
    }
}
