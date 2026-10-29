package com.ruoyi.binZhou.sxz.domain;

public class DiseaseAutoTaskQuery
{
    private String taskName;
    private Long parkId;
    private Long deviceId;
    private String taskStatus;
    private String runState;

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Long getParkId() { return parkId; }
    public void setParkId(Long parkId) { this.parkId = parkId; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public String getTaskStatus() { return taskStatus; }
    public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }
    public String getRunState() { return runState; }
    public void setRunState(String runState) { this.runState = runState; }
}
