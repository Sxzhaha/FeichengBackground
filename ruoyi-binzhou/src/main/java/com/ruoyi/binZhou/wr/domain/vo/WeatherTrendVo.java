package com.ruoyi.binZhou.wr.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 天气趋势图数据对象
 *
 * 用于封装：
 * 1. 过去若干天历史天气实况
 * 2. 当前最新天气实况
 * 3. 未来若干天天气预报
 */
public class WeatherTrendVo
{
    /** 当前日期，格式：yyyy-MM-dd */
    private String today;

    /** 过去若干天历史天气 */
    private List<HistoryWeatherItem> history = new ArrayList<>();

    /** 当前最新天气实况 */
    private CurrentWeatherItem current;

    /** 未来若干天天气预报 */
    private List<ForecastWeatherItem> forecast = new ArrayList<>();

    public String getToday()
    {
        return today;
    }

    public void setToday(String today)
    {
        this.today = today;
    }

    public List<HistoryWeatherItem> getHistory()
    {
        return history;
    }

    public void setHistory(List<HistoryWeatherItem> history)
    {
        this.history = history;
    }

    public CurrentWeatherItem getCurrent()
    {
        return current;
    }

    public void setCurrent(CurrentWeatherItem current)
    {
        this.current = current;
    }

    public List<ForecastWeatherItem> getForecast()
    {
        return forecast;
    }

    public void setForecast(List<ForecastWeatherItem> forecast)
    {
        this.forecast = forecast;
    }

    /** 历史天气每日汇总项 */
    public static class HistoryWeatherItem
    {
        /** 日期，格式：yyyy-MM-dd */
        private String date;

        /** 当日平均温度 */
        private BigDecimal temperature;

        /** 当日平均湿度 */
        private BigDecimal humidity;

        /** 当日平均风速，单位 m/s */
        private BigDecimal windSpeed;

        /** 冻害概率，0-100 */
        private Integer frostProbability;

        /** 干热风概率，0-100 */
        private Integer hotWindProbability;

        public String getDate()
        {
            return date;
        }

        public void setDate(String date)
        {
            this.date = date;
        }

        public BigDecimal getTemperature()
        {
            return temperature;
        }

        public void setTemperature(BigDecimal temperature)
        {
            this.temperature = temperature;
        }

        public BigDecimal getHumidity()
        {
            return humidity;
        }

        public void setHumidity(BigDecimal humidity)
        {
            this.humidity = humidity;
        }

        public BigDecimal getWindSpeed()
        {
            return windSpeed;
        }

        public void setWindSpeed(BigDecimal windSpeed)
        {
            this.windSpeed = windSpeed;
        }

        public Integer getFrostProbability()
        {
            return frostProbability;
        }

        public void setFrostProbability(Integer frostProbability)
        {
            this.frostProbability = frostProbability;
        }

        public Integer getHotWindProbability()
        {
            return hotWindProbability;
        }

        public void setHotWindProbability(Integer hotWindProbability)
        {
            this.hotWindProbability = hotWindProbability;
        }
    }

    /** 当前最新天气实况 */
    public static class CurrentWeatherItem
    {
        /** 日期，格式：yyyy-MM-dd */
        private String date;

        /** 实况上报时间，格式：yyyy-MM-dd HH:mm:ss */
        private String reportTime;

        /** 当前温度 */
        private BigDecimal temperature;

        /** 当前湿度 */
        private BigDecimal humidity;

        /** 当前风速，单位 m/s */
        private BigDecimal windSpeed;

        /** 冻害概率，0-100 */
        private Integer frostProbability;

        /** 干热风概率，0-100 */
        private Integer hotWindProbability;

        public String getDate()
        {
            return date;
        }

        public void setDate(String date)
        {
            this.date = date;
        }

        public String getReportTime()
        {
            return reportTime;
        }

        public void setReportTime(String reportTime)
        {
            this.reportTime = reportTime;
        }

        public BigDecimal getTemperature()
        {
            return temperature;
        }

