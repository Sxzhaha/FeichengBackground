package com.ruoyi.binZhou.xwq.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 物候期识别结果返回对象。
 *
 * 用于 POST /xwq/phenology/predict 的 data 字段，
 * 避免直接把数据库实体作为接口返回对象。
 */
public class PhenologyPredictResultVo
{
    private Long recordId;
    private Long phenologyId;
    private Long imageId;
    private String imageName;
    private String imageUrl;

    private Long modelId;
    private String modelName;

    private String phenologyName;
    private String phenologyCode;
    private String phenologyCrop;
    private BigDecimal phenologyConfidence;
    private String phenologyResult;
    private String phenologyDesc;
    private String phenologyAdvice;
    private String phenologyNextName;
    private String phenologyResultImgUrl;

    private String recordMode;
    private Long recordStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Long getPhenologyId()
    {
        return phenologyId;
    }

    public void setPhenologyId(Long phenologyId)
    {
        this.phenologyId = phenologyId;
    }

    public Long getImageId()
    {
        return imageId;
    }

    public void setImageId(Long imageId)
    {
        this.imageId = imageId;
    }

    public String getImageName()
    {
        return imageName;
    }

    public void setImageName(String imageName)
    {
        this.imageName = imageName;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public Long getModelId()
    {
        return modelId;
    }

    public void setModelId(Long modelId)
    {
        this.modelId = modelId;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
    }

    public String getPhenologyName()
    {
        return phenologyName;
    }

    public void setPhenologyName(String phenologyName)
    {
        this.phenologyName = phenologyName;
    }

    public String getPhenologyCode()
    {
        return phenologyCode;
    }

    public void setPhenologyCode(String phenologyCode)
    {
        this.phenologyCode = phenologyCode;
    }

    public String getPhenologyCrop()
    {
        return phenologyCrop;
    }

    public void setPhenologyCrop(String phenologyCrop)
    {
        this.phenologyCrop = phenologyCrop;
    }

    public BigDecimal getPhenologyConfidence()
    {
        return phenologyConfidence;
    }

    public void setPhenologyConfidence(BigDecimal phenologyConfidence)
    {
        this.phenologyConfidence = phenologyConfidence;
    }

    public String getPhenologyResult()
    {
        return phenologyResult;
    }

    public void setPhenologyResult(String phenologyResult)
    {
        this.phenologyResult = phenologyResult;
    }

    public String getPhenologyDesc()
    {
        return phenologyDesc;
    }

    public void setPhenologyDesc(String phenologyDesc)
    {
        this.phenologyDesc = phenologyDesc;
    }

    public String getPhenologyAdvice()
    {
        return phenologyAdvice;
    }

    public void setPhenologyAdvice(String phenologyAdvice)
    {
        this.phenologyAdvice = phenologyAdvice;
    }

    public String getPhenologyNextName()
    {
        return phenologyNextName;
    }

    public void setPhenologyNextName(String phenologyNextName)
    {
        this.phenologyNextName = phenologyNextName;
    }

    public String getPhenologyResultImgUrl()
    {
        return phenologyResultImgUrl;
    }

    public void setPhenologyResultImgUrl(String phenologyResultImgUrl)
    {
        this.phenologyResultImgUrl = phenologyResultImgUrl;
    }

    public String getRecordMode()
    {
        return recordMode;
    }

    public void setRecordMode(String recordMode)
    {
        this.recordMode = recordMode;
    }

    public Long getRecordStatus()
    {
        return recordStatus;
    }

    public void setRecordStatus(Long recordStatus)
    {
        this.recordStatus = recordStatus;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    @Override
    public String toString()
    {
        return "PhenologyPredictResultVo{" +
                "recordId=" + recordId +
                ", phenologyId=" + phenologyId +
                ", imageId=" + imageId +
                ", imageName='" + imageName + '\'' +
                ", modelId=" + modelId +
                ", modelName='" + modelName + '\'' +
                ", phenologyName='" + phenologyName + '\'' +
                ", phenologyCode='" + phenologyCode + '\'' +
                ", phenologyCrop='" + phenologyCrop + '\'' +
                ", phenologyConfidence=" + phenologyConfidence +
                ", recordStatus=" + recordStatus +
                ", createTime=" + createTime +
                '}';
    }
}
