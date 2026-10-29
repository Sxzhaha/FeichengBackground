package com.ruoyi.binZhou.sxz.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.annotation.Excel;

public class DiseaseAutoManageItemRow
{
    @Excel(name = "明细ID") private Long itemId;
    @Excel(name = "任务名称") private String taskName;
    @Excel(name = "批次ID") private Long runId;
    @Excel(name = "源图片ID") private Long sourceImageId;
    @Excel(name = "AI图片ID") private Long aiImageId;
    @Excel(name = "识别记录ID") private Long recordId;
    @Excel(name = "病害结果ID") private Long diseaseId;
    @Excel(name = "病虫害名称") private String diseaseName;
    @Excel(name = "置信度") private BigDecimal confidence;
    @Excel(name = "处理状态") private String itemStatus;
    @Excel(name = "尝试次数") private Integer attemptCount;
    @Excel(name = "开始时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss") private Date startTime;
    @Excel(name = "完成时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss") private Date finishTime;
    @Excel(name = "异常信息", width = 30) private String errorMessage;
    private String imageUrl;
    private String resultImageUrl;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    public Long getSourceImageId() { return sourceImageId; }
    public void setSourceImageId(Long sourceImageId) { this.sourceImageId = sourceImageId; }
    public Long getAiImageId() { return aiImageId; }
    public void setAiImageId(Long aiImageId) { this.aiImageId = aiImageId; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getDiseaseId() { return diseaseId; }
    public void setDiseaseId(Long diseaseId) { this.diseaseId = diseaseId; }
    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }
    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    public String getItemStatus() { return itemStatus; }
    public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }
    public Integer getAttemptCount() { return attemptCount; }
    public void setAttemptCount(Integer attemptCount) { this.attemptCount = attemptCount; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getResultImageUrl() { return resultImageUrl; }
    public void setResultImageUrl(String resultImageUrl) { this.resultImageUrl = resultImageUrl; }
}
