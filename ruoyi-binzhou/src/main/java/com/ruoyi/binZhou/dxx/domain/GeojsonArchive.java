package com.ruoyi.binZhou.dxx.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * GeoJSON存档对象 dxx_geojson_archive
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class GeojsonArchive extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 存档ID */
    private Long archiveId;

    /** 所属图层ID */
    @Excel(name = "所属图层ID")
    private Long datasetId;

    /** 存档名称 */
    @Excel(name = "存档名称")
    private String archiveName;

    /** 版本号 */
    @Excel(name = "版本号")
    private String versionNo;

    /** 原始文件名 */
    @Excel(name = "原始文件名")
    private String originalName;

    /** 服务器文件路径 */
    @Excel(name = "服务器文件路径")
    private String filePath;

    /** 文件大小（字节） */
    @Excel(name = "文件大小", readConverterExp = "字=节")
    private Long fileSize;

    /** 文件SHA256值 */
    @Excel(name = "文件SHA256值")
    private String fileHash;

    /** GeoJSON要素数量 */
    @Excel(name = "GeoJSON要素数量")
    private Long featureCount;

    /** 几何类型 */
    @Excel(name = "几何类型")
    private String geometryType;

    /** 坐标系统 */
    @Excel(name = "坐标系统")
    private String coordinateSys;

    /** 最小经度 */
    @Excel(name = "最小经度")
    private BigDecimal minLng;

    /** 最小纬度 */
    @Excel(name = "最小纬度")
    private BigDecimal minLat;

    /** 最大经度 */
    @Excel(name = "最大经度")
    private BigDecimal maxLng;

    /** 最大纬度 */
    @Excel(name = "最大纬度")
    private BigDecimal maxLat;

    /** 边线颜色 */
    @Excel(name = "边线颜色")
    private String previewColor;

    /** 填充颜色 */
    @Excel(name = "填充颜色")
    private String fillColor;

    /** 填充透明度 */
    @Excel(name = "填充透明度")
    private BigDecimal fillOpacity;

    /** 扩展显示样式 */
    @Excel(name = "扩展显示样式")
    private String styleJson;

    /** 是否默认：0否，1是 */
    @Excel(name = "是否默认：0否，1是")
    private String isDefault;

    /** 状态：0正常，1停用 */
    @Excel(name = "状态：0正常，1停用")
    private String status;

    /** 删除标志：0存在，2删除 */
    private String delFlag;

    public void setArchiveId(Long archiveId) 
    {
        this.archiveId = archiveId;
    }

    public Long getArchiveId() 
    {
        return archiveId;
    }

    public void setDatasetId(Long datasetId) 
    {
        this.datasetId = datasetId;
    }

    public Long getDatasetId() 
    {
        return datasetId;
    }

    public void setArchiveName(String archiveName) 
    {
        this.archiveName = archiveName;
    }

    public String getArchiveName() 
    {
        return archiveName;
    }

    public void setVersionNo(String versionNo) 
    {
        this.versionNo = versionNo;
    }

    public String getVersionNo() 
    {
        return versionNo;
    }

    public void setOriginalName(String originalName) 
    {
        this.originalName = originalName;
    }

    public String getOriginalName() 
    {
        return originalName;
    }

    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }

    public void setFileSize(Long fileSize) 
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize() 
    {
        return fileSize;
    }

    public void setFileHash(String fileHash) 
    {
        this.fileHash = fileHash;
    }

    public String getFileHash() 
    {
        return fileHash;
    }

    public void setFeatureCount(Long featureCount) 
    {
        this.featureCount = featureCount;
    }

    public Long getFeatureCount() 
    {
        return featureCount;
    }

    public void setGeometryType(String geometryType) 
    {
        this.geometryType = geometryType;
    }

    public String getGeometryType() 
    {
        return geometryType;
    }

    public void setCoordinateSys(String coordinateSys) 
    {
        this.coordinateSys = coordinateSys;
    }

    public String getCoordinateSys() 
    {
        return coordinateSys;
    }

    public void setMinLng(BigDecimal minLng) 
    {
        this.minLng = minLng;
    }

    public BigDecimal getMinLng() 
    {
        return minLng;
    }

    public void setMinLat(BigDecimal minLat) 
    {
        this.minLat = minLat;
    }

    public BigDecimal getMinLat() 
    {
        return minLat;
    }

    public void setMaxLng(BigDecimal maxLng) 
    {
        this.maxLng = maxLng;
    }

    public BigDecimal getMaxLng() 
    {
        return maxLng;
    }

    public void setMaxLat(BigDecimal maxLat) 
    {
        this.maxLat = maxLat;
    }

    public BigDecimal getMaxLat() 
    {
        return maxLat;
    }

    public void setPreviewColor(String previewColor) 
    {
        this.previewColor = previewColor;
    }

    public String getPreviewColor() 
    {
        return previewColor;
    }

    public void setFillColor(String fillColor) 
    {
        this.fillColor = fillColor;
    }

    public String getFillColor() 
    {
        return fillColor;
    }

    public void setFillOpacity(BigDecimal fillOpacity) 
    {
        this.fillOpacity = fillOpacity;
    }

    public BigDecimal getFillOpacity() 
    {
        return fillOpacity;
    }

    public void setStyleJson(String styleJson) 
    {
        this.styleJson = styleJson;
    }

    public String getStyleJson() 
    {
        return styleJson;
    }

    public void setIsDefault(String isDefault) 
    {
        this.isDefault = isDefault;
    }

    public String getIsDefault() 
    {
        return isDefault;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("archiveId", getArchiveId())
            .append("datasetId", getDatasetId())
            .append("archiveName", getArchiveName())
            .append("versionNo", getVersionNo())
            .append("originalName", getOriginalName())
            .append("filePath", getFilePath())
            .append("fileSize", getFileSize())
            .append("fileHash", getFileHash())
            .append("featureCount", getFeatureCount())
            .append("geometryType", getGeometryType())
            .append("coordinateSys", getCoordinateSys())
            .append("minLng", getMinLng())
            .append("minLat", getMinLat())
            .append("maxLng", getMaxLng())
            .append("maxLat", getMaxLat())
            .append("previewColor", getPreviewColor())
            .append("fillColor", getFillColor())
            .append("fillOpacity", getFillOpacity())
            .append("styleJson", getStyleJson())
            .append("isDefault", getIsDefault())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
