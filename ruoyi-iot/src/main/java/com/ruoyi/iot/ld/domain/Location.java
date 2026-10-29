package com.ruoyi.iot.ld.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.xss.Xss;

/**
 * 物联网园区管理-位置表 location
 * 
 * @author ruoyi
 */
public class Location extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 位置ID，主键 */
    private Long locationId;

    /** 关联区域ID，外键 */
    private Long areaId;

    /** 位置名称（如XX车间-XX设备位） */
    private String locationName;

    /** 位置编码（唯一，物联网设备唯一绑定） */
    private String locationCode;

    /** 经纬度坐标信息 */
    private String coordinate;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    @NotNull(message = "区域ID不能为空")
    public Long getAreaId() {
        return areaId;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Xss(message = "位置名称不能包含脚本字符")
    @NotBlank(message = "位置名称不能为空")
    @Size(min = 0, max = 64, message = "位置名称不能超过64个字符")
    public String getLocationName() {
        return locationName;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    @Xss(message = "位置编码不能包含脚本字符")
    @NotBlank(message = "位置编码不能为空")
    @Size(min = 0, max = 32, message = "位置编码不能超过32个字符")
    public String getLocationCode() {
        return locationCode;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getCoordinate() {
        return coordinate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("locationId", getLocationId())
                .append("areaId", getAreaId())
                .append("locationName", getLocationName())
                .append("locationCode", getLocationCode())
                .append("coordinate", getCoordinate())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
