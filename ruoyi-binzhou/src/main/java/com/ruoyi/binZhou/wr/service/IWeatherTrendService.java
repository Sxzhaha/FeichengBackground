package com.ruoyi.binZhou.wr.service;

import com.ruoyi.binZhou.wr.domain.vo.WeatherTrendVo;

/**
 * 天气趋势图服务接口
 */
public interface IWeatherTrendService
{
    /**
     * 查询天气趋势图数据
     *
     * @param parkId 园区ID
     * @param historyDays 历史天数
     * @param forecastDays 未来预报天数
     * @return 天气趋势图数据
     */
    WeatherTrendVo selectWeatherTrend(
            Long parkId,
            Integer historyDays,
            Integer forecastDays
    );
}
