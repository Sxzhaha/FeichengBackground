package com.ruoyi.iot.ld.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 摄像头定时抓拍任务对象 camera_task
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public class CameraTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务主键ID */
    private Long taskId;

    /** 任务名称 (例如: 1号大棚每日晨检) */
    @Excel(name = "任务名称 (例如: 1号大棚每日晨检)")
    private String taskName;

    /** 关联预置点ID  */
    @Excel(name = "关联预置点ID")
    private Long presetId;

    /** 任务类型 (ONCE=单次执行, CRON=周期循环) */
    @Excel(name = "任务类型 (ONCE=单次执行, CRON=周期循环)")
    private String jobType;

    /** 执行时间 (仅当 job_type=ONCE 时有效) */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "执行时间 (仅当 job_type=ONCE 时有效)", width = 30, dateFormat = "yyyy-MM-dd")
    private Date executeTime;

    /** Cron执行表达式 (仅当 job_type=CRON 时有效，如 "0 0 8 * * ?") */
    @Excel(name = "Cron执行表达式")
    private String cronExpression;

    /** 任务状态 (0=正常/启动, 1=暂停/停止, 2=已完成, 3=执行失败) */
    @Excel(name = "任务状态 (0=正常/启动, 1=暂停/停止, 2=已完成, 3=执行失败)")
    private String status;

    @Excel(name = "创建人ID")
    private Long creatorId;;

    @Excel(name = "创建人名称")
    private String creatorName;

    @Excel(name = "执行次数")
    private Integer count;

    @Excel(name = "设备名称")
    private String deviceName;
    /** 设备名称 */
    @Excel(name = "设备序列号")
    private String snCode;

    /** 预置点索引 (通常1-12) */
    @Excel(name = "预置点索引 (通常1-12)")
    private Integer pointIndex;




    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return this.taskId;
    }

    public void setTaskName(String taskName) 
    {
        this.taskName = taskName;
    }

    public String getTaskName() 
    {
        return taskName;
    }

    public void setJobType(String jobType) 
    {
        this.jobType = jobType;
    }

    public String getJobType() 
    {
        return jobType;
    }

    public void setExecuteTime(Date executeTime) 
    {
        this.executeTime = executeTime;
    }

    public Date getExecuteTime() 
    {
        return executeTime;
    }

    public void setCronExpression(String cronExpression) 
    {
        this.cronExpression = cronExpression;
    }

    public String getCronExpression() 
    {
        return cronExpression;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }


    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Long getPresetId() {
        return presetId;
    }

    public void setPresetId(Long presetId) {
        this.presetId = presetId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSnCode() {
        return snCode;
    }

    public void setSnCode(String snCode) {
        this.snCode = snCode;
    }

    public Integer getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(Integer pointIndex) {
        this.pointIndex = pointIndex;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("taskName", getTaskName())
            .append("presetId", getPresetId())
            .append("jobType", getJobType())
            .append("executeTime", getExecuteTime())
            .append("cronExpression", getCronExpression())
            .append("status", getStatus())
            .append("creatorId", getCreatorId())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("creatorName", getCreatorName())
            .append("count", getCount())
            .toString();
    }
}
