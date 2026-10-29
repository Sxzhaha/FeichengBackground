package com.ruoyi.iot.ld.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 物模型定义对象 iot_thing_model
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
public class IotThingModel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模型ID */
    private Long modelId;

    /** 关联产品ID */
    @Excel(name = "关联产品ID")
    private Long productId;

    /** 标识符(JSON中的Key) */
    @Excel(name = "标识符(JSON中的Key)")
    private String identifier;

    /** 属性名称(显示用) */
    @Excel(name = "属性名称(显示用)")
    private String modelName;

    /** 数据类型(int, float, bool, enum, string) */
    @Excel(name = "数据类型(int, float, bool, enum, string)")
    private String type;

    /** 数据定义(单位、范围、枚举值) */
    @Excel(name = "数据定义(单位、范围、枚举值)")
    private String specs;

    /** 是否在列表展示 */
    @Excel(name = "是否在列表展示")
    private Integer isMonitor;

    /** 关联产品名称 */
    @Excel(name = "关联产品名称")
    private String productName;

    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }

    public void setModelId(Long modelId) 
    {
        this.modelId = modelId;
    }

    public Long getModelId() 
    {
        return modelId;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setIdentifier(String identifier) 
    {
        this.identifier = identifier;
    }

    public String getIdentifier() 
    {
        return identifier;
    }

    public void setModelName(String modelName) 
    {
        this.modelName = modelName;
    }

    public String getModelName() 
    {
        return modelName;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setSpecs(String specs) 
    {
        this.specs = specs;
    }

    public String getSpecs() 
    {
        return specs;
    }

    public void setIsMonitor(Integer isMonitor) 
    {
        this.isMonitor = isMonitor;
    }

    public Integer getIsMonitor() 
    {
        return isMonitor;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("modelId", getModelId())
            .append("productId", getProductId())
            .append("identifier", getIdentifier())
            .append("modelName", getModelName())
            .append("type", getType())
            .append("specs", getSpecs())
            .append("isMonitor", getIsMonitor())
            .append("productName", getProductName())
            .toString();
    }
}
