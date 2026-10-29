package com.ruoyi.binZhou.wr.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.binZhou.wr.domain.vo.*;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.concurrent.*;

@Service
public class AgriAdviceService {
    private static final long TTL_MS = 5 * 60 * 1000L;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
            .withResolverStyle(ResolverStyle.STRICT);
    private final Map<String, Entry> cache = new LinkedHashMap<>();
    private final Semaphore permits = new Semaphore(4);
    @Autowired private AgriReportService reportService;
    @Autowired private AiDecisionService aiDecisionService;

    public AgriAdviceDTO generate(Long userId, AgriAdviceRequest request) {
        if (userId == null) throw new ServiceException("请先登录");
        if (request == null || request.getParkId() == null || request.getParkId() <= 0)
            throw new ServiceException("请选择有效园区");
        String begin = normalizeTime(request.getBeginTime(), false);
        String end = normalizeTime(request.getEndTime(), true);
        if ((begin == null) != (end == null)) throw new ServiceException("开始和结束时间必须同时提供或同时省略");
        if (begin != null && begin.compareTo(end) > 0) throw new ServiceException("开始时间不能晚于结束时间");
        String key = userId + ":" + request.getParkId() + ":" + begin + ":" + end + ":" + LocalDate.now();
        Entry entry;
        boolean owner = false;
        synchronized (cache) {
            long now = System.currentTimeMillis();
            cache.entrySet().removeIf(item -> item.getValue().future.isDone() && now - item.getValue().completedAt >= TTL_MS);
            entry = cache.get(key);
            if (entry != null && Boolean.TRUE.equals(request.getForce()) && entry.future.isDone()) {
                cache.remove(key);
                entry = null;
            }
            if (entry == null) {
                if (cache.size() >= 64) {
                    Iterator<Map.Entry<String, Entry>> iterator = cache.entrySet().iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().getValue().future.isDone()) { iterator.remove(); break; }
                    }
                    if (cache.size() >= 64) throw new ServiceException("AI服务繁忙，请稍后重试");
                }
                entry = new Entry();
                cache.put(key, entry);
                owner = true;
            }
        }
        if (owner) {
            boolean acquired = permits.tryAcquire();
            try {
                if (!acquired) throw new ServiceException("AI服务繁忙，请稍后重试");
                AgriReportDTO report = reportService.buildAgriReport(request.getParkId(), begin, end);
                if (report.getParkOptions() == null || report.getParkOptions().stream()
                        .noneMatch(park -> request.getParkId().equals(park.getId())))
                    throw new ServiceException("所选园区不存在或园区目录不可用");
                String context = buildContext(report, request.getParkId(), begin, end);
                AgriAdviceDTO result = aiDecisionService.generateAgriAdvice(context);
                entry.completedAt = System.currentTimeMillis();
                entry.future.complete(result);
            } catch (Exception e) {
                entry.future.completeExceptionally(e);
                synchronized (cache) { cache.remove(key, entry); }
            } finally {
                if (acquired) permits.release();
            }
        }
        try {
            return entry.future.get(35, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceException("AI建议请求已中断，请重试");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ServiceException) throw (ServiceException) e.getCause();
            throw new ServiceException("AI建议生成失败，请重试");
        } catch (TimeoutException e) {
            throw new ServiceException("AI建议生成超时，请稍后重试");
        }
    }

    private String normalizeTime(String value, boolean end) {
        if (value == null || value.trim().isEmpty()) return null;
        String text = value.trim();
        if (text.length() == 10) text += end ? " 23:59:59" : " 00:00:00";
        if (text.length() != 19) throw new ServiceException("统计时间格式不正确");
        try { return LocalDateTime.parse(text, TIME).format(TIME); }
        catch (RuntimeException e) { throw new ServiceException("统计时间格式不正确"); }
    }

    private String buildContext(AgriReportDTO report, Long parkId, String begin, String end) {
        JSONObject context = new JSONObject();
        context.put("crop", "冬枣");
        context.put("parkId", parkId);
        context.put("statisticsScope", begin == null ? "全部历史，不是当前实时风险" : "指定历史统计期");
        context.put("beginTime", begin);
        context.put("endTime", end);
        context.put("forecastReferenceDate", LocalDate.now().toString());
        WeatherReportDTO weather = report.getWeather();
        context.put("weatherWarnings", fields("total", weather.getTotal(), "levels", weather.getLevelStats()));
        DiseaseReportDTO disease = report.getDisease();
        context.put("disease", fields("total", disease.getTotal(), "warningCount", disease.getWarningCount(),
                "warningRatePercent", disease.getWarningRate(), "types", types(disease.getTypeStats())));
        PestReportDTO pest = report.getPest();
        context.put("pest", fields("total", pest.getTotal(), "totalCount", pest.getTotalCount(), "species", types(pest.getTopSpecies())));
        PhenologyReportDTO ph = report.getPhenology();
        List<Map<String, Object>> stages = new ArrayList<>();
        if (ph.getStageStats() != null) for (Map<String, Object> row : ph.getStageStats()) {
            if (row == null || stages.size() >= 8) continue;
            stages.add(fields("stage", safe(row.get("stage"), 60), "count", row.get("count")));
        }
        context.put("historicalPhenology", fields("total", ph.getTotal(), "dominant", safe(ph.getDominant(), 60), "stages", stages));
        context.put("taskTotalOnly", report.getTask().getTotal());
        LiveWeatherDTO live = report.getLive();
        context.put("historicalWeather", fields("hasData", live.getHasData(), "maxTempC", live.getMaxTemp(),
                "minTempC", live.getMinTemp(), "avgHumidityPercent", live.getAvgHumidity(), "precipitationMm", live.getPrecipitation()));
        context.put("currentForecastWithDates", safe(report.getForecast().getSummary(), 1800));
        List<String> quality = new ArrayList<>();
        if (report.getDataQualityNotes() != null) for (String note : report.getDataQualityNotes()) {
            if (quality.size() < 8) quality.add(safe(note, 300));
        }
        context.put("dataQualityNotes", quality);
        context.put("limitations", Arrays.asList("气象预警1低2中3高；历史预警不是当前告警。",
                "病害虫情仅覆盖可归属本园区的记录，识别次数不表示严重程度；虫数不作为风险阈值。",
                "历史物候可能包含模拟来源，不等于当前生育阶段。", "农事任务无可靠状态，不推断完成率或逾期。",
                "风速单位尚未核实，未提供风速；没有土壤墒情数据，不推断土壤缺水。",
                "未来天气是当前获取的带日期预报，不属于历史统计期。"));
        String json = JSON.toJSONString(context);
        if (json.length() > 12000) throw new ServiceException("报告上下文过长，请缩小统计范围");
        return json;
    }

    private List<Map<String, Object>> types(List<TypeStatDTO> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (rows != null) for (TypeStatDTO row : rows) {
            if (row != null && result.size() < 8) result.add(fields("name", safe(row.getName(), 60), "count", row.getCount()));
        }
        return result;
    }
    private Map<String, Object> fields(Object... pairs) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) result.put((String) pairs[i], pairs[i + 1]);
        return result;
    }
    private String safe(Object value, int limit) {
        if (!(value instanceof String)) return null;
        String text = ((String) value).replaceAll("[\\p{Cntrl}]", " ").trim();
        return text.length() <= limit ? text : text.substring(0, limit);
    }
    private static final class Entry {
        final CompletableFuture<AgriAdviceDTO> future = new CompletableFuture<>();
        volatile long completedAt;
    }
}
