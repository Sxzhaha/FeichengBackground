package com.ruoyi.binZhou.sxz.domain;

import java.math.BigDecimal;
import java.util.Date;

public class DiseaseAutoTaskSummary
{
    private Long taskCount;
    private Long enabledTaskCount;
    private Long disabledTaskCount;
    private Long abnormalTaskCount;
    private Long todayRunCount;
    private Long todayImageCount;
    private Long todayHealthyCount;
    private Long todayDiseaseCount;
    private BigDecimal todayDetectionRate;
    private BigDecimal todaySuccessRate;
    private Date latestRecognizeTime;

    public Long getTaskCount() { return taskCount; }
    public void setTaskCount(Long taskCount) { this.taskCount = taskCount; }
    public Long getEnabledTaskCount() { return enabledTaskCount; }
    public void setEnabledTaskCount(Long enabledTaskCount) { this.enabledTaskCount = enabledTaskCount; }
    public Long getDisabledTaskCount() { return disabledTaskCount; }
    public void setDisabledTaskCount(Long disabledTaskCount) { this.disabledTaskCount = disabledTaskCount; }
    public Long getAbnormalTaskCount() { return abnormalTaskCount; }
    public void setAbnormalTaskCount(Long abnormalTaskCount) { this.abnormalTaskCount = abnormalTaskCount; }
    public Long getTodayRunCount() { return todayRunCount; }
    public void setTodayRunCount(Long todayRunCount) { this.todayRunCount = todayRunCount; }
    public Long getTodayImageCount() { return todayImageCount; }
    public void setTodayImageCount(Long todayImageCount) { this.todayImageCount = todayImageCount; }
    public Long getTodayHealthyCount() { return todayHealthyCount; }
    public void setTodayHealthyCount(Long todayHealthyCount) { this.todayHealthyCount = todayHealthyCount; }
    public Long getTodayDiseaseCount() { return todayDiseaseCount; }
    public void setTodayDiseaseCount(Long todayDiseaseCount) { this.todayDiseaseCount = todayDiseaseCount; }
    public BigDecimal getTodayDetectionRate() { return todayDetectionRate; }
    public void setTodayDetectionRate(BigDecimal todayDetectionRate) { this.todayDetectionRate = todayDetectionRate; }
    public BigDecimal getTodaySuccessRate() { return todaySuccessRate; }
    public void setTodaySuccessRate(BigDecimal todaySuccessRate) { this.todaySuccessRate = todaySuccessRate; }
    public Date getLatestRecognizeTime() { return latestRecognizeTime; }
    public void setLatestRecognizeTime(Date latestRecognizeTime) { this.latestRecognizeTime = latestRecognizeTime; }
}
