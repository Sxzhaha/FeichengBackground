package com.ruoyi.iot.ld.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 园区实时与当天天气实况记录对象 ld_agri_weather_live
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
public class AgriWeatherLive extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识ID，唯一自增主键 */
    private Long id;

    /** 园区ID，关联园区基础信息表 */
    @Excel(name = "园区ID，关联园区基础信息表")
    private String parkId;

    /** 数据抓取时间，接口数据调用时间（精确到分） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "数据抓取时间，接口数据调用时间", readConverterExp = "精=确到分")
    private Date fetchTime;

    /** 气象发布时间，气象局官方数据发布或更新的时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "气象发布时间，气象局官方数据发布或更新的时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reportTime;

    /** 当前温度，单位：摄氏度（℃） */
    @Excel(name = "当前温度，单位：摄氏度", readConverterExp = "℃=")
    private Double temperature;

    /** 当前湿度，相对湿度，单位：百分比（%），范围 0-100 */
    @Excel(name = "当前湿度，相对湿度，单位：百分比", readConverterExp = "%=")
    private Long humidity;

    /** 风向，风向文字描述（如：东北风、西南风） */
    @Excel(name = "风向，风向文字描述", readConverterExp = "如=：东北风、西南风")
    private String windDirection;

    /** 风速，单位：米/秒（m/s） */
    @Excel(name = "风速，单位：米/秒", readConverterExp = "m=/s")
    private Double windSpeed;

    /** 风力等级，风力级数（如：3级、4级） */
    @Excel(name = "风力等级，风力级数", readConverterExp = "如=：3级、4级")
    private Long windScale;

    /** 降水量，当前或过去1小时累计降水量，单位：毫米（mm） */
    @Excel(name = "降水量，当前或过去1小时累计降水量，单位：毫米", readConverterExp = "m=m")
    private Double precipitation;

    /** 大气压强，单位：百帕（hPa） */
    @Excel(name = "大气压强，单位：百帕", readConverterExp = "h=Pa")
    private Double airPressure;

    /** 天气现象代码，天气现象图标/代码（如：晴=100，雨=301） */
    @Excel(name = "天气现象代码，天气现象图标/代码", readConverterExp = "如=：晴=100，雨=301")
    private String weatherCode;

    /** 天气现象文字，天气现象文字描述（如：晴、大雨、多云） */
    @Excel(name = "天气现象文字，天气现象文字描述", readConverterExp = "如=：晴、大雨、多云")
    private String weatherText;

    /** 预留状态扩展字段：数据有效性状态或气象来源类型，默认1 */
    @Excel(name = "预留状态扩展字段：数据有效性状态或气象来源类型，默认1")
    private Long remark1;

    /** 预留文本扩展字段：接口原始报文快照或额外气象评价描述 */
    @Excel(name = "预留文本扩展字段：接口原始报文快照或额外气象评价描述")
    private String remark2;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setParkId(String parkId) 
    {
        this.parkId = parkId;
    }

    public String getParkId() 
    {
        return parkId;
    }

    public void setFetchTime(Date fetchTime) 
    {
        this.fetchTime = fetchTime;
    }

    public Date getFetchTime() 
    {
        return fetchTime;
    }

    public void setReportTime(Date reportTime) 
    {
        this.reportTime = reportTime;
    }

    public Date getReportTime() 
    {
        return reportTime;
    }

    public void setTemperature(Double temperature) 
    {
        this.temperature = temperature;
    }

    public Double getTemperature() 
    {
        return temperature;
    }

    public void setHumidity(Long humidity) 
    {
        this.humidity = humidity;
    }

    public Long getHumidity() 
    {
        return humidity;
    }

    public void setWindDirection(String windDirection) 
    {
        this.windDirection = windDirection;
    }

    public String getWindDirection() 
    {
        return windDirection;
    }

    public void setWindSpeed(Double windSpeed) 
    {
        this.windSpeed = windSpeed;
    }

    public Double getWindSpeed() 
    {
        return windSpeed;
    }

    public void setWindScale(Long windScale) 
    {
        this.windScale = windScale;
    }

    public Long getWindScale() 
    {
        return windScale;
    }

    public void setPrecipitation(Double precipitation) 
    {
        this.precipitation = precipitation;
    }

    public Double getPrecipitation() 
    {
        return precipitation;
    }

    public void setAirPressure(Double airPressure) 
    {
        this.airPressure = airPressure;
    }

    public Double getAirPressure() 
    {
        return airPressure;
    }

    public void setWeatherCode(String weatherCode) 
    {
        this.weatherCode = weatherCode;
    }

    public String getWeatherCode() 
    {
        return weatherCode;
    }

    public void setWeatherText(String weatherText) 
    {
        this.weatherText = weatherText;
    }

    public String getWeatherText() 
    {
        return weatherText;
    }

    public void setRemark1(Long remark1) 
    {
        this.remark1 = remark1;
    }

    public Long getRemark1() 
    {
        return remark1;
    }

    public void setRemark2(String remark2) 
    {
        this.remark2 = remark2;
    }

    public String getRemark2() 
    {
        return remark2;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parkId", getParkId())
            .append("fetchTime", getFetchTime())
            .append("reportTime", getReportTime())
            .append("temperature", getTemperature())
            .append("humidity", getHumidity())
            .append("windDirection", getWindDirection())
            .append("windSpeed", getWindSpeed())
            .append("windScale", getWindScale())
            .append("precipitation", getPrecipitation())
            .append("airPressure", getAirPressure())
            .append("weatherCode", getWeatherCode())
            .append("weatherText", getWeatherText())
            .append("remark1", getRemark1())
            .append("remark2", getRemark2())
            .toString();
    }
}
