package com.ruoyi.binZhou.wr.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.binZhou.wr.domain.vo.WeatherTrendVo;
import com.ruoyi.binZhou.wr.mapper.WeatherTrendMapper;
import com.ruoyi.binZhou.wr.service.IWeatherTrendService;
import com.ruoyi.binZhou.wr.service.support.WeatherRiskCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * 天气趋势图服务实现类
 *
 * 负责组合：
 * 1. 过去若干天历史天气实况
 * 2. 当前最新天气实况
 * 3. 未来若干天天气预报
 */
@Service
public class WeatherTrendServiceImpl implements IWeatherTrendService
{
    @Autowired
    private WeatherTrendMapper weatherTrendMapper;

    /**
     * 默认历史天数
     */
    private static final int DEFAULT_HISTORY_DAYS = 5;

    /**
     * 默认未来预报天数
     */
    private static final int DEFAULT_FORECAST_DAYS = 7;

    /**
     * 最大历史查询天数
     */
    private static final int MAX_HISTORY_DAYS = 30;

    /**
     * 最大未来预报天数
     */
    private static final int MAX_FORECAST_DAYS = 15;

    @Override
    public WeatherTrendVo selectWeatherTrend(
            Long parkId,
            Integer historyDays,
            Integer forecastDays)
    {
        Long safeParkId = validateParkId(parkId);
        int safeHistoryDays = normalizeHistoryDays(historyDays);
        int safeForecastDays = normalizeForecastDays(forecastDays);

        List<WeatherTrendVo.HistoryWeatherItem> history =
                weatherTrendMapper.selectHistoryWeather(
                        safeParkId,
                        safeHistoryDays
                );

        WeatherTrendVo.CurrentWeatherItem current =
                weatherTrendMapper.selectCurrentWeather(safeParkId);

        List<WeatherTrendVo.ForecastWeatherItem> forecast =
                weatherTrendMapper.selectForecastWeather(
                        safeParkId,
                        safeForecastDays
                );

        fillHistoryRiskProbability(history);
        fillCurrentRiskProbability(current);
        fillForecastRiskProbability(forecast);

        WeatherTrendVo result = new WeatherTrendVo();

        result.setToday(
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
        );

        result.setHistory(
                history == null
                        ? Collections.emptyList()
                        : history
        );

        result.setCurrent(current);

        result.setForecast(
                forecast == null
                        ? Collections.emptyList()
                        : forecast
        );

        return result;
    }

    private void fillHistoryRiskProbability(List<WeatherTrendVo.HistoryWeatherItem> history)
    {
        if (history == null)
        {
            return;
        }

        for (WeatherTrendVo.HistoryWeatherItem item : history)
        {
            item.setFrostProbability(
                    WeatherRiskCalculator.calculateFrostProbability(
                            item.getTemperature(),
                            item.getHumidity()
                    )
            );
            item.setHotWindProbability(
                    WeatherRiskCalculator.calculateHotWindProbability(
                            item.getTemperature(),
                            item.getHumidity(),
                            item.getWindSpeed()
                    )
            );
        }
    }

    private void fillCurrentRiskProbability(WeatherTrendVo.CurrentWeatherItem current)
    {
        if (current == null)
        {
            return;
        }

        current.setFrostProbability(
                WeatherRiskCalculator.calculateFrostProbability(
                        current.getTemperature(),
                        current.getHumidity()
                )
        );
        current.setHotWindProbability(
                WeatherRiskCalculator.calculateHotWindProbability(
                        current.getTemperature(),
                        current.getHumidity(),
                        current.getWindSpeed()
                )
        );
    }

    private void fillForecastRiskProbability(List<WeatherTrendVo.ForecastWeatherItem> forecast)
    {
        if (forecast == null)
        {
            return;
        }

        for (WeatherTrendVo.ForecastWeatherItem item : forecast)
        {
            item.setWindSpeed(resolveForecastWindSpeed(item));
            item.setFrostProbability(
                    WeatherRiskCalculator.calculateFrostProbability(
                            item.getTempMin(),
                            item.getHumidity()
                    )
            );
            item.setHotWindProbability(
                    WeatherRiskCalculator.calculateHotWindProbability(
                            item.getTempMax(),
                            item.getHumidity(),
                            item.getWindSpeed()
                    )
            );
        }
    }

    private BigDecimal resolveForecastWindSpeed(WeatherTrendVo.ForecastWeatherItem item)
    {
        BigDecimal windSpeedDay = extractWindSpeedDay(
                item.getRawForecastJson(),
                item.getDate()
        );
        if (windSpeedDay != null)
        {
            return WeatherRiskCalculator.kilometersPerHourToMetersPerSecond(windSpeedDay);
        }

        return WeatherRiskCalculator.windScaleToSpeed(item.getWindScaleDay());
    }

    private BigDecimal extractWindSpeedDay(String rawForecastJson, String forecastDate)
    {
        if (rawForecastJson == null || rawForecastJson.trim().isEmpty()
                || forecastDate == null || forecastDate.trim().isEmpty())
        {
            return null;
        }

        try
        {
            JSONObject root = JSON.parseObject(rawForecastJson);
            JSONArray daily = root == null ? null : root.getJSONArray("daily");
            if (daily == null)
            {
                return null;
            }

            for (int index = 0; index < daily.size(); index++)
            {
                JSONObject day = daily.getJSONObject(index);
                if (day == null || !forecastDate.equals(day.getString("fxDate")))
                {
                    continue;
                }

                BigDecimal windSpeed = day.getBigDecimal("windSpeedDay");
                return windSpeed != null && windSpeed.signum() >= 0 ? windSpeed : null;
            }
        }
        catch (RuntimeException ignored)
        {
            return null;
        }

        return null;
    }

    /**
     * 校验园区ID
     */
    private Long validateParkId(Long parkId)
    {
        if (parkId == null || parkId <= 0)
        {
            return 1L;
        }

        return parkId;
    }

    /**
     * 处理历史天数
     */
    private int normalizeHistoryDays(Integer historyDays)
    {
        if (historyDays == null || historyDays <= 0)
        {
            return DEFAULT_HISTORY_DAYS;
        }

        return Math.min(historyDays, MAX_HISTORY_DAYS);
    }

    /**
     * 处理未来预报天数
     */
    private int normalizeForecastDays(Integer forecastDays)
    {
        if (forecastDays == null || forecastDays <= 0)
        {
            return DEFAULT_FORECAST_DAYS;
        }

        return Math.min(forecastDays, MAX_FORECAST_DAYS);
    }
}
