package com.ruoyi.binZhou.wr.service.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule-based weather risk calculation used by the weather trend endpoint.
 */
public final class WeatherRiskCalculator
{
    private static final BigDecimal KILOMETERS_PER_HOUR_PER_METER_PER_SECOND = BigDecimal.valueOf(3.6D);
    private static final Pattern WIND_SCALE_NUMBER = Pattern.compile("\\d+");

    private WeatherRiskCalculator()
    {
    }

    public static Integer calculateFrostProbability(BigDecimal temperature, BigDecimal humidity)
    {
        if (temperature == null || humidity == null)
        {
            return null;
        }

        double temperatureValue = temperature.doubleValue();
        if (temperatureValue > 5D)
        {
            return 0;
        }

        int probability;
        if (temperatureValue <= -2D)
        {
            probability = 95;
        }
        else if (temperatureValue <= 0D)
        {
            probability = 80;
        }
        else if (temperatureValue <= 2D)
        {
            probability = 50;
        }
        else
        {
            probability = 20;
        }

        if (humidity.doubleValue() >= 85D)
        {
            probability += 10;
        }

        return clampProbability(probability);
    }

    public static Integer calculateHotWindProbability(
            BigDecimal temperature,
            BigDecimal humidity,
            BigDecimal windSpeed)
    {
        if (temperature == null || humidity == null || windSpeed == null)
        {
            return null;
        }

        double temperatureValue = temperature.doubleValue();
        if (temperatureValue < 32D)
        {
            return 0;
        }

        int probability;
        if (temperatureValue >= 38D)
        {
            probability = 85;
        }
        else if (temperatureValue >= 35D)
        {
            probability = 60;
        }
        else
        {
            probability = 30;
        }

        double humidityValue = humidity.doubleValue();
        if (humidityValue < 30D)
        {
            probability += 15;
        }
        else if (humidityValue < 50D)
        {
            probability += 10;
        }
        else if (humidityValue > 70D)
        {
            probability -= 20;
        }

        double windSpeedValue = windSpeed.doubleValue();
        if (windSpeedValue >= 3D)
        {
            probability += 10;
        }
        if (windSpeedValue >= 5D)
        {
            probability += 15;
        }

        return clampProbability(probability);
    }

    /**
     * QWeather Web API v7 wind speed is km/h; risk thresholds use m/s.
     */
    public static BigDecimal kilometersPerHourToMetersPerSecond(BigDecimal windSpeed)
    {
        if (windSpeed == null || windSpeed.signum() < 0)
        {
            return null;
        }
        return windSpeed.divide(KILOMETERS_PER_HOUR_PER_METER_PER_SECOND, 2, RoundingMode.HALF_UP);
    }

    /**
     * Uses the upper Beaufort level of a range as a conservative representative value.
     */
    public static BigDecimal windScaleToSpeed(String windScaleDay)
    {
        Integer scale = parseWindScaleUpperBound(windScaleDay);
        if (scale == null)
        {
            return null;
        }

        if (scale <= 0) return BigDecimal.valueOf(0.2D);
        if (scale == 1) return BigDecimal.valueOf(1.5D);
        if (scale == 2) return BigDecimal.valueOf(3.3D);
        if (scale == 3) return BigDecimal.valueOf(5.4D);
        if (scale == 4) return BigDecimal.valueOf(7.9D);
        if (scale == 5) return BigDecimal.valueOf(10.7D);
        if (scale == 6) return BigDecimal.valueOf(13.8D);
        return BigDecimal.valueOf(17.1D);
    }

    public static Integer parseWindScaleUpperBound(String windScaleDay)
    {
        if (windScaleDay == null || windScaleDay.trim().isEmpty())
        {
            return null;
        }

        String normalized = windScaleDay.trim()
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\uFF0D', '-');
        Matcher matcher = WIND_SCALE_NUMBER.matcher(normalized);
        Integer upperBound = null;
        while (matcher.find())
        {
            try
            {
                int value = Integer.parseInt(matcher.group());
                upperBound = upperBound == null ? value : Math.max(upperBound, value);
            }
            catch (NumberFormatException ignored)
            {
                return null;
            }
        }

        if (upperBound == null)
        {
            return null;
        }

        if (normalized.startsWith("<") || normalized.startsWith("\uFF1C") || normalized.startsWith("\u5C0F\u4E8E"))
        {
            upperBound = Math.max(0, upperBound - 1);
        }

        return upperBound;
    }

    private static int clampProbability(int probability)
    {
        return Math.max(0, Math.min(100, probability));
    }
}
