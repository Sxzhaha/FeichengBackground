package com.ruoyi.binZhou.sxz.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoResultQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTask;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskItem;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskResult;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskRun;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskSummary;

public interface IDiseaseAutoTaskService
{
    List<DiseaseAutoTask> selectTaskList(DiseaseAutoTaskQuery query);

    DiseaseAutoTask selectTaskById(Long taskId);

    int insertTask(DiseaseAutoTask task);

    int updateTask(DiseaseAutoTask task);

    int deleteTaskByIds(Long[] taskIds);

    int updateTaskStatus(Long taskId, String taskStatus);

    DiseaseAutoTaskRun executeTask(Long taskId, String triggerType);

    DiseaseAutoTaskRun executeImmediately(Long taskId);

    void executeDueTasks();

    List<DiseaseAutoTaskRun> selectRunList(Long taskId);

    List<DiseaseAutoTaskItem> selectRunItems(Long runId);

    DiseaseAutoTaskSummary selectSummary();

    List<DiseaseAutoTaskResult> selectResultList(DiseaseAutoResultQuery query);

    DiseaseAutoTaskResult selectResultByItemId(Long itemId);

    Map<String, Object> selectOptions(Long parkId);
}
