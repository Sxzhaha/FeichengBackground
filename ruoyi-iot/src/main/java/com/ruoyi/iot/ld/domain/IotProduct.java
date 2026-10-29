package com.ruoyi.iot.ld.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Product object iot_product.
 *
 * Product type:
 * 0: collection terminal, reports data only.
 * 1: control terminal, reports data and accepts control responses.
 */
public class IotProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    public static final Integer TYPE_COLLECTOR = 0;

    public static final Integer TYPE_CONTROLLER = 1;

    /** Product ID. */
    private Long productId;

    /** Product name. */
    @Excel(name = "产品名称")
    private String productName;

    /** Product unique key. */
    @Excel(name = "产品标识")
    private String productKey;

    /** Product terminal type. */
    @Excel(name = "终端类型")
    private Integer type;

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

    public void setProductKey(String productKey)
    {
        this.productKey = productKey;
    }

    public String getProductKey()
    {
        return productKey;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return type;
    }

    public boolean isCollector()
    {
        return TYPE_COLLECTOR.equals(type);
    }

    public boolean isController()
    {
        return TYPE_CONTROLLER.equals(type);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("productKey", getProductKey())
            .append("type", getType())
            .toString();
    }
}
