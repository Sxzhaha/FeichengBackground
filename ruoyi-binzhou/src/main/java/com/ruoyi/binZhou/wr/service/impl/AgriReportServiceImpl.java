package com.ruoyi.binZhou.wr.service.impl;

import com.ruoyi.binZhou.wr.domain.vo.*;
import com.ruoyi.binZhou.wr.service.AgriReportService;
import com.ruoyi.binZhou.wr.service.support.AgriReportAssessment;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgriReportServiceImpl implements AgriReportService {

    private static final String EMPTY_TEXT = "当前统计范围内暂无相关监测数据，建议扩大查询时间范围或开展对应监测";
    private static final DecimalFormat DF1 = new DecimalFormat("0.0");
    private static final DecimalFormat DF2 = new DecimalFormat("0.00");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public AgriReportDTO buildAgriReport(Long parkId, String beginTime, String endTime) {
        if (parkId != null && parkId <= 0) {
            throw new com.ruoyi.common.exception.ServiceException("parkId必须为有效园区ID");
        }
        if (StringUtils.hasText(beginTime) != StringUtils.hasText(endTime)) {
            throw new com.ruoyi.common.exception.ServiceException("beginTime和endTime必须同时提供或同时省略");
        }
        // 补齐时分秒，统一完整时间格式
        String fullBegin = beginTime;
        String fullEnd = endTime;
        if (beginTime != null && beginTime.length() == 10) {
            fullBegin = beginTime + " 00:00:00";
        }
        if (endTime != null && endTime.length() == 10) {
            fullEnd = endTime + " 23:59:59";
        }
        if (StringUtils.hasText(fullBegin)) try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(java.time.format.ResolverStyle.STRICT);
            if (java.time.LocalDateTime.parse(fullBegin, formatter).isAfter(java.time.LocalDateTime.parse(fullEnd, formatter))) {
                throw new IllegalArgumentException("invalid range");
            }
        } catch (RuntimeException e) {
            throw new com.ruoyi.common.exception.ServiceException("统计时间格式错误或开始时间晚于结束时间");
        }
        System.out.println("===查询完整时间区间===");
        System.out.println("开始：" + fullBegin);
        System.out.println("结束：" + fullEnd);

        List<ParkOptionDTO> accessibleParks = buildParkOptions();
        List<Long> parkIds = accessibleParks.stream().map(ParkOptionDTO::getId)
                .filter(id -> parkId == null || parkId.equals(id)).collect(Collectors.toList());
        if (parkId != null && parkIds.isEmpty()) {
            throw new com.ruoyi.common.exception.ServiceException("园区不存在或无权访问该园区");
        }
        AgriReportAssessment.Evidence evidence = new AgriReportAssessment.Evidence();
        WeatherReportDTO weather = buildWeatherReport(parkIds, fullBegin, fullEnd, evidence);
        DiseaseReportDTO disease = buildDiseaseReport(parkIds, fullBegin, fullEnd, evidence);
        PestReportDTO pest = buildPestReport(parkIds, fullBegin, fullEnd, evidence);
        PhenologyReportDTO phenology = buildPhenologyReport(parkIds, fullBegin, fullEnd);
        TaskReportDTO task = buildTaskReport(parkIds, fullBegin, fullEnd);
        LiveWeatherDTO live = buildLiveWeather(parkIds, fullBegin, fullEnd);
        ForecastDTO forecast = buildForecast(parkIds);

        AgriReportDTO dto = new AgriReportDTO();
        dto.setWeather(weather);
        dto.setDisease(disease);
        dto.setPest(pest);
        dto.setPhenology(phenology);
        dto.setTask(task);
        dto.setLive(live);
        dto.setForecast(forecast);
        dto.setParkOptions(accessibleParks);
        dto.setDataQualityNotes(Arrays.asList(
                "仅统计具有可核实园区关联的记录，缺少关联的历史记录不会下放到所选园区。",
                "物候历史记录可能包含模拟来源，当前无法可靠识别全部来源。",
                "农事完成状态缺少可靠依据，完成、进行中、待执行和逾期指标暂不可统计。"));
        task.setFinished(null);
        task.setRunning(null);
        task.setPending(null);
        task.setOverdue(null);
        task.setFinishRate(null);
        AgriReportAssessment.apply(dto, evidence, !StringUtils.hasText(fullBegin));
        return dto;
    }

    private WeatherReportDTO buildWeatherReport(List<Long> parkIds, String beginTime, String endTime,
                                               AgriReportAssessment.Evidence evidence) {
        WeatherReportDTO dto = new WeatherReportDTO();
        dto.setHasData(false);
        dto.setTotal(0);
        dto.setLevelStats(buildLevelStats(0, 0, 0, 0));
        dto.setExtremes(emptyWeatherExtremes());
        dto.setTopRows(new ArrayList<>());

        StringBuilder sql = new StringBuilder();
        List<Object> args = new ArrayList<>();
        sql.append("select r.id, r.task_id, r.device_id, r.warning_type, r.warning_level, r.indicator_code, r.current_value, r.ai_suggestion, r.warning_time ");
        sql.append("from wr_warning_record r left join wr_warning_task t on t.id = r.task_id ");
        sql.append("left join wr_warning_template w on w.id = t.template_id where 1 = 1 ");
        appendTimeRange(sql, args, "r.warning_time", beginTime, endTime);
        appendParkScope(sql, args, "w.park_id", parkIds);
        sql.append("order by r.warning_time desc, r.id desc");
        List<Map<String, Object>> rows = query(sql.toString(), args);
        System.out.println("【气象预警查询行数】" + rows.size());
        int total = rows.size();
        int high = 0, medium = 0, low = 0, unmarked = 0;
        for (Map<String, Object> row : rows) {
            evidence.recordWeatherSuggestion(time(row.get("warning_time")), str(row, "ai_suggestion"));
            String level = str(row, "warning_level");
            if ("3".equals(level)) high++;
            else if ("2".equals(level)) medium++;
            else if ("1".equals(level)) low++;
            else unmarked++;
        }
        dto.setHasData(total > 0);
        dto.setTotal(total);
        dto.setLevelStats(buildLevelStats(high, medium, low, unmarked, total));
        WeatherExtremesDTO extremes = emptyWeatherExtremes();
        extremes.setMaxTemp(maxWarningValue(rows, "AIR_TEMP", "℃"));
        extremes.setMinSoilHum("-");
        fillLiveExtremes(extremes, parkIds, beginTime, endTime);
        dto.setExtremes(extremes);
        dto.setTopRows(rows.stream().limit(10).map(this::toWeatherRow).collect(Collectors.toList()));
        return dto;
    }

    // 病虫害
    private DiseaseReportDTO buildDiseaseReport(List<Long> parkIds, String beginTime, String endTime,
                                               AgriReportAssessment.Evidence evidence) {
        DiseaseReportDTO dto = new DiseaseReportDTO();
        dto.setHasData(false);
        dto.setTotal(0);
        dto.setWarningCount(0);
        dto.setWarningRate("0.00");
        dto.setAvgConfidence("0.00");
        dto.setTypeStats(new ArrayList<>());
        dto.setLevelStats(buildLevelStats(0, 0, 0, 0));
        dto.setTopRows(new ArrayList<>());

        StringBuilder diseaseSql = new StringBuilder(
                "select d.disease_id, d.disease_name, d.disease_result, d.disease_confidence, d.is_warning, d.warning_level, d.create_time " +
                        "from xwq_ai_disease d left join xwq_ai_record r on r.record_id = d.record_id " +
                        "where 1 = 1 ");
        List<Object> diseaseArgs = new ArrayList<>();
        appendParkScope(diseaseSql, diseaseArgs, "r.park_id", parkIds);
        appendTimeRange(diseaseSql, diseaseArgs, "d.create_time", beginTime, endTime);
        diseaseSql.append("order by d.create_time desc");
        List<Map<String, Object>> rows = query(diseaseSql.toString(), diseaseArgs);
        System.out.println("【病虫害查询行数】" + rows.size());
        int total = rows.size();
        dto.setTotal(total);
        dto.setHasData(total > 0);
        if (total == 0) return dto;

        int warningCount = 0;
        int high = 0, medium = 0, low = 0;
        BigDecimal confidenceSum = BigDecimal.ZERO;
        Map<String, Integer> typeMap = new LinkedHashMap<>();
        Map<String, List<Map<String, Object>>> diseaseStatMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String level = str(row, "warning_level");
            evidence.recordDisease(level, riskFlag(row));
            if (level.contains("高")) high++;
            else if (level.contains("中")) medium++;
            else if (level.contains("低")) low++;
            String diseaseName = text(str(row, "disease_name"), "未知病虫害");
            BigDecimal confidence = decimal(row, "disease_confidence");
            if (integer(row, "is_warning") == 1) warningCount++;
            confidenceSum = confidenceSum.add(confidence);

            // 病虫害重点统计过滤健康叶片
            if (!diseaseName.contains("健康")) {
                typeMap.put(diseaseName, typeMap.getOrDefault(diseaseName, 0) + 1);
                diseaseStatMap.computeIfAbsent(diseaseName, k -> new ArrayList<>()).add(row);
            }
        }
        dto.setWarningCount(warningCount);
        dto.setWarningRate(percent(warningCount, total));
        dto.setAvgConfidence(DF2.format(confidenceSum.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)));
        dto.setTypeStats(buildDiseaseTypeStats(diseaseStatMap, total, 6));
        dto.setLevelStats(buildLevelStats(high, medium, low, total));
        // 重点风险识别 TOP5：仅展示 is_warning=1 的异常病虫害，按出现次数排序
        Map<String, List<Map<String, Object>>> warningGroups = rows.stream()
                .filter(row -> integer(row, "is_warning") == 1)
                .collect(Collectors.groupingBy(
                        row -> text(str(row, "disease_name"), "未知病虫害"),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<Map<String, Object>> topWarningRows = warningGroups.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .limit(5)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("disease_id", entry.getValue().get(0).get("disease_id"));
                    item.put("disease_name", entry.getKey());
                    item.put("disease_confidence",
                            entry.getValue().stream()
                                    .map(r -> decimal(r, "disease_confidence"))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                                    .divide(BigDecimal.valueOf(entry.getValue().size()), 4, RoundingMode.HALF_UP));
                    item.put("warning_level", entry.getValue().stream()
                            .map(r -> str(r, "warning_level"))
                            .filter(v -> !v.isEmpty())
                            .findFirst().orElse(""));
                    // TOP5显示最近一次识别时间
                    item.put("create_time", entry.getValue().stream()
                            .map(r -> str(r, "create_time"))
                            .filter(v -> !v.isEmpty())
                            .max(String::compareTo)
                            .orElse(""));
                    return item;
                })
                .collect(Collectors.toList());

        dto.setTopRows(topWarningRows.stream()
                .map(this::toDiseaseRow)
                .collect(Collectors.toList()));
        return dto;
    }

    // 虫情：TRIM去除空格、>=<=替代between
    private PestReportDTO buildPestReport(List<Long> parkIds, String beginTime, String endTime,
                                         AgriReportAssessment.Evidence evidence) {
        PestReportDTO dto = new PestReportDTO();
        dto.setHasData(false);
        dto.setTotal(0);
        dto.setTotalCount(0);
        dto.setWarningCount(0);
        dto.setAvgConfidence("0.00");
        dto.setLevelStats(buildLevelStats(0, 0, 0, 0));
        dto.setTopSpecies(new ArrayList<>());

        StringBuilder sql = new StringBuilder("select p.pest_a as raw_time, TRIM(p.pest_a) as create_time, p.pest_id, p.pest_name, p.pest_confidence, p.pest_count, p.pest_total_count, p.is_warning, p.warning_level " +
                "from xwq_ai_pest p left join xwq_ai_record r on r.record_id = p.record_id " +
                "left join xwq_ai_image img on img.image_id = p.image_id " +
                "where 1 = 1 ");
        List<Object> args = new ArrayList<>();
        appendParkScope(sql, args, "coalesce(r.park_id, img.park_id)", parkIds);
        appendTimeRange(sql, args, "str_to_date(TRIM(p.pest_a), '%Y-%m-%d %H:%i:%s')", beginTime, endTime);
        sql.append("order by p.pest_id desc");
        List<Map<String, Object>> rows = query(sql.toString(), args);
        System.out.println("【虫情查询行数】" + rows.size());
        if (!rows.isEmpty()) {
            System.out.println("虫情第一条原始时间值：" + rows.get(0).get("raw_time"));
        }

        int total = rows.size();
        dto.setTotal(total);
        dto.setHasData(total > 0);
        if (total == 0) return dto;

        int totalCount = 0;
        int high = 0;
        int medium = 0;
        int low = 0;
        int unmarked = 0;
        BigDecimal confidenceSum = BigDecimal.ZERO;
        int confidenceCount = 0;
        Map<String, Integer> speciesMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            evidence.recordPest(str(row, "warning_level"), riskFlag(row));
            int pestCount = integer(row, "pest_count");
            int pestTotalCount = integer(row, "pest_total_count");
            int count = pestTotalCount > 0 ? pestTotalCount : pestCount;
            String pestName = text(str(row, "pest_name"), "未知虫害");
            BigDecimal confidence = decimal(row, "pest_confidence");
            if (confidence.compareTo(BigDecimal.ZERO) > 0) {
                confidenceSum = confidenceSum.add(confidence);
                confidenceCount++;
            }
            totalCount += count;
            speciesMap.put(pestName, speciesMap.getOrDefault(pestName, 0) + count);

            // 每条识别记录只归入一个等级。兼容中文、数字编码和英文值。
            String level = normalizeRiskLevel(str(row, "warning_level"));
            int isWarning = integer(row, "is_warning");
            if ("HIGH".equals(level)) {
                high++;
            } else if ("MEDIUM".equals(level)) {
                medium++;
            } else if ("LOW".equals(level)) {
                low++;
            } else {
                // is_warning=1 但等级缺失时无法可靠判断具体等级，仍归为未标记，
                // 避免错误地全部算作高风险。
                unmarked++;
            }
        }
        int warningCount = high + medium + low;
        dto.setTotalCount(totalCount);
        dto.setWarningCount(warningCount);
        dto.setLevelStats(buildLevelStats(high, medium, low, unmarked, total));
        if (confidenceCount > 0) {
            dto.setAvgConfidence(DF2.format(
                    confidenceSum.multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(confidenceCount), 4, RoundingMode.HALF_UP)
            ));
        }
        dto.setTopSpecies(buildTypeStats(speciesMap, Math.max(totalCount, 1), 3));
        return dto;
    }

    // 物候：TRIM去除空格、>=<=替代between
    private PhenologyReportDTO buildPhenologyReport(List<Long> parkIds, String beginTime, String endTime) {
        PhenologyReportDTO dto = new PhenologyReportDTO();
        dto.setHasData(false);
        dto.setTotal(0);
        dto.setDominant("-");
        dto.setAvgConfidence("0.00");
        dto.setGrowthJudgement(EMPTY_TEXT);

        StringBuilder sql = new StringBuilder("select ph.create_time as raw_time, ph.create_time, ph.phenology_name, ph.phenology_confidence " +
                "from xwq_ai_phenology ph left join xwq_ai_image img on img.image_id = ph.image_id " +
                "left join xwq_ai_record r on r.record_id = ph.record_id " +
                "where 1 = 1 ");
        List<Object> args = new ArrayList<>();
        appendParkScope(sql, args, "coalesce(img.park_id, r.park_id)", parkIds);
        appendTimeRange(sql, args, "ph.create_time", beginTime, endTime);
        sql.append("order by ph.create_time desc");
        List<Map<String, Object>> rows = query(sql.toString(), args);
        System.out.println("【物候查询行数】" + rows.size());
        if (!rows.isEmpty()) {
            System.out.println("物候第一条原始时间值：" + rows.get(0).get("raw_time"));
        }

        int total = rows.size();
        dto.setTotal(total);
        dto.setHasData(total > 0);
        if (total == 0) return dto;

        BigDecimal confidenceSum = BigDecimal.ZERO;
        Map<String, Integer> phenologyMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String phenologyName = text(str(row, "phenology_name"), "未知物候期");
            BigDecimal confidence = decimal(row, "phenology_confidence");
            confidenceSum = confidenceSum.add(confidence);
            phenologyMap.put(phenologyName, phenologyMap.getOrDefault(phenologyName, 0) + 1);
        }

        String dominant = phenologyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("-");
        dto.setDominant(dominant);

        // 根据数据库 xwq_ai_phenology 实际记录统计各物候阶段数量及占比
        List<Map<String, Object>> stageStats = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : phenologyMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("stage", entry.getKey());
            item.put("count", entry.getValue());

            BigDecimal rate = BigDecimal.valueOf(entry.getValue())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

            item.put("rate", rate + "%");
            stageStats.add(item);
        }

        // 按识别数量从高到低排列
        stageStats.sort((a, b) ->
                Integer.compare(
                        Integer.parseInt(String.valueOf(b.get("count"))),
                        Integer.parseInt(String.valueOf(a.get("count")))
                ));

        dto.setStageStats(stageStats);

        BigDecimal dominantRate = BigDecimal.valueOf(phenologyMap.get(dominant))
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        dto.setDominantRate(dominantRate.toString());

        dto.setAvgConfidence(DF2.format(confidenceSum.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)));
        dto.setGrowthJudgement("当前园区冬枣主导物候期为“" + dominant + "”，建议围绕该阶段开展水肥调控、病虫害防控和田间巡查。");
        return dto;
    }

    private TaskReportDTO buildTaskReport(List<Long> parkIds, String beginTime, String endTime) {
        TaskReportDTO dto = new TaskReportDTO();
        dto.setHasData(false);
        dto.setTotal(0);
        dto.setFinished(0);
        dto.setRunning(0);
        dto.setPending(0);
        dto.setOverdue(0);
        dto.setFinishRate("0.00");
        dto.setTypeStats(new ArrayList<>());
        dto.setOverdueRows(new ArrayList<>());

        StringBuilder taskSql = new StringBuilder();
        List<Object> taskArgs = new ArrayList<>();

        taskSql.append("select t.id, t.task_no, t.work_type_id, t.start_time, t.end_time, ");
        taskSql.append("t.description, t.operator_id, t.create_time ");
        taskSql.append("from ylm_agri_task t ");

        taskSql.append(" left join area a on t.area_id = a.area_id ");

        taskSql.append(" where 1 = 1 ");
        if (StringUtils.hasText(beginTime)) {
            taskSql.append(" and ((t.create_time between ? and ?) ");
            taskSql.append(" or (t.start_time <= ? and t.end_time >= ?)) ");
            taskArgs.add(beginTime);
            taskArgs.add(endTime);
            taskArgs.add(endTime);
            taskArgs.add(beginTime);
        }

        appendParkScope(taskSql, taskArgs, "a.park_id", parkIds);

        taskSql.append(" order by t.create_time desc");

        List<Map<String, Object>> rows = query(
                taskSql.toString(),
                taskArgs
        );
        System.out.println("【农事任务查询行数】" + rows.size());
        int total = rows.size();
        dto.setTotal(total);
        dto.setHasData(total > 0);
        if (total == 0) return dto;

        Map<String, Integer> typeMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String typeName = "农事类型" + text(str(row, "work_type_id"), "未知");
            typeMap.put(typeName, typeMap.getOrDefault(typeName, 0) + 1);
        }
        dto.setTypeStats(buildTypeStats(typeMap, total, 5));
        return dto;
    }

    private LiveWeatherDTO buildLiveWeather(List<Long> parkIds, String beginTime, String endTime) {
        LiveWeatherDTO dto = new LiveWeatherDTO();
        dto.setHasData(false);
        dto.setMaxTemp("-");
        dto.setMinTemp("-");
        dto.setAvgHumidity("-");
        dto.setPrecipitation("-");
        dto.setAvgWind("-");

        StringBuilder sql = new StringBuilder();
        List<Object> args = new ArrayList<>();

        sql.append("select max(temperature) max_temp, min(temperature) min_temp, ");
        sql.append("avg(humidity) avg_humidity, sum(precipitation) precipitation, ");
        sql.append("avg(wind_speed) avg_wind ");
        sql.append("from ld_agri_weather_live ");
        sql.append("where 1 = 1 ");
        appendTimeRange(sql, args, "fetch_time", beginTime, endTime);

        appendParkScope(sql, args, "park_id", parkIds);

        System.out.println("【实时气象查询】parkIds=" + parkIds
                + " 时间=" + beginTime + " ~ " + endTime);

        List<Map<String,Object>> rows = query(sql.toString(), args);

        if (CollectionUtils.isEmpty(rows)) {
            return dto;
        }

        Map<String,Object> row = rows.get(0);

        if (row.get("max_temp") == null) {
            return dto;
        }

        dto.setHasData(true);
        dto.setMaxTemp(format(row.get("max_temp"), 1));
        dto.setMinTemp(format(row.get("min_temp"), 1));
        dto.setAvgHumidity(format(row.get("avg_humidity"), 1));
        dto.setPrecipitation(format(row.get("precipitation"), 1));
        dto.setAvgWind(format(row.get("avg_wind"), 1));

        return dto;
    }


    /**
     * 查询所选园区从今天开始的未来三天天气。
     *
     * 注意：未来天气不使用报告的历史统计起止时间。
     * 同一园区、同一预报日期如果存在多次抓取记录，仅采用 fetch_time 最新的一条。
     */
    private ForecastDTO buildForecast(List<Long> parkIds) {
        if (parkIds.size() == 1) return buildParkForecast(parkIds.get(0));
        ForecastDTO dto = new ForecastDTO();
        List<String> summaries = new ArrayList<>();
        for (Long id : parkIds) {
            ForecastDTO parkForecast = buildParkForecast(id);
            summaries.add("园区ID " + id + "：" + parkForecast.getSummary());
        }
        dto.setSummary(summaries.isEmpty() ? "当前可访问园区范围内暂无未来天气数据"
                : String.join("；", summaries));
        return dto;
    }

    private ForecastDTO buildParkForecast(Long parkId) {
        ForecastDTO dto = new ForecastDTO();
        dto.setSummary(EMPTY_TEXT);

        if (parkId == null) {
            System.out.println("【未来天气查询】未选择园区，跳过查询");
            return dto;
        }

        String sql =
                "select f.forecast_date, f.temp_max, f.temp_min, f.precip_total, " +
                        "f.precip_prob, f.weather_text_day, f.forecast_type " +
                        "from ld_agri_weather_forecast f " +
                        "inner join (" +
                        "    select park_id, forecast_date, max(fetch_time) latest_fetch_time " +
                        "    from ld_agri_weather_forecast " +
                        "    where park_id = ? " +
                        "      and forecast_date >= curdate() " +
                        "      and forecast_date < date_add(curdate(), interval 3 day) " +
                        "    group by park_id, forecast_date" +
                        ") latest " +
                        "on f.park_id = latest.park_id " +
                        "and f.forecast_date = latest.forecast_date " +
                        "and f.fetch_time = latest.latest_fetch_time " +
                        "where f.park_id = ? " +
                        "order by f.forecast_date asc " +
                        "limit 3";

        List<Object> args = Arrays.asList(parkId, parkId);

        System.out.println("【未来天气查询】parkId=" + parkId + "，从今天起查询未来三天");

        List<Map<String, Object>> rows = query(sql, args);
        System.out.println("【未来天气查询行数】" + rows.size());

        if (CollectionUtils.isEmpty(rows)) {
            return dto;
        }

        String summary = rows.stream()
                .map(row -> time(row.get("forecast_date")) + " "
                        + text(str(row, "weather_text_day"), "天气待更新")
                        + "，气温" + format(row.get("temp_min"), 1)
                        + "至" + format(row.get("temp_max"), 1)
                        + "℃，降水概率" + format(row.get("precip_prob"), 1) + "%")
                .collect(Collectors.joining("；"));

        dto.setSummary(summary + "。");
        return dto;
    }


    /**
     * 从数据库 park 表读取园区下拉选项。
     *
     * park 表字段：
     * - park_id   园区主键
     * - park_name 园区名称
     */
    private List<ParkOptionDTO> buildParkOptions() {
        if (!tableExists("park")) {
            System.err.println("【园区查询失败】数据库中不存在 park 表");
            return new ArrayList<>();
        }

        Long userId = SecurityUtils.getUserId();
        boolean admin = SecurityUtils.isAdmin(userId);
        List<Map<String, Object>> rows = query(
                "select park_id as id, park_name as name " +
                        "from park " +
                        "where 1 = 1 " +
                        (admin ? "" : "and user_id = ? ") +
                        "order by park_id asc",
                admin ? new ArrayList<>() : new ArrayList<>(Collections.singletonList(userId))
        );

        System.out.println("【园区查询行数】" + rows.size());

        return rows.stream()
                .map(row -> {
                    ParkOptionDTO dto = new ParkOptionDTO();
                    dto.setId(longVal(row, "id"));
                    dto.setName(text(str(row, "name"), "未命名园区"));
                    return dto;
                })
                .filter(item -> item.getId() != null)
                .collect(Collectors.toList());
    }

    private DiseaseRowDTO toDiseaseRow(Map<String, Object> row) {
        DiseaseRowDTO dto = new DiseaseRowDTO();
        dto.setId(longVal(row, "disease_id"));
        dto.setName(text(str(row, "disease_name"), "未知病虫害"));
        dto.setConfidence(DF2.format(decimal(row, "disease_confidence").multiply(BigDecimal.valueOf(100))));
        String wLevel = str(row, "warning_level");
        if (wLevel.contains("高")) {
            dto.setLevelText("高风险");
            dto.setLevelClass("danger");
        } else if (wLevel.contains("中")) {
            dto.setLevelText("中风险");
            dto.setLevelClass("warning");
        } else if (wLevel.contains("低")) {
            dto.setLevelText("低风险");
            dto.setLevelClass("green");
        } else {
            dto.setLevelText("正常");
            dto.setLevelClass("green");
        }
        dto.setTime(time(row.get("create_time")));
        return dto;
    }

    private WeatherRowDTO toWeatherRow(Map<String, Object> row) {
        WeatherRowDTO dto = new WeatherRowDTO();
        dto.setId(longVal(row, "id"));
        dto.setTime(time(row.get("warning_time")));
        dto.setDevice("设备" + text(str(row, "device_id"), "-"));
        dto.setIndicator(indicatorName(str(row, "indicator_code")));
        dto.setValue(text(str(row, "current_value"), "-"));
        String level = str(row, "warning_level");
        if ("3".equals(level)) {
            dto.setLevelText("高风险");
            dto.setLevelClass("danger");
        } else if ("2".equals(level)) {
            dto.setLevelText("中风险");
            dto.setLevelClass("warning");
        } else if ("1".equals(level)) {
            dto.setLevelText("低风险");
            dto.setLevelClass("green");
        } else {
            dto.setLevelText("未标记");
            dto.setLevelClass("info");
        }
        dto.setSuggestion(shortText(text(str(row, "ai_suggestion"), "系统已记录该预警，请结合现场情况及时核查处置。"), 60));
        return dto;
    }

    private TaskRowDTO toTaskRow(Map<String, Object> row) {
        TaskRowDTO dto = new TaskRowDTO();
        dto.setId(longVal(row, "id"));
        dto.setName("任务" + text(str(row, "task_no"), text(str(row, "id"), "-")));
        dto.setEndTime(time(row.get("end_time")));
        dto.setOperator(text(str(row, "operator_id"), "未分配"));
        return dto;
    }

    private void fillLiveExtremes(WeatherExtremesDTO extremes, List<Long> parkIds, String beginTime, String endTime) {
        if (!tableExists("ld_agri_weather_live")) return;
        StringBuilder sql = new StringBuilder();
        List<Object> args = new ArrayList<>();
        sql.append("select max(temperature) max_temp, sum(precipitation) precipitation, max(wind_speed) max_wind ");
        sql.append("from ld_agri_weather_live where 1 = 1 ");
        appendTimeRange(sql, args, "fetch_time", beginTime, endTime);
        appendParkScope(sql, args, "park_id", parkIds);
        List<Map<String, Object>> rows = query(sql.toString(), args);
        if (CollectionUtils.isEmpty(rows)) return;
        Map<String, Object> row = rows.get(0);
        if ("-".equals(extremes.getMaxTemp())) extremes.setMaxTemp(format(row.get("max_temp"), 1) + "℃");
        extremes.setPrecipitation(format(row.get("precipitation"), 1) + "mm");
        extremes.setMaxWind(format(row.get("max_wind"), 1) + "m/s");
    }

    private WeatherExtremesDTO emptyWeatherExtremes() {
        WeatherExtremesDTO dto = new WeatherExtremesDTO();
        dto.setMaxTemp("-");
        dto.setMinSoilHum("-");
        dto.setPrecipitation("-");
        dto.setMaxWind("-");
        return dto;
    }

    private LevelStatsDTO buildLevelStats(int high, int medium, int low, int total) {
        int unmarked = Math.max(total - high - medium - low, 0);
        return buildLevelStats(high, medium, low, unmarked, total);
    }

    private LevelStatsDTO buildLevelStats(int high, int medium, int low, int unmarked, int total) {
        LevelStatsDTO dto = new LevelStatsDTO();
        dto.setHigh(high);
        dto.setMedium(medium);
        dto.setLow(low);
        dto.setUnmarked(unmarked);
        dto.setHighRate(percent(high, total));
        dto.setMediumRate(percent(medium, total));
        dto.setLowRate(percent(low, total));
        dto.setUnmarkedRate(percent(unmarked, total));
        return dto;
    }

    /**
     * 将数据库中的预警等级统一为 HIGH / MEDIUM / LOW。
     * 数字编码未验证，不推断其含义，归入未标记。
     */
    private String normalizeRiskLevel(String rawLevel) {
        if (!StringUtils.hasText(rawLevel)) return "";
        String level = rawLevel.trim().toLowerCase(Locale.ROOT);
        if (level.contains("高") || "high".equals(level) || "danger".equals(level)) {
            return "HIGH";
        }
        if (level.contains("中") || "medium".equals(level) || "warning".equals(level)) {
            return "MEDIUM";
        }
        if (level.contains("低") || "low".equals(level) || "green".equals(level)) {
            return "LOW";
        }
        return "";
    }

    private List<TypeStatDTO> buildDiseaseTypeStats(Map<String, List<Map<String, Object>>> map, int total, int limit) {
        if (map == null || map.isEmpty()) return new ArrayList<>();

        return map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .limit(limit)
                .map(entry -> {
                    TypeStatDTO dto = new TypeStatDTO();
                    List<Map<String, Object>> list = entry.getValue();

                    dto.setName(entry.getKey());
                    dto.setCount(list.size());
                    dto.setRate(percent(list.size(), total));

                    BigDecimal avg = list.stream()
                            .map(r -> decimal(r, "disease_confidence"))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(list.size()), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

                    dto.setAvgConfidence(DF2.format(avg));

                    dto.setLastTime(list.stream()
                            .map(r -> str(r, "create_time"))
                            .filter(v -> !v.isEmpty())
                            .max(String::compareTo)
                            .orElse("--"));

                    return dto;
                }).collect(Collectors.toList());
    }

    private List<TypeStatDTO> buildTypeStats(Map<String, Integer> map, int total, int limit) {
        if (map == null || map.isEmpty()) return new ArrayList<>();
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    TypeStatDTO dto = new TypeStatDTO();
                    dto.setName(entry.getKey());
                    dto.setCount(entry.getValue());
                    dto.setRate(percent(entry.getValue(), total));
                    return dto;
                }).collect(Collectors.toList());
    }


    private String maxWarningValue(List<Map<String, Object>> rows, String indicator, String unit) {
        Optional<BigDecimal> max = rows.stream()
                .filter(r -> indicator.equalsIgnoreCase(str(r, "indicator_code")))
                .map(r -> decimal(r, "current_value"))
                .max(Comparator.naturalOrder());
        return max.map(v -> DF1.format(v) + unit).orElse("-");
    }

    private String topName(List<TypeStatDTO> list) {
        return CollectionUtils.isEmpty(list) ? "-" : list.get(0).getName();
    }

    private String indicatorName(String code) {
        if ("AIR_TEMP".equalsIgnoreCase(code)) return "空气温度";
        if ("SOIL_HUM".equalsIgnoreCase(code)) return "土壤湿度";
        if ("SOIL_TEMP".equalsIgnoreCase(code)) return "土壤温度";
        return text(code, "未知指标");
    }

    private boolean tableExists(String tableName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "select count(1) from information_schema.tables where table_schema = database() and table_name = ?",
                    Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean hasColumn(String tableName, String columnName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "select count(1) from information_schema.columns where table_schema = database() and table_name = ? and column_name = ?",
                    Integer.class, tableName, columnName);
            return count != null && count > 0;
        } catch (Exception e) {
            return true;
        }
    }

    // Empty authorization scopes must never become unrestricted queries.
    private void appendParkScope(StringBuilder sql, List<Object> args, String column, List<Long> parkIds) {
        if (parkIds.isEmpty()) {
            sql.append("and 1 = 0 ");
            return;
        }
        sql.append("and ").append(column).append(" in (")
                .append(String.join(",", Collections.nCopies(parkIds.size(), "?"))).append(") ");
        args.addAll(parkIds);
    }

    // 打印SQL和参数，方便调试
    private void appendTimeRange(StringBuilder sql, List<Object> args, String column, String beginTime, String endTime) {
        if (StringUtils.hasText(beginTime)) {
            sql.append("and ").append(column).append(" between ? and ? ");
            args.add(beginTime);
            args.add(endTime);
        }
    }

    private List<Map<String, Object>> query(String sql, List<Object> args) {
        try {
            System.out.println("执行SQL：" + sql);
            System.out.println("参数：" + args);
            return jdbcTemplate.queryForList(sql, args.toArray());
        } catch (Exception e) {
            throw new com.ruoyi.common.exception.ServiceException("综合报表数据查询失败，请检查数据源及字段配置");
        }
    }

    private String percent(int numerator, int denominator) {
        if (denominator <= 0) return "0.00";
        return DF2.format(numerator * 100D / denominator);
    }

    private String format(Object value, int scale) {
        if (value == null) return "-";
        try {
            BigDecimal decimal = new BigDecimal(String.valueOf(value));
            return scale == 1 ? DF1.format(decimal) : DF2.format(decimal);
        } catch (Exception e) {
            return "-";
        }
    }

    private String str(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) value = row.get(toCamel(key));
        return value == null ? "" : String.valueOf(value);
    }

    private int integer(Map<String, Object> row, String key) {
        try {
            String v = str(row, key);
            return StringUtils.hasText(v) ? new BigDecimal(v).intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int riskFlag(Map<String, Object> row) {
        String flag = str(row, "is_warning").trim();
        return "1".equals(flag) ? 1 : "0".equals(flag) ? 0 : -1;
    }

    private Long longVal(Map<String, Object> row, String key) {
        try {
            String v = str(row, key);
            return StringUtils.hasText(v) ? new BigDecimal(v).longValue() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal decimal(Map<String, Object> row, String key) {
        try {
            String v = str(row, key);
            return StringUtils.hasText(v) ? new BigDecimal(v) : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Date date(Map<String, Object> row, String key) {
        Object val = row.get(key);
        if (val == null) val = row.get(toCamel(key));
        if (val instanceof Date) return (Date) val;
        if (val instanceof Timestamp) return new Date(((Timestamp) val).getTime());
        return null;
    }

    private String time(Object value) {
        if (value == null) return "-";
        String s = String.valueOf(value);
        return s.length() >= 19 ? s.substring(0, 19) : s;
    }

    private String text(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    private String shortText(String value, int length) {
        if (value == null) return "-";
        return value.length() <= length ? value : value.substring(0, length) + "...";
    }

    // 修复变量upper拼写错误，消除Cannot resolve symbol 'upper'报错
    private String toCamel(String underline) {
        if (underline == null || !underline.contains("_")) return underline;
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (char c : underline.toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else if (upperNext) {
                sb.append(String.valueOf(c).toUpperCase(Locale.ROOT));
                upperNext = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
