package com.ruoyi.binZhou.wr.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 预警模板规则对象 wr_warning_template_rule
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
public class WarningTemplateRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模板规则唯一标识 */
    private Long id;

    /** 关联预警模板ID */
    @Excel(name = "关联预警模板ID")
    private Long templateId;

    /** 监测指标编码，如温度、湿度、PH、EC等 */
    @Excel(name = "监测指标编码，如温度、湿度、PH、EC等")
    private String indicatorCode;

    /** 比较操作符：>、<、>=、<=、between等 */
    @Excel(name = "比较操作符：>、<、>=、<=、between等")
    private String operator;

    /** 阈值下限 */
    @Excel(name = "阈值下限")
    private BigDecimal thresholdMin;

    /** 阈值上限 */
    @Excel(name = "阈值上限")
    private BigDecimal thresholdMax;

    /** 预警等级：1一级、2二级、3三级、4四级 */
    @Excel(name = "预警等级：1一级、2二级、3三级、4四级")
    private Long level;

    /** 持续多少秒后触发 */
    @Excel(name = "持续多少秒后触发")
    private Long durationSeconds;

    /** 触发类型：instant瞬时、duration持续、combined组合 */
    @Excel(name = "触发类型：instant瞬时、duration持续、combined组合")
    private String triggerType;

    /** 逻辑分组，多指标组合时使用 */
    @Excel(name = "逻辑分组，多指标组合时使用")
    private String logicGroup;

    /** 逻辑运算符：AND或OR */
    @Excel(name = "逻辑运算符：AND或OR")
    private String logicOperator;

    /** 规则执行或展示顺序 */
    @Excel(name = "规则执行或展示顺序")
    private Long sortOrder;

    /** 记录创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "记录创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 记录更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "记录更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    /** 预警模板名称 (非数据库原生字段，用于前端展示) */
    @Excel(name = "关联模板名称")
    private String templateName;

    // 记得生成 get/set 方法！
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }

    public void setIndicatorCode(String indicatorCode) 
    {
        this.indicatorCode = indicatorCode;
    }

    public String getIndicatorCode() 
    {
        return indicatorCode;
    }

    public void setOperator(String operator) 
    {
        this.operator = operator;
    }

    public String getOperator() 
    {
        return operator;
    }

    public void setThresholdMin(BigDecimal thresholdMin) 
    {
        this.thresholdMin = thresholdMin;
    }

    public BigDecimal getThresholdMin() 
    {
        return thresholdMin;
    }

    public void setThresholdMax(BigDecimal thresholdMax) 
    {
        this.thresholdMax = thresholdMax;
    }

    public BigDecimal getThresholdMax() 
    {
        return thresholdMax;
    }

    public void setLevel(Long level) 
    {
        this.level = level;
    }

    public Long getLevel() 
    {
        return level;
    }

    public void setDurationSeconds(Long durationSeconds) 
    {
        this.durationSeconds = durationSeconds;
    }

    public Long getDurationSeconds() 
    {
        return durationSeconds;
    }

    public void setTriggerType(String triggerType) 
    {
        this.triggerType = triggerType;
    }

    public String getTriggerType() 
    {
        return triggerType;
    }

    public void setLogicGroup(String logicGroup) 
    {
        this.logicGroup = logicGroup;
    }

    public String getLogicGroup() 
    {
        return logicGroup;
    }

    public void setLogicOperator(String logicOperator) 
    {
        this.logicOperator = logicOperator;
    }

    public String getLogicOperator() 
    {
        return logicOperator;
    }

    public void setSortOrder(Long sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder() 
    {
        return sortOrder;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("templateId", getTemplateId())
            .append("indicatorCode", getIndicatorCode())
            .append("operator", getOperator())
            .append("thresholdMin", getThresholdMin())
            .append("thresholdMax", getThresholdMax())
            .append("level", getLevel())
            .append("durationSeconds", getDurationSeconds())
            .append("triggerType", getTriggerType())
            .append("logicGroup", getLogicGroup())
            .append("logicOperator", getLogicOperator())
            .append("sortOrder", getSortOrder())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
