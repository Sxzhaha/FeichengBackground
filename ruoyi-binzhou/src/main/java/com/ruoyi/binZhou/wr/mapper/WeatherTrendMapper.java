package com.ruoyi.binZhou.wr.mapper;

import com.ruoyi.binZhou.wr.domain.vo.WeatherTrendVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 天气趋势图数据 Mapper
 *
 * 负责查询：
 * 1. 过去若干天每日平均温湿度
 * 2. 当前最新天气实况
 * 3. 最新一批未来天气预报
 */
public interface WeatherTrendMapper
{
    /**
     * 查询过去若干天每日平均温湿度
     *
     * @param parkId 园区ID
     * @param historyDays 历史天数
     * @return 历史天气列表
     */
    List<WeatherTrendVo.HistoryWeatherItem> selectHistoryWeather(
            @Param("parkId") Long parkId,
            @Param("historyDays") Integer historyDays
    );

    /**
     * 查询当前最新天气实况
     *
     * @param parkId 园区ID
     * @return 当前最新天气
     */
    WeatherTrendVo.CurrentWeatherItem selectCurrentWeather(
            @Param("parkId") Long parkId
    );

    /**
     * 查询最新一批未来天气预报
     *
     * @param parkId 园区ID
     * @param forecastDays 未来天数
     * @return 未来天气预报列表
     */
    List<WeatherTrendVo.ForecastWeatherItem> selectForecastWeather(
            @Param("parkId") Long parkId,
            @Param("forecastDays") Integer forecastDays
    );
}