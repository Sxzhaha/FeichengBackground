package com.ruoyi.binZhou.sxz.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoResultQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTask;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskItem;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskOption;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskResult;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskRun;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskSummary;

public interface DiseaseAutoTaskMapper
{
    List<DiseaseAutoTask> selectTaskList(DiseaseAutoTaskQuery query);

    DiseaseAutoTask selectTaskById(Long taskId);

    int insertTask(DiseaseAutoTask task);

    int updateTask(DiseaseAutoTask task);

    int deleteTaskByIds(@Param("taskIds") Long[] taskIds);

    int updateTaskStatus(@Param("taskId") Long taskId, @Param("taskStatus") String taskStatus);

    List<DiseaseAutoTask> selectDueTaskList(@Param("limit") Integer limit);

    int acquireTask(@Param("taskId") Long taskId, @Param("token") String token);

    int acquireTaskForImmediate(@Param("taskId") Long taskId, @Param("token") String token);

    int releaseTask(DiseaseAutoTask task);

    int insertRun(DiseaseAutoTaskRun run);

    int updateRun(DiseaseAutoTaskRun run);

    List<DiseaseAutoTaskRun> selectRunList(@Param("taskId") Long taskId);

    DiseaseAutoTaskSummary selectSummary();

    List<DiseaseAutoTaskOption> selectParkOptions();

    List<DiseaseAutoTaskOption> selectDeviceOptions(@Param("parkId") Long parkId);

    List<DiseaseAutoTaskOption> selectModelOptions();

    List<DiseaseAutoTaskOption> selectCaptureTaskOptions();

    DiseaseAutoTaskOption selectCaptureTaskOptionById(@Param("cameraTaskId") Long cameraTaskId);

    Long selectDeviceParkId(@Param("deviceId") Long deviceId);

    int countAvailableModel(@Param("modelId") Long modelId);

    int countDuplicateTask(DiseaseAutoTask task);

    List<DiseaseAutoTaskItem> selectSourceImages(@Param("task") DiseaseAutoTask task,
                                                 @Param("limit") int limit);

    DiseaseAutoTaskItem selectLatestSourceImage(@Param("task") DiseaseAutoTask task);

    int insertItem(DiseaseAutoTaskItem item);

    int insertAiImage(@Param("task") DiseaseAutoTask task, @Param("item") DiseaseAutoTaskItem item);

    Long selectAiImageId(@Param("sourceImageId") Long sourceImageId);

    int updateAiImageStatus(@Param("imageId") Long imageId, @Param("imageStatus") Long imageStatus);

    int updateItemRunning(DiseaseAutoTaskItem item);

    int updateItemSuccess(DiseaseAutoTaskItem item);

    int updateItemFailed(DiseaseAutoTaskItem item);

    List<DiseaseAutoTaskResult> selectResultList(DiseaseAutoResultQuery query);

    DiseaseAutoTaskResult selectResultByItemId(@Param("itemId") Long itemId);

    List<DiseaseAutoTaskItem> selectRunItems(@Param("runId") Long runId);
}
