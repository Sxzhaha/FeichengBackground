package com.ruoyi.iot.ld.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备地块关联对象 camera_plot_rel
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public class CameraPlotRel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 设备ID */
    @Excel(name = "设备ID")
    private Long deviceId;

    /** 地块ID */
    @Excel(name = "地块ID")
    private Long plotId;

    /** 云台预置位编号(仅球机有效) */
    @Excel(name = "云台预置位编号(仅球机有效)")
    private Long presetIndex;
    /** 云台预置位编号(仅球机有效) */
    @Excel(name = "地块名称")
    private String plotName;
    @Excel(name = "地块位置")
    private String plotLocation;
    @Excel(name = "地块面积")
    private Double plotArea;
    @Excel(name = "地块作物")
    private String plotCrop;
    @Excel(name = "设备名称")
    private String deviceName;
    @Excel(name = "设备序列号")
    private String snCode;
    @Excel(name = "所属人ID")
    private Long creatorId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }

    public void setPlotId(Long plotId) 
    {
        this.plotId = plotId;
    }

    public Long getPlotId() 
    {
        return plotId;
    }

    public void setPresetIndex(Long presetIndex) 
    {
        this.presetIndex = presetIndex;
    }

    public Long getPresetIndex() 
    {
        return presetIndex;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public String getPlotLocation() {
        return plotLocation;
    }

    public void setPlotLocation(String plotLocation) {
        this.plotLocation = plotLocation;
    }

    public Double getPlotArea() {
        return plotArea;
    }

    public void setPlotArea(Double plotArea) {
        this.plotArea = plotArea;
    }

    public String getPlotCrop() {
        return plotCrop;
    }

    public void setPlotCrop(String plotCrop) {
        this.plotCrop = plotCrop;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceId", getDeviceId())
            .append("plotId", getPlotId())
            .append("presetIndex", getPresetIndex())
            .append("remark", getRemark())
            .toString();
    }
}
