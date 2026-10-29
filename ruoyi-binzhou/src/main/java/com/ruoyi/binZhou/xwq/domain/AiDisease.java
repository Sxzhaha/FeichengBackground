package com.ruoyi.binZhou.xwq.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 病虫害识别结果对象 xwq_ai_disease
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class AiDisease extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识一条病虫害识别结果 */
    private Long diseaseId;

    /** 关联识别记录表 record_id */
    @Excel(name = "关联识别记录表 record_id")
    private Long recordId;

    /** 关联图片信息表 image_id */
    @Excel(name = "关联图片信息表 image_id")
    private Long imageId;

    /** 病虫害名称，如条锈病、白粉病、叶枯病等 */
    @Excel(name = "病虫害名称，如条锈病、白粉病、叶枯病等")
    private String diseaseName;

    /** 病虫害标准编码 */
    @Excel(name = "病虫害标准编码")
    private String diseaseCode;

    /** 作物类型，如小麦、玉米、水稻 */
    @Excel(name = "作物类型，如小麦、玉米、水稻")
    private String diseaseCrop;

    /** 模型预测置信度 */
    @Excel(name = "模型预测置信度")
    private BigDecimal diseaseConfidence;

    /** 模型给出的诊断结论 */
    @Excel(name = "模型给出的诊断结论")
    private String diseaseResult;

    /** 识别到的症状表现 */
    @Excel(name = "识别到的症状表现")
    private String diseaseDesc;

    /** 药剂防治、农艺措施等建议 */
    @Excel(name = "药剂防治、农艺措施等建议")
    private String diseaseAdvice;

    /** 识别后的标注图、打框图 */
    @Excel(name = "识别后的标注图、打框图")
    private String diseaseResultImgUrl;

    /** 是否触发预警：0 否，1 是 */
    @Excel(name = "是否触发预警：0 否，1 是")
    private Long isWarning;

    /** 生成的预警等级 */
    @Excel(name = "生成的预警等级")
    private String warningLevel;

    private String imageName;
    private String imageUrl;
    private String recordNo;
    private String recordMode;
    private Long recordStatus;

    /** 备用字段2 */
    @Excel(name = "备用字段2")
    private String diseaseB;

    /** 备用字段3 */
    @Excel(name = "备用字段3")
    private String diseaseC;

    /** 备用字段4 */
    @Excel(name = "备用字段4")
    private String diseaseD;

    /** 备用字段5 */
    @Excel(name = "备用字段5")
    private String diseaseE;

    /** 备用字段6 */
    @Excel(name = "备用字段6")
    private String diseaseF;

    /** 备用字段7 */
    @Excel(name = "备用字段7")
    private String diseaseG;

    /** 备用字段8 */
    @Excel(name = "备用字段8")
    private String diseaseH;

    /** 备用字段9 */
    @Excel(name = "备用字段9")
    private String diseaseI;

    /** 备用字段10 */
    @Excel(name = "备用字段10")
    private String diseaseJ;

    /** 园区ID，来自 xwq_ai_record.park_id */
    private Long parkId;

    /** 区域/地块ID，来自 xwq_ai_record.plot_id */
    private Long areaId;

    public Long getParkId() {
        return parkId;
    }

    public void setParkId(Long parkId) {
        this.parkId = parkId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public void setDiseaseId(Long diseaseId) 
    {
        this.diseaseId = diseaseId;
    }

    public Long getDiseaseId() 
    {
        return diseaseId;
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

    public void setDiseaseName(String diseaseName) 
    {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseName() 
    {
        return diseaseName;
    }

    public void setDiseaseCode(String diseaseCode) 
    {
        this.diseaseCode = diseaseCode;
    }

    public String getDiseaseCode() 
    {
        return diseaseCode;
    }

    public void setDiseaseCrop(String diseaseCrop) 
    {
        this.diseaseCrop = diseaseCrop;
    }

    public String getDiseaseCrop() 
    {
        return diseaseCrop;
    }

    public void setDiseaseConfidence(BigDecimal diseaseConfidence) 
    {
        this.diseaseConfidence = diseaseConfidence;
    }

    public BigDecimal getDiseaseConfidence() 
    {
        return diseaseConfidence;
    }

    public void setDiseaseResult(String diseaseResult) 
    {
        this.diseaseResult = diseaseResult;
    }

    public String getDiseaseResult() 
    {
        return diseaseResult;
    }

    public void setDiseaseDesc(String diseaseDesc) 
    {
        this.diseaseDesc = diseaseDesc;
    }

    public String getDiseaseDesc() 
    {
        return diseaseDesc;
    }

    public void setDiseaseAdvice(String diseaseAdvice) 
    {
        this.diseaseAdvice = diseaseAdvice;
    }

    public String getDiseaseAdvice() 
    {
        return diseaseAdvice;
    }

    public void setDiseaseResultImgUrl(String diseaseResultImgUrl) 
    {
        this.diseaseResultImgUrl = diseaseResultImgUrl;
    }

    public String getDiseaseResultImgUrl() 
    {
        return diseaseResultImgUrl;
    }

    public void setIsWarning(Long isWarning) 
    {
        this.isWarning = isWarning;
    }

    public Long getIsWarning() 
    {
        return isWarning;
    }

    public void setWarningLevel(String warningLevel) 
    {
        this.warningLevel = warningLevel;
    }

    public String getWarningLevel() 
    {
        return warningLevel;
    }

    public void setDiseaseB(String diseaseB) 
    {
        this.diseaseB = diseaseB;
    }

    public String getDiseaseB() 
    {
        return diseaseB;
    }

    public void setDiseaseC(String diseaseC) 
    {
        this.diseaseC = diseaseC;
    }

    public String getDiseaseC() 
    {
        return diseaseC;
    }

    public void setDiseaseD(String diseaseD) 
    {
        this.diseaseD = diseaseD;
    }

    public String getDiseaseD() 
    {
        return diseaseD;
    }

    public void setDiseaseE(String diseaseE) 
    {
        this.diseaseE = diseaseE;
    }

    public String getDiseaseE() 
    {
        return diseaseE;
    }

    public void setDiseaseF(String diseaseF) 
    {
        this.diseaseF = diseaseF;
    }

    public String getDiseaseF() 
    {
        return diseaseF;
    }

    public void setDiseaseG(String diseaseG) 
    {
        this.diseaseG = diseaseG;
    }

    public String getDiseaseG() 
    {
        return diseaseG;
    }

    public void setDiseaseH(String diseaseH) 
    {
        this.diseaseH = diseaseH;
    }

    public String getDiseaseH() 
    {
        return diseaseH;
    }

    public void setDiseaseI(String diseaseI) 
    {
        this.diseaseI = diseaseI;
    }

    public String getDiseaseI() 
    {
        return diseaseI;
    }

    public void setDiseaseJ(String diseaseJ) 
    {
        this.diseaseJ = diseaseJ;
    }

    public String getDiseaseJ() 
    {
        return diseaseJ;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("diseaseId", getDiseaseId())
            .append("recordId", getRecordId())
            .append("imageId", getImageId())
            .append("diseaseName", getDiseaseName())
            .append("diseaseCode", getDiseaseCode())
            .append("diseaseCrop", getDiseaseCrop())
            .append("diseaseConfidence", getDiseaseConfidence())
            .append("diseaseResult", getDiseaseResult())
            .append("diseaseDesc", getDiseaseDesc())
            .append("diseaseAdvice", getDiseaseAdvice())
            .append("diseaseResultImgUrl", getDiseaseResultImgUrl())
            .append("isWarning", getIsWarning())
            .append("warningLevel", getWarningLevel())
            .append("createTime", getCreateTime())
            .append("diseaseB", getDiseaseB())
            .append("diseaseC", getDiseaseC())
            .append("diseaseD", getDiseaseD())
            .append("diseaseE", getDiseaseE())
            .append("diseaseF", getDiseaseF())
            .append("diseaseG", getDiseaseG())
            .append("diseaseH", getDiseaseH())
            .append("diseaseI", getDiseaseI())
            .append("diseaseJ", getDiseaseJ())
            .toString();
    }
}
