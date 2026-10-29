package com.ruoyi.iot.ld.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.xss.Xss;

/**
 * 物联网园区管理-园区表 park
 * 
 * @author ruoyi
 */
public class Park extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 园区ID，主键 */
    private Long parkId;

    /** 园区名称（唯一） */
    private String parkName;

    /** 园区编码（唯一，物联网设备关联用） */
    private String parkCode;

    /** 经纬度坐标信息 */
    private String coordinate;

    /** 行政区（市级） */
    private String region;

    /** 地区代码 */
    private String regionCode;

    // 用户Id
    private Long userId;

    public Long getParkId() {
        return parkId;
    }

    public void setParkId(Long parkId) {
        this.parkId = parkId;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    @Xss(message = "园区名称不能包含脚本字符")
    @NotBlank(message = "园区名称不能为空")
    @Size(min = 0, max = 64, message = "园区名称不能超过64个字符")
    public String getParkName() {
        return parkName;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    @Xss(message = "园区编码不能包含脚本字符")
    @NotBlank(message = "园区编码不能为空")
    @Size(min = 0, max = 32, message = "园区编码不能超过32个字符")
    public String getParkCode() {
        return parkCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("parkId", getParkId())
                .append("parkName", getParkName())
                .append("parkCode", getParkCode())
                .append("coordinate", getCoordinate())
                .append("region", getRegion())
                .append("regionCode", getRegionCode())
                .append("userId", getUserId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
