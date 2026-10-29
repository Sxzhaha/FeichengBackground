package com.ruoyi.binZhou.wr.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 预警模板对象 wr_warning_template
 *
 * @author ruoyi
 * @date 2026-06-26
 */
public class WarningTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预警模板唯一标识 */
    private Long id;

    /** 预警模板名称 */
    @Excel(name = "预警模板名称")
    private String templateName;

    /** 预警类型：如气象、土壤墒情、水肥异常、病虫害等 */
    @Excel(name = "预警类型：如气象、土壤墒情、水肥异常、病虫害等")
    private Long warningType;

    /** 园区ID */
    @Excel(name = "园区ID")
    private Long parkId;

    /** 园区名称 (关联查询字段) */
    @Excel(name = "园区名称")
    private String parkName;

    /** 地块ID */
    @Excel(name = "地块ID")
    private Long areaId;

    /** 地块名称 (关联查询字段) */
    @Excel(name = "地块名称")
    private String areaName;

    /** 区域ID */
    @Excel(name = "区域ID")
    private Long locationId;

    /** 区域名称 (关联查询字段) */
    @Excel(name = "区域名称")
    private String locationName;

    /** 适用作物 */
    @Excel(name = "适用作物")
    private String cropName;

    /** 模板适用场景及说明 */
    @Excel(name = "模板适用场景及说明")
    private String description;

    /** 状态：0禁用，1启用，2作废 */
    @Excel(name = "状态：0禁用，1启用，2作废")
    private Long status;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setWarningType(Long warningType)
    {
        this.warningType = warningType;
    }

    public Long getWarningType()
    {
        return warningType;
    }

    public void setParkId(Long parkId)
    {
        this.parkId = parkId;
    }

    public Long getParkId()
    {
        return parkId;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public void setAreaId(Long areaId)
    {
        this.areaId = areaId;
    }

    public Long getAreaId()
    {
        return areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setLocationId(Long locationId)
    {
        this.locationId = locationId;
    }

    public Long getLocationId()
    {
        return locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setCropName(String cropName)
    {
        this.cropName = cropName;
    }

    public String getCropName()
    {
        return cropName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("templateName", getTemplateName())
                .append("warningType", getWarningType())
                .append("parkId", getParkId())
                .append("parkName", getParkName())
                .append("areaId", getAreaId())
                .append("areaName", getAreaName())
                .append("locationId", getLocationId())
                .append("locationName", getLocationName())
                .append("cropName", getCropName())
                .append("description", getDescription())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}