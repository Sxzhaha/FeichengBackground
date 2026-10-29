package com.ruoyi.binZhou.sxz.domain;

import java.math.BigDecimal;
import java.util.Date;

public class DiseaseAutoTaskResult
{
    private Long itemId;
    private Long taskId;
    private String taskName;
    private Long runId;
    private Long recordId;
    private Long diseaseId;
    private Long imageId;
    private String imageName;
    private String imageUrl;
    private String resultImageUrl;
    private String diseaseName;
    private BigDecimal confidence;
    private String warningLevel;
    private String diseaseResult;
    private String diseaseDesc;
    private String diseaseAdvice;
    private Long parkId;
    private String parkName;
    private Long deviceId;
    private String deviceName;
    private Long modelId;
    private String modelName;
    private String cropType;
    private String recordMode;
    private Date recognizeTime;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getDiseaseId() { return diseaseId; }
    public void setDiseaseId(Long diseaseId) { this.diseaseId = diseaseId; }
    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getResultImageUrl() { return resultImageUrl; }
    public void setResultImageUrl(String resultImageUrl) { this.resultImageUrl = resultImageUrl; }
    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }
    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    public String getWarningLevel() { return warningLevel; }
    public void setWarningLevel(String warningLevel) { this.warningLevel = warningLevel; }
    public String getDiseaseResult() { return diseaseResult; }
    public void setDiseaseResult(String diseaseResult) { this.diseaseResult = diseaseResult; }
    public String getDiseaseDesc() { return diseaseDesc; }
    public void setDiseaseDesc(String diseaseDesc) { this.diseaseDesc = diseaseDesc; }
    public String getDiseaseAdvice() { return diseaseAdvice; }
    public void setDiseaseAdvice(String diseaseAdvice) { this.diseaseAdvice = diseaseAdvice; }
    public Long getParkId() { return parkId; }
    public void setParkId(Long parkId) { this.parkId = parkId; }
    public String getParkName() { return parkName; }
    public void setParkName(String parkName) { this.parkName = parkName; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }
    public String getRecordMode() { return recordMode; }
    public void setRecordMode(String recordMode) { this.recordMode = recordMode; }
    public Date getRecognizeTime() { return recognizeTime; }
    public void setRecognizeTime(Date recognizeTime) { this.recognizeTime = recognizeTime; }
}
