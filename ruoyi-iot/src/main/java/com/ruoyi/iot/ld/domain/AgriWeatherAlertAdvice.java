package com.ruoyi.iot.ld.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 极端天气突发预警与防灾建议对象 ld_agri_weather_alert_advice
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
public class AgriWeatherAlertAdvice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识ID，唯一自增主键 */
    private Long id;

    /** 关联实时天气表或者未来天气表id，是数组形式例如[2,5,8] */
    @Excel(name = "关联实时天气表或者未来天气表id，是数组形式例如[2,5,8]")
    private String weatherId;

    /** 触发异常实况，触发大模型研判的异常天气数据（如：极端高温39.5℃持续） */
    @Excel(name = "触发异常实况，触发大模型研判的异常天气数据", readConverterExp = "如=：极端高温39.5℃持续")
    private String anomalyInfo;

    /** 预警级别，大模型评估的风险等级（如：蓝色、黄色、橙色、红色） */
    @Excel(name = "预警级别，大模型评估的风险等级", readConverterExp = "如=：蓝色、黄色、橙色、红色")
    private String alertLevel;

    /** 空气综合评价，大模型对当前空气综合指标（负氧离子、PM2.5等）的评价文字 */
    @Excel(name = "空气综合评价，大模型对当前空气综合指标", readConverterExp = "负=氧离子、PM2.5等")
    private String aqiEvaluation;

    /** 大模型防灾建议，大模型自动生成的针对性、个性化农事应急实操建议文本 */
    @Excel(name = "大模型防灾建议，大模型自动生成的针对性、个性化农事应急实操建议文本")
    private String alertAdvice;

    /** 处理状态，0:未处理；1:专家确认并推送；2:人工核销/误报 */
    @Excel(name = "处理状态，0:未处理；1:专家确认并推送；2:人工核销/误报")
    private Long auditStatus;

    /** 预留状态扩展字段：大模型预警推送渠道或紧急程度分类，默认1 */
    @Excel(name = "预留状态扩展字段：大模型预警推送渠道或紧急程度分类，默认1")
    private Long remark1;

    /** 预留文本扩展字段：专家人工核销原因或大模型推理链上下文快照 */
    @Excel(name = "预留文本扩展字段：专家人工核销原因或大模型推理链上下文快照")
    private String remark2;

    /** 预警类型，是实时还是预报 0代表实时，1代表未来天气 */
    @Excel(name = "预警类型，是实时还是预报 0代表实时，1代表未来天气")
    private Long type;

    /** 园区ID (查询条件) */
    private String parkId;

    /** 园区名称 (展示字段) */
    private String parkName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setWeatherId(String weatherId) 
    {
        this.weatherId = weatherId;
    }

    public String getWeatherId() 
    {
        return weatherId;
    }

    public void setAnomalyInfo(String anomalyInfo) 
    {
        this.anomalyInfo = anomalyInfo;
    }

    public String getAnomalyInfo() 
    {
        return anomalyInfo;
    }

    public void setAlertLevel(String alertLevel) 
    {
        this.alertLevel = alertLevel;
    }

    public String getAlertLevel() 
    {
        return alertLevel;
    }

    public void setAqiEvaluation(String aqiEvaluation) 
    {
        this.aqiEvaluation = aqiEvaluation;
    }

    public String getAqiEvaluation() 
    {
        return aqiEvaluation;
    }

    public void setAlertAdvice(String alertAdvice) 
    {
        this.alertAdvice = alertAdvice;
    }

    public String getAlertAdvice() 
    {
        return alertAdvice;
    }

    public void setAuditStatus(Long auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public Long getAuditStatus() 
    {
        return auditStatus;
    }

    public void setRemark1(Long remark1) 
    {
        this.remark1 = remark1;
    }

    public Long getRemark1() 
    {
        return remark1;
    }

    public void setRemark2(String remark2) 
    {
        this.remark2 = remark2;
    }

    public String getRemark2() 
    {
        return remark2;
    }

    public void setType(Long type) 
    {
        this.type = type;
    }

    public Long getType() 
    {
        return type;
    }

    public void setParkId(String parkId) 
    {
        this.parkId = parkId;
    }

    public String getParkId() 
    {
        return parkId;
    }

    public void setParkName(String parkName)
    {
        this.parkName = parkName;
    }

    public String getParkName()
    {
        return parkName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("weatherId", getWeatherId())
            .append("anomalyInfo", getAnomalyInfo())
            .append("alertLevel", getAlertLevel())
            .append("aqiEvaluation", getAqiEvaluation())
            .append("alertAdvice", getAlertAdvice())
            .append("auditStatus", getAuditStatus())
            .append("createTime", getCreateTime())
            .append("remark1", getRemark1())
            .append("remark2", getRemark2())
            .append("type", getType())
            .append("parkId", getParkId())
            .append("parkName", getParkName())
            .toString();
    }
}
