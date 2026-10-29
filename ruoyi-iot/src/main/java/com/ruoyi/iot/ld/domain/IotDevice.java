package com.ruoyi.iot.ld.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 物联网设备对象 iot_device
 * * @author ruoyi
 */
public class IotDevice extends BaseEntity
{
	private static final long serialVersionUID = 1L;

	/** 设备ID */
	private Long deviceId;

	/** 设备名称 */
	@Excel(name = "设备名称")
	private String deviceName;

	/** 设备编号/序列号 */
	@Excel(name = "设备编号")
	private String deviceSn;

	/** 协议类型 */
	@Excel(name = "协议类型")
	private String protocolType;

	/** 状态（0离线 1在线） */
	@Excel(name = "状态", readConverterExp = "0=离线,1=在线")
	private String status;

	/** 最后上线时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Excel(name = "最后上线时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
	private Date lastOnlineTime;
	@Excel(name = "位置Id")
	private Long locationId;
	@Excel(name = "区域名称")
	private String locationName;
	@Excel(name = "区域Id")
	private Long areaId;
	@Excel(name = "区域名称")
	private String areaName;
	@Excel(name = "园区Id")
	private Long parkId;
	@Excel(name = "园区名称")
	private String parkName;
	/** 产品ID */
	private Long productId;
	private Integer productType;
//	所属园区的用户ID
	private Long userId;
	private Long creatorId;

	// Getters and Setters

	public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
	public Long getDeviceId() { return deviceId; }

	public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
	public String getDeviceName() { return deviceName; }

	public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
	public String getDeviceSn() { return deviceSn; }

	public void setProtocolType(String protocolType) { this.protocolType = protocolType; }
	public String getProtocolType() { return protocolType; }

	public void setStatus(String status) { this.status = status; }
	public String getStatus() { return status; }

	public void setLastOnlineTime(Date lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }
	public Date getLastOnlineTime() { return lastOnlineTime; }

	public void setProductId(Long productId) { this.productId = productId; }
	public Long getProductId() { return productId; }

	public Integer getProductType() { return productType; }
	public void setProductType(Integer productType) { this.productType = productType; }

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Long getParkId() {
		return parkId;
	}

	public void setParkId(Long parkId) {
		this.parkId = parkId;
	}

	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
				.append("deviceId", getDeviceId())
				.append("deviceName", getDeviceName())
				.append("deviceSn", getDeviceSn())
				.append("protocolType", getProtocolType())
				.append("status", getStatus())
				.append("lastOnlineTime", getLastOnlineTime())
				.append("productId", getProductId())
				.append("productType", getProductType())
				.append("location",getLocationId())
				.append("area",getAreaId())
				.append("park",getParkId())
				.append("createBy", getCreateBy())
				.append("createTime", getCreateTime())
				.append("updateBy", getUpdateBy())
				.append("updateTime", getUpdateTime())
				.append("remark", getRemark())
				.toString();
	}
}
