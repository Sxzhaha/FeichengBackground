package com.ruoyi.binZhou.ylm.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 农事任务对象 ylm_agri_task
 *
 * @author ruoyi
 * @date 2026-06-28
 */
@TableName("ylm_agri_task")
public class AgriTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 任务编码 */
    @Excel(name = "任务编码", sort = 1)
    private String taskNo;

    /** 农事类型（字典ID） */
    private Long workTypeId;

    /** 农事类型名称 */
    @Excel(name = "农事类型", sort = 2)
    @TableField(exist = false)
    private String workTypeName;

    /** 作业方式（字典ID） */
    private Long workMethodId;

    /** 作业方式名称 */
    @Excel(name = "作业方式", sort = 3)
    @TableField(exist = false)
    private String workMethodName;

    /** 区域ID */
    private Long areaId;

    /** 角落/位置ID */
    private Long locationId;

    /** 园区ID */
    @TableField(exist = false)
    private Long parkId;

    /** 园区名称 */
    @TableField(exist = false)
    private String parkName;

    /** 地块名称 */
    @TableField(exist = false)
    private String areaName;

    /** 角落/位置名称 */
    @TableField(exist = false)
    private String locationName;

    /** 区域完整名称 */
    @Excel(name = "区域", sort = 4)
    @TableField(exist = false)
    private String regionFullName;

    /** 任务描述 */
    @Excel(name = "任务描述", sort = 5)
    private String description;

    /** 照片地址 */
    @Excel(name = "照片地址", sort = 6)
    private String photoUrls;

    /** 任务创建人ID */
    private String createById;

    /** 任务创建人名称 */
    @Excel(name = "创建人", sort = 7)
    @TableField(exist = false)
    private String createByName;

    /** 是否精确匹配创建人ID */
    @TableField(exist = false)
    private Boolean exactCreateById;

    /** 操作人ID */
    private String operatorId;

    /** 操作人名称 */
    @Excel(name = "操作人", sort = 8)
    @TableField(exist = false)
    private String operatorName;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", sort = 9, width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", sort = 10, width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 导出用创建时间 */
    @Excel(name = "创建时间", sort = 11, width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date exportCreateTime;

    /** 导出用备注 */
    @Excel(name = "备注", sort = 12)
    @TableField(exist = false)
    private String exportRemark;

    /** 备用字段A */
    private String agriTaskA;

    /** 备用字段B */
    private String agriTaskB;

    /** 备用字段C */
    private String agriTaskC;

    /** 备用字段D */
    private String agriTaskD;

    /** 当前登录人是否可以操作该记录 */
    @TableField(exist = false)
    private Boolean ownRecord;

    /** 导出时按勾选记录过滤 */
    @TableField(exist = false)
    private Long[] ids;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setTaskNo(String taskNo)
    {
        this.taskNo = taskNo;
    }

    public String getTaskNo()
    {
        return taskNo;
    }

    public void setWorkTypeId(Long workTypeId)
    {
        this.workTypeId = workTypeId;
    }

    public Long getWorkTypeId()
    {
        return workTypeId;
    }

    public void setWorkTypeName(String workTypeName)
    {
        this.workTypeName = workTypeName;
    }

    public String getWorkTypeName()
    {
        return workTypeName;
    }

    public void setWorkMethodId(Long workMethodId)
    {
        this.workMethodId = workMethodId;
    }

    public Long getWorkMethodId()
    {
        return workMethodId;
    }

    public void setWorkMethodName(String workMethodName)
    {
        this.workMethodName = workMethodName;
    }

    public String getWorkMethodName()
    {
        return workMethodName;
    }

    public void setAreaId(Long areaId)
    {
        this.areaId = areaId;
    }

    public Long getAreaId()
    {
        return areaId;
    }

    public void setLocationId(Long locationId)
    {
        this.locationId = locationId;
    }

    public Long getLocationId()
    {
        return locationId;
    }

    public void setParkId(Long parkId)
    {
        this.parkId = parkId;
    }

    public Long getParkId()
    {
        return parkId;
    }

    public void setParkName(String parkName)
    {
        this.parkName = parkName;
    }

    public String getParkName()
    {
        return parkName;
    }

    public void setAreaName(String areaName)
    {
        this.areaName = areaName;
    }

    public String getAreaName()
    {
        return areaName;
    }

    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public void setRegionFullName(String regionFullName)
    {
        this.regionFullName = regionFullName;
    }

    public String getRegionFullName()
    {
        return regionFullName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setPhotoUrls(String photoUrls)
    {
        this.photoUrls = photoUrls;
    }

    public String getPhotoUrls()
    {
        return photoUrls;
    }

    public void setCreateById(String createById)
    {
        this.createById = createById;
    }

    public String getCreateById()
    {
        return createById;
    }

    public void setCreateByName(String createByName)
    {
        this.createByName = createByName;
    }

    public String getCreateByName()
    {
        return createByName;
    }

    public void setExactCreateById(Boolean exactCreateById)
    {
        this.exactCreateById = exactCreateById;
    }

    public Boolean getExactCreateById()
    {
        return exactCreateById;
    }

    public void setOperatorId(String operatorId)
    {
        this.operatorId = operatorId;
    }

    public String getOperatorId()
    {
        return operatorId;
    }

    public void setOperatorName(String operatorName)
    {
        this.operatorName = operatorName;
    }

    public String getOperatorName()
    {
        return operatorName;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setExportCreateTime(Date exportCreateTime)
    {
        this.exportCreateTime = exportCreateTime;
    }

    public Date getExportCreateTime()
    {
        return exportCreateTime;
    }

    public void setExportRemark(String exportRemark)
    {
        this.exportRemark = exportRemark;
    }

    public String getExportRemark()
    {
        return exportRemark;
    }

    public void setAgriTaskA(String agriTaskA)
    {
        this.agriTaskA = agriTaskA;
    }

    public String getAgriTaskA()
    {
        return agriTaskA;
    }

    public void setAgriTaskB(String agriTaskB)
    {
        this.agriTaskB = agriTaskB;
    }

    public String getAgriTaskB()
    {
        return agriTaskB;
    }

    public void setAgriTaskC(String agriTaskC)
    {
        this.agriTaskC = agriTaskC;
    }

    public String getAgriTaskC()
    {
        return agriTaskC;
    }

    public void setAgriTaskD(String agriTaskD)
    {
        this.agriTaskD = agriTaskD;
    }

    public String getAgriTaskD()
    {
        return agriTaskD;
    }

    public void setOwnRecord(Boolean ownRecord)
    {
        this.ownRecord = ownRecord;
    }

    public Boolean getOwnRecord()
    {
        return ownRecord;
    }

    public void setIds(Long[] ids)
    {
        this.ids = ids;
    }

    public Long[] getIds()
    {
        return ids;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("taskNo", getTaskNo())
                .append("workTypeId", getWorkTypeId())
                .append("workTypeName", getWorkTypeName())
                .append("workMethodId", getWorkMethodId())
                .append("workMethodName", getWorkMethodName())
                .append("areaId", getAreaId())
                .append("locationId", getLocationId())
                .append("parkId", getParkId())
                .append("parkName", getParkName())
                .append("areaName", getAreaName())
                .append("locationName", getLocationName())
                .append("regionFullName", getRegionFullName())
                .append("description", getDescription())
                .append("photoUrls", getPhotoUrls())
                .append("createById", getCreateById())
                .append("createByName", getCreateByName())
                .append("exactCreateById", getExactCreateById())
                .append("operatorId", getOperatorId())
                .append("operatorName", getOperatorName())
                .append("createTime", getCreateTime())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("remark", getRemark())
                .append("exportCreateTime", getExportCreateTime())
                .append("exportRemark", getExportRemark())
                .append("agriTaskA", getAgriTaskA())
                .append("agriTaskB", getAgriTaskB())
                .append("agriTaskC", getAgriTaskC())
                .append("agriTaskD", getAgriTaskD())
                .append("ownRecord", getOwnRecord())
                .append("ids", getIds())
                .toString();
    }
}
