package com.ruoyi.binZhou.xwq.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 物候期识别工作台历史记录查询条件。
 *
 * 仅用于前台识别工作台的关联查询，不映射数据库表。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
public class PhenologyRecognitionQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图片ID */
    private Long imageId;

    /** 模型ID */
    private Long modelId;

    /** 作物类型 */
    private String phenologyCrop;

    /** 物候期名称 */
    private String phenologyName;

    /** 图片名称 */
    private String imageName;

    /** 模型名称 */
    private String modelName;

    /** 识别方式 */
    private String recordMode;

    /** 识别状态：0待识别，1识别中，2成功，3失败 */
    private Long recordStatus;

    public Long getImageId()
    {
        return imageId;
    }

    public void setImageId(Long imageId)
    {
        this.imageId = imageId;
    }

    public Long getModelId()
    {
        return modelId;
    }

    public void setModelId(Long modelId)
    {
        this.modelId = modelId;
    }

    public String getPhenologyCrop()
    {
        return phenologyCrop;
    }

    public void setPhenologyCrop(String phenologyCrop)
    {
        this.phenologyCrop = phenologyCrop;
    }

    public String getPhenologyName()
    {
        return phenologyName;
    }

    public void setPhenologyName(String phenologyName)
    {
        this.phenologyName = phenologyName;
    }

    public String getImageName()
    {
        return imageName;
    }

    public void setImageName(String imageName)
    {
        this.imageName = imageName;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
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

    @Override
    public String toString()
    {
        return "PhenologyRecognitionQuery{" +
                "imageId=" + imageId +
                ", modelId=" + modelId +
                ", phenologyCrop='" + phenologyCrop + '\'' +
                ", phenologyName='" + phenologyName + '\'' +
                ", imageName='" + imageName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", recordMode='" + recordMode + '\'' +
                ", recordStatus=" + recordStatus +
                '}';
    }
}
