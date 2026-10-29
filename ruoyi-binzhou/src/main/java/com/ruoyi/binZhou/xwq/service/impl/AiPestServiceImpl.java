package com.ruoyi.binZhou.xwq.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ruoyi.binZhou.xwq.mapper.AiImageMapper;
import com.ruoyi.binZhou.xwq.mapper.AiModelMapper;
import com.ruoyi.binZhou.xwq.mapper.AiRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.xwq.mapper.AiPestMapper;
import com.ruoyi.binZhou.xwq.domain.AiPest;
import com.ruoyi.binZhou.xwq.service.IAiPestService;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.domain.AiRecord;
import com.ruoyi.iot.ld.domain.AgriWeatherForecast;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.service.IAgriWeatherForecastService;
import com.ruoyi.iot.ld.service.IIotDeviceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Base64;
import java.util.UUID;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.springframework.util.StreamUtils;
/**
 * 虫情识别结果Service业务层处理
 *
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class AiPestServiceImpl implements IAiPestService
{
    private static final String DASHSCOPE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String[] REPORT_MARKERS = new String[] {
            "<<OVERALL>>",
            "<<DISTRIBUTION>>",
            "<<RISK>>",
            "<<ADVICE>>"
    };
    private static final BigDecimal REPORT_LOW_CONFIDENCE = new BigDecimal("0.60000");
    private static final int REPORT_RECORD_LIMIT = 1000;
    private static final int REPORT_SERIES_LIMIT = 30;
    private static final int REPORT_MIN_LENGTH = 60;
    private static final int REPORT_MAX_LENGTH = 8000;

    @Autowired
    private AiPestMapper aiPestMapper;

    @Autowired
    private AiImageMapper aiImageMapper;

    @Autowired
    private AiRecordMapper aiRecordMapper;

    @Autowired
    private AiModelMapper aiModelMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IAgriWeatherForecastService agriWeatherForecastService;

    @Autowired
    private IIotDeviceService iotDeviceService;

    @Value("${dashscope.apiKey}")
    private String dashscopeApiKey;

    @Value("${dashscope.model:qwen3.7-plus}")
    private String dashscopeModel;

    @Value("${ai-model.api-key:}")
    private String modelApiKey;

    /**
     * 查询虫情识别结果
     *
     * @param pestId 虫情识别结果主键
     * @return 虫情识别结果
     */
    @Override
    public AiPest selectAiPestByPestId(Long pestId)
    {
        return aiPestMapper.selectAiPestByPestId(pestId);
    }

    /**
     * 查询虫情识别结果列表
     *
     * @param aiPest 虫情识别结果
     * @return 虫情识别结果
     */
    @Override
    public List<AiPest> selectAiPestList(AiPest aiPest)
    {
        return aiPestMapper.selectAiPestList(aiPest);
    }

    /**
     * 新增虫情识别结果
     *
     * @param aiPest 虫情识别结果
     * @return 结果
     */
    @Override
    public int insertAiPest(AiPest aiPest)
    {
        if (aiPest.getPestA() == null || aiPest.getPestA().trim().isEmpty())
        {
            aiPest.setPestA(DateUtils.getTime());
        }
        return aiPestMapper.insertAiPest(aiPest);
    }

    /**
     * 修改虫情识别结果
     *
     * @param aiPest 虫情识别结果
     * @return 结果
     */
    @Override
    public int updateAiPest(AiPest aiPest)
    {
        return aiPestMapper.updateAiPest(aiPest);
    }

    /**
     * 批量删除虫情识别结果
     *
     * @param pestIds 需要删除的虫情识别结果主键
     * @return 结果
     */
    @Override
    public int deleteAiPestByPestIds(Long[] pestIds)
    {
        return aiPestMapper.deleteAiPestByPestIds(pestIds);
    }

    /**
     * 删除虫情识别结果信息
     *
     * @param pestId 虫情识别结果主键
     * @return 结果
     */
    @Override
    public int deleteAiPestByPestId(Long pestId)
    {
        return aiPestMapper.deleteAiPestByPestId(pestId);
    }

    /**
     * 根据勾选记录或筛选条件查询真实虫情数据并生成报告文本。
     */
    @Override
    public String generateReportText(Map<String, Object> params)
    {
        if (params == null)
        {
            throw new ServiceException("请求参数不能为空");
        }

        List<AiPest> records = queryPestReportRecords(params);
        if (records.isEmpty())
        {
            throw new ServiceException("未查询到虫情记录");
        }

        List<AiPest> validRecords = new ArrayList<AiPest>();
        for (AiPest record : records)
        {
            if (record != null && !isReportBlank(record.getPestName()))
            {
                validRecords.add(record);
            }
        }
        if (validRecords.isEmpty())
        {
            throw new ServiceException("查询结果中没有有效虫情记录");
        }
        if (validRecords.size() > REPORT_RECORD_LIMIT)
        {
            throw new ServiceException("单次生成报告最多支持" + REPORT_RECORD_LIMIT + "条有效记录");
        }

        Collections.sort(validRecords, new Comparator<AiPest>()
        {
            @Override
            public int compare(AiPest left, AiPest right)
            {
                Date leftTime = parsePestRecordTime(left.getPestA());
                Date rightTime = parsePestRecordTime(right.getPestA());
                if (leftTime == null && rightTime != null) return 1;
                if (leftTime != null && rightTime == null) return -1;
                if (leftTime != null)
                {
                    int result = leftTime.compareTo(rightTime);
                    if (result != 0) return result;
                }
                Long leftId = left.getPestId();
                Long rightId = right.getPestId();
                if (leftId == null) return rightId == null ? 0 : 1;
                return rightId == null ? -1 : leftId.compareTo(rightId);
            }
        });

        Map<String, Object> statistics = calculatePestReportStatistics(validRecords);
        String systemPrompt = buildPestReportSystemPrompt();
        String userPrompt = buildPestReportUserPrompt(statistics);

        if (isReportBlank(dashscopeApiKey))
        {
            throw new ServiceException("DashScope API Key为空");
        }

        for (int attempt = 0; attempt < 2; attempt++)
        {
            try
            {
                String content = cleanPestReportText(
                        callQwenPestReportText(systemPrompt, userPrompt)
                );
                String validationCode = validatePestReportMarkers(content);
                if (validationCode == null)
                {
                    return content;
                }
                if (!isRetryablePestReportValidation(validationCode) || attempt == 1)
                {
                    break;
                }
            }
            catch (ServiceException e)
            {
                throw e;
            }
            catch (ResourceAccessException e)
            {
                if (attempt == 1) break;
            }
            catch (HttpStatusCodeException e)
            {
                int status = e.getRawStatusCode();
                if (status == 401 || status == 403)
                {
                    throw new ServiceException("DashScope鉴权失败，HTTP " + status);
                }
                if (status < 500 || status >= 600 || attempt == 1) break;
            }
            catch (Exception e)
            {
                break;
            }
        }

        return buildFallbackPestReportText(statistics);
    }

    private List<AiPest> queryPestReportRecords(Map<String, Object> params)
    {
        AiPest query = new AiPest();
        List<Long> pestIds = parsePestReportIds(params.get("pestIds"));

        if (!pestIds.isEmpty())
        {
            query.getParams().put("pestIds", pestIds);
        }
        else
        {
            query.setParkId(getPestReportLongParam(params, "parkId"));
            query.setAreaId(getPestReportLongParam(params, "areaId"));
            query.setDeviceId(getPestReportLongParam(params, "deviceId"));
            query.setLocationId(getPestReportStringParam(params, "locationId", 50));
            query.setPestName(getPestReportStringParam(params, "pestName", 100));
            query.setWarningLevel(getPestReportStringParam(params, "warningLevel", 20));

            String startDate = getPestReportDateParam(params, "startDate");
            String endDate = getPestReportDateParam(params, "endDate");
            if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0)
            {
                throw new ServiceException("startDate不能晚于endDate");
            }
            if (startDate != null) query.getParams().put("startDate", startDate);
            if (endDate != null) query.getParams().put("endDate", endDate);
        }

        List<AiPest> records = aiPestMapper.selectAiPestList(query);
        return records == null ? new ArrayList<AiPest>() : records;
    }

    private List<Long> parsePestReportIds(Object value)
    {
        LinkedHashSet<Long> ids = new LinkedHashSet<Long>();
        if (value == null || value.toString().trim().isEmpty())
        {
            return new ArrayList<Long>();
        }

        List<Object> values = new ArrayList<Object>();
        if (value instanceof Iterable)
        {
            for (Object item : (Iterable<?>) value) values.add(item);
        }
        else if (value.getClass().isArray())
        {
            int length = java.lang.reflect.Array.getLength(value);
            for (int i = 0; i < length; i++) values.add(java.lang.reflect.Array.get(value, i));
        }
        else
        {
            String text = value.toString().trim();
            if (text.startsWith("[") && text.endsWith("]"))
            {
                text = text.substring(1, text.length() - 1);
            }
            if (!text.trim().isEmpty()) values.addAll(Arrays.<Object>asList(text.split(",")));
        }

        for (Object item : values)
        {
            if (item == null || item.toString().trim().isEmpty())
            {
                throw new ServiceException("pestIds包含空值");
            }
            try
            {
                Long id = Long.valueOf(item.toString().trim());
                if (id <= 0) throw new NumberFormatException();
                ids.add(id);
            }
            catch (Exception e)
            {
                throw new ServiceException("pestIds必须为正整数");
            }
        }

        if (ids.size() > REPORT_RECORD_LIMIT)
        {
            throw new ServiceException("pestIds最多支持" + REPORT_RECORD_LIMIT + "个");
        }
        return new ArrayList<Long>(ids);
    }

    private Long getPestReportLongParam(Map<String, Object> params, String key)
    {
        Object value = params.get(key);
        if (value == null || value.toString().trim().isEmpty()) return null;
        try
        {
            Long result = Long.valueOf(value.toString().trim());
            if (result <= 0) throw new NumberFormatException();
            return result;
        }
        catch (Exception e)
        {
            throw new ServiceException(key + "必须为正整数");
        }
    }

    private String getPestReportStringParam(Map<String, Object> params, String key, int maxLength)
    {
        Object value = params.get(key);
        if (value == null || value.toString().trim().isEmpty()) return null;
        String text = value.toString().trim();
        if (text.length() > maxLength)
        {
            throw new ServiceException(key + "长度不能超过" + maxLength + "个字符");
        }
        return text;
    }

    private String getPestReportDateParam(Map<String, Object> params, String key)
    {
        String value = getPestReportStringParam(params, key, 10);
        if (value == null) return null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try
        {
            Date date = format.parse(value);
            if (!value.equals(format.format(date))) throw new Exception();
            return value;
        }
        catch (Exception e)
        {
            throw new ServiceException(key + "格式不正确，应为yyyy-MM-dd");
        }
    }

    private Map<String, Object> calculatePestReportStatistics(List<AiPest> records)
    {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Long> pestCounts = new LinkedHashMap<String, Long>();
        Set<Long> imageIds = new LinkedHashSet<Long>();
        Set<String> crops = new LinkedHashSet<String>();
        Set<String> parks = new LinkedHashSet<String>();
        Set<String> areas = new LinkedHashSet<String>();
        Set<String> locations = new LinkedHashSet<String>();
        Set<String> devices = new LinkedHashSet<String>();
        BigDecimal confidenceTotal = BigDecimal.ZERO;
        BigDecimal minimumConfidence = null;
        int confidenceCount = 0;
        int lowConfidenceCount = 0;
        int highWarning = 0;
        int middleWarning = 0;
        int lowWarning = 0;
        int noWarning = 0;
        long totalPestCount = 0L;
        Date startTime = null;
        Date endTime = null;

        for (AiPest record : records)
        {
            String pestName = record.getPestName().trim();
            long count = getPestReportCount(record);
            Long previous = pestCounts.get(pestName);
            pestCounts.put(pestName, previous == null ? count : previous + count);
            totalPestCount += count;

            if (record.getImageId() != null) imageIds.add(record.getImageId());
            addPestReportText(crops, record.getPestCrop());
            addPestReportScope(parks, record.getParkName(), record.getParkId());
            addPestReportScope(areas, record.getAreaName(), record.getAreaId());
            addPestReportScope(locations, record.getLocationName(), record.getLocationId());
            addPestReportScope(devices, record.getDeviceName(), record.getDeviceId());
            String warning = record.getWarningLevel();
            if (warning != null && warning.contains("高")) highWarning++;
            else if (warning != null && warning.contains("中")) middleWarning++;
            else if (warning != null && warning.contains("低")) lowWarning++;
            else noWarning++;

            BigDecimal confidence = normalizePestReportConfidence(record.getPestConfidence());
            if (confidence != null)
            {
                confidenceTotal = confidenceTotal.add(confidence);
                confidenceCount++;
                if (minimumConfidence == null || confidence.compareTo(minimumConfidence) < 0)
                {
                    minimumConfidence = confidence;
                }
                if (confidence.compareTo(REPORT_LOW_CONFIDENCE) < 0) lowConfidenceCount++;
            }

            Date time = parsePestRecordTime(record.getPestA());
            if (time != null)
            {
                if (startTime == null || time.before(startTime)) startTime = time;
                if (endTime == null || time.after(endTime)) endTime = time;
            }
        }

        String dominantPest = null;
        long dominantCount = -1L;
        for (Map.Entry<String, Long> entry : pestCounts.entrySet())
        {
            if (entry.getValue() > dominantCount)
            {
                dominantPest = entry.getKey();
                dominantCount = entry.getValue();
            }
        }

        LinkedHashMap<String, String> distribution = new LinkedHashMap<String, String>();
        for (Map.Entry<String, Long> entry : pestCounts.entrySet())
        {
            distribution.put(
                    entry.getKey(),
                    entry.getValue() + "头（" + formatPestRatio(entry.getValue(), totalPestCount) + "）"
            );
        }

        BigDecimal averageConfidence = confidenceCount == 0 ? null
                : confidenceTotal.divide(BigDecimal.valueOf(confidenceCount), 5, RoundingMode.HALF_UP);
        BigDecimal dominantPercent = totalPestCount <= 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(dominantCount).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalPestCount), 2, RoundingMode.HALF_UP);
        String riskLevel;
        if (highWarning > 0 || totalPestCount >= 1000 || dominantPercent.compareTo(BigDecimal.valueOf(70)) >= 0)
        {
            riskLevel = "高风险";
        }
        else if (middleWarning > 0 || totalPestCount >= 300 || dominantPercent.compareTo(BigDecimal.valueOf(50)) >= 0)
        {
            riskLevel = "中风险";
        }
        else
        {
            riskLevel = "低风险";
        }

        result.put("validCount", records.size());
        result.put("imageCount", imageIds.size());
        result.put("cropTypes", crops.isEmpty() ? "未知" : joinPestReportText(crops));
        result.put("monitoringScope", buildPestMonitoringScope(parks, areas, locations, devices));
        result.put("startDate", formatPestReportDate(startTime));
        result.put("endDate", formatPestReportDate(endTime));
        result.put("totalPestCount", totalPestCount);
        result.put("distribution", distribution);
        result.put("dominantPest", dominantPest);
        result.put("dominantCount", dominantCount);
        result.put("dominantRatio", dominantPercent.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
        result.put("averageConfidence", formatPestConfidence(averageConfidence));
        result.put("minimumConfidence", formatPestConfidence(minimumConfidence));
        result.put("confidenceCount", confidenceCount);
        result.put("lowConfidenceCount", lowConfidenceCount);
        result.put("highWarning", highWarning);
        result.put("middleWarning", middleWarning);
        result.put("lowWarning", lowWarning);
        result.put("noWarning", noWarning);
        result.put("riskLevel", riskLevel);
        result.put("timeSeries", buildPestReportTimeSeries(records));
        return result;
    }

    private long getPestReportCount(AiPest record)
    {
        Long value = record.getPestTotalCount() != null
                ? record.getPestTotalCount() : record.getPestCount();
        return value == null ? 0L : Math.max(0L, value);
    }

    private BigDecimal normalizePestReportConfidence(BigDecimal confidence)
    {
        if (confidence == null || confidence.compareTo(BigDecimal.ZERO) < 0) return null;
        BigDecimal result = confidence;
        if (result.compareTo(BigDecimal.ONE) > 0 && result.compareTo(BigDecimal.valueOf(100)) <= 0)
        {
            result = result.divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP);
        }
        return result.compareTo(BigDecimal.ONE) > 0 ? null : result;
    }

    private Date parsePestRecordTime(String value)
    {
        if (isReportBlank(value)) return null;
        String text = value.trim();
        String[] patterns = new String[] { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
        for (String pattern : patterns)
        {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.setLenient(false);
            try
            {
                Date date = format.parse(text);
                if (text.equals(format.format(date))) return date;
            }
            catch (Exception ignored)
            {
            }
        }
        return null;
    }

    private List<String> buildPestReportTimeSeries(List<AiPest> records)
    {
        List<String> series = new ArrayList<String>();
        int start = Math.max(0, records.size() - REPORT_SERIES_LIMIT);
        for (int i = start; i < records.size(); i++)
        {
            AiPest record = records.get(i);
            series.add(
                    formatPestReportDate(parsePestRecordTime(record.getPestA()))
                            + "，" + record.getPestName().trim()
                            + "，虫体" + getPestReportCount(record) + "头"
                            + "，置信度" + formatPestConfidence(normalizePestReportConfidence(record.getPestConfidence()))
                            + "，预警" + (isReportBlank(record.getWarningLevel()) ? "无" : record.getWarningLevel().trim())
            );
        }
        return series;
    }

    private void addPestReportText(Set<String> values, String value)
    {
        if (!isReportBlank(value)) values.add(value.trim());
    }

    private void addPestReportScope(Set<String> values, String name, Object id)
    {
        if (!isReportBlank(name)) values.add(name.trim());
        else if (id != null) values.add("ID=" + id.toString());
    }

    private String buildPestMonitoringScope(Set<String> parks, Set<String> areas,
                                            Set<String> locations, Set<String> devices)
    {
        List<String> parts = new ArrayList<String>();
        if (!parks.isEmpty()) parts.add("园区：" + joinPestReportText(parks));
        if (!areas.isEmpty()) parts.add("地块：" + joinPestReportText(areas));
        if (!locations.isEmpty()) parts.add("位置：" + joinPestReportText(locations));
        if (!devices.isEmpty()) parts.add("设备：" + joinPestReportText(devices));
        return parts.isEmpty() ? "未知" : joinPestReportText(parts);
    }

    private String joinPestReportText(Iterable<String> values)
    {
        StringBuilder text = new StringBuilder();
        for (String value : values)
        {
            if (text.length() > 0) text.append("；");
            text.append(value);
        }
        return text.toString();
    }

    private String formatPestReportDate(Date date)
    {
        return date == null ? "未知" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    private String formatPestConfidence(BigDecimal confidence)
    {
        if (confidence == null) return "未知";
        return confidence.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
    }

    private String formatPestRatio(long count, long total)
    {
        if (total <= 0) return "0.00%";
        return BigDecimal.valueOf(count).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP).toPlainString() + "%";
    }

    private String buildPestReportSystemPrompt()
    {
        return "你是滨州智慧农业系统中的冬枣虫情监测报告生成助手。\n\n"
                + "报告事实只能来自输入中的当前监测统计；防治措施可以使用通用的冬枣绿色防控专业知识，但不得把常识写成已经发生的现场事实。\n\n"
                + "要求：\n"
                + "1. 不得修改或重新计算记录数量、虫体数量、比例、置信度、预警数量和日期。\n"
                + "2. 不得虚构气象、土壤、病虫害调查和防治效果数据。\n"
                + "3. 数据不足时明确说明“现有监测数据不足以判断”。\n"
                + "4. 风险研判必须基于输入的虫体数量、预警统计和时间序列；没有统一采样强度或田间虫口密度时，不得仅凭累计虫量断言种群密度或防治阈值已被突破。\n"
                + "5. 不依赖、复述或推定历史防治结论，必须从本次当前数据独立形成建议。\n"
                + "6. 防治建议必须与冬枣、主要虫害、当前风险等级和数据质量相匹配，优先采用监测复核、诱捕、物理或生物防控。\n"
                + "7. 每条建议应包含当前数据依据、拟采取的动作和后续核验指标；缺少必要前提时使用条件式表达。\n"
                + "8. 未提供物候期、防治阈值、药剂登记或现场复核结果时，不得给出具体药剂名称、剂量、施药次数或安全间隔期；确需化学防治时提示按当地登记标签和植保人员意见执行。\n"
                + "9. 建议生成3至5条。\n"
                + "10. 不输出JSON、HTML、Markdown代码块、开场白和结束语。\n"
                + "11. 严格按照以下标记顺序输出：\n\n"
                + "<<OVERALL>>\n<<DISTRIBUTION>>\n<<RISK>>\n<<ADVICE>>\n\n"
                + "除规定章节外不得输出其他内容。";
    }

    @SuppressWarnings("unchecked")
    private String buildPestReportUserPrompt(Map<String, Object> statistics)
    {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请严格使用以下Java统计结果生成虫情报告，不得修改任何真实数字。\n\n");
        prompt.append("【基础信息】\n")
                .append("作物类型：").append(statistics.get("cropTypes")).append("\n")
                .append("监测范围：").append(statistics.get("monitoringScope")).append("\n")
                .append("开始日期：").append(statistics.get("startDate")).append("\n")
                .append("结束日期：").append(statistics.get("endDate")).append("\n")
                .append("有效记录数量：").append(statistics.get("validCount")).append("\n")
                .append("图片数量：").append(statistics.get("imageCount")).append("\n\n");
        prompt.append("【统计结果】\n")
                .append("虫体总数：").append(statistics.get("totalPestCount")).append("头\n")
                .append("主要虫害：").append(statistics.get("dominantPest")).append("\n")
                .append("主要虫害数量：").append(statistics.get("dominantCount")).append("头\n")
                .append("主要虫害占比：").append(statistics.get("dominantRatio")).append("\n")
                .append("平均置信度：").append(statistics.get("averageConfidence")).append("\n")
                .append("最低置信度：").append(statistics.get("minimumConfidence")).append("\n")
                .append("低于60.00%的记录数量：").append(statistics.get("lowConfidenceCount")).append("\n")
                .append("高预警记录：").append(statistics.get("highWarning")).append("条\n")
                .append("中预警记录：").append(statistics.get("middleWarning")).append("条\n")
                .append("低预警记录：").append(statistics.get("lowWarning")).append("条\n")
                .append("无预警记录：").append(statistics.get("noWarning")).append("条\n")
                .append("Java规则风险等级：").append(statistics.get("riskLevel")).append("\n\n");

        prompt.append("【主要虫种分布】\n");
        Map<String, String> distribution = (Map<String, String>) statistics.get("distribution");
        for (Map.Entry<String, String> entry : distribution.entrySet())
        {
            prompt.append(entry.getKey()).append("：").append(entry.getValue()).append("\n");
        }
        prompt.append("\n【时间序列】\n统计使用全部有效记录，以下最多展示最近")
                .append(REPORT_SERIES_LIMIT).append("条并已按时间升序排列。\n");
        for (String item : (List<String>) statistics.get("timeSeries"))
        {
            prompt.append(item).append("\n");
        }
        prompt.append("\n【建议生成原则】\n")
                .append("本次不提供历史防治建议。请只依据以上当前监测数据独立研判；历史结论不是生成建议的前提。\n")
                .append("建议应明确对应主要虫害、风险等级、预警情况和置信度质量，并说明实施后继续观察什么指标。\n")
                .append("必须原样采用上述记录数量、虫体数量、比例、置信度、预警数量和日期。")
                .append("只输出四个规定章节，建议3至5条。");
        return prompt.toString();
    }

    private String callQwenPestReportText(String systemPrompt, String userPrompt)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + dashscopeApiKey.trim());

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", dashscopeModel);
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);
        messages.add(userMessage);
        requestBody.put("messages", messages);

        HttpEntity<String> entity = new HttpEntity<String>(requestBody.toJSONString(), headers);
        ResponseEntity<String> response;
        try
        {
            response = restTemplate.postForEntity(DASHSCOPE_URL, entity, String.class);
        }
        catch (HttpStatusCodeException e)
        {
            int status = e.getRawStatusCode();
            if (status == 401 || status == 403)
            {
                throw new ServiceException("DashScope鉴权失败，HTTP " + status);
            }
            throw e;
        }

        int status = response.getStatusCodeValue();
        if (status == 401 || status == 403)
        {
            throw new ServiceException("DashScope鉴权失败，HTTP " + status);
        }
        if (status >= 500 && status < 600)
        {
            throw new ResourceAccessException("DashScope服务异常，HTTP " + status);
        }
        if (!response.getStatusCode().is2xxSuccessful() || isReportBlank(response.getBody())) return null;

        JSONObject responseJson = JSONObject.parseObject(response.getBody());
        JSONArray choices = responseJson.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) return null;
        JSONObject choice = choices.getJSONObject(0);
        JSONObject message = choice == null ? null : choice.getJSONObject("message");
        return message == null ? null : message.getString("content");
    }

    private String cleanPestReportText(String content)
    {
        return content == null ? null
                : content.replaceAll("(?i)```(?:text|markdown|json)?", "").trim();
    }

    private String validatePestReportMarkers(String content)
    {
        if (isReportBlank(content)) return "EMPTY_CONTENT";
        if (content.length() < REPORT_MIN_LENGTH || content.length() > REPORT_MAX_LENGTH) return "INVALID_LENGTH";

        int previous = -1;
        for (String marker : REPORT_MARKERS)
        {
            int index = content.indexOf(marker);
            if (index < 0) return "MISSING_MARKER";
            if (content.indexOf(marker, index + marker.length()) >= 0) return "DUPLICATE_MARKER";
            if (index <= previous) return "WRONG_ORDER";
            previous = index;
        }
        if (!content.startsWith(REPORT_MARKERS[0])) return "EXTRA_CONTENT";
        for (int i = 0; i < REPORT_MARKERS.length; i++)
        {
            int start = content.indexOf(REPORT_MARKERS[i]) + REPORT_MARKERS[i].length();
            int end = i == REPORT_MARKERS.length - 1 ? content.length() : content.indexOf(REPORT_MARKERS[i + 1]);
            if (end <= start || content.substring(start, end).trim().isEmpty()) return "EMPTY_SECTION";
        }
        return null;
    }

    private boolean isRetryablePestReportValidation(String code)
    {
        return "EMPTY_CONTENT".equals(code) || "MISSING_MARKER".equals(code) || "WRONG_ORDER".equals(code);
    }

    @SuppressWarnings("unchecked")
    private String buildFallbackPestReportText(Map<String, Object> statistics)
    {
        StringBuilder report = new StringBuilder();
        report.append("<<OVERALL>>\n本次基于")
                .append(statistics.get("validCount")).append("条有效虫情记录和")
                .append(statistics.get("imageCount")).append("张图片生成报告，作物类型为")
                .append(statistics.get("cropTypes")).append("，监测范围为")
                .append(statistics.get("monitoringScope")).append("，监测时间为")
                .append(statistics.get("startDate")).append("至")
                .append(statistics.get("endDate")).append("。累计虫体")
                .append(statistics.get("totalPestCount")).append("头，主要虫害为")
                .append(statistics.get("dominantPest")).append("，共")
                .append(statistics.get("dominantCount")).append("头，占")
                .append(statistics.get("dominantRatio")).append("。\n\n");

        report.append("<<DISTRIBUTION>>\n");
        Map<String, String> distribution = (Map<String, String>) statistics.get("distribution");
        for (Map.Entry<String, String> entry : distribution.entrySet())
        {
            report.append(entry.getKey()).append("：").append(entry.getValue()).append("。\n");
        }

        report.append("\n<<RISK>>\n综合风险等级为").append(statistics.get("riskLevel"))
                .append("。高预警").append(statistics.get("highWarning")).append("条，中预警")
                .append(statistics.get("middleWarning")).append("条，低预警")
                .append(statistics.get("lowWarning")).append("条，无预警")
                .append(statistics.get("noWarning")).append("条。平均置信度为")
                .append(statistics.get("averageConfidence")).append("，最低置信度为")
                .append(statistics.get("minimumConfidence")).append("，低于60.00%的记录有")
                .append(statistics.get("lowConfidenceCount")).append("条。未提供气象、土壤和田间调查数据，不对这些因素作推断。\n\n");

        report.append("<<ADVICE>>\n");
        List<String> advice = new ArrayList<String>();
        String pestName = String.valueOf(statistics.get("dominantPest"));
        advice.add("当前主要虫害为“" + pestName + "”（"
                + statistics.get("dominantCount") + "头，占" + statistics.get("dominantRatio")
                + "），应在对应监测范围加密巡查并人工复核虫种、虫口数量和受害部位，复核结果作为是否升级防控的依据。");
        advice.add("当前规则风险等级为" + statistics.get("riskLevel") + "，并有高预警"
                + statistics.get("highWarning") + "条、中预警" + statistics.get("middleWarning")
                + "条；应优先采用诱捕、清除虫源、物理阻隔或适用的生物防控措施，实施后比较同一设备和同一采样口径下的虫量及预警变化。");
        if (((Number) statistics.get("confidenceCount")).intValue() == 0)
        {
            advice.add("当前记录没有有效置信度，无法评估识别结果的稳定性，应先人工复核虫种和原始图像，再决定是否升级防控措施。");
        }
        else if (((Number) statistics.get("lowConfidenceCount")).intValue() > 0)
        {
            advice.add("低于60.00%置信度的记录有" + statistics.get("lowConfidenceCount")
                    + "条，应优先复核这些图像和识别结果，避免将低置信度数据直接作为施药依据。");
        }
        advice.add("现有数据未包含统一采样强度、田间虫口密度和防治阈值，不能仅凭累计虫量判断需要施药；若现场复核确认达到当地防治指标，应按当地登记标签和植保人员意见选择药剂并执行安全间隔期。");
        advice.add("继续在同一监测范围、设备和采样口径下采集数据，重点跟踪“" + pestName
                + "”虫量、占比及预警等级是否连续上升，并据此调整后续措施。");
        int count = Math.min(5, advice.size());
        for (int i = 0; i < count; i++)
        {
            report.append(i + 1).append(". ").append(advice.get(i));
            if (i < count - 1) report.append("\n");
        }
        return report.toString().trim();
    }

    private boolean isReportBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }


    /**
     * 识别临时上传图片。
     *
     * 处理顺序：
     * 1. 使用临时图片URL调用模型；
     * 2. 模型失败时不写任何业务表，并删除临时原图；
     * 3. 模型成功后，在同一事务中依次写入图片表、记录表、虫情结果表；
     * 4. 任意数据库写入失败，三张表全部回滚，并删除临时原图和结果图。
     *
     * 本方法不修改现有 predictByImage，避免影响设备图片和自动识别链路。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> predictUploadedImage(
            Map<String, Object> params)
    {
        String imageUrl = getRequiredStringParam(
                params,
                "imageUrl",
                "imageUrl不能为空"
        );
        Long modelId = getRequiredLongParam(
                params,
                "modelId",
                "modelId不能为空，请先选择虫情识别模型"
        );

        String imageName = getStringParam(
                params,
                "imageName",
                extractFileName(imageUrl)
        );
        String imageFormat = getStringParam(
                params,
                "imageFormat",
                extractFileSuffix(imageName)
        );
        String cropType = getStringParam(
                params,
                "cropType",
                "冬枣"
        );
        String recordMode = getStringParam(
                params,
                "recordMode",
                "手动上传"
        );
        String relativePath = getStringParam(
                params,
                "relativePath",
                ""
        );
        Long imageSize = getOptionalLongParam(
                params,
                "imageSize"
        );
        Long deviceId = getOptionalLongParam(
                params,
                "deviceId"
        );

        String resultImageUrlForCleanup = null;

        try
        {
            IotDevice device = resolveOptionalDevice(deviceId);
            AiModel model = aiModelMapper.selectAiModelByModelId(
                    modelId
            );

            validatePestModel(model, modelId);

            String responseBody = callPestModel(
                    model,
                    imageUrl,
                    System.currentTimeMillis(),
                    imageName
            );

            if (responseBody == null
                    || responseBody.trim().isEmpty())
            {
                throw new ServiceException("模型服务返回为空");
            }

            JSONObject modelResult = parseModelResult(
                    responseBody
            );

            Boolean success = modelResult.getBoolean("success");
            if (Boolean.FALSE.equals(success))
            {
                String modelMessage = modelResult.getString(
                        "message"
                );
                throw new ServiceException(
                        modelMessage == null
                                || modelMessage.trim().isEmpty()
                                ? "模型识别失败"
                                : modelMessage
                );
            }

            String pestName = modelResult.getString(
                    "pestName"
            );
            if (pestName == null || pestName.trim().isEmpty())
            {
                pestName = modelResult.getString("name");
            }
            if (pestName == null || pestName.trim().isEmpty())
            {
                throw new ServiceException(
                        "模型返回缺少虫害名称，不能判定为识别成功"
                );
            }

            BigDecimal confidence = getConfidence(
                    modelResult
            );
            Long pestCount = getLongValue(
                    modelResult,
                    "pestCount",
                    "count",
                    1L
            );
            Long pestTotalCount = getLongValue(
                    modelResult,
                    "pestTotalCount",
                    "totalCount",
                    pestCount
            );

            String pestResult = modelResult.getString(
                    "pestResult"
            );
            if (pestResult == null
                    || pestResult.trim().isEmpty())
            {
                pestResult = "识别到虫害："
                        + pestName
                        + "，数量："
                        + pestTotalCount;
            }

            String pestAdvice = modelResult.getString(
                    "pestAdvice"
            );
            if (pestAdvice == null
                    || pestAdvice.trim().isEmpty())
            {
                pestAdvice = "建议结合田间虫口密度、作物生育期"
                        + "和历史虫情变化进行综合判断，必要时采取"
                        + "绿色防控或药剂防治措施。";
            }

            String pestResultImgUrl = modelResult.getString(
                    "pestResultImgUrl"
            );
            if (pestResultImgUrl == null
                    || pestResultImgUrl.trim().isEmpty())
            {
                pestResultImgUrl = modelResult.getString(
                        "resultImageUrl"
                );
            }

            String resultImageBase64 = modelResult.getString(
                    "resultImageBase64"
            );
            if (resultImageBase64 == null
                    || resultImageBase64.trim().isEmpty())
            {
                resultImageBase64 = modelResult.getString(
                        "image_base64"
                );
            }
            if (resultImageBase64 != null
                    && !resultImageBase64.trim().isEmpty())
            {
                pestResultImgUrl = savePestResultImage(
                        resultImageBase64,
                        imageName
                );
                resultImageUrlForCleanup = pestResultImgUrl;
            }

            String bboxJson = modelResult.getString(
                    "bboxJson"
            );
            if (bboxJson == null || bboxJson.trim().isEmpty())
            {
                bboxJson = modelResult.getString("boxes");
            }

            /*
             * 模型已成功，下面开始写入三张业务表。
             * 此方法由同一个 Spring 事务管理。
             */
            AiImage image = new AiImage();
            image.setImageName(imageName);
            image.setImageUrl(imageUrl);
            image.setImageResultUrl(pestResultImgUrl);
            image.setImageAiType("pest");
            image.setImageSource("手动上传");
            image.setImageSize(imageSize);
            image.setImageFormat(imageFormat);
            image.setImageStatus(2L);
            image.setImageA(cropType);
            image.setCreateBy(getCurrentUsername());
            image.setCreateTime(DateUtils.getNowDate());
            applyDeviceBinding(image, device);

            if (relativePath != null
                    && !relativePath.trim().isEmpty())
            {
                image.setRemark(
                        "来源文件夹路径：" + relativePath
                );
            }
            else
            {
                image.setRemark(
                        "智能分析页面手动上传识别成功"
                );
            }

            int imageRows = aiImageMapper.insertAiImage(image);
            if (imageRows <= 0 || image.getImageId() == null)
            {
                throw new ServiceException(
                        "图片信息写入失败"
                );
            }

            AiRecord record = new AiRecord();
            record.setRecordNo(
                    "PEST" + System.currentTimeMillis()
            );
            record.setRecordType("虫情");
            record.setRecordMode(recordMode);
            record.setModelId(model.getModelId());
            record.setModelName(model.getModelName());
            record.setImageId(image.getImageId());
            record.setCropType(cropType);
            record.setRecordStatus(2L);
            record.setCreateBy(getCurrentUsername());
            record.setCreateTime(DateUtils.getNowDate());
            applyDeviceBinding(record, device);
            record.setRemark(
                    "临时上传图片识别成功后创建"
            );

            int recordRows = aiRecordMapper.insertAiRecord(
                    record
            );
            if (recordRows <= 0 || record.getRecordId() == null)
            {
                throw new ServiceException(
                        "识别记录写入失败"
                );
            }

            AiPest pest = new AiPest();
            pest.setRecordId(record.getRecordId());
            pest.setImageId(image.getImageId());
            pest.setPestName(pestName);
            pest.setPestCrop(cropType);
            pest.setPestConfidence(confidence);
            pest.setPestCount(pestCount);
            pest.setPestTotalCount(pestTotalCount);
            pest.setPestResult(pestResult);
            pest.setPestAdvice(pestAdvice);
            pest.setPestResultImgUrl(pestResultImgUrl);
            pest.setBboxJson(bboxJson);
            pest.setIsWarning(0L);
            pest.setWarningLevel("无");
            pest.setPestA(DateUtils.getTime());

            int pestRows = aiPestMapper.insertAiPest(pest);
            if (pestRows <= 0 || pest.getPestId() == null)
            {
                throw new ServiceException(
                        "虫情识别结果写入失败"
                );
            }

            Map<String, Object> result = new HashMap<>();
            result.put("pestId", pest.getPestId());
            result.put("recordId", record.getRecordId());
            result.put("imageId", image.getImageId());
            result.put("imageName", image.getImageName());
            result.put("imageUrl", image.getImageUrl());
            result.put(
                    "imageResultUrl",
                    image.getImageResultUrl()
            );
            result.put("pestName", pestName);
            result.put("pestConfidence", confidence);
            result.put("pestCount", pestCount);
            result.put(
                    "pestTotalCount",
                    pestTotalCount
            );
            result.put("pestResult", pestResult);
            result.put("pestAdvice", pestAdvice);
            result.put(
                    "pestResultImgUrl",
                    pestResultImgUrl
            );
            result.put("bboxJson", bboxJson);
            result.put("pestDetails", modelResult.get("pestDetails"));
            result.put("counts", modelResult.get("counts"));
            result.put("warningLevel", "无");
            result.put("deviceId", device == null ? null : device.getDeviceId());
            result.put("deviceName", device == null ? null : device.getDeviceName());
            result.put("deviceSn", device == null ? null : device.getDeviceSn());
            result.put("parkId", device == null ? null : device.getParkId());
            result.put("parkName", device == null ? null : device.getParkName());
            result.put("areaId", device == null ? null : device.getAreaId());
            result.put("areaName", device == null ? null : device.getAreaName());
            result.put("locationId", device == null ? null : device.getLocationId());
            result.put("locationName", device == null ? null : device.getLocationName());
            result.put("recordMode", recordMode);

            return result;
        }
        catch (Exception e)
        {
            /*
             * 只清理手动上传的物理文件。
             * 数据库写入由 @Transactional 自动回滚。
             */
            deleteProfileFileQuietly(imageUrl);

            if (resultImageUrlForCleanup != null
                    && !resultImageUrlForCleanup.equals(imageUrl))
            {
                deleteProfileFileQuietly(
                        resultImageUrlForCleanup
                );
            }

            if (e instanceof ServiceException)
            {
                throw (ServiceException) e;
            }

            throw new ServiceException(
                    "虫情识别失败：" + e.getMessage()
            );
        }
    }

    private void validatePestModel(
            AiModel model,
            Long modelId)
    {
        if (model == null)
        {
            throw new ServiceException(
                    "模型不存在，modelId=" + modelId
            );
        }

        if (model.getModelStatus() == null
                || model.getModelStatus() == 0)
        {
            throw new ServiceException(
                    "当前模型已停用，不能用于识别"
            );
        }

        if (model.getModelType() != null
                && !model.getModelType().trim().isEmpty()
                && !"虫情".equals(model.getModelType().trim()))
        {
            throw new ServiceException(
                    "当前模型不是虫情识别模型"
            );
        }

        if (model.getModelUrl() == null
                || model.getModelUrl().trim().isEmpty())
        {
            throw new ServiceException(
                    "模型接口地址为空，请先在模型管理中配置 model_url"
            );
        }
        if (model.getModelCode() == null || model.getModelCode().trim().isEmpty())
        {
            throw new ServiceException("模型编码为空，请先在模型管理中配置 model_code");
        }
    }

    private String getRequiredStringParam(
            Map<String, Object> params,
            String key,
            String errorMessage)
    {
        Object value = params == null ? null : params.get(key);

        if (value == null
                || value.toString().trim().isEmpty())
        {
            throw new ServiceException(errorMessage);
        }

        return value.toString().trim();
    }

    private Long getRequiredLongParam(
            Map<String, Object> params,
            String key,
            String errorMessage)
    {
        Object value = params == null ? null : params.get(key);

        if (value == null
                || value.toString().trim().isEmpty())
        {
            throw new ServiceException(errorMessage);
        }

        try
        {
            return Long.valueOf(value.toString());
        }
        catch (Exception e)
        {
            throw new ServiceException(
                    key + "格式不正确"
            );
        }
    }

    private IotDevice resolveOptionalDevice(Long deviceId)
    {
        if (deviceId == null)
        {
            return null;
        }

        IotDevice device = iotDeviceService
                .selectIotDeviceByDeviceId(deviceId);

        if (device == null)
        {
            throw new ServiceException("关联设备不存在或已删除");
        }
        if (device.getParkId() == null
                || device.getAreaId() == null)
        {
            throw new ServiceException(
                    "关联设备未配置有效园区或地块"
            );
        }

        return device;
    }

    private void applyDeviceBinding(
            AiImage image,
            IotDevice device)
    {
        if (device == null)
        {
            return;
        }

        image.setDeviceId(device.getDeviceId());
        image.setParkId(device.getParkId());
        image.setPlotId(device.getAreaId());
        if (device.getLocationId() != null)
        {
            image.setImageB(
                    String.valueOf(device.getLocationId())
            );
        }
    }

    private void applyDeviceBinding(
            AiRecord record,
            IotDevice device)
    {
        if (device == null)
        {
            return;
        }

        record.setDeviceId(device.getDeviceId());
        record.setParkId(device.getParkId());
        record.setPlotId(device.getAreaId());
    }

    private Long getOptionalLongParam(
            Map<String, Object> params,
            String key)
    {
        Object value = params == null ? null : params.get(key);

        if (value == null
                || value.toString().trim().isEmpty())
        {
            return null;
        }

        try
        {
            return Long.valueOf(value.toString());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private String getStringParam(
            Map<String, Object> params,
            String key,
            String defaultValue)
    {
        Object value = params == null ? null : params.get(key);

        if (value == null
                || value.toString().trim().isEmpty())
        {
            return defaultValue;
        }

        return value.toString().trim();
    }

    private String extractFileName(String imageUrl)
    {
        if (imageUrl == null || imageUrl.trim().isEmpty())
        {
            return "pest_" + System.currentTimeMillis() + ".jpg";
        }

        String cleanUrl = imageUrl.split("\\?")[0]
                .split("#")[0];
        int index = cleanUrl.lastIndexOf("/");

        return index >= 0
                ? cleanUrl.substring(index + 1)
                : cleanUrl;
    }

    private String extractFileSuffix(String fileName)
    {
        if (fileName == null || fileName.trim().isEmpty())
        {
            return "jpg";
        }

        int index = fileName.lastIndexOf(".");
        return index >= 0 && index < fileName.length() - 1
                ? fileName.substring(index + 1).toLowerCase()
                : "jpg";
    }

    private void deleteProfileFileQuietly(String fileUrl)
    {
        if (fileUrl == null || fileUrl.trim().isEmpty())
        {
            return;
        }

        String cleanUrl = fileUrl.split("\\?")[0]
                .split("#")[0];

        /*
         * 远程模型服务器返回的 http/https 文件不属于本服务，
         * 不在这里删除。
         */
        if (cleanUrl.startsWith("http://")
                || cleanUrl.startsWith("https://"))
        {
            return;
        }

        try
        {
            String relativePath = cleanUrl;
            int profileIndex = cleanUrl.indexOf("/profile/");

            if (profileIndex >= 0)
            {
                relativePath = cleanUrl.substring(
                        profileIndex + "/profile/".length()
                );
            }
            else if (cleanUrl.startsWith("profile/"))
            {
                relativePath = cleanUrl.substring(
                        "profile/".length()
                );
            }
            else
            {
                return;
            }

            Path profilePath = Paths.get(
                    RuoYiConfig.getProfile()
            ).toAbsolutePath().normalize();

            Path targetPath = profilePath.resolve(
                    relativePath.replace(
                            "/",
                            File.separator
                    )
            ).normalize();

            /*
             * 防止通过 ../ 删除 profile 目录外的文件。
             */
            if (!targetPath.startsWith(profilePath))
            {
                return;
            }

            Files.deleteIfExists(targetPath);
        }
        catch (Exception e)
        {
            /*
             * 文件清理失败不能覆盖原始识别异常。
             */
        }
    }

    private String getCurrentUsername()
    {
        try
        {
            String username = SecurityUtils.getUsername();
            return username == null
                    || username.trim().isEmpty()
                    ? "system"
                    : username;
        }
        catch (Exception e)
        {
            return "system";
        }
    }

    /**
     * 根据图片ID进行虫情识别
     *
     * 说明：
     * 1. AiImage 只读取图片，不保存识别结果
     * 2. AiRecord 保存识别记录
     * 3. AiPest 保存虫情识别结果
     * 4. 不使用 PestDiagnosisHistory
     * 5. 暂时不使用默认模型查询，必须传 modelId
     */
    @Override
    @Transactional
    public Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode)
    {
        String generatedResultImageUrl = null;

        // 1. 读取图片信息表
        AiImage image = aiImageMapper.selectAiImageByImageId(imageId);
        if (image == null)
        {
            throw new ServiceException("图片不存在，imageId=" + imageId);
        }

        if (image.getImageUrl() == null || image.getImageUrl().trim().isEmpty())
        {
            throw new ServiceException("图片地址为空，无法识别");
        }

        // 2. 读取模型信息
        AiModel model = aiModelMapper.selectAiModelByModelId(modelId);
        if (model == null)
        {
            throw new ServiceException("模型不存在，modelId=" + modelId);
        }

        if (model.getModelStatus() == null || model.getModelStatus() == 0)
        {
            throw new ServiceException("当前模型已停用，不能用于识别");
        }

        if (model.getModelUrl() == null || model.getModelUrl().trim().isEmpty())
        {
            throw new ServiceException("模型接口地址为空，请先在模型管理中配置 model_url");
        }

        // 3. 创建识别记录 AiRecord
        AiRecord record = new AiRecord();
        record.setRecordNo("PEST" + System.currentTimeMillis());
        record.setRecordType("虫情");
        record.setRecordMode(recordMode);
        record.setModelId(model.getModelId());
        record.setModelName(model.getModelName());
        record.setImageId(image.getImageId());
        record.setCropType(cropType);
        record.setDeviceId(image.getDeviceId());
        record.setParkId(image.getParkId());
        record.setPlotId(image.getPlotId());
        record.setRecordStatus(1L); // 1 识别中
        aiRecordMapper.insertAiRecord(record);

        try
        {
            // 4. 调用模型服务
            String responseBody = callPestModel(
                    model,
                    image.getImageUrl(),
                    image.getImageId(),
                    image.getImageName()
            );

            if (responseBody == null || responseBody.trim().isEmpty())
            {
                throw new ServiceException("模型服务返回为空");
            }

            // 5. 解析模型返回结果
            JSONObject modelResult = parseModelResult(responseBody);

            String pestName = modelResult.getString("pestName");
            if (pestName == null || pestName.trim().isEmpty())
            {
                pestName = modelResult.getString("name");
            }
            if (pestName == null || pestName.trim().isEmpty())
            {
                pestName = "未知虫害";
            }

            BigDecimal confidence = getConfidence(modelResult);

            Long pestCount = getLongValue(modelResult, "pestCount", "count", 1L);
            Long pestTotalCount = getLongValue(modelResult, "pestTotalCount", "totalCount", pestCount);

            String pestResult = modelResult.getString("pestResult");
            if (pestResult == null || pestResult.trim().isEmpty())
            {
                pestResult = "识别到虫害：" + pestName + "，数量：" + pestTotalCount;
            }

            String pestAdvice = modelResult.getString("pestAdvice");
            if (pestAdvice == null || pestAdvice.trim().isEmpty())
            {
                pestAdvice = "建议结合田间虫口密度、作物生育期和历史虫情变化进行综合判断，必要时采取绿色防控或药剂防治措施。";
            }

            String pestResultImgUrl = modelResult.getString("pestResultImgUrl");
            if (pestResultImgUrl == null || pestResultImgUrl.trim().isEmpty())
            {
                pestResultImgUrl = modelResult.getString("resultImageUrl");
            }

            String resultImageBase64 = modelResult.getString("resultImageBase64");
            if (resultImageBase64 == null || resultImageBase64.trim().isEmpty())
            {
                resultImageBase64 = modelResult.getString("image_base64");
            }
            if (resultImageBase64 != null && !resultImageBase64.trim().isEmpty())
            {
                pestResultImgUrl = savePestResultImage(
                        resultImageBase64,
                        image.getImageName()
                );
                generatedResultImageUrl = pestResultImgUrl;
            }

            String bboxJson = modelResult.getString("bboxJson");
            if (bboxJson == null || bboxJson.trim().isEmpty())
            {
                bboxJson = modelResult.getString("boxes");
            }

            // 6. 保存虫情结果 AiPest
            AiPest pest = new AiPest();
            pest.setRecordId(record.getRecordId());
            pest.setImageId(image.getImageId());
            pest.setPestName(pestName);
            pest.setPestCrop(cropType);
            pest.setPestConfidence(confidence);
            pest.setPestCount(pestCount);
            pest.setPestTotalCount(pestTotalCount);
            pest.setPestResult(pestResult);
            pest.setPestAdvice(pestAdvice);
            pest.setPestResultImgUrl(pestResultImgUrl);
            pest.setBboxJson(bboxJson);
            pest.setIsWarning(0L);
            pest.setWarningLevel("无");
            pest.setPestA(DateUtils.getTime());

            aiPestMapper.insertAiPest(pest);

            // 7. 更新 AiRecord 和 AiImage
            record.setRecordStatus(2L); // 2 成功
            aiRecordMapper.updateAiRecord(record);

            // 识别成功后，更新图片状态为已识别
            image.setImageResultUrl(pestResultImgUrl);
            image.setImageStatus(2L);
            image.setUpdateTime(DateUtils.getNowDate());
            aiImageMapper.updateAiImage(image);

            // 8. 返回前端
            Map<String, Object> vo = new HashMap<>();
            vo.put("pestId", pest.getPestId());
            vo.put("recordId", record.getRecordId());
            vo.put("imageId", image.getImageId());
            vo.put("imageName", image.getImageName());
            vo.put("imageUrl", image.getImageUrl());
            vo.put("imageResultUrl", pestResultImgUrl);
            vo.put("pestName", pestName);
            vo.put("pestConfidence", confidence);
            vo.put("pestCount", pestCount);
            vo.put("pestTotalCount", pestTotalCount);
            vo.put("pestResult", pestResult);
            vo.put("pestAdvice", pestAdvice);
            vo.put("pestResultImgUrl", pestResultImgUrl);
            vo.put("bboxJson", bboxJson);
            vo.put("pestDetails", modelResult.get("pestDetails"));
            vo.put("counts", modelResult.get("counts"));
            vo.put("warningLevel", "无");

            return vo;
        }
        catch (Exception e)
        {
            if (generatedResultImageUrl != null)
            {
                deleteProfileFileQuietly(generatedResultImageUrl);
            }

            // 识别失败，只更新 AiRecord，不更新 AiImage
            // 识别失败，更新 AiRecord 和 AiImage
            record.setRecordStatus(3L); // 3 失败

            String msg = e.getMessage();
            if (msg != null && msg.length() > 500)
            {
                msg = msg.substring(0, 500);
            }
            record.setRecordA(msg);

            aiRecordMapper.updateAiRecord(record);

            // 识别失败后，更新图片状态为识别失败
            aiImageMapper.updateImageStatusById(image.getImageId(), 3L);

            throw new ServiceException("虫情识别失败：" + e.getMessage());

        }
    }


    /**
     * 根据历史虫情和未来天气预测虫情趋势
     */
    @Override
    public Map<String, Object> predictPestTrend(Long parkId, Long areaId, String locationId, Long deviceId, String pestName, Integer days)
    {
        int predictDays = (days != null && days == 7) ? 7 : 3;
        String targetPestName = (pestName == null || pestName.trim().isEmpty()) ? "未知虫害" : pestName.trim();

        Map<String, Object> params = new HashMap<>();
        params.put("parkId", parkId);
        params.put("areaId", areaId);
        params.put("locationId", locationId);
        params.put("deviceId", deviceId);
        params.put("pestName", targetPestName);

        List<Map<String, Object>> rows = aiPestMapper.selectPestHistoryTrend(params);
        List<Integer> history = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        if (rows != null)
        {
            for (Map<String, Object> row : rows)
            {
                labels.add(String.valueOf(getMapValueIgnoreCase(row, "dateLabel")));
                history.add(toInteger(getMapValueIgnoreCase(row, "pestCount")));
            }
        }
        if (history.isEmpty())
        {
            history.add(0);
            labels.add(new SimpleDateFormat("MM-dd").format(new Date()));
        }

        List<AgriWeatherForecast> forecasts = queryForecasts(parkId, predictDays);
        String prompt = buildPestPredictPrompt(targetPestName, history, forecasts, predictDays);

        List<Integer> qwenPredict;
        try
        {
            qwenPredict = callQwenForPestPredict(prompt, predictDays);
        }
        catch (Exception e)
        {
            qwenPredict = fallbackPredict(history, forecasts, predictDays, targetPestName);
        }

        List<Integer> predict = new ArrayList<>();
        int lastHistory = history.get(history.size() - 1);
        predict.add(lastHistory);
        if (qwenPredict == null || qwenPredict.size() != predictDays)
        {
            qwenPredict = fallbackPredict(history, forecasts, predictDays, targetPestName);
        }
        predict.addAll(qwenPredict.subList(0, predictDays));

        labels.addAll(buildFutureLabels(predictDays));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pestName", targetPestName);
        result.put("days", predictDays);
        result.put("history", history);
        result.put("predict", predict);
        result.put("labels", labels);
        return result;
    }

    private List<AgriWeatherForecast> queryForecasts(Long parkId, int days)
    {
        AgriWeatherForecast query = new AgriWeatherForecast();
        if (parkId != null)
        {
            query.setParkId(String.valueOf(parkId));
        }
        query.getParams().put("latest", "true");
        List<AgriWeatherForecast> list = agriWeatherForecastService.selectAgriWeatherForecastList(query);
        if (list == null)
        {
            return new ArrayList<>();
        }
        Collections.sort(list, new Comparator<AgriWeatherForecast>()
        {
            @Override
            public int compare(AgriWeatherForecast o1, AgriWeatherForecast o2)
            {
                Date d1 = o1.getForecastDate();
                Date d2 = o2.getForecastDate();
                if (d1 == null && d2 == null) return 0;
                if (d1 == null) return 1;
                if (d2 == null) return -1;
                return d1.compareTo(d2);
            }
        });
        if (list.size() > days)
        {
            return new ArrayList<>(list.subList(0, days));
        }
        return list;
    }

    private String buildPestPredictPrompt(String pestName, List<Integer> history, List<AgriWeatherForecast> weatherList, int days)
    {
        StringBuilder weather = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (AgriWeatherForecast item : weatherList)
        {
            weather.append("日期：").append(item.getForecastDate() == null ? "未知" : sdf.format(item.getForecastDate()))
                    .append("，天气：").append(item.getWeatherTextDay()).append("/").append(item.getWeatherTextNight())
                    .append("，最高温：").append(item.getTempMax())
                    .append("，最低温：").append(item.getTempMin())
                    .append("，湿度：").append(item.getHumidityAvg())
                    .append("，降雨量：").append(item.getPrecipTotal())
                    .append("，降水概率：").append(item.getPrecipProb())
                    .append("\n");
        }

        return "你是一名智慧农业虫情预测专家。\n\n" +
                "请根据冬枣园区历史虫情数量和未来天气预测未来虫害数量。\n\n" +
                "虫害名称：\n\n" + pestName + "\n\n" +
                "历史虫情：\n\n" + history + "\n\n" +
                "未来天气：\n\n" + weather + "\n" +
                "预测要求：\n\n" +
                "1、只预测虫害数量。\n\n" +
                "2、不要预测风险等级。\n\n" +
                "3、不要预测文字分析。\n\n" +
                "4、不要输出Markdown。\n\n" +
                "5、不要输出解释。\n\n" +
                "6、不要输出单位。\n\n" +
                "7、必须返回JSON数组。\n\n" +
                "8、数组长度必须等于未来预测天数。\n\n" +
                "9、每个元素必须为整数。\n\n" +
                "10、预测数量必须与历史最后一天保持平滑变化。\n\n" +
                "虫害规律：\n\n" +
                "【枣瘿蚊】\n" +
                "连续阴雨、高湿（>85%）快速增长。\n\n" +
                "【枣尺蠖】\n" +
                "高温增加，大雨下降。\n\n" +
                "【龟蜡蚧】\n" +
                "变化缓慢。\n\n" +
                "未来预测天数：" + days + "\n\n" +
                "示例：\n\n" +
                "[\n30,\n42,\n58,\n73,\n91,\n115,\n138\n]";
    }

    private List<Integer> callQwenForPestPredict(String prompt, int days)
    {
        if (dashscopeApiKey == null || dashscopeApiKey.trim().isEmpty())
        {
            throw new ServiceException("DashScope API Key为空");
        }

        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + dashscopeApiKey);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen3.6-plus");

        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你只返回JSON数组，不返回任何解释。输出必须可被JSON解析。");
        messages.add(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        requestBody.put("messages", messages);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null)
        {
            throw new ServiceException("Qwen虫情预测调用失败");
        }

        JSONObject responseJson = JSONObject.parseObject(response.getBody());
        JSONArray choices = responseJson.getJSONArray("choices");
        if (choices == null || choices.isEmpty())
        {
            throw new ServiceException("Qwen虫情预测返回为空");
        }
        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
        if (message == null)
        {
            throw new ServiceException("Qwen虫情预测消息为空");
        }
        return parseQwenPredictArray(message.getString("content"), days);
    }

    private List<Integer> parseQwenPredictArray(String content, int days)
    {
        if (content == null || content.trim().isEmpty())
        {
            throw new ServiceException("Qwen虫情预测内容为空");
        }
        String text = content.replace("```json", "").replace("```JSON", "").replace("```", "").trim();
        int start = text.indexOf("[");
        int end = text.lastIndexOf("]");
        if (start < 0 || end <= start)
        {
            throw new ServiceException("Qwen虫情预测不是JSON数组");
        }
        JSONArray array = JSONArray.parseArray(text.substring(start, end + 1));
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < array.size() && result.size() < days; i++)
        {
            result.add(Math.max(0, array.getIntValue(i)));
        }
        if (result.size() != days)
        {
            throw new ServiceException("Qwen虫情预测数组长度不正确");
        }
        return result;
    }

    private List<Integer> fallbackPredict(List<Integer> history, List<AgriWeatherForecast> weatherList, int days, String pestName)
    {
        List<Integer> result = new ArrayList<>();
        int last = history == null || history.isEmpty() ? 0 : history.get(history.size() - 1);
        int previous = history != null && history.size() > 1 ? history.get(history.size() - 2) : last;
        int trend = Math.max(-10, Math.min(10, last - previous));

        for (int i = 0; i < days; i++)
        {
            AgriWeatherForecast weather = weatherList != null && i < weatherList.size() ? weatherList.get(i) : null;
            int adjust = trend;
            if (weather != null)
            {
                Double humidity = weather.getHumidityAvg();
                Double rain = weather.getPrecipTotal();
                Double tempMax = weather.getTempMax();
                if ("枣瘿蚊".equals(pestName) && humidity != null && humidity > 85)
                {
                    adjust += 8;
                }
                if ("枣尺蠖".equals(pestName) && tempMax != null && tempMax > 32)
                {
                    adjust += 5;
                }
                if (rain != null && rain > 20)
                {
                    adjust -= "枣瘿蚊".equals(pestName) ? 0 : 6;
                }
                if ("龟蜡蚧".equals(pestName))
                {
                    adjust = Math.max(-3, Math.min(3, adjust));
                }
            }
            last = Math.max(0, last + adjust);
            result.add(last);
        }
        return result;
    }

    private List<String> buildFutureLabels(int days)
    {
        List<String> labels = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < days; i++)
        {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            labels.add(sdf.format(calendar.getTime()));
        }
        return labels;
    }

    private Object getMapValueIgnoreCase(Map<String, Object> map, String key)
    {
        if (map == null || key == null)
        {
            return null;
        }
        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            if (key.equalsIgnoreCase(entry.getKey()))
            {
                return entry.getValue();
            }
        }
        return null;
    }

    private Integer toInteger(Object value)
    {
        if (value == null)
        {
            return 0;
        }
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        try
        {
            return new BigDecimal(value.toString()).intValue();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    /**
     * 解析模型返回结果
     *
     * 支持三种格式：
     * 1. [{...}]
     * 2. {"data": {...}}
     * 3. {"data": [{...}]}
     */
    private JSONObject parseModelResult(String responseBody)
    {
        String text = responseBody.trim();

        if (text.startsWith("["))
        {
            JSONArray arr = JSONArray.parseArray(text);
            if (arr.isEmpty())
            {
                throw new ServiceException("模型未识别到虫害目标");
            }
            return arr.getJSONObject(0);
        }

        JSONObject obj = JSONObject.parseObject(text);

        if (obj.containsKey("data"))
        {
            Object data = obj.get("data");

            if (data instanceof JSONArray)
            {
                JSONArray arr = obj.getJSONArray("data");
                if (arr.isEmpty())
                {
                    throw new ServiceException("模型未识别到虫害目标");
                }
                return arr.getJSONObject(0);
            }

            if (data instanceof JSONObject)
            {
                return obj.getJSONObject("data");
            }
        }

        return obj;
    }

    /**
     * 获取置信度
     *
     * 数据库存 DECIMAL(6,5)，建议保存 0.95600 这种格式。
     * 如果模型返回 95.6，则自动转成 0.95600。
     */
    private BigDecimal getConfidence(JSONObject obj)
    {
        BigDecimal confidence = obj.getBigDecimal("pestConfidence");

        if (confidence == null)
        {
            confidence = obj.getBigDecimal("confidence");
        }

        if (confidence == null)
        {
            confidence = BigDecimal.ZERO;
        }

        if (confidence.compareTo(BigDecimal.ONE) > 0)
        {
            confidence = confidence.divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP);
        }

        return confidence.setScale(5, RoundingMode.HALF_UP);
    }

    /**
     * 从模型返回 JSON 中读取 Long 类型字段
     */
    private Long getLongValue(JSONObject obj, String key1, String key2, Long defaultValue)
    {
        Long value = obj.getLong(key1);

        if (value == null)
        {
            value = obj.getLong(key2);
        }

        return value == null ? defaultValue : value;
    }


    private byte[] readImageBytes(String imageUrl) throws Exception
    {
        String value = imageUrl.trim();
        if (value.startsWith("data:image/"))
        {
            int separator = value.indexOf(";base64,");
            if (separator < 0) throw new ServiceException("识别图片 Base64 格式无效");
            return Base64.getDecoder().decode(value.substring(separator + 8));
        }
        String path = value.startsWith("http") ? URI.create(value).getPath() : value;
        int profileIndex = path.indexOf("/profile/");

        // 当前后端管理的本地上传文件，直接通过 ruoyi.profile 读取
        if (profileIndex >= 0)
        {
            String relative = path.substring(profileIndex + "/profile/".length());
            Path root = Paths.get(RuoYiConfig.getProfile())
                    .toAbsolutePath().normalize();
            Path target = root.resolve(relative.replace("/", File.separator))
                    .normalize();

            if (!target.startsWith(root))
            {
                throw new ServiceException("图片路径超出上传目录");
            }
            if (Files.isRegularFile(target))
            {
                return Files.readAllBytes(target);
            }
        }

        // 对象存储或外部图片地址由 Java 后端下载
        if (value.startsWith("http://") || value.startsWith("https://"))
        {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(value).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("User-Agent", "Binzhou-Backend/1.0");

            try (InputStream input = connection.getInputStream())
            {
                return StreamUtils.copyToByteArray(input);
            }
            finally
            {
                connection.disconnect();
            }
        }

        throw new ServiceException("无法读取图片：" + imageUrl);
    }

    private String savePestResultImage(String imageBase64, String imageName)
            throws Exception
    {
        String encoded = imageBase64 == null ? "" : imageBase64.trim();
        if (encoded.isEmpty())
        {
            return null;
        }

        int commaIndex = encoded.indexOf(',');
        if (encoded.startsWith("data:") && commaIndex >= 0)
        {
            encoded = encoded.substring(commaIndex + 1);
        }

        // Limit the decoded result image to about 25 MB.
        if (encoded.length() > 35 * 1024 * 1024)
        {
            throw new ServiceException("模型返回的结果图过大");
        }

        final byte[] imageBytes;
        try
        {
            imageBytes = Base64.getDecoder().decode(encoded);
        }
        catch (IllegalArgumentException e)
        {
            throw new ServiceException("模型返回的结果图 Base64 格式无效");
        }

        if (imageBytes.length == 0)
        {
            throw new ServiceException("模型返回的结果图为空");
        }

        String profile = RuoYiConfig.getProfile();
        if (profile == null || profile.trim().isEmpty())
        {
            throw new ServiceException("未配置 ruoyi.profile，无法保存结果图");
        }

        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        Path root = Paths.get(profile).toAbsolutePath().normalize();
        Path resultDir = root.resolve("upload")
                .resolve("pest-result")
                .resolve(datePath)
                .normalize();

        if (!resultDir.startsWith(root))
        {
            throw new ServiceException("结果图路径超出上传目录");
        }
        Files.createDirectories(resultDir);

        String baseName = imageName == null ? "pest" : imageName.trim();
        int dotIndex = baseName.lastIndexOf('.');
        if (dotIndex > 0)
        {
            baseName = baseName.substring(0, dotIndex);
        }
        baseName = baseName.replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
        if (baseName.isEmpty())
        {
            baseName = "pest";
        }

        String fileName = baseName + "_result_"
                + UUID.randomUUID().toString().replace("-", "") + ".jpg";
        Path target = resultDir.resolve(fileName).normalize();
        if (!target.startsWith(resultDir))
        {
            throw new ServiceException("结果图文件名非法");
        }

        Files.write(target, imageBytes);
        return "/profile/upload/pest-result/" + datePath + "/" + fileName;
    }

    private String callPestModel(
            AiModel model, String imageUrl, Long imageId, String imageName)
            throws Exception
    {
        if (model.getModelCode() == null || model.getModelCode().trim().isEmpty())
        {
            throw new ServiceException("模型编码为空，请先在模型管理中配置 model_code");
        }
        byte[] imageBytes = readImageBytes(imageUrl);
        String filename = imageName == null || imageName.trim().isEmpty()
                ? extractFileName(imageUrl) : imageName;

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model.getModelCode().trim());
        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        JSONArray content = new JSONArray();
        JSONObject imageContent = new JSONObject();
        imageContent.put("type", "image_url");
        JSONObject imageUrlContent = new JSONObject();
        imageUrlContent.put("url", "data:" + detectImageMimeType(imageBytes, filename) + ";base64,"
                + Base64.getEncoder().encodeToString(imageBytes));
        imageContent.put("image_url", imageUrlContent);
        content.add(imageContent);
        userMessage.put("content", content);
        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (modelApiKey == null || modelApiKey.trim().isEmpty())
        {
            throw new ServiceException("AI 模型 API Key 未配置");
        }
        headers.setBearerAuth(modelApiKey.trim());

        ResponseEntity<String> response = restTemplate.postForEntity(
                model.getModelUrl().trim(),
                new HttpEntity<>(requestBody.toJSONString(), headers),
                String.class
        );

        return response.getBody();
    }

    private String detectImageMimeType(byte[] bytes, String fileName)
    {
        if (bytes.length >= 8 && (bytes[0] & 0xff) == 0x89 && bytes[1] == 'P'
                && bytes[2] == 'N' && bytes[3] == 'G') return "image/png";
        if (bytes.length >= 3 && (bytes[0] & 0xff) == 0xff
                && (bytes[1] & 0xff) == 0xd8) return "image/jpeg";
        if (bytes.length >= 12 && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F'
                && bytes[3] == 'F' && bytes[8] == 'W' && bytes[9] == 'E'
                && bytes[10] == 'B' && bytes[11] == 'P') return "image/webp";
        if (bytes.length >= 2 && bytes[0] == 'B' && bytes[1] == 'M') return "image/bmp";
        String lower = fileName == null ? "" : fileName.toLowerCase(java.util.Locale.ROOT);
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".bmp")) return "image/bmp";
        return "image/jpeg";
    }
}
