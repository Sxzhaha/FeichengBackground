package com.ruoyi.iot.ld.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 园区未来天气预报趋势对象 ld_agri_weather_forecast
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
public class AgriWeatherForecast extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识ID，唯一自增主键 */
    private Long id;

    /** 园区ID */
    @Excel(name = "园区ID")
    private String parkId;

    /** 数据抓取时间，接口数据拉取的具体时间点 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "数据抓取时间，接口数据拉取的具体时间点", width = 30, dateFormat = "yyyy-MM-dd")
    private Date fetchTime;

    /** 预报目标日期，被预测的未来某一天日期（YYYY-MM-DD） */
    @Excel(name = "预报目标日期，被预测的未来某一天日期", readConverterExp = "Y=YYY-MM-DD")
    private Date forecastDate;

    /** 最高温度，该日最高温度预测（℃） */
    @Excel(name = "最高温度，该日最高温度预测", readConverterExp = "℃=")
    private Double tempMax;

    /** 最低温度，该日最低温度预测（℃） */
    @Excel(name = "最低温度，该日最低温度预测", readConverterExp = "℃=")
    private Double tempMin;

    /** 平均湿度，该日平均相对湿度预测（%） */
    @Excel(name = "平均湿度，该日平均相对湿度预测", readConverterExp = "%=")
    private Double humidityAvg;

    /** 白天风向，白天风向文字描述 */
    @Excel(name = "白天风向，白天风向文字描述")
    private String windDirDay;

    /** 白天风力，白天风力等级描述 */
    @Excel(name = "白天风力，白天风力等级描述")
    private String windScaleDay;

    /** 全天降水量，该日总降水量预测，单位：毫米（mm） */
    @Excel(name = "全天降水量，该日总降水量预测，单位：毫米", readConverterExp = "m=m")
    private Double precipTotal;

    /** 降水概率，降水概率（%），范围 0-100 */
    @Excel(name = "降水概率，降水概率", readConverterExp = "%=")
    private Double precipProb;

    /** 白天天气，白天天气现象（如：多云） */
    @Excel(name = "白天天气，白天天气现象", readConverterExp = "如=：多云")
    private String weatherTextDay;

    /** 夜间天气，夜间天气现象（如：雷阵雨） */
    @Excel(name = "夜间天气，夜间天气现象", readConverterExp = "如=：雷阵雨")
    private String weatherTextNight;

    /** 白天天气代码，白天天气现象图标代码（如：100） */
    @Excel(name = "白天天气代码")
    private String dayCode;

    /** 晚间天气代码，晚间天气现象图标代码（如：150） */
    @Excel(name = "晚间天气代码")
    private String moonCode;

    /** 预报天数类型，预报类型：7表示7天预报，15表示15天预报 */
    @Excel(name = "预报天数类型，预报类型：7表示7天预报，15表示15天预报")
    private Integer forecastType;

    /** 预留状态扩展字段：数据有效性状态或气象局源头标识，默认1 */
    @Excel(name = "预留状态扩展字段：数据有效性状态或气象局源头标识，默认1")
    private Long remark1;

    /** 预留文本扩展字段：中长期趋势分析备注或扩展气象JSON参数 */
    @Excel(name = "预留文本扩展字段：中长期趋势分析备注或扩展气象JSON参数")
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

    public void setForecastDate(Date forecastDate) 
    {
        this.forecastDate = forecastDate;
    }

    public Date getForecastDate() 
    {
        return forecastDate;
    }

    public void setTempMax(Double tempMax) 
    {
        this.tempMax = tempMax;
    }

    public Double getTempMax() 
    {
        return tempMax;
    }

    public void setTempMin(Double tempMin) 
    {
        this.tempMin = tempMin;
    }

    public Double getTempMin() 
    {
        return tempMin;
    }

    public void setHumidityAvg(Double humidityAvg) 
    {
        this.humidityAvg = humidityAvg;
    }

    public Double getHumidityAvg() 
    {
        return humidityAvg;
    }

    public void setWindDirDay(String windDirDay) 
    {
        this.windDirDay = windDirDay;
    }

    public String getWindDirDay() 
    {
        return windDirDay;
    }

    public void setWindScaleDay(String windScaleDay) 
    {
        this.windScaleDay = windScaleDay;
    }

    public String getWindScaleDay() 
    {
        return windScaleDay;
    }

    public void setPrecipTotal(Double precipTotal) 
    {
        this.precipTotal = precipTotal;
    }

    public Double getPrecipTotal() 
    {
        return precipTotal;
    }

    public void setPrecipProb(Double precipProb) 
    {
        this.precipProb = precipProb;
    }

    public Double getPrecipProb() 
    {
        return precipProb;
    }

    public void setWeatherTextDay(String weatherTextDay) 
    {
        this.weatherTextDay = weatherTextDay;
    }

    public String getWeatherTextDay() 
    {
        return weatherTextDay;
    }

    public void setWeatherTextNight(String weatherTextNight) 
    {
        this.weatherTextNight = weatherTextNight;
    }

    public String getWeatherTextNight() 
    {
        return weatherTextNight;
    }

    public void setDayCode(String dayCode) 
    {
        this.dayCode = dayCode;
    }

    public String getDayCode() 
    {
        return dayCode;
    }

    public void setMoonCode(String moonCode) 
    {
        this.moonCode = moonCode;
    }

    public String getMoonCode() 
    {
        return moonCode;
    }

    public void setForecastType(Integer forecastType) 
    {
        this.forecastType = forecastType;
    }

    public Integer getForecastType() 
    {
        return forecastType;
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
            .append("forecastDate", getForecastDate())
            .append("tempMax", getTempMax())
            .append("tempMin", getTempMin())
            .append("humidityAvg", getHumidityAvg())
            .append("windDirDay", getWindDirDay())
            .append("windScaleDay", getWindScaleDay())
            .append("precipTotal", getPrecipTotal())
            .append("precipProb", getPrecipProb())
            .append("weatherTextDay", getWeatherTextDay())
            .append("weatherTextNight", getWeatherTextNight())
            .append("dayCode", getDayCode())
            .append("moonCode", getMoonCode())
            .append("forecastType", getForecastType())
            .append("remark1", getRemark1())
            .append("remark2", getRemark2())
            .toString();
    }
}
