package com.ruoyi.iot.ld.domain;

import com.fasterxml.jackson.annotation.JsonRawValue;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备通信日志对象 iot_device_log
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
public class IotDeviceLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long logId;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private String deviceSn;

    /** 类型(1属性上报 2事件 3命令响应) */
    @Excel(name = "类型(1属性上报 2下发 3命令响应)")
    private Integer logType;

    /** 原始报文或JSON数据 */
    @JsonRawValue
    @Excel(name = "原始报文或JSON数据")
    private String payload;

    public void setLogId(Long logId) 
    {
        this.logId = logId;
    }

    public Long getLogId() 
    {
        return logId;
    }

    public void setDeviceSn(String deviceSn) 
    {
        this.deviceSn = deviceSn;
    }

    public String getDeviceSn() 
    {
        return deviceSn;
    }

    public void setLogType(Integer logType) 
    {
        this.logType = logType;
    }

    public Integer getLogType() 
    {
        return logType;
    }

    public void setPayload(String payload) 
    {
        this.payload = payload;
    }

    public String getPayload() 
    {
        return payload;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("deviceSn", getDeviceSn())
            .append("logType", getLogType())
            .append("payload", getPayload())
            .append("createTime", getCreateTime())
            .toString();
    }
}
