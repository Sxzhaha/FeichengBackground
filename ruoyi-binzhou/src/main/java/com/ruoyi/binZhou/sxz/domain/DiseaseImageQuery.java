package com.ruoyi.binZhou.sxz.domain;

public class DiseaseImageQuery
{
    private String imageName;
    private String diseaseName;
    private Long parkId;
    private String recordMode;
    private String taskName;
    private String deviceName;
    private String beginTime;
    private String endTime;

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }
    public Long getParkId() { return parkId; }
    public void setParkId(Long parkId) { this.parkId = parkId; }
    public String getRecordMode() { return recordMode; }
    public void setRecordMode(String recordMode) { this.recordMode = recordMode; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getBeginTime() { return beginTime; }
    public void setBeginTime(String beginTime) { this.beginTime = beginTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
