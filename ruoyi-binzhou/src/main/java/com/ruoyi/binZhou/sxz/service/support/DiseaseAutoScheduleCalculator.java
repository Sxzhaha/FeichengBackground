package com.ruoyi.binZhou.sxz.service.support;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTask;

public final class DiseaseAutoScheduleCalculator
{
    public static final String TYPE_FIXED = "fixed";
    public static final String TYPE_INTERVAL = "interval";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private DiseaseAutoScheduleCalculator()
    {
    }

    public static Date calculateNextRun(DiseaseAutoTask task, Date baseTime)
    {
        validate(task);
        if (baseTime == null)
        {
            baseTime = new Date();
        }

        LocalDateTime base = LocalDateTime.ofInstant(baseTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime next = TYPE_FIXED.equals(task.getScheduleType())
                ? calculateFixed(task, base)
                : calculateInterval(task, base);
        return Date.from(next.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static void validate(DiseaseAutoTask task)
    {
        if (task == null)
        {
            throw new IllegalArgumentException("任务配置不能为空");
        }
        if (!TYPE_FIXED.equals(task.getScheduleType()) && !TYPE_INTERVAL.equals(task.getScheduleType()))
        {
            throw new IllegalArgumentException("调度方式必须为fixed或interval");
        }

        LocalTime start = parseOptionalTime(task.getActiveStartTime());
        LocalTime end = parseOptionalTime(task.getActiveEndTime());
        if (start != null && end != null && start.isAfter(end))
        {
            throw new IllegalArgumentException("每日开始时间不能晚于结束时间");
        }

        if (TYPE_FIXED.equals(task.getScheduleType()))
        {
            List<LocalTime> times = parseFixedTimes(task.getFixedTimes());
            if (times.isEmpty())
            {
                throw new IllegalArgumentException("固定执行时间不能为空");
            }
            for (LocalTime time : times)
            {
                if ((start != null && time.isBefore(start)) || (end != null && time.isAfter(end)))
                {
                    throw new IllegalArgumentException("固定执行时间必须位于每日生效时间范围内");
                }
            }
        }
        else if (task.getIntervalMinutes() == null || task.getIntervalMinutes() < 1)
        {
            throw new IllegalArgumentException("间隔执行分钟数必须大于0");
        }
    }

    public static String normalizeFixedTimes(String fixedTimes)
    {
        List<LocalTime> times = parseFixedTimes(fixedTimes);
        List<String> normalized = new ArrayList<>();
        for (LocalTime time : times)
        {
            normalized.add(time.format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        return String.join(",", normalized);
    }

    private static LocalDateTime calculateFixed(DiseaseAutoTask task, LocalDateTime base)
    {
        List<LocalTime> times = parseFixedTimes(task.getFixedTimes());
        for (LocalTime time : times)
        {
            LocalDateTime candidate = LocalDateTime.of(base.toLocalDate(), time);
            if (candidate.isAfter(base))
            {
                return candidate;
            }
        }
        return LocalDateTime.of(base.toLocalDate().plusDays(1), times.get(0));
    }

    private static LocalDateTime calculateInterval(DiseaseAutoTask task, LocalDateTime base)
    {
        LocalDateTime candidate = base.plusMinutes(task.getIntervalMinutes());
        LocalTime start = parseOptionalTime(task.getActiveStartTime());
        LocalTime end = parseOptionalTime(task.getActiveEndTime());
        if (start == null || end == null)
        {
            return candidate;
        }
        if (candidate.toLocalTime().isBefore(start))
        {
            return LocalDateTime.of(candidate.toLocalDate(), start);
        }
        if (candidate.toLocalTime().isAfter(end))
        {
            return LocalDateTime.of(candidate.toLocalDate().plusDays(1), start);
        }
        return candidate;
    }

    private static List<LocalTime> parseFixedTimes(String value)
    {
        if (value == null || value.trim().isEmpty())
        {
            return Collections.emptyList();
        }
        Set<LocalTime> unique = new LinkedHashSet<>();
        for (String part : value.split(","))
        {
            if (!part.trim().isEmpty())
            {
                unique.add(parseTime(part));
            }
        }
        List<LocalTime> result = new ArrayList<>(unique);
        Collections.sort(result);
        return result;
    }

    private static LocalTime parseOptionalTime(String value)
    {
        return value == null || value.trim().isEmpty() ? null : parseTime(value);
    }

    private static LocalTime parseTime(String value)
    {
        String normalized = value.trim();
        if (normalized.length() == 5)
        {
            normalized += ":00";
        }
        try
        {
            return LocalTime.parse(normalized, TIME_FORMAT);
        }
        catch (DateTimeParseException e)
        {
            throw new IllegalArgumentException("时间格式必须为HH:mm或HH:mm:ss：" + value);
        }
    }
}
