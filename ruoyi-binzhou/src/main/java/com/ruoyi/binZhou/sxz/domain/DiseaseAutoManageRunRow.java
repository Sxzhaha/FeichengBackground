package com.ruoyi.binZhou.sxz.domain;

import java.util.Date;
import com.ruoyi.common.annotation.Excel;

public class DiseaseAutoManageRunRow
{
    @Excel(name = "批次ID") private Long runId;
    @Excel(name = "任务ID") private Long taskId;
    @Excel(name = "任务名称") private String taskName;
    @Excel(name = "触发方式") private String triggerType;
    @Excel(name = "执行状态") private String runStatus;
    @Excel(name = "开始时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss") private Date startTime;
    @Excel(name = "结束时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss") private Date endTime;
    @Excel(name = "发现图片") private Integer sourceCount;
    @Excel(name = "发起识别") private Integer recognizeCount;
    @Excel(name = "成功") private Integer successCount;
    @Excel(name = "失败") private Integer failCount;
    @Excel(name = "跳过") private Integer skipCount;
    @Excel(name = "异常信息", width = 30) private String errorMessage;

    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
    public String getRunStatus() { return runStatus; }
    public void setRunStatus(String runStatus) { this.runStatus = runStatus; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Integer getSourceCount() { return sourceCount; }
    public void setSourceCount(Integer sourceCount) { this.sourceCount = sourceCount; }
    public Integer getRecognizeCount() { return recognizeCount; }
    public void setRecognizeCount(Integer recognizeCount) { this.recognizeCount = recognizeCount; }
    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }
    public Integer getFailCount() { return failCount; }
    public void setFailCount(Integer failCount) { this.failCount = failCount; }
    public Integer getSkipCount() { return skipCount; }
    public void setSkipCount(Integer skipCount) { this.skipCount = skipCount; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
