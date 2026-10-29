package com.ruoyi.binZhou.xwq.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户默认监测范围对象 xwq_user_default_scope
 * 
 * @author ruoyi
 * @date 2026-10-13
 */
public class UserDefaultScope extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 默认范围主键 */
    private Long scopeId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 默认园区ID */
    @Excel(name = "默认园区ID")
    private Long parkId;

    /** 默认地块ID */
    @Excel(name = "默认地块ID")
    private Long areaId;

    /** 默认设备ID */
    @Excel(name = "默认设备ID")
    private Long deviceId;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setScopeId(Long scopeId) 
    {
        this.scopeId = scopeId;
    }

    public Long getScopeId() 
    {
        return scopeId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setParkId(Long parkId) 
    {
        this.parkId = parkId;
    }

    public Long getParkId() 
    {
        return parkId;
    }

    public void setAreaId(Long areaId) 
    {
        this.areaId = areaId;
    }

    public Long getAreaId() 
    {
        return areaId;
    }

    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("scopeId", getScopeId())
            .append("userId", getUserId())
            .append("parkId", getParkId())
            .append("areaId", getAreaId())
            .append("deviceId", getDeviceId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
