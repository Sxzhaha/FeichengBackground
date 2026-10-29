package com.ruoyi.binZhou.xwq.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户默认公共模型对象 xwq_user_default_model
 * 
 * @author ruoyi
 * @date 2026-10-13
 */
public class UserDefaultModel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 默认模型主键 */
    private Long preferenceId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 识别场景（disease病虫害 pest虫情 phenology物候） */
    @Excel(name = "识别场景", readConverterExp = "d=isease病虫害,p=est虫情,p=henology物候")
    private String sceneCode;

    /** 公共模型ID（空表示跟随系统） */
    @Excel(name = "公共模型ID", readConverterExp = "空=表示跟随系统")
    private Long modelId;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setPreferenceId(Long preferenceId) 
    {
        this.preferenceId = preferenceId;
    }

    public Long getPreferenceId() 
    {
        return preferenceId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setSceneCode(String sceneCode) 
    {
        this.sceneCode = sceneCode;
    }

    public String getSceneCode() 
    {
        return sceneCode;
    }

    public void setModelId(Long modelId) 
    {
        this.modelId = modelId;
    }

    public Long getModelId() 
    {
        return modelId;
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
            .append("preferenceId", getPreferenceId())
            .append("userId", getUserId())
            .append("sceneCode", getSceneCode())
            .append("modelId", getModelId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
