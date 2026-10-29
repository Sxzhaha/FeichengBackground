package com.ruoyi.iot.ld.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Product thing-model alert rule object ld_agri_product_alert_config.
 */
public class AgriProductAlertConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "物模型ID")
    private Long modelId;

    @Excel(name = "设备ID")
    private Long deviceId;

    private Long productId;

    private String productName;

    private String modelName;

    private String deviceName;

    private String deviceSn;

    @Excel(name = "预警级别")
    private String alertLevel;

    @Excel(name = "预警下限")
    private BigDecimal thresholdMin;

    @Excel(name = "预警上限")
    private BigDecimal thresholdMax;

    @Excel(name = "预警规则描述")
    private String ruleDescription;

    @Excel(name = "是否启用", readConverterExp = "0=禁用,1=启用")
    private Long isEnabled;

    @Excel(name = "创建人")
    private Long creator;

    @Excel(name = "扩展字段1")
    private Long remark1;

    @Excel(name = "扩展字段2")
    private String remark2;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setModelId(Long modelId)
    {
        this.modelId = modelId;
    }

    public Long getModelId()
    {
        return modelId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId()
    {
        return deviceId;
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

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
    }

    public String getDeviceSn()
    {
        return deviceSn;
    }

    public void setAlertLevel(String alertLevel)
    {
        this.alertLevel = alertLevel;
    }

    public String getAlertLevel()
    {
        return alertLevel;
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

    public void setRuleDescription(String ruleDescription)
    {
        this.ruleDescription = ruleDescription;
    }

    public String getRuleDescription()
    {
        return ruleDescription;
    }

    public void setIsEnabled(Long isEnabled)
    {
        this.isEnabled = isEnabled;
    }

    public Long getIsEnabled()
    {
        return isEnabled;
    }

    public void setCreator(Long creator)
    {
        this.creator = creator;
    }

    public Long getCreator()
    {
        return creator;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("modelId", getModelId())
            .append("deviceId", getDeviceId())
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("modelName", getModelName())
            .append("deviceName", getDeviceName())
            .append("deviceSn", getDeviceSn())
            .append("alertLevel", getAlertLevel())
            .append("thresholdMin", getThresholdMin())
            .append("thresholdMax", getThresholdMax())
            .append("ruleDescription", getRuleDescription())
            .append("isEnabled", getIsEnabled())
            .append("createTime", getCreateTime())
            .append("creator", getCreator())
            .append("remark1", getRemark1())
            .append("remark2", getRemark2())
            .toString();
    }
}
