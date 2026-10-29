package com.ruoyi.binZhou.wr.service.support;

import java.util.Locale;

/**
 * Canonical weather indicator codes used by warning rules and live weather queries.
 */
public final class WarningIndicatorCode
{
    public static final String AIR_TEMP = "AIR_TEMP";
    public static final String AIR_HUMIDITY = "AIR_HUMIDITY";
    public static final String WIND_SPEED = "WIND_SPEED";

    private WarningIndicatorCode()
    {
    }

    public static String normalize(String indicatorCode)
    {
        if (indicatorCode == null || indicatorCode.trim().isEmpty())
        {
            return null;
        }

        String normalized = indicatorCode.trim().toUpperCase(Locale.ROOT);
        if (AIR_TEMP.equals(normalized) || "TEMPERATURE".equals(normalized))
        {
            return AIR_TEMP;
        }
        if (AIR_HUMIDITY.equals(normalized) || "HUMIDITY".equals(normalized))
        {
            return AIR_HUMIDITY;
        }
        if (WIND_SPEED.equals(normalized) || "WINDSPEED".equals(normalized))
        {
            return WIND_SPEED;
        }
        return null;
    }

    public static String unit(String indicatorCode)
    {
        String normalized = normalize(indicatorCode);
        if (AIR_TEMP.equals(normalized))
        {
            return "℃";
        }
        if (AIR_HUMIDITY.equals(normalized))
        {
            return "%";
        }
        if (WIND_SPEED.equals(normalized))
        {
            return "m/s";
        }
        return "";
    }
}
