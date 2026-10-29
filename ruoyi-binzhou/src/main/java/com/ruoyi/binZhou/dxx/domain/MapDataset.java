package com.ruoyi.binZhou.dxx.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 地图图层对象 dxx_map_dataset
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class MapDataset extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图层ID */
    private Long datasetId;

    /** 图层名称 */
    @Excel(name = "图层名称")
    private String datasetName;

    /** 图层编码 */
    @Excel(name = "图层编码")
    private String datasetCode;

    /** 图层说明 */
    @Excel(name = "图层说明")
    private String description;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Long sortNum;

    /** 状态：0正常，1停用 */
    @Excel(name = "状态：0正常，1停用")
    private String status;

    /** 删除标志：0存在，2删除 */
    private String delFlag;

    public void setDatasetId(Long datasetId) 
    {
        this.datasetId = datasetId;
    }

    public Long getDatasetId() 
    {
        return datasetId;
    }

    public void setDatasetName(String datasetName) 
    {
        this.datasetName = datasetName;
    }

    public String getDatasetName() 
    {
        return datasetName;
    }

    public void setDatasetCode(String datasetCode) 
    {
        this.datasetCode = datasetCode;
    }

    public String getDatasetCode() 
    {
        return datasetCode;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setSortNum(Long sortNum) 
    {
        this.sortNum = sortNum;
    }

    public Long getSortNum() 
    {
        return sortNum;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("datasetId", getDatasetId())
            .append("datasetName", getDatasetName())
            .append("datasetCode", getDatasetCode())
            .append("description", getDescription())
            .append("sortNum", getSortNum())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
