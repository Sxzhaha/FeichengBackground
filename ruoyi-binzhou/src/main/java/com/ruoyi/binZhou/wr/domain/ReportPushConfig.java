package com.ruoyi.binZhou.wr.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 农情报推送配置对象 wr_report_push_config
 * 
 * @author ruoyi
 * @date 2026-07-02
 */
public class ReportPushConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long configId;

    /** 用户ID(sys_user.user_id) */
    @Excel(name = "用户ID(sys_user.user_id)")
    private Long userId;

    /** 角色类型：farmer-农户, county-县级, city-市级 */
    @Excel(name = "角色类型：farmer-农户, county-县级, city-市级")
    private String userRole;

    /** 推送频率：daily-每日, weekly-每周, monthly-每月, realtime-即时 */
    @Excel(name = "推送频率：daily-每日, weekly-每周, monthly-每月, realtime-即时")
    private String pushFrequency;

    /** 推送渠道：system-系统通知, sms-短信, wechat-微信公众号 */
    @Excel(name = "推送渠道：system-系统通知, sms-短信, wechat-微信公众号")
    private String pushChannel;

    /** 管辖区域编码(县市级用户有效) */
    @Excel(name = "管辖区域编码(县市级用户有效)")
    private String areaCode;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setConfigId(Long configId) 
    {
        this.configId = configId;
    }

    public Long getConfigId() 
    {
        return configId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserRole(String userRole) 
    {
        this.userRole = userRole;
    }

    public String getUserRole() 
    {
        return userRole;
    }

    public void setPushFrequency(String pushFrequency) 
    {
        this.pushFrequency = pushFrequency;
    }

    public String getPushFrequency() 
    {
        return pushFrequency;
    }

    public void setPushChannel(String pushChannel) 
    {
        this.pushChannel = pushChannel;
    }

    public String getPushChannel() 
    {
        return pushChannel;
    }

    public void setAreaCode(String areaCode) 
    {
        this.areaCode = areaCode;
    }

    public String getAreaCode() 
    {
        return areaCode;
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
            .append("configId", getConfigId())
            .append("userId", getUserId())
            .append("userRole", getUserRole())
            .append("pushFrequency", getPushFrequency())
            .append("pushChannel", getPushChannel())
            .append("areaCode", getAreaCode())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
