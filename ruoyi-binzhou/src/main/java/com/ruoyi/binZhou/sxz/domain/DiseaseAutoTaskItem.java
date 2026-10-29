package com.ruoyi.binZhou.sxz.domain;

import java.util.Date;

public class DiseaseAutoTaskItem
{
    private Long itemId;
    private Long taskId;
    private Long runId;
    private Long sourceImageId;
    private Long modelIdSnapshot;
    private Date captureTime;
    private Long aiImageId;
    private Long recordId;
    private Long diseaseId;
    private String itemStatus;
    private Integer attemptCount;
    private Date nextRetryTime;
    private Date startTime;
    private Date finishTime;
    private String errorMessage;
    private String imageUrl;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    public Long getSourceImageId() { return sourceImageId; }
    public void setSourceImageId(Long sourceImageId) { this.sourceImageId = sourceImageId; }
    public Long getModelIdSnapshot() { return modelIdSnapshot; }
    public void setModelIdSnapshot(Long modelIdSnapshot) { this.modelIdSnapshot = modelIdSnapshot; }
    public Date getCaptureTime() { return captureTime; }
    public void setCaptureTime(Date captureTime) { this.captureTime = captureTime; }
    public Long getAiImageId() { return aiImageId; }
    public void setAiImageId(Long aiImageId) { this.aiImageId = aiImageId; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getDiseaseId() { return diseaseId; }
    public void setDiseaseId(Long diseaseId) { this.diseaseId = diseaseId; }
    public String getItemStatus() { return itemStatus; }
    public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }
    public Integer getAttemptCount() { return attemptCount; }
    public void setAttemptCount(Integer attemptCount) { this.attemptCount = attemptCount; }
    public Date getNextRetryTime() { return nextRetryTime; }
    public void setNextRetryTime(Date nextRetryTime) { this.nextRetryTime = nextRetryTime; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
