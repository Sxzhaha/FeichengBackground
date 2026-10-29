package com.ruoyi.iot.ld.domain;

import java.util.Date;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Manual control event object iot_manual_control_event.
 */
public class IotManualControlEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long eventId;

    @Excel(name = "控制终端设备ID")
    private Long deviceId;

    @Excel(name = "控制终端设备SN")
    private String deviceSn;

    @Excel(name = "控制字段标识符")
    private String targetIdentifiers;

    @Excel(name = "MQTT下发主题")
    private String topic;

    @Excel(name = "下发payload报文")
    private String payload;

    @Excel(name = "指令标识符")
    private String res;

    @Excel(name = "设备响应报文")
    private String responsePayload;

    @Excel(name = "响应说明")
    private String responseDesc;

    @Excel(name = "状态", readConverterExp = "0=待下发,1=已下发,2=成功,3=失败,4=超时")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下发时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "响应时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date responseTime;

    private String deviceName;

    private Map<String, Object> commands;

    public Long getEventId()
    {
        return eventId;
    }

    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
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

    public String getTargetIdentifiers()
    {
        return targetIdentifiers;
    }

    public void setTargetIdentifiers(String targetIdentifiers)
    {
        this.targetIdentifiers = targetIdentifiers;
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

    public String getRes()
    {
        return res;
    }

    public void setRes(String res)
    {
        this.res = res;
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

    public Map<String, Object> getCommands()
    {
        return commands;
    }

    public void setCommands(Map<String, Object> commands)
    {
        this.commands = commands;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("eventId", getEventId())
            .append("deviceId", getDeviceId())
            .append("deviceSn", getDeviceSn())
            .append("targetIdentifiers", getTargetIdentifiers())
            .append("topic", getTopic())
            .append("payload", getPayload())
            .append("res", getRes())
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
