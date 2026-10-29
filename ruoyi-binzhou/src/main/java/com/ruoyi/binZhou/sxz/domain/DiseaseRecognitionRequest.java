package com.ruoyi.binZhou.sxz.domain;

public class DiseaseRecognitionRequest
{
    private Long imageId;
    private Long modelId;
    private String cropType;
    private String recordMode;

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
}
