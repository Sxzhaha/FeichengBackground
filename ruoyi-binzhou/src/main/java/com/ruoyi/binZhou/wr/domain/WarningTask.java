package com.ruoyi.binZhou.wr.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 预警任务对象 wr_warning_task
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
public class WarningTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    /** 关联预警模板名称 (非数据库字段，用于前端展示) */
    private String templateName;

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    /** 预警任务唯一标识 */
    private Long id;

    /** 预警任务名称 */
    @Excel(name = "预警任务名称")
    private String taskName;

    /** 关联预警模板ID */
    @Excel(name = "关联预警模板ID")
    private Long templateId;

    /** 定时任务表达式 */
    @Excel(name = "定时任务表达式")
    private String cronExpression;

    /** 每日监控开始时间 */
    @JsonFormat(pattern = "HH:mm:ss")
    @Excel(name = "每日监控开始时间", width = 30, dateFormat = "HH:mm:ss")
    private Date monitorStartTime;

    /** 每日监控结束时间 */
    @JsonFormat(pattern = "HH:mm:ss")
    @Excel(name = "每日监控结束时间", width = 30, dateFormat = "HH:mm:ss")
    private Date monitorEndTime;

    /** 数据源类型：传感器数据、第三方数据、虫情数据、长势数据等 */
    @Excel(name = "数据源类型：传感器数据、第三方数据、虫情数据、长势数据等")
    private String dataSourceType;

    /** 关联数据源ID */
    @Excel(name = "关联数据源ID")
    private Long dataSourceId;

    /** 数据查询条件配置，注意做好安全校验 */
    @Excel(name = "数据查询条件配置，注意做好安全校验")
    private String queryConfigJson;

    /** 规则类型：rule基于规则库、nlp基于自然语言、mixed混合 */
    @Excel(name = "规则类型：rule基于规则库、nlp基于自然语言、mixed混合")
    private String ruleSourceType;

    /** 任务状态：running、paused、stopped、error */
    @Excel(name = "任务状态：running、paused、stopped、error")
    private String status;

    /** 业务扩展字段1 */
    @Excel(name = "业务扩展字段1")
    private String reservedField1;

    /** 业务扩展字段2 */
    @Excel(name = "业务扩展字段2")
    private String reservedField2;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setTaskName(String taskName) 
    {
        this.taskName = taskName;
    }

    public String getTaskName() 
    {
        return taskName;
    }

    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }

    public void setCronExpression(String cronExpression) 
    {
        this.cronExpression = cronExpression;
    }

    public String getCronExpression() 
    {
        return cronExpression;
    }

    public void setMonitorStartTime(Date monitorStartTime) 
    {
        this.monitorStartTime = monitorStartTime;
    }

    public Date getMonitorStartTime() 
    {
        return monitorStartTime;
    }

    public void setMonitorEndTime(Date monitorEndTime) 
    {
        this.monitorEndTime = monitorEndTime;
    }

    public Date getMonitorEndTime() 
    {
        return monitorEndTime;
    }

    public void setDataSourceType(String dataSourceType) 
    {
        this.dataSourceType = dataSourceType;
    }

    public String getDataSourceType() 
    {
        return dataSourceType;
    }

    public void setDataSourceId(Long dataSourceId) 
    {
        this.dataSourceId = dataSourceId;
    }

    public Long getDataSourceId() 
    {
        return dataSourceId;
    }

    public void setQueryConfigJson(String queryConfigJson) 
    {
        this.queryConfigJson = queryConfigJson;
    }

    public String getQueryConfigJson() 
    {
        return queryConfigJson;
    }

    public void setRuleSourceType(String ruleSourceType) 
    {
        this.ruleSourceType = ruleSourceType;
    }

    public String getRuleSourceType() 
    {
        return ruleSourceType;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setReservedField1(String reservedField1) 
    {
        this.reservedField1 = reservedField1;
    }

    public String getReservedField1() 
    {
        return reservedField1;
    }

    public void setReservedField2(String reservedField2) 
    {
        this.reservedField2 = reservedField2;
    }

    public String getReservedField2() 
    {
        return reservedField2;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("taskName", getTaskName())
            .append("templateId", getTemplateId())
            .append("cronExpression", getCronExpression())
            .append("monitorStartTime", getMonitorStartTime())
            .append("monitorEndTime", getMonitorEndTime())
            .append("dataSourceType", getDataSourceType())
            .append("dataSourceId", getDataSourceId())
            .append("queryConfigJson", getQueryConfigJson())
            .append("ruleSourceType", getRuleSourceType())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("reservedField1", getReservedField1())
            .append("reservedField2", getReservedField2())
            .toString();
    }
}
