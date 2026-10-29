package com.ruoyi.binZhou.xwq.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 识别记录对象 xwq_ai_record
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class AiRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识一次识别行为 */
    private Long recordId;

    /** 识别记录编号 */
    @Excel(name = "识别记录编号")
    private String recordNo;

    /** 识别类型：病虫害、虫情、物候期 */
    @Excel(name = "识别类型：病虫害、虫情、物候期")
    private String recordType;

    /** 识别方式：手动上传、自动定时 */
    @Excel(name = "识别方式：手动上传、自动定时")
    private String recordMode;

    /** 本次识别使用的模型 ID */
    @Excel(name = "本次识别使用的模型 ID")
    private Long modelId;

    /** 本次识别使用的模型名称快照 */
    @Excel(name = "本次识别使用的模型名称快照")
    private String modelName;

    /** 关联图片信息表 image_id */
    @Excel(name = "关联图片信息表 image_id")
    private Long imageId;

    /** 作物类型，如小麦、玉米、水稻 */
    @Excel(name = "作物类型，如小麦、玉米、水稻")
    private String cropType;

    /** 来源设备 ID */
    @Excel(name = "来源设备 ID")
    private Long deviceId;

    /** 所属园区 */
    @Excel(name = "所属园区")
    private Long parkId;

    /** 所属地块 */
    @Excel(name = "所属地块")
    private Long plotId;

    /** 识别状态：0 待识别，1 识别中，2 成功，3 失败 */
    @Excel(name = "识别状态：0 待识别，1 识别中，2 成功，3 失败")
    private Long recordStatus;

    /** 备用字段1 */
    @Excel(name = "备用字段1")
    private String recordA;

    /** 备用字段2 */
    @Excel(name = "备用字段2")
    private String recordB;

    /** 备用字段3 */
    @Excel(name = "备用字段3")
    private String recordC;

    /** 备用字段4 */
    @Excel(name = "备用字段4")
    private String recordD;

    /** 备用字段5 */
    @Excel(name = "备用字段5")
    private String recordE;

    /** 备用字段6 */
    @Excel(name = "备用字段6")
    private String recordF;

    /** 备用字段7 */
    @Excel(name = "备用字段7")
    private String recordG;

    /** 备用字段8 */
    @Excel(name = "备用字段8")
    private String recordH;

    /** 备用字段9 */
    @Excel(name = "备用字段9")
    private String recordI;

    /** 备用字段10 */
    @Excel(name = "备用字段10")
    private String recordJ;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }

    public void setRecordNo(String recordNo) 
    {
        this.recordNo = recordNo;
    }

    public String getRecordNo() 
    {
        return recordNo;
    }

    public void setRecordType(String recordType) 
    {
        this.recordType = recordType;
    }

    public String getRecordType() 
    {
        return recordType;
    }

    public void setRecordMode(String recordMode) 
    {
        this.recordMode = recordMode;
    }

    public String getRecordMode() 
    {
        return recordMode;
    }

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

    public void setImageId(Long imageId) 
    {
        this.imageId = imageId;
    }

    public Long getImageId() 
    {
        return imageId;
    }

    public void setCropType(String cropType) 
    {
        this.cropType = cropType;
    }

    public String getCropType() 
    {
        return cropType;
    }

    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }

    public void setParkId(Long parkId) 
    {
        this.parkId = parkId;
    }

    public Long getParkId() 
    {
        return parkId;
    }

    public void setPlotId(Long plotId) 
    {
        this.plotId = plotId;
    }

    public Long getPlotId() 
    {
        return plotId;
    }

    public void setRecordStatus(Long recordStatus) 
    {
        this.recordStatus = recordStatus;
    }

    public Long getRecordStatus() 
    {
        return recordStatus;
    }

    public void setRecordA(String recordA) 
    {
        this.recordA = recordA;
    }

    public String getRecordA() 
    {
        return recordA;
    }

    public void setRecordB(String recordB) 
    {
        this.recordB = recordB;
    }

    public String getRecordB() 
    {
        return recordB;
    }

    public void setRecordC(String recordC) 
    {
        this.recordC = recordC;
    }

    public String getRecordC() 
    {
        return recordC;
    }

    public void setRecordD(String recordD) 
    {
        this.recordD = recordD;
    }

    public String getRecordD() 
    {
        return recordD;
    }

    public void setRecordE(String recordE) 
    {
        this.recordE = recordE;
    }

    public String getRecordE() 
    {
        return recordE;
    }

    public void setRecordF(String recordF) 
    {
        this.recordF = recordF;
    }

    public String getRecordF() 
    {
        return recordF;
    }

    public void setRecordG(String recordG) 
    {
        this.recordG = recordG;
    }

    public String getRecordG() 
    {
        return recordG;
    }

    public void setRecordH(String recordH) 
    {
        this.recordH = recordH;
    }

    public String getRecordH() 
    {
        return recordH;
    }

    public void setRecordI(String recordI) 
    {
        this.recordI = recordI;
    }

    public String getRecordI() 
    {
        return recordI;
    }

    public void setRecordJ(String recordJ) 
    {
        this.recordJ = recordJ;
    }

    public String getRecordJ() 
    {
        return recordJ;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("recordNo", getRecordNo())
            .append("recordType", getRecordType())
            .append("recordMode", getRecordMode())
            .append("modelId", getModelId())
            .append("modelName", getModelName())
            .append("imageId", getImageId())
            .append("cropType", getCropType())
            .append("deviceId", getDeviceId())
            .append("parkId", getParkId())
            .append("plotId", getPlotId())
            .append("recordStatus", getRecordStatus())
            .append("recordA", getRecordA())
            .append("recordB", getRecordB())
            .append("recordC", getRecordC())
            .append("recordD", getRecordD())
            .append("recordE", getRecordE())
            .append("recordF", getRecordF())
            .append("recordG", getRecordG())
            .append("recordH", getRecordH())
            .append("recordI", getRecordI())
            .append("recordJ", getRecordJ())
            .toString();
    }
}
