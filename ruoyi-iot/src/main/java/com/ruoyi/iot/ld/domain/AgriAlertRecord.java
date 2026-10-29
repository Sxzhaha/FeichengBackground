package com.ruoyi.iot.ld.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 产品预警触发与历史处理记录对象 ld_agri_alert_record
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class AgriAlertRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识ID，唯一自增主键 */
    private Long id;

    /** 设备日志ID，关联设备上报日志表或流水表的唯一ID */
    @Excel(name = "设备日志ID，关联设备上报日志表或流水表的唯一ID")
    private Long deviceLogId;

    /** 预警等级配置ID，关联 ld_agri_alert_level_config 表的 id */
    @Excel(name = "预警等级配置ID，关联 ld_agri_alert_level_config 表的 id")
    private Long levelConfigId;

    /** 报警时实际数值，触发报警时的传感器真实读数（如：40.2） */
    @Excel(name = "报警时实际数值，触发报警时的传感器真实读数", readConverterExp = "如=：40.2")
    private BigDecimal actualValue;

    /** 大模型防灾建议，联动大模型自动生成的针对该条预警的防灾应急实操建议文本 */
    @Excel(name = "大模型防灾建议，联动大模型自动生成的针对该条预警的防灾应急实操建议文本")
    private String alertAdvice;

    /** 人工处理状态，0:未处理；1:已处理/已确认；2:误报消除 */
    @Excel(name = "人工处理状态，0:未处理；1:已处理/已确认；2:误报消除")
    private Long handleStatus;

    /** 处理人ID，执行核销的系统用户或农艺专家ID */
    @Excel(name = "处理人ID，执行核销的系统用户或农艺专家ID")
    private String handlerId;

    /** 处理时间，人工确认或核销的具体时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "处理时间，人工确认或核销的具体时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date handleTime;

    /** 预留状态扩展字段：处理优先级或后续跟进状态，默认1 */
    @Excel(name = "预留状态扩展字段：处理优先级或后续跟进状态，默认1")
    private Long remark1;

    /** 预留文本扩展字段：人工处理备注或大模型追问历史JSON结构 */
    @Excel(name = "预留文本扩展字段：人工处理备注或大模型追问历史JSON结构")
    private String remark2;

    /** 设备序列号 */
    private String deviceSn;

    /** 设备名称 */
    private String deviceName;

    /** 设备上报原始数据 */
    private String payload;

    /** 产品ID */
    private Long productId;

    /** 产品名称 */
    private String productName;

    /** 园区ID (查询条件) */
    private String parkId;

    /** 位置ID (查询条件) */
    private Long locationId;

    /** 区域ID (查询条件) */
    private Long areaId;

    public void setDeviceSn(String deviceSn) 
    {
        this.deviceSn = deviceSn;
    }

    public String getDeviceSn() 
    {
        return deviceSn;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setPayload(String payload)
    {
        this.payload = payload;
    }

    public String getPayload()
    {
        return payload;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setDeviceLogId(Long deviceLogId) 
    {
        this.deviceLogId = deviceLogId;
    }

    public Long getDeviceLogId() 
    {
        return deviceLogId;
    }

    public void setLevelConfigId(Long levelConfigId) 
    {
        this.levelConfigId = levelConfigId;
    }

    public Long getLevelConfigId() 
    {
        return levelConfigId;
    }

    public void setActualValue(BigDecimal actualValue) 
    {
        this.actualValue = actualValue;
    }

    public BigDecimal getActualValue() 
    {
        return actualValue;
    }

    public void setAlertAdvice(String alertAdvice) 
    {
        this.alertAdvice = alertAdvice;
    }

    public String getAlertAdvice() 
    {
        return alertAdvice;
    }

    public void setHandleStatus(Long handleStatus) 
    {
        this.handleStatus = handleStatus;
    }

    public Long getHandleStatus() 
    {
        return handleStatus;
    }

    public void setHandlerId(String handlerId) 
    {
        this.handlerId = handlerId;
    }

    public String getHandlerId() 
    {
        return handlerId;
    }

    public void setHandleTime(Date handleTime) 
    {
        this.handleTime = handleTime;
    }

    public Date getHandleTime() 
    {
        return handleTime;
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

    public void setParkId(String parkId) 
    {
        this.parkId = parkId;
    }

    public String getParkId() 
    {
        return parkId;
    }

    public void setLocationId(Long locationId) 
    {
        this.locationId = locationId;
    }

    public Long getLocationId() 
    {
        return locationId;
    }

    public void setAreaId(Long areaId) 
    {
        this.areaId = areaId;
    }

    public Long getAreaId() 
    {
        return areaId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceLogId", getDeviceLogId())
            .append("levelConfigId", getLevelConfigId())
            .append("actualValue", getActualValue())
            .append("alertAdvice", getAlertAdvice())
            .append("handleStatus", getHandleStatus())
            .append("handlerId", getHandlerId())
            .append("handleTime", getHandleTime())
            .append("createTime", getCreateTime())
            .append("remark1", getRemark1())
            .append("remark2", getRemark2())
            .append("deviceSn", getDeviceSn())
            .append("deviceName", getDeviceName())
            .append("payload", getPayload())
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("parkId", getParkId())
            .append("locationId", getLocationId())
            .append("areaId", getAreaId())
            .toString();
    }
}
