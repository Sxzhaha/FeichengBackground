package com.ruoyi.binZhou.xwq.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 虫情识别结果对象 xwq_ai_pest
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class AiPest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识一条虫情识别结果 */
    private Long pestId;

    /** 关联识别记录表 record_id */
    @Excel(name = "关联识别记录表 record_id")
    private Long recordId;

    /** 关联图片信息表 image_id */
    @Excel(name = "关联图片信息表 image_id")
    private Long imageId;

    /** 虫害名称，如蚜虫、飞虱、蓟马等 */
    @Excel(name = "虫害名称，如蚜虫、飞虱、蓟马等")
    private String pestName;

    /** 虫害标准编码 */
    @Excel(name = "虫害标准编码")
    private String pestCode;

    /** 作物类型，如小麦、玉米、水稻 */
    @Excel(name = "作物类型，如小麦、玉米、水稻")
    private String pestCrop;

    /** 当前虫害类型的识别置信度 */
    @Excel(name = "当前虫害类型的识别置信度")
    private BigDecimal pestConfidence;

    /** 当前虫害类型的数量 */
    @Excel(name = "当前虫害类型的数量")
    private Long pestCount;

    /** 当前图片中识别到的虫体总数 */
    @Excel(name = "当前图片中识别到的虫体总数")
    private Long pestTotalCount;

    /** 虫情识别结论，类型-数量 */
    @Excel(name = "虫情识别结论，类型-数量")
    private String pestResult;

    /** 虫害防治建议 */
    @Excel(name = "虫害防治建议")
    private String pestAdvice;

    /** 存放虫体检测框结果图片 */
    @Excel(name = "存放虫体检测框结果图片")
    private String pestResultImgUrl;

    /** 存放虫体检测框坐标、类别和置信度 */
    @Excel(name = "存放虫体检测框坐标、类别和置信度")
    private String bboxJson;

    /** 是否触发预警：0 否，1 是 */
    @Excel(name = "是否触发预警：0 否，1 是")
    private Long isWarning;

    /** 根据虫口数量或密度生成的预警等级 */
    @Excel(name = "根据虫口数量或密度生成的预警等级")
    private String warningLevel;

    /** 图片名称，来自 xwq_ai_image.image_name */
    private String imageName;

    /** 图片地址，来自 xwq_ai_image.image_url */
    private String imageUrl;

    /** 识别记录编号，来自 xwq_ai_record.record_no */
    private String recordNo;

    /** 识别方式，来自 xwq_ai_record.record_mode */
    private String recordMode;

    /** 识别状态，来自 xwq_ai_record.record_status */
    private Long recordStatus;


    /** 识别时间 */
    @Excel(name = "识别时间")
    private String pestA;

    /** 备用字段2 */
    @Excel(name = "备用字段2")
    private String pestB;

    /** 备用字段3 */
    @Excel(name = "备用字段3")
    private String pestC;

    /** 备用字段4 */
    @Excel(name = "备用字段4")
    private String pestD;

    /** 备用字段5 */
    @Excel(name = "备用字段5")
    private String pestE;

    /** 备用字段6 */
    @Excel(name = "备用字段6")
    private String pestF;

    /** 备用字段7 */
    @Excel(name = "备用字段7")
    private String pestG;

    /** 备用字段8 */
    @Excel(name = "备用字段8")
    private String pestH;

    /** 备用字段9 */
    @Excel(name = "备用字段9")
    private String pestI;

    /** 备用字段10 */
    @Excel(name = "备用字段10")
    private String pestJ;

    /** 园区ID，来自 xwq_ai_image.park_id */
    private Long parkId;

    /** 地块/区域ID，来自 xwq_ai_image.plot_id，实际对应 area.area_id */
    private Long areaId;

    /** 角落/位置ID，来自 xwq_ai_image.image_b，实际对应 location.location_id */
    private String locationId;

    /** 设备ID，来自 xwq_ai_image.device_id */
    private Long deviceId;

    /** 园区名称 */
    private String parkName;

    /** 地块/区域名称 */
    private String areaName;

    /** 角落/位置名称 */
    private String locationName;

    /** 设备名称 */
    private String deviceName;

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

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }



    public void setPestId(Long pestId) 
    {
        this.pestId = pestId;
    }

    public Long getPestId() 
    {
        return pestId;
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

    public void setPestName(String pestName) 
    {
        this.pestName = pestName;
    }

    public String getPestName() 
    {
        return pestName;
    }

    public void setPestCode(String pestCode) 
    {
        this.pestCode = pestCode;
    }

    public String getPestCode() 
    {
        return pestCode;
    }

    public void setPestCrop(String pestCrop) 
    {
        this.pestCrop = pestCrop;
    }

    public String getPestCrop() 
    {
        return pestCrop;
    }

    public void setPestConfidence(BigDecimal pestConfidence) 
    {
        this.pestConfidence = pestConfidence;
    }

    public BigDecimal getPestConfidence() 
    {
        return pestConfidence;
    }

    public void setPestCount(Long pestCount) 
    {
        this.pestCount = pestCount;
    }

    public Long getPestCount() 
    {
        return pestCount;
    }

    public void setPestTotalCount(Long pestTotalCount) 
    {
        this.pestTotalCount = pestTotalCount;
    }

    public Long getPestTotalCount() 
    {
        return pestTotalCount;
    }

    public void setPestResult(String pestResult) 
    {
        this.pestResult = pestResult;
    }

    public String getPestResult() 
    {
        return pestResult;
    }

    public void setPestAdvice(String pestAdvice) 
    {
        this.pestAdvice = pestAdvice;
    }

    public String getPestAdvice() 
    {
        return pestAdvice;
    }

    public void setPestResultImgUrl(String pestResultImgUrl) 
    {
        this.pestResultImgUrl = pestResultImgUrl;
    }

    public String getPestResultImgUrl() 
    {
        return pestResultImgUrl;
    }

    public void setBboxJson(String bboxJson) 
    {
        this.bboxJson = bboxJson;
    }

    public String getBboxJson() 
    {
        return bboxJson;
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

    public String getRecordNo()
    {
        return recordNo;
    }

    public void setRecordNo(String recordNo)
    {
        this.recordNo = recordNo;
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

    public void setPestA(String pestA) 
    {
        this.pestA = pestA;
    }

    public String getPestA() 
    {
        return pestA;
    }

    public void setPestB(String pestB) 
    {
        this.pestB = pestB;
    }

    public String getPestB() 
    {
        return pestB;
    }

    public void setPestC(String pestC) 
    {
        this.pestC = pestC;
    }

    public String getPestC() 
    {
        return pestC;
    }

    public void setPestD(String pestD) 
    {
        this.pestD = pestD;
    }

    public String getPestD() 
    {
        return pestD;
    }

    public void setPestE(String pestE) 
    {
        this.pestE = pestE;
    }

    public String getPestE() 
    {
        return pestE;
    }

    public void setPestF(String pestF) 
    {
        this.pestF = pestF;
    }

    public String getPestF() 
    {
        return pestF;
    }

    public void setPestG(String pestG) 
    {
        this.pestG = pestG;
    }

    public String getPestG() 
    {
        return pestG;
    }

    public void setPestH(String pestH) 
    {
        this.pestH = pestH;
    }

    public String getPestH() 
    {
        return pestH;
    }

    public void setPestI(String pestI) 
    {
        this.pestI = pestI;
    }

    public String getPestI() 
    {
        return pestI;
    }

    public void setPestJ(String pestJ) 
    {
        this.pestJ = pestJ;
    }

    public String getPestJ() 
    {
        return pestJ;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pestId", getPestId())
            .append("recordId", getRecordId())
            .append("imageId", getImageId())
            .append("pestName", getPestName())
            .append("pestCode", getPestCode())
            .append("pestCrop", getPestCrop())
            .append("pestConfidence", getPestConfidence())
            .append("pestCount", getPestCount())
            .append("pestTotalCount", getPestTotalCount())
            .append("pestResult", getPestResult())
            .append("pestAdvice", getPestAdvice())
            .append("pestResultImgUrl", getPestResultImgUrl())
            .append("bboxJson", getBboxJson())
            .append("isWarning", getIsWarning())
            .append("warningLevel", getWarningLevel())
            .append("imageName", getImageName())
            .append("imageUrl", getImageUrl())
            .append("recordNo", getRecordNo())
            .append("recordMode", getRecordMode())
            .append("recordStatus", getRecordStatus())
            .append("pestA", getPestA())
            .append("pestB", getPestB())
            .append("pestC", getPestC())
            .append("pestD", getPestD())
            .append("pestE", getPestE())
            .append("pestF", getPestF())
            .append("pestG", getPestG())
            .append("pestH", getPestH())
            .append("pestI", getPestI())
            .append("pestJ", getPestJ())
            .append("parkId", getParkId())
            .append("areaId", getAreaId())
            .append("locationId", getLocationId())
            .append("deviceId", getDeviceId())
            .append("parkName", getParkName())
            .append("areaName", getAreaName())
            .append("locationName", getLocationName())
            .append("deviceName", getDeviceName())
            .toString();
    }
}
