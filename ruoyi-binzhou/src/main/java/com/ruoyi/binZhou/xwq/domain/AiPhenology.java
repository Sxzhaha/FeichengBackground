package com.ruoyi.binZhou.xwq.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 物候期识别结果对象 xwq_ai_phenology
 *
 * @author ruoyi
 * @date 2026-06-24
 */
public class AiPhenology extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识一条物候期识别结果 */
    private Long phenologyId;

    /** 关联识别记录表 record_id */
    @Excel(name = "关联识别记录表 record_id")
    private Long recordId;

    /** 关联图片信息表 image_id */
    @Excel(name = "关联图片信息表 image_id")
    private Long imageId;

    /** 园区 ID，查询字段，来源于 xwq_ai_image.park_id */
    private Long parkId;

    /** 地块 ID，查询字段，对应 xwq_ai_image.plot_id */
    private Long areaId;

    /** 角落/位置 ID，查询字段，对应 xwq_ai_image.image_b */
    private String locationId;

    /** 设备 ID，查询字段，来源于 xwq_ai_image.device_id */
    private Long deviceId;

    /** 园区名称，联表返回字段 */
    @Excel(name = "园区")
    private String parkName;

    /** 地块名称，联表返回字段 */
    @Excel(name = "地块")
    private String areaName;

    /** 角落/位置名称，联表返回字段 */
    @Excel(name = "角落")
    private String locationName;

    /** 设备名称，联表返回字段 */
    @Excel(name = "设备")
    private String deviceName;

    /** 当前物候期名称 */
    @Excel(name = "当前物候期名称")
    private String phenologyName;

    /** 物候期标准编码 */
    @Excel(name = "物候期标准编码")
    private String phenologyCode;

    /** 作物类型 */
    @Excel(name = "作物类型")
    private String phenologyCrop;

    /** 模型识别置信度 */
    @Excel(name = "模型识别置信度")
    private BigDecimal phenologyConfidence;

    /** 物候期识别结论 */
    @Excel(name = "物候期识别结论")
    private String phenologyResult;

    /** 当前作物生长状态描述 */
    @Excel(name = "当前作物生长状态描述")
    private String phenologyDesc;

    /** 当前物候期对应的农事建议 */
    @Excel(name = "当前物候期对应的农事建议")
    private String phenologyAdvice;

    /** 预测或标准中的下一生育阶段 */
    @Excel(name = "预测或标准中的下一生育阶段")
    private String phenologyNextName;

    /** 物候期识别结果图片 */
    @Excel(name = "物候期识别结果图片")
    private String phenologyResultImgUrl;

    /** 备用字段1 */
    @Excel(name = "备用字段1")
    private String phenologyA;

    /** 备用字段2 */
    @Excel(name = "备用字段2")
    private String phenologyB;

    /** 备用字段3 */
    @Excel(name = "备用字段3")
    private String phenologyC;

    /** 备用字段4 */
    @Excel(name = "备用字段4")
    private String phenologyD;

    /** 备用字段5 */
    @Excel(name = "备用字段5")
    private String phenologyE;

    /** 备用字段6 */
    @Excel(name = "备用字段6")
    private String phenologyF;

    /** 备用字段7 */
    @Excel(name = "备用字段7")
    private String phenologyG;

    /** 备用字段8 */
    @Excel(name = "备用字段8")
    private String phenologyH;

    /** 备用字段9 */
    @Excel(name = "备用字段9")
    private String phenologyI;

    /** 备用字段10 */
    @Excel(name = "备用字段10")
    private String phenologyJ;

    public void setPhenologyId(Long phenologyId)
    {
        this.phenologyId = phenologyId;
    }

    public Long getPhenologyId()
    {
        return phenologyId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setImageId(Long imageId)
    {
        this.imageId = imageId;
    }

    public Long getImageId()
    {
        return imageId;
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

    public void setLocationId(String locationId)
    {
        this.locationId = locationId;
    }

    public String getLocationId()
    {
        return locationId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId()
    {
        return deviceId;
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

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setPhenologyName(String phenologyName)
    {
        this.phenologyName = phenologyName;
    }

    public String getPhenologyName()
    {
        return phenologyName;
    }

    public void setPhenologyCode(String phenologyCode)
    {
        this.phenologyCode = phenologyCode;
    }

    public String getPhenologyCode()
    {
        return phenologyCode;
    }

    public void setPhenologyCrop(String phenologyCrop)
    {
        this.phenologyCrop = phenologyCrop;
    }

    public String getPhenologyCrop()
    {
        return phenologyCrop;
    }

    public void setPhenologyConfidence(BigDecimal phenologyConfidence)
    {
        this.phenologyConfidence = phenologyConfidence;
    }

    public BigDecimal getPhenologyConfidence()
    {
        return phenologyConfidence;
    }

    public void setPhenologyResult(String phenologyResult)
    {
        this.phenologyResult = phenologyResult;
    }

    public String getPhenologyResult()
    {
        return phenologyResult;
    }

    public void setPhenologyDesc(String phenologyDesc)
    {
        this.phenologyDesc = phenologyDesc;
    }

    public String getPhenologyDesc()
    {
        return phenologyDesc;
    }

    public void setPhenologyAdvice(String phenologyAdvice)
    {
        this.phenologyAdvice = phenologyAdvice;
    }

    public String getPhenologyAdvice()
    {
        return phenologyAdvice;
    }

    public void setPhenologyNextName(String phenologyNextName)
    {
        this.phenologyNextName = phenologyNextName;
    }

    public String getPhenologyNextName()
    {
        return phenologyNextName;
    }

    public void setPhenologyResultImgUrl(String phenologyResultImgUrl)
    {
        this.phenologyResultImgUrl = phenologyResultImgUrl;
    }

    public String getPhenologyResultImgUrl()
    {
        return phenologyResultImgUrl;
    }

    public void setPhenologyA(String phenologyA)
    {
        this.phenologyA = phenologyA;
    }

    public String getPhenologyA()
    {
        return phenologyA;
    }

    public void setPhenologyB(String phenologyB)
    {
        this.phenologyB = phenologyB;
    }

    public String getPhenologyB()
    {
        return phenologyB;
    }

    public void setPhenologyC(String phenologyC)
    {
        this.phenologyC = phenologyC;
    }

    public String getPhenologyC()
    {
        return phenologyC;
    }

    public void setPhenologyD(String phenologyD)
    {
        this.phenologyD = phenologyD;
    }

    public String getPhenologyD()
    {
        return phenologyD;
    }

    public void setPhenologyE(String phenologyE)
    {
        this.phenologyE = phenologyE;
    }

    public String getPhenologyE()
    {
        return phenologyE;
    }

    public void setPhenologyF(String phenologyF)
    {
        this.phenologyF = phenologyF;
    }

    public String getPhenologyF()
    {
        return phenologyF;
    }

    public void setPhenologyG(String phenologyG)
    {
        this.phenologyG = phenologyG;
    }

    public String getPhenologyG()
    {
        return phenologyG;
    }

    public void setPhenologyH(String phenologyH)
    {
        this.phenologyH = phenologyH;
    }

    public String getPhenologyH()
    {
        return phenologyH;
    }

    public void setPhenologyI(String phenologyI)
    {
        this.phenologyI = phenologyI;
    }

    public String getPhenologyI()
    {
        return phenologyI;
    }

    public void setPhenologyJ(String phenologyJ)
    {
        this.phenologyJ = phenologyJ;
    }

    public String getPhenologyJ()
    {
        return phenologyJ;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("phenologyId", getPhenologyId())
                .append("recordId", getRecordId())
                .append("imageId", getImageId())
                .append("parkId", getParkId())
                .append("areaId", getAreaId())
                .append("locationId", getLocationId())
                .append("deviceId", getDeviceId())
                .append("parkName", getParkName())
                .append("areaName", getAreaName())
                .append("locationName", getLocationName())
                .append("deviceName", getDeviceName())
                .append("phenologyName", getPhenologyName())
                .append("phenologyCode", getPhenologyCode())
                .append("phenologyCrop", getPhenologyCrop())
                .append("phenologyConfidence", getPhenologyConfidence())
                .append("phenologyResult", getPhenologyResult())
                .append("phenologyDesc", getPhenologyDesc())
                .append("phenologyAdvice", getPhenologyAdvice())
                .append("phenologyNextName", getPhenologyNextName())
                .append("phenologyResultImgUrl", getPhenologyResultImgUrl())
                .append("phenologyA", getPhenologyA())
                .append("phenologyB", getPhenologyB())
                .append("phenologyC", getPhenologyC())
                .append("phenologyD", getPhenologyD())
                .append("phenologyE", getPhenologyE())
                .append("phenologyF", getPhenologyF())
                .append("phenologyG", getPhenologyG())
                .append("phenologyH", getPhenologyH())
                .append("phenologyI", getPhenologyI())
                .append("phenologyJ", getPhenologyJ())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
