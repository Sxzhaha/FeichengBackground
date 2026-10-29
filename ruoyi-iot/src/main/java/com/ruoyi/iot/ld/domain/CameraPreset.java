package com.ruoyi.iot.ld.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 摄像头预置点对象 camera_preset
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public class CameraPreset extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 设备ID */
    @Excel(name = "设备ID")
    private Long deviceId;
    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;
    /** 设备名称 */
    @Excel(name = "设备序列号")
    private String snCode;

    /** 预置点索引 (通常1-12) */
    @Excel(name = "预置点索引 (通常1-12)")
    private Integer pointIndex;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long creatorId;

    /** 创建人名称 */
    @Excel(name = "创建人名称")
    private String creatorName;

    /** 主键 */
    private Long id;

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId()
    {
        return deviceId;
    }

    public void setPointIndex(Integer pointIndex)
    {
        this.pointIndex = pointIndex;
    }

    public Integer getPointIndex()
    {
        return pointIndex;
    }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSnCode() {
        return snCode;
    }

    public void setSnCode(String snCode) {
        this.snCode = snCode;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("deviceId", getDeviceId())
            .append("pointIndex", getPointIndex())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("id", getId())
            .toString();
    }
}
