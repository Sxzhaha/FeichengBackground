package com.ruoyi.binZhou.xwq.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 物候期识别请求参数。
 *
 * 前端只传图片、模型和作物信息。
 * 模型服务地址必须由后端根据 modelId 从数据库读取，
 * 禁止由前端直接传入。
 */
public class PhenologyPredictRequest
{
    /** 待识别图片 ID */
    @NotNull(message = "图片ID不能为空")
    private Long imageId;

    /** 使用的模型 ID */
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    /** 作物类型，值应与 xwq_crop_type 字典编码一致 */
    @NotBlank(message = "作物类型不能为空")
    private String cropType;

    /** 识别方式：手动上传、自动定时 */
    private String recordMode = "手动上传";

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

    public String getCropType()
    {
        return cropType;
    }

    public void setCropType(String cropType)
    {
        this.cropType = cropType;
    }

    public String getRecordMode()
    {
        return recordMode;
    }

    public void setRecordMode(String recordMode)
    {
        this.recordMode = recordMode;
    }

    @Override
    public String toString()
    {
        return "PhenologyPredictRequest{" +
                "imageId=" + imageId +
                ", modelId=" + modelId +
                ", cropType='" + cropType + '\'' +
                ", recordMode='" + recordMode + '\'' +
                '}';
    }
}
