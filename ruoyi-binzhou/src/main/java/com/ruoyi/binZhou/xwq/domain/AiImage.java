package com.ruoyi.binZhou.xwq.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 图片信息对象 xwq_ai_image
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class AiImage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识一张图片 */
    private Long imageId;

    /** 图片原始名称 */
    @Excel(name = "图片原始名称")
    private String imageName;

    /** 图片存储地址 */
    @Excel(name = "图片存储地址")
    private String imageUrl;

    /** 模型识别后的打框图、标注图地址 */
    @Excel(name = "模型识别后的打框图、标注图地址")
    private String imageResultUrl;

    /** 分割 mask 图片地址 */
    @Excel(name = "分割 mask 图片地址")
    private String imageMaskUrl;

    /** 识别业务类型：disease 病虫害，pest 虫情，phenology 物候期，unknown 未知 */
    @Excel(name = "识别业务类型：disease 病虫害，pest 虫情，phenology 物候期，unknown 未知")
    private String imageAiType;

    /** 图片来源：手动上传、摄像头、虫情设备、无人机、手机端 */
    @Excel(name = "图片来源：手动上传、摄像头、虫情设备、无人机、手机端")
    private String imageSource;

    /** 来源设备 ID、摄像头 ID、任务 ID 等 */
    @Excel(name = "来源设备 ID、摄像头 ID、任务 ID 等")
    private Long imageSourceId;

    /** 图片宽度，单位像素 */
    @Excel(name = "图片宽度，单位像素")
    private Long imageWidth;

    /** 图片高度，单位像素 */
    @Excel(name = "图片高度，单位像素")
    private Long imageHeight;

    /** 图片大小，单位字节 */
    @Excel(name = "图片大小，单位字节")
    private Long imageSize;

    /** 图片格式，如 jpg、png、jpeg */
    @Excel(name = "图片格式，如 jpg、png、jpeg")
    private String imageFormat;

    /** 园区 ID */
    @Excel(name = "园区 ID")
    private Long parkId;

    /** 地块 ID */
    @Excel(name = "地块 ID")
    private Long plotId;

    /** 设备 ID，关联摄像头、虫情测报仪等设备 */
    @Excel(name = "设备 ID，关联摄像头、虫情测报仪等设备")
    private Long deviceId;

    /** 图片采集位置经度 */
    @Excel(name = "图片采集位置经度")
    private BigDecimal longitude;

    /** 图片采集位置纬度 */
    @Excel(name = "图片采集位置纬度")
    private BigDecimal latitude;

    /** 图片状态：0 待识别，1 已识别，2 识别失败 */
    @Excel(name = "图片状态：0 待识别，1 已识别，2 识别失败")
    private Long imageStatus;

    /** 备用字段1 */
    @Excel(name = "备用字段1")
    private String imageA;

    /** 角落/位置ID */
    @Excel(name = "角落/位置ID")
    private String imageB;

    /** 备用字段3 */
    @Excel(name = "备用字段3")
    private String imageC;

    /** 备用字段4 */
    @Excel(name = "备用字段4")
    private String imageD;

    /** 备用字段5 */
    @Excel(name = "备用字段5")
    private String imageE;

    /** 备用字段6 */
    @Excel(name = "备用字段6")
    private String imageF;

    /** 备用字段7 */
    @Excel(name = "备用字段7")
    private String imageG;

    /** 备用字段8 */
    @Excel(name = "备用字段8")
    private String imageH;

    /** 备用字段9 */
    @Excel(name = "备用字段9")
    private String imageI;

    /** 备用字段10 */
    @Excel(name = "备用字段10")
    private String imageJ;

    public void setImageId(Long imageId) 
    {
        this.imageId = imageId;
    }

    public Long getImageId() 
    {
        return imageId;
    }

    public void setImageName(String imageName) 
    {
        this.imageName = imageName;
    }

    public String getImageName() 
    {
        return imageName;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }

    public void setImageResultUrl(String imageResultUrl) 
    {
        this.imageResultUrl = imageResultUrl;
    }

    public String getImageResultUrl() 
    {
        return imageResultUrl;
    }

    public void setImageMaskUrl(String imageMaskUrl) 
    {
        this.imageMaskUrl = imageMaskUrl;
    }

    public String getImageMaskUrl() 
    {
        return imageMaskUrl;
    }

    public void setImageAiType(String imageAiType) 
    {
        this.imageAiType = imageAiType;
    }

    public String getImageAiType() 
    {
        return imageAiType;
    }

    public void setImageSource(String imageSource) 
    {
        this.imageSource = imageSource;
    }

    public String getImageSource() 
    {
        return imageSource;
    }

    public void setImageSourceId(Long imageSourceId) 
    {
        this.imageSourceId = imageSourceId;
    }

    public Long getImageSourceId() 
    {
        return imageSourceId;
    }

    public void setImageWidth(Long imageWidth) 
    {
        this.imageWidth = imageWidth;
    }

    public Long getImageWidth() 
    {
        return imageWidth;
    }

    public void setImageHeight(Long imageHeight) 
    {
        this.imageHeight = imageHeight;
    }

    public Long getImageHeight() 
    {
        return imageHeight;
    }

    public void setImageSize(Long imageSize) 
    {
        this.imageSize = imageSize;
    }

    public Long getImageSize() 
    {
        return imageSize;
    }

    public void setImageFormat(String imageFormat) 
    {
        this.imageFormat = imageFormat;
    }

    public String getImageFormat() 
    {
        return imageFormat;
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

    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }

    public void setLongitude(BigDecimal longitude) 
    {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude() 
    {
        return longitude;
    }

    public void setLatitude(BigDecimal latitude) 
    {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude() 
    {
        return latitude;
    }

    public void setImageStatus(Long imageStatus) 
    {
        this.imageStatus = imageStatus;
    }

    public Long getImageStatus() 
    {
        return imageStatus;
    }

    public void setImageA(String imageA) 
    {
        this.imageA = imageA;
    }

    public String getImageA() 
    {
        return imageA;
    }

    public void setImageB(String imageB) 
    {
        this.imageB = imageB;
    }

    public String getImageB() 
    {
        return imageB;
    }

    public void setImageC(String imageC) 
    {
        this.imageC = imageC;
    }

    public String getImageC() 
    {
        return imageC;
    }

    public void setImageD(String imageD) 
    {
        this.imageD = imageD;
    }

    public String getImageD() 
    {
        return imageD;
    }

    public void setImageE(String imageE) 
    {
        this.imageE = imageE;
    }

    public String getImageE() 
    {
        return imageE;
    }

    public void setImageF(String imageF) 
    {
        this.imageF = imageF;
    }

    public String getImageF() 
    {
        return imageF;
    }

    public void setImageG(String imageG) 
    {
        this.imageG = imageG;
    }

    public String getImageG() 
    {
        return imageG;
    }

    public void setImageH(String imageH) 
    {
        this.imageH = imageH;
    }

    public String getImageH() 
    {
        return imageH;
    }

    public void setImageI(String imageI) 
    {
        this.imageI = imageI;
    }

    public String getImageI() 
    {
        return imageI;
    }

    public void setImageJ(String imageJ) 
    {
        this.imageJ = imageJ;
    }

    public String getImageJ() 
    {
        return imageJ;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("imageId", getImageId())
            .append("imageName", getImageName())
            .append("imageUrl", getImageUrl())
            .append("imageResultUrl", getImageResultUrl())
            .append("imageMaskUrl", getImageMaskUrl())
            .append("imageAiType", getImageAiType())
            .append("imageSource", getImageSource())
            .append("imageSourceId", getImageSourceId())
            .append("imageWidth", getImageWidth())
            .append("imageHeight", getImageHeight())
            .append("imageSize", getImageSize())
            .append("imageFormat", getImageFormat())
            .append("parkId", getParkId())
            .append("plotId", getPlotId())
            .append("deviceId", getDeviceId())
            .append("longitude", getLongitude())
            .append("latitude", getLatitude())
            .append("imageStatus", getImageStatus())
            .append("imageA", getImageA())
            .append("imageB", getImageB())
            .append("imageC", getImageC())
            .append("imageD", getImageD())
            .append("imageE", getImageE())
            .append("imageF", getImageF())
            .append("imageG", getImageG())
            .append("imageH", getImageH())
            .append("imageI", getImageI())
            .append("imageJ", getImageJ())
            .toString();
    }
}
