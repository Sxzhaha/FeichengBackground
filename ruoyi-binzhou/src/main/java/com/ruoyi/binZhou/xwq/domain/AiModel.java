package com.ruoyi.binZhou.xwq.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 模型管理对象 xwq_ai_model
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class AiModel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识一个模型 */
    private Long modelId;

    /** 模型名称 */
    @Excel(name = "模型名称")
    private String modelName;

    /** 系统内部模型编号 */
    @Excel(name = "系统内部模型编号")
    private String modelCode;

    /** 模型类型，字典：病虫害、虫情、物候期 */
    @Excel(name = "模型类型，字典：病虫害、虫情、物候期")
    private String modelType;

    /** 适用作物，如小麦、玉米、水稻 */
    @Excel(name = "适用作物，如小麦、玉米、水稻")
    private String modelCrop;

    /** 模型服务调用地址 */
    @Excel(name = "模型服务调用地址")
    private String modelUrl;

    /** 模型用途说明 */
    @Excel(name = "模型用途说明")
    private String modelDesc;

    /** 模型状态：0 停用，1 启用 */
    @Excel(name = "模型状态：0 停用，1 启用")
    private Long modelStatus;

    /** 备用字段1 */
    @Excel(name = "备用字段1")
    private String modelA;

    /** 备用字段2 */
    @Excel(name = "备用字段2")
    private String modelB;

    /** 备用字段3 */
    @Excel(name = "备用字段3")
    private String modelC;

    /** 备用字段4 */
    @Excel(name = "备用字段4")
    private String modelD;

    /** 备用字段5 */
    @Excel(name = "备用字段5")
    private String modelE;

    /** 备用字段6 */
    @Excel(name = "备用字段6")
    private String modelF;

    /** 备用字段7 */
    @Excel(name = "备用字段7")
    private String modelG;

    /** 备用字段8 */
    @Excel(name = "备用字段8")
    private String modelH;

    /** 备用字段9 */
    @Excel(name = "备用字段9")
    private String modelI;

    /** 备用字段10 */
    @Excel(name = "备用字段10")
    private String modelJ;

    public void setModelId(Long modelId) 
    {
        this.modelId = modelId;
    }

    public Long getModelId() 
    {
        return modelId;
    }

    public void setModelName(String modelName) 
    {
        this.modelName = modelName;
    }

    public String getModelName() 
    {
        return modelName;
    }

    public void setModelCode(String modelCode) 
    {
        this.modelCode = modelCode;
    }

    public String getModelCode() 
    {
        return modelCode;
    }

    public void setModelType(String modelType) 
    {
        this.modelType = modelType;
    }

    public String getModelType() 
    {
        return modelType;
    }

    public void setModelCrop(String modelCrop) 
    {
        this.modelCrop = modelCrop;
    }

    public String getModelCrop() 
    {
        return modelCrop;
    }

    public void setModelUrl(String modelUrl) 
    {
        this.modelUrl = modelUrl;
    }

    public String getModelUrl() 
    {
        return modelUrl;
    }

    public void setModelDesc(String modelDesc) 
    {
        this.modelDesc = modelDesc;
    }

    public String getModelDesc() 
    {
        return modelDesc;
    }

    public void setModelStatus(Long modelStatus) 
    {
        this.modelStatus = modelStatus;
    }

    public Long getModelStatus() 
    {
        return modelStatus;
    }

    public void setModelA(String modelA) 
    {
        this.modelA = modelA;
    }

    public String getModelA() 
    {
        return modelA;
    }

    public void setModelB(String modelB) 
    {
        this.modelB = modelB;
    }

    public String getModelB() 
    {
        return modelB;
    }

    public void setModelC(String modelC) 
    {
        this.modelC = modelC;
    }

    public String getModelC() 
    {
        return modelC;
    }

    public void setModelD(String modelD) 
    {
        this.modelD = modelD;
    }

    public String getModelD() 
    {
        return modelD;
    }

    public void setModelE(String modelE) 
    {
        this.modelE = modelE;
    }

    public String getModelE() 
    {
        return modelE;
    }

    public void setModelF(String modelF) 
    {
        this.modelF = modelF;
    }

    public String getModelF() 
    {
        return modelF;
    }

    public void setModelG(String modelG) 
    {
        this.modelG = modelG;
    }

    public String getModelG() 
    {
        return modelG;
    }

    public void setModelH(String modelH) 
    {
        this.modelH = modelH;
    }

    public String getModelH() 
    {
        return modelH;
    }

    public void setModelI(String modelI) 
    {
        this.modelI = modelI;
    }

    public String getModelI() 
    {
        return modelI;
    }

    public void setModelJ(String modelJ) 
    {
        this.modelJ = modelJ;
    }

    public String getModelJ() 
    {
        return modelJ;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("modelId", getModelId())
            .append("modelName", getModelName())
            .append("modelCode", getModelCode())
            .append("modelType", getModelType())
            .append("modelCrop", getModelCrop())
            .append("modelUrl", getModelUrl())
            .append("modelDesc", getModelDesc())
            .append("modelStatus", getModelStatus())
            .append("modelA", getModelA())
            .append("modelB", getModelB())
            .append("modelC", getModelC())
            .append("modelD", getModelD())
            .append("modelE", getModelE())
            .append("modelF", getModelF())
            .append("modelG", getModelG())
            .append("modelH", getModelH())
            .append("modelI", getModelI())
            .append("modelJ", getModelJ())
            .toString();
    }
}
