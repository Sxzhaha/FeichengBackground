package com.ruoyi.iot.ld.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 控制事件下发对象 iot_control_event
 */
public class IotControlEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 控制事件ID */
    private Long eventId;

    /** 关联联动控制规则ID */
    @Excel(name = "联动控制规则ID")
    private Long controlId;

    /** 关联预警记录ID */
    @Excel(name = "预警记录ID")
    private Long alertRecordId;

    /** 控制终端设备ID */
    @Excel(name = "控制终端设备ID")
    private Long deviceId;

    /** 控制终端设备编号 */
    @Excel(name = "控制终端设备编号")
    private String deviceSn;

    /** 控制物模型ID */
    @Excel(name = "控制物模型ID")
    private Long targetModelId;

    /** 控制字段标识符 */
    @Excel(name = "控制字段标识符")
    private String targetIdentifier;

    /** MQTT下发主题 */
    @Excel(name = "MQTT下发主题")
    private String topic;

    /** 下发payload报文 */
    @Excel(name = "下发payload报文")
    private String payload;

    /** 设备响应报文 */
    @Excel(name = "设备响应报文")
    private String responsePayload;

    /** 响应说明 */
    @Excel(name = "响应说明")
    private String responseDesc;

    /** 状态：0待下发 1已下发 2成功 3失败 4超时 */
    @Excel(name = "状态", readConverterExp = "0=待下发,1=已下发,2=成功,3=失败,4=超时")
    private Integer status;

    /** 下发时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下发时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    /** 响应时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "响应时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date responseTime;

    /** 控制终端设备名称 */
    private String deviceName;

    /** 联动动作名称 */
    private String actionName;

    /** 控制物模型名称 */
    private String targetModelName;

    /** 预警等级 */
    private String alertLevel;

    /** 预警规则说明 */
    private String ruleDescription;

    public Long getEventId()
    {
        return eventId;
    }

    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }

    public Long getControlId()
    {
        return controlId;
    }

    public void setControlId(Long controlId)
    {
        this.controlId = controlId;
    }

    public Long getAlertRecordId()
    {
        return alertRecordId;
    }

    public void setAlertRecordId(Long alertRecordId)
    {
        this.alertRecordId = alertRecordId;
    }

    public Long getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getDeviceSn()
    {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
    }

    public Long getTargetModelId()
    {
        return targetModelId;
    }

    public void setTargetModelId(Long targetModelId)
    {
        this.targetModelId = targetModelId;
    }

    public String getTargetIdentifier()
    {
        return targetIdentifier;
    }

    public void setTargetIdentifier(String targetIdentifier)
    {
        this.targetIdentifier = targetIdentifier;
    }

    public String getTopic()
    {
        return topic;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(String payload)
    {
        this.payload = payload;
    }

    public String getResponsePayload()
    {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload)
    {
        this.responsePayload = responsePayload;
    }

    public String getResponseDesc()
    {
        return responseDesc;
    }

    public void setResponseDesc(String responseDesc)
    {
        this.responseDesc = responseDesc;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getResponseTime()
    {
        return responseTime;
    }

    public void setResponseTime(Date responseTime)
    {
        this.responseTime = responseTime;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    public String getTargetModelName()
    {
        return targetModelName;
    }

    public void setTargetModelName(String targetModelName)
    {
        this.targetModelName = targetModelName;
    }

    public String getAlertLevel()
    {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel)
    {
        this.alertLevel = alertLevel;
    }

    public String getRuleDescription()
    {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription)
    {
        this.ruleDescription = ruleDescription;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("eventId", getEventId())
            .append("controlId", getControlId())
            .append("alertRecordId", getAlertRecordId())
            .append("deviceId", getDeviceId())
            .append("deviceSn", getDeviceSn())
            .append("targetModelId", getTargetModelId())
            .append("targetIdentifier", getTargetIdentifier())
            .append("topic", getTopic())
            .append("payload", getPayload())
            .append("responsePayload", getResponsePayload())
            .append("responseDesc", getResponseDesc())
            .append("status", getStatus())
            .append("sendTime", getSendTime())
            .append("responseTime", getResponseTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
