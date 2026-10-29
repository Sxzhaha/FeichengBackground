package com.ruoyi.binZhou.ld.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 品种信息对象 ld_crop_variety
 * 
 * @author ruoyi
 * @date 2025-07-22
 */
public class CropVariety extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 作物种类ID */
    @Excel(name = "作物种类ID")
    private Long categoryId;

    /** 品种名称 */
    @Excel(name = "品种名称")
    private String varietyName;

    /** 审定编号 */
    @Excel(name = "审定编号")
    private String approvalNumber;

    /** 审定年份 */
    @Excel(name = "审定年份")
    private String approvalYear;

    /** 是否转基因(0否1是) */
    @Excel(name = "是否转基因(0否1是)")
    private Integer isGmo;

    /** 审定机构 */
    @Excel(name = "审定机构")
    private String approvalInstitution;

    /** 选(引)育单位(人) */
    private String breedingUnit;

    /** 品种来源 */
    private String varietySource;

    /** 适宜种植区域 */
    private String suitableArea;

    /** 特征特性（标准象完整描述） */
    private String characteristics;

    /** 主要栽培措施 */
    private String cultivationMeasures;

    /** 状态(0隐藏1正常) */
    @Excel(name = "状态(0隐藏1正常)")
    private Long status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }

    public void setVarietyName(String varietyName) 
    {
        this.varietyName = varietyName;
    }

    public String getVarietyName() 
    {
        return varietyName;
    }

    public void setApprovalNumber(String approvalNumber) 
    {
        this.approvalNumber = approvalNumber;
    }

    public String getApprovalNumber() 
    {
        return approvalNumber;
    }

    public void setApprovalYear(String approvalYear) 
    {
        this.approvalYear = approvalYear;
    }

    public String getApprovalYear() 
    {
        return approvalYear;
    }

    public void setIsGmo(Integer isGmo) 
    {
        this.isGmo = isGmo;
    }

    public Integer getIsGmo() 
    {
        return isGmo;
    }

    public void setApprovalInstitution(String approvalInstitution) 
    {
        this.approvalInstitution = approvalInstitution;
    }

    public String getApprovalInstitution() 
    {
        return approvalInstitution;
    }

    public void setBreedingUnit(String breedingUnit) 
    {
        this.breedingUnit = breedingUnit;
    }

    public String getBreedingUnit() 
    {
        return breedingUnit;
    }

    public void setVarietySource(String varietySource) 
    {
        this.varietySource = varietySource;
    }

    public String getVarietySource() 
    {
        return varietySource;
    }

    public void setSuitableArea(String suitableArea) 
    {
        this.suitableArea = suitableArea;
    }

    public String getSuitableArea() 
    {
        return suitableArea;
    }

    public void setCharacteristics(String characteristics) 
    {
        this.characteristics = characteristics;
    }

    public String getCharacteristics() 
    {
        return characteristics;
    }

    public void setCultivationMeasures(String cultivationMeasures) 
    {
        this.cultivationMeasures = cultivationMeasures;
    }

    public String getCultivationMeasures() 
    {
        return cultivationMeasures;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("categoryId", getCategoryId())
            .append("varietyName", getVarietyName())
            .append("approvalNumber", getApprovalNumber())
            .append("approvalYear", getApprovalYear())
            .append("isGmo", getIsGmo())
            .append("approvalInstitution", getApprovalInstitution())
            .append("breedingUnit", getBreedingUnit())
            .append("varietySource", getVarietySource())
            .append("suitableArea", getSuitableArea())
            .append("characteristics", getCharacteristics())
            .append("cultivationMeasures", getCultivationMeasures())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
