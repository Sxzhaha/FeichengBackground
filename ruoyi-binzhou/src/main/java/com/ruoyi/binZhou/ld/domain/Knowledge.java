package com.ruoyi.binZhou.ld.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 病虫害百科对象 knowledge
 * 
 * @author ruoyi
 * @date 2025-05-12
 */
public class Knowledge extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    @Excel(name = "百科id")
    private Long id;

    /** 一级分类 */
    @Excel(name = "一级分类")
    private String plusDsiCategoryfirst;

    /** 二级分类 */
    @Excel(name = "二级分类")
    private String plusDsiCategorysecond;

    /** 三级分类 */
    @Excel(name = "三级分类")
    private String plusDsiCategorythird;

    /** 病虫害名称 */
    @Excel(name = "病虫害名称")
    private String plusDsiName;

    /** 类别 */
    @Excel(name = "类别")
    private Long disPestType;

    /** 英文名称 */
    @Excel(name = "英文名称")
    private String plusDsiNameeng;

    /** 别名 */
    @Excel(name = "别名")
    private String plusDsiSynonyms;

    /** 简介 */
    @Excel(name = "简介")
    private String plusDsiIntroduction;

    /** 图片路径 */
    @Excel(name = "图片路径")
    private String plusDsiPath;

    /** 危害症状 */
    @Excel(name = "危害症状")
    private String plusDsiDamagesym;

    /** 病源 */
    @Excel(name = "病源")
    private String plusDsiPathogen;

    /** 侵染 */
    @Excel(name = "侵染")
    private String plusDsiCycle;

    /** 发生规律 */
    @Excel(name = "发生规律")
    private String plusDsiOfactor;

    /** 形态 */
    @Excel(name = "形态")
    private String plusDsiMorphology;

    /** 习性 */
    @Excel(name = "习性")
    private String plusDsiHabits;

    /** 防治方法 */
    @Excel(name = "防治方法")
    private String plusDsiCmethod;

    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /**  */
    @Excel(name = "")
    private String createdBy;

    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastupdatedAt;

    /**  */
    @Excel(name = "")
    private String lastupdatedBy;

    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deletedAt;

    /**  */
    @Excel(name = "")
    private String deletedBy;

    /** 默认0，1为删除 */
    @Excel(name = "默认0，1为删除")
    private Long isdeleted;

    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "", width = 30, dateFormat = "yyyy-MM-dd")
    private Date timestamp;

    /**  */
    @Excel(name = "")
    private String companyid;

    /**  */
    @Excel(name = "")
    private String organizeid;

    /** 省名称 */
    @Excel(name = "省名称")
    private String provice;

    /** 省编码 */
    @Excel(name = "省编码")
    private String proviceCode;

    /** 市名称 */
    @Excel(name = "市名称")
    private String city;

    /** 市编码 */
    @Excel(name = "市编码")
    private String cityCode;

    /** 县名称 */
    @Excel(name = "县名称")
    private String district;

    /** 县编码 */
    @Excel(name = "县编码")
    private String districtCode;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setPlusDsiCategoryfirst(String plusDsiCategoryfirst) 
    {
        this.plusDsiCategoryfirst = plusDsiCategoryfirst;
    }

    public String getPlusDsiCategoryfirst() 
    {
        return plusDsiCategoryfirst;
    }

    public void setPlusDsiCategorysecond(String plusDsiCategorysecond) 
    {
        this.plusDsiCategorysecond = plusDsiCategorysecond;
    }

    public String getPlusDsiCategorysecond() 
    {
        return plusDsiCategorysecond;
    }

    public void setPlusDsiCategorythird(String plusDsiCategorythird) 
    {
        this.plusDsiCategorythird = plusDsiCategorythird;
    }

    public String getPlusDsiCategorythird() 
    {
        return plusDsiCategorythird;
    }

    public void setPlusDsiName(String plusDsiName) 
    {
        this.plusDsiName = plusDsiName;
    }

    public String getPlusDsiName() 
    {
        return plusDsiName;
    }

    public void setDisPestType(Long disPestType) 
    {
        this.disPestType = disPestType;
    }

    public Long getDisPestType() 
    {
        return disPestType;
    }

    public void setPlusDsiNameeng(String plusDsiNameeng) 
    {
        this.plusDsiNameeng = plusDsiNameeng;
    }

    public String getPlusDsiNameeng() 
    {
        return plusDsiNameeng;
    }

    public void setPlusDsiSynonyms(String plusDsiSynonyms) 
    {
        this.plusDsiSynonyms = plusDsiSynonyms;
    }

    public String getPlusDsiSynonyms() 
    {
        return plusDsiSynonyms;
    }

    public void setPlusDsiIntroduction(String plusDsiIntroduction) 
    {
        this.plusDsiIntroduction = plusDsiIntroduction;
    }

    public String getPlusDsiIntroduction() 
    {
        return plusDsiIntroduction;
    }

    public void setPlusDsiPath(String plusDsiPath) 
    {
        this.plusDsiPath = plusDsiPath;
    }

    public String getPlusDsiPath() 
    {
        return plusDsiPath;
    }

    public void setPlusDsiDamagesym(String plusDsiDamagesym) 
    {
        this.plusDsiDamagesym = plusDsiDamagesym;
    }

    public String getPlusDsiDamagesym() 
    {
        return plusDsiDamagesym;
    }

    public void setPlusDsiPathogen(String plusDsiPathogen) 
    {
        this.plusDsiPathogen = plusDsiPathogen;
    }

    public String getPlusDsiPathogen() 
    {
        return plusDsiPathogen;
    }

    public void setPlusDsiCycle(String plusDsiCycle) 
    {
        this.plusDsiCycle = plusDsiCycle;
    }

    public String getPlusDsiCycle() 
    {
        return plusDsiCycle;
    }

    public void setPlusDsiOfactor(String plusDsiOfactor) 
    {
        this.plusDsiOfactor = plusDsiOfactor;
    }

    public String getPlusDsiOfactor() 
    {
        return plusDsiOfactor;
    }

    public void setPlusDsiMorphology(String plusDsiMorphology) 
    {
        this.plusDsiMorphology = plusDsiMorphology;
    }

    public String getPlusDsiMorphology() 
    {
        return plusDsiMorphology;
    }

    public void setPlusDsiHabits(String plusDsiHabits) 
    {
        this.plusDsiHabits = plusDsiHabits;
    }

    public String getPlusDsiHabits() 
    {
        return plusDsiHabits;
    }

    public void setPlusDsiCmethod(String plusDsiCmethod) 
    {
        this.plusDsiCmethod = plusDsiCmethod;
    }

    public String getPlusDsiCmethod() 
    {
        return plusDsiCmethod;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setCreatedBy(String createdBy) 
    {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() 
    {
        return createdBy;
    }

    public void setLastupdatedAt(Date lastupdatedAt) 
    {
        this.lastupdatedAt = lastupdatedAt;
    }

    public Date getLastupdatedAt() 
    {
        return lastupdatedAt;
    }

    public void setLastupdatedBy(String lastupdatedBy) 
    {
        this.lastupdatedBy = lastupdatedBy;
    }

    public String getLastupdatedBy() 
    {
        return lastupdatedBy;
    }

    public void setDeletedAt(Date deletedAt) 
    {
        this.deletedAt = deletedAt;
    }

    public Date getDeletedAt() 
    {
        return deletedAt;
    }

    public void setDeletedBy(String deletedBy) 
    {
        this.deletedBy = deletedBy;
    }

    public String getDeletedBy() 
    {
        return deletedBy;
    }

    public void setIsdeleted(Long isdeleted) 
    {
        this.isdeleted = isdeleted;
    }

    public Long getIsdeleted() 
    {
        return isdeleted;
    }

    public void setTimestamp(Date timestamp) 
    {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() 
    {
        return timestamp;
    }

    public void setCompanyid(String companyid) 
    {
        this.companyid = companyid;
    }

    public String getCompanyid() 
    {
        return companyid;
    }

    public void setOrganizeid(String organizeid) 
    {
        this.organizeid = organizeid;
    }

    public String getOrganizeid() 
    {
        return organizeid;
    }

    public void setProvice(String provice) 
    {
        this.provice = provice;
    }

    public String getProvice() 
    {
        return provice;
    }

    public void setProviceCode(String proviceCode) 
    {
        this.proviceCode = proviceCode;
    }

    public String getProviceCode() 
    {
        return proviceCode;
    }

    public void setCity(String city) 
    {
        this.city = city;
    }

    public String getCity() 
    {
        return city;
    }

    public void setCityCode(String cityCode) 
    {
        this.cityCode = cityCode;
    }

    public String getCityCode() 
    {
        return cityCode;
    }

    public void setDistrict(String district) 
    {
        this.district = district;
    }

    public String getDistrict() 
    {
        return district;
    }

    public void setDistrictCode(String districtCode) 
    {
        this.districtCode = districtCode;
    }

    public String getDistrictCode() 
    {
        return districtCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("plusDsiCategoryfirst", getPlusDsiCategoryfirst())
            .append("plusDsiCategorysecond", getPlusDsiCategorysecond())
            .append("plusDsiCategorythird", getPlusDsiCategorythird())
            .append("plusDsiName", getPlusDsiName())
            .append("disPestType", getDisPestType())
            .append("plusDsiNameeng", getPlusDsiNameeng())
            .append("plusDsiSynonyms", getPlusDsiSynonyms())
            .append("plusDsiIntroduction", getPlusDsiIntroduction())
            .append("plusDsiPath", getPlusDsiPath())
            .append("plusDsiDamagesym", getPlusDsiDamagesym())
            .append("plusDsiPathogen", getPlusDsiPathogen())
            .append("plusDsiCycle", getPlusDsiCycle())
            .append("plusDsiOfactor", getPlusDsiOfactor())
            .append("plusDsiMorphology", getPlusDsiMorphology())
            .append("plusDsiHabits", getPlusDsiHabits())
            .append("plusDsiCmethod", getPlusDsiCmethod())
            .append("createdAt", getCreatedAt())
            .append("createdBy", getCreatedBy())
            .append("lastupdatedAt", getLastupdatedAt())
            .append("lastupdatedBy", getLastupdatedBy())
            .append("deletedAt", getDeletedAt())
            .append("deletedBy", getDeletedBy())
            .append("isdeleted", getIsdeleted())
            .append("timestamp", getTimestamp())
            .append("companyid", getCompanyid())
            .append("organizeid", getOrganizeid())
            .append("provice", getProvice())
            .append("proviceCode", getProviceCode())
            .append("city", getCity())
            .append("cityCode", getCityCode())
            .append("district", getDistrict())
            .append("districtCode", getDistrictCode())
            .toString();
    }
}
