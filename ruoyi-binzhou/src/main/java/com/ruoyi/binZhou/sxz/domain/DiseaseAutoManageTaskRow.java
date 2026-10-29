package com.ruoyi.binZhou.sxz.domain;

import java.util.Date;
import com.ruoyi.common.annotation.Excel;

public class DiseaseAutoManageTaskRow
{
    @Excel(name = "任务ID")
    private Long taskId;
    @Excel(name = "任务名称")
    private String taskName;
    @Excel(name = "园区")
    private String parkName;
    @Excel(name = "设备")
    private String deviceName;
    @Excel(name = "识别模型")
    private String modelName;
    @Excel(name = "作物类型")
    private String cropType;
    @Excel(name = "调度类型")
    private String scheduleType;
    @Excel(name = "固定时刻")
    private String fixedTimes;
    @Excel(name = "间隔分钟")
    private Integer intervalMinutes;
    @Excel(name = "任务状态")
    private String taskStatus;
    @Excel(name = "运行状态")
    private String runState;
    @Excel(name = "上次执行时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastRunTime;
    @Excel(name = "下次执行时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date nextRunTime;
    @Excel(name = "最近执行结果")
    private String lastRunStatus;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getParkName() { return parkName; }
    public void setParkName(String parkName) { this.parkName = parkName; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }
    public String getScheduleType() { return scheduleType; }
    public void setScheduleType(String scheduleType) { this.scheduleType = scheduleType; }
    public String getFixedTimes() { return fixedTimes; }
    public void setFixedTimes(String fixedTimes) { this.fixedTimes = fixedTimes; }
    public Integer getIntervalMinutes() { return intervalMinutes; }
    public void setIntervalMinutes(Integer intervalMinutes) { this.intervalMinutes = intervalMinutes; }
    public String getTaskStatus() { return taskStatus; }
    public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }
    public String getRunState() { return runState; }
    public void setRunState(String runState) { this.runState = runState; }
    public Date getLastRunTime() { return lastRunTime; }
    public void setLastRunTime(Date lastRunTime) { this.lastRunTime = lastRunTime; }
    public Date getNextRunTime() { return nextRunTime; }
    public void setNextRunTime(Date nextRunTime) { this.nextRunTime = nextRunTime; }
    public String getLastRunStatus() { return lastRunStatus; }
    public void setLastRunStatus(String lastRunStatus) { this.lastRunStatus = lastRunStatus; }
}