        public void setTemperature(BigDecimal temperature)
        {
            this.temperature = temperature;
        }

        public BigDecimal getHumidity()
        {
            return humidity;
        }

        public void setHumidity(BigDecimal humidity)
        {
            this.humidity = humidity;
        }

        public BigDecimal getWindSpeed()
        {
            return windSpeed;
        }

        public void setWindSpeed(BigDecimal windSpeed)
        {
            this.windSpeed = windSpeed;
        }

        public Integer getFrostProbability()
        {
            return frostProbability;
        }

        public void setFrostProbability(Integer frostProbability)
        {
            this.frostProbability = frostProbability;
        }

        public Integer getHotWindProbability()
        {
            return hotWindProbability;
        }

        public void setHotWindProbability(Integer hotWindProbability)
        {
            this.hotWindProbability = hotWindProbability;
        }
    }

    /** 未来天气预报项 */
    public static class ForecastWeatherItem
    {
        /** 预报日期，格式：yyyy-MM-dd */
        private String date;

        /** 最高温度 */
        private BigDecimal tempMax;

        /** 最低温度 */
        private BigDecimal tempMin;

        /** 平均温度 */
        private BigDecimal temperature;

        /** 平均湿度 */
        private BigDecimal humidity;

        /** 预报风速，单位 m/s */
        private BigDecimal windSpeed;

        /** 白天风力等级 */
        private String windScaleDay;

        /** 和风天气原始预报，仅供服务层读取 windSpeedDay */
        @JsonIgnore
        private String rawForecastJson;

        /** 冻害概率，0-100 */
        private Integer frostProbability;

        /** 干热风概率，0-100 */
        private Integer hotWindProbability;

        /** 白天天气描述 */
        private String weatherTextDay;

        /** 夜间天气描述 */
        private String weatherTextNight;

        public String getDate()
        {
            return date;
        }

        public void setDate(String date)
        {
            this.date = date;
        }

        public BigDecimal getTempMax()
        {
            return tempMax;
        }

        public void setTempMax(BigDecimal tempMax)
        {
            this.tempMax = tempMax;
        }

        public BigDecimal getTempMin()
        {
            return tempMin;
        }

        public void setTempMin(BigDecimal tempMin)
        {
            this.tempMin = tempMin;
        }

        public BigDecimal getTemperature()
        {
            return temperature;
        }

        public void setTemperature(BigDecimal temperature)
        {
            this.temperature = temperature;
        }

        public BigDecimal getHumidity()
        {
            return humidity;
        }

        public void setHumidity(BigDecimal humidity)
        {
            this.humidity = humidity;
        }

        public BigDecimal getWindSpeed()
        {
            return windSpeed;
        }

        public void setWindSpeed(BigDecimal windSpeed)
        {
            this.windSpeed = windSpeed;
        }

        public String getWindScaleDay()
        {
            return windScaleDay;
        }

        public void setWindScaleDay(String windScaleDay)
        {
            this.windScaleDay = windScaleDay;
        }

        public String getRawForecastJson()
        {
            return rawForecastJson;
        }

        public void setRawForecastJson(String rawForecastJson)
        {
            this.rawForecastJson = rawForecastJson;
        }

        public Integer getFrostProbability()
        {
            return frostProbability;
        }

        public void setFrostProbability(Integer frostProbability)
        {
            this.frostProbability = frostProbability;
        }

        public Integer getHotWindProbability()
        {
            return hotWindProbability;
        }

        public void setHotWindProbability(Integer hotWindProbability)
        {
            this.hotWindProbability = hotWindProbability;
        }

        public String getWeatherTextDay()
        {
            return weatherTextDay;
        }

        public void setWeatherTextDay(String weatherTextDay)
        {
            this.weatherTextDay = weatherTextDay;
        }

        public String getWeatherTextNight()
        {
            return weatherTextNight;
        }

        public void setWeatherTextNight(String weatherTextNight)
        {
            this.weatherTextNight = weatherTextNight;
        }
    }
}
