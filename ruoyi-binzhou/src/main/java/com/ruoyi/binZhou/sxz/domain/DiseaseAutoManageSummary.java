package com.ruoyi.binZhou.sxz.domain;

public class DiseaseAutoManageSummary
{
    private Long taskCount;
    private Long enabledTaskCount;
    private Long runningTaskCount;
    private Long runCount;
    private Long successRunCount;
    private Long itemCount;
    private Long successItemCount;
    private Long failedItemCount;

    public Long getTaskCount() { return taskCount; }
    public void setTaskCount(Long taskCount) { this.taskCount = taskCount; }
    public Long getEnabledTaskCount() { return enabledTaskCount; }
    public void setEnabledTaskCount(Long enabledTaskCount) { this.enabledTaskCount = enabledTaskCount; }
    public Long getRunningTaskCount() { return runningTaskCount; }
    public void setRunningTaskCount(Long runningTaskCount) { this.runningTaskCount = runningTaskCount; }
    public Long getRunCount() { return runCount; }
    public void setRunCount(Long runCount) { this.runCount = runCount; }
    public Long getSuccessRunCount() { return successRunCount; }
    public void setSuccessRunCount(Long successRunCount) { this.successRunCount = successRunCount; }
    public Long getItemCount() { return itemCount; }
    public void setItemCount(Long itemCount) { this.itemCount = itemCount; }
    public Long getSuccessItemCount() { return successItemCount; }
    public void setSuccessItemCount(Long successItemCount) { this.successItemCount = successItemCount; }
    public Long getFailedItemCount() { return failedItemCount; }
    public void setFailedItemCount(Long failedItemCount) { this.failedItemCount = failedItemCount; }
}
