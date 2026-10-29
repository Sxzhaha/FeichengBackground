package com.ruoyi.iot.ld.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.xss.Xss;

/**
 * 物联网园区管理-区域表 area
 * 
 * @author ruoyi
 */
public class Area extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 区域ID，主键 */
    private Long areaId;

    /** 关联园区ID，外键 */
    private Long parkId;

    /** 区域名称 */
    private String areaName;

    /** 区域编码（唯一，物联网设备关联用） */
    private String areaCode;

    /** 经纬度坐标信息 */
    private String coordinate;

    public Long getAreaId()
    {
        return areaId;
    }

    public void setAreaId(Long areaId)
    {
        this.areaId = areaId;
    }

    public void setParkId(Long parkId)
    {
        this.parkId = parkId;
    }

    @NotNull(message = "园区ID不能为空")
    public Long getParkId()
    {
        return parkId;
    }

    public void setAreaName(String areaName)
    {
        this.areaName = areaName;
    }

    @Xss(message = "区域名称不能包含脚本字符")
    @NotBlank(message = "区域名称不能为空")
    @Size(min = 0, max = 64, message = "区域名称不能超过64个字符")
    public String getAreaName()
    {
        return areaName;
    }

    public void setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }

    @Xss(message = "区域编码不能包含脚本字符")
    @NotBlank(message = "区域编码不能为空")
    @Size(min = 0, max = 32, message = "区域编码不能超过32个字符")
    public String getAreaCode()
    {
        return areaCode;
    }

    public void setCoordinate(String coordinate)
    {
        this.coordinate = coordinate;
    }

    public String getCoordinate()
    {
        return coordinate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("areaId", getAreaId())
            .append("parkId", getParkId())
            .append("areaName", getAreaName())
            .append("areaCode", getAreaCode())
            .append("coordinate", getCoordinate())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
