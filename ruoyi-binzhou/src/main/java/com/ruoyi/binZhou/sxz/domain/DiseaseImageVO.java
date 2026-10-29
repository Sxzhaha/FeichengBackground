package com.ruoyi.binZhou.sxz.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DiseaseImageVO
{
    private Long diseaseId;
    private Long recordId;
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
    private String modelName;
    private String parkName;
    private String deviceName;
    private String cropType;
    private String recordMode;
    private Long taskId;
    private Long runId;
    private String taskName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date captureTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recognitionTime;

    public Long getDiseaseId() { return diseaseId; }
    public void setDiseaseId(Long diseaseId) { this.diseaseId = diseaseId; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
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
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getParkName() { return parkName; }
    public void setParkName(String parkName) { this.parkName = parkName; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }
    public String getRecordMode() { return recordMode; }
    public void setRecordMode(String recordMode) { this.recordMode = recordMode; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getRunId() { return runId; }
    public void setRunId(Long runId) { this.runId = runId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Date getUploadTime() { return uploadTime; }
    public void setUploadTime(Date uploadTime) { this.uploadTime = uploadTime; }
    public Date getCaptureTime() { return captureTime; }
    public void setCaptureTime(Date captureTime) { this.captureTime = captureTime; }
    public Date getRecognitionTime() { return recognitionTime; }
    public void setRecognitionTime(Date recognitionTime) { this.recognitionTime = recognitionTime; }
}
