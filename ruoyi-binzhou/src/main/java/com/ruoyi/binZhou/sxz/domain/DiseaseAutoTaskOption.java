package com.ruoyi.binZhou.sxz.domain;

import java.util.Date;

public class DiseaseAutoTaskOption
{
    private Long value;
    private String label;
    private Long deviceId;
    private String deviceName;
    private String deviceSn;
    private Long parkId;
    private String parkName;
    private Long areaId;
    private String areaName;
    private Long presetId;
    private Integer pointIndex;
    private String jobType;
    private Date executeTime;
    private String cronExpression;
    private Integer captureCount;
    private String status;

    public Long getValue() { return value; }
    public void setValue(Long value) { this.value = value; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getDeviceSn() { return deviceSn; }
    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public Long getParkId() { return parkId; }
    public void setParkId(Long parkId) { this.parkId = parkId; }
    public String getParkName() { return parkName; }
    public void setParkName(String parkName) { this.parkName = parkName; }
    public Long getAreaId() { return areaId; }
    public void setAreaId(Long areaId) { this.areaId = areaId; }
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public Long getPresetId() { return presetId; }
    public void setPresetId(Long presetId) { this.presetId = presetId; }
    public Integer getPointIndex() { return pointIndex; }
    public void setPointIndex(Integer pointIndex) { this.pointIndex = pointIndex; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public Date getExecuteTime() { return executeTime; }
    public void setExecuteTime(Date executeTime) { this.executeTime = executeTime; }
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public Integer getCaptureCount() { return captureCount; }
    public void setCaptureCount(Integer captureCount) { this.captureCount = captureCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
