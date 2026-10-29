package com.ruoyi.binZhou.wr.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 预警记录对象 wr_warning_record
 * * @author ruoyi
 * @date 2026-06-26
 */
public class WarningRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String taskName;

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    /** 预警记录唯一标识 */
    private Long id;

    /** 关联预警任务ID */
    @Excel(name = "关联预警任务ID")
    private Long taskId;

    /** 触发设备ID */
    @Excel(name = "触发设备ID")
    private Long deviceId;

    /** 触发设备名称 (关联字段) */
    @Excel(name = "触发设备名称")
    private String deviceName;

    /** 预警负责人ID (角色ID) */
    @Excel(name = "预警负责人ID")
    private Long responsibleUserId;

    /** 预警负责人名称 (角色名称 - 关联字段) */
    @Excel(name = "预警负责人名称")
    private String roleName;

    /** 预警类型：气象、土壤、水肥、病虫害等 */
    @Excel(name = "预警类型：气象、土壤、水肥、病虫害等")
    private Long warningType;

    /** 预警等级：1紧急、2较重、3一般、4提示 */
    @Excel(name = "预警等级：1紧急、2较重、3一般、4提示")
    private Long warningLevel;

    /** 触发预警的监测指标编码 */
    @Excel(name = "触发预警的监测指标编码")
    private String indicatorCode;

    /** 触发时监测值 */
    @Excel(name = "触发时监测值")
    private BigDecimal currentValue;

    /** AI生成处置建议 */
    @Excel(name = "AI生成处置建议")
    private String aiSuggestion;

    /** AI分析结果 */
    @Excel(name = "AI分析结果")
    private String aiResultJson;

    /** 预警发生时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预警发生时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date warningTime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId()
    {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setResponsibleUserId(Long responsibleUserId)
    {
        this.responsibleUserId = responsibleUserId;
    }

    public Long getResponsibleUserId()
    {
        return responsibleUserId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setWarningType(Long warningType)
    {
        this.warningType = warningType;
    }

    public Long getWarningType()
    {
        return warningType;
    }

    public void setWarningLevel(Long warningLevel)
    {
        this.warningLevel = warningLevel;
    }

    public Long getWarningLevel()
    {
        return warningLevel;
    }

    public void setIndicatorCode(String indicatorCode)
    {
        this.indicatorCode = indicatorCode;
    }

    public String getIndicatorCode()
    {
        return indicatorCode;
    }

    public void setCurrentValue(BigDecimal currentValue)
    {
        this.currentValue = currentValue;
    }

    public BigDecimal getCurrentValue()
    {
        return currentValue;
    }

    public void setAiSuggestion(String aiSuggestion)
    {
        this.aiSuggestion = aiSuggestion;
    }

    public String getAiSuggestion()
    {
        return aiSuggestion;
    }

    public void setAiResultJson(String aiResultJson)
    {
        this.aiResultJson = aiResultJson;
    }

    public String getAiResultJson()
    {
        return aiResultJson;
    }

    public void setWarningTime(Date warningTime)
    {
        this.warningTime = warningTime;
    }

    public Date getWarningTime()
    {
        return warningTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("taskId", getTaskId())
                .append("taskName", getTaskName())
                .append("deviceId", getDeviceId())
                .append("deviceName", getDeviceName())
                .append("responsibleUserId", getResponsibleUserId())
                .append("roleName", getRoleName())
                .append("warningType", getWarningType())
                .append("warningLevel", getWarningLevel())
                .append("indicatorCode", getIndicatorCode())
                .append("currentValue", getCurrentValue())
                .append("aiSuggestion", getAiSuggestion())
                .append("aiResultJson", getAiResultJson())
                .append("warningTime", getWarningTime())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}