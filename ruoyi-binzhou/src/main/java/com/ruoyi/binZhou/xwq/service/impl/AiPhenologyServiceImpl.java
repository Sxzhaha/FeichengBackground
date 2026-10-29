package com.ruoyi.binZhou.xwq.service.impl;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.domain.AiPhenology;
import com.ruoyi.binZhou.xwq.domain.AiRecord;
import com.ruoyi.binZhou.xwq.mapper.AiImageMapper;
import com.ruoyi.binZhou.xwq.mapper.AiModelMapper;
import com.ruoyi.binZhou.xwq.mapper.AiPhenologyMapper;
import com.ruoyi.binZhou.xwq.mapper.AiRecordMapper;
import com.ruoyi.binZhou.xwq.service.IAiPhenologyService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.service.IIotDeviceService;

/**
 * 物候期识别结果Service业务层处理
 *
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class AiPhenologyServiceImpl implements IAiPhenologyService
{
    /**
     * 按当前 xwq_ai_image 表定义：
     * 0 待识别，1 已识别，2 识别失败。
     *
     * 本接口只有模型成功后才插入图片，因此直接保存为已识别。
     */
    private static final Long IMAGE_STATUS_RECOGNIZED = 1L;

    /**
     * xwq_ai_record：
     * 0 待识别，1 识别中，2 成功，3 失败。
     */
    private static final Long RECORD_STATUS_SUCCESS = 2L;

    private static final String DEFAULT_CROP_TYPE = "冬枣";
    private static final String IMAGE_AI_TYPE = "phenology";
    private static final String RECORD_TYPE = "物候期";
    private static final String IMAGE_SOURCE = "手动上传";
    private static final String DEFAULT_RECORD_MODE = "手动上传";
    private static final String DASHSCOPE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String[] REPORT_MARKERS = new String[] {
            "<<OVERALL>>",
            "<<DISTRIBUTION>>",
            "<<TREND>>",
            "<<ABNORMAL>>",
            "<<ADVICE>>"
    };
    private static final BigDecimal LOW_CONFIDENCE_LIMIT = new BigDecimal("0.60000");
    private static final int REPORT_RECORD_LIMIT = 1000;
    private static final int REPORT_SERIES_LIMIT = 30;
    private static final int REPORT_MIN_LENGTH = 80;
    private static final int REPORT_MAX_LENGTH = 8000;

    @Value("${dashscope.apiKey:}")
    private String dashscopeApiKey;

    @Value("${dashscope.model:qwen3.7-plus}")
    private String dashscopeModel;

    @Autowired
    private AiPhenologyMapper aiPhenologyMapper;

    @Autowired
    private AiImageMapper aiImageMapper;

    @Autowired
    private AiRecordMapper aiRecordMapper;

    @Autowired
    private AiModelMapper aiModelMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IIotDeviceService iotDeviceService;

    /**
     * 查询物候期识别结果
     */
    @Override
    public AiPhenology selectAiPhenologyByPhenologyId(
            Long phenologyId)
    {
        return aiPhenologyMapper
                .selectAiPhenologyByPhenologyId(phenologyId);
    }

    /**
     * 查询物候期识别结果列表
     */
    @Override
    public List<AiPhenology> selectAiPhenologyList(
            AiPhenology aiPhenology)
    {
        return aiPhenologyMapper
                .selectAiPhenologyList(aiPhenology);
    }

    /**
     * 新增物候期识别结果
     */
    @Override
    public int insertAiPhenology(
            AiPhenology aiPhenology)
    {
        aiPhenology.setCreateBy(getCurrentUsername());
        aiPhenology.setCreateTime(DateUtils.getNowDate());
        return aiPhenologyMapper
                .insertAiPhenology(aiPhenology);
    }

    /**
     * 修改物候期识别结果
     */
    @Override
    public int updateAiPhenology(
            AiPhenology aiPhenology)
    {
        aiPhenology.setUpdateBy(getCurrentUsername());
        aiPhenology.setUpdateTime(DateUtils.getNowDate());
        return aiPhenologyMapper
                .updateAiPhenology(aiPhenology);
    }

    /**
     * 批量删除物候期识别结果
     */
    @Override
    public int deleteAiPhenologyByPhenologyIds(
            Long[] phenologyIds)
    {
        return aiPhenologyMapper
                .deleteAiPhenologyByPhenologyIds(phenologyIds);
    }

    /**
     * 删除物候期识别结果信息
     */
    @Override
    public int deleteAiPhenologyByPhenologyId(
            Long phenologyId)
    {
        return aiPhenologyMapper
                .deleteAiPhenologyByPhenologyId(phenologyId);
    }

    /**
     * 根据勾选记录或筛选条件查询真实物候数据，并生成固定章节报告文本。
     */
    @Override
    public String generateReportText(
            Map<String, Object> params)
    {
        if (params == null)
        {
            throw new ServiceException("请求参数不能为空");
        }

        List<AiPhenology> records =
                queryReportRecords(params);

        if (records.isEmpty())
        {
            throw new ServiceException("未查询到物候期记录");
        }

        List<AiPhenology> validRecords =
                new ArrayList<AiPhenology>();

        for (AiPhenology record : records)
        {
            if (record != null
                    && !isBlank(record.getPhenologyName()))
            {
                validRecords.add(record);
            }
        }

        if (validRecords.isEmpty())
        {
            throw new ServiceException("查询结果中没有有效物候期记录");
        }

        if (validRecords.size() > REPORT_RECORD_LIMIT)
        {
            throw new ServiceException(
                    "单次生成报告最多支持"
                            + REPORT_RECORD_LIMIT
                            + "条有效记录"
            );
        }

        Collections.sort(
                validRecords,
                new Comparator<AiPhenology>()
                {
                    @Override
                    public int compare(
                            AiPhenology left,
                            AiPhenology right)
                    {
                        Date leftTime = left.getCreateTime();
                        Date rightTime = right.getCreateTime();

                        if (leftTime == null
                                && rightTime != null)
                        {
                            return 1;
                        }
                        if (leftTime != null
                                && rightTime == null)
                        {
                            return -1;
                        }
                        if (leftTime != null)
                        {
                            int result = leftTime.compareTo(rightTime);
                            if (result != 0)
                            {
                                return result;
                            }
                        }

                        Long leftId = left.getPhenologyId();
                        Long rightId = right.getPhenologyId();

                        if (leftId == null)
                        {
                            return rightId == null ? 0 : 1;
                        }
                        return rightId == null
                                ? -1
                                : leftId.compareTo(rightId);
                    }
                }
        );

        Map<String, Object> statistics =
                calculateReportStatistics(validRecords);
        String systemPrompt = buildReportSystemPrompt();
        String userPrompt = buildReportUserPrompt(statistics);

        if (isBlank(dashscopeApiKey))
        {
            throw new ServiceException("DashScope API Key为空");
        }

        for (int attempt = 0; attempt < 2; attempt++)
        {
            try
            {
                String modelText = callQwenReportText(
                        systemPrompt,
                        userPrompt
                );
                String cleanText = cleanModelText(modelText);
                String validationCode =
                        validateReportMarkers(cleanText);

                if (validationCode == null)
                {
                    return cleanText;
                }

                if (!isRetryableReportValidation(validationCode)
                        || attempt == 1)
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
                if (attempt == 1)
                {
                    break;
                }
            }
            catch (HttpStatusCodeException e)
            {
                int status = e.getRawStatusCode();

                if (status == 401 || status == 403)
                {
                    throw new ServiceException(
                            "DashScope鉴权失败，HTTP " + status
                    );
                }

                if (status < 500
                        || status >= 600
                        || attempt == 1)
                {
                    break;
                }
            }
            catch (Exception e)
            {
                break;
            }
        }

        return buildFallbackReportText(statistics);
    }

    private List<AiPhenology> queryReportRecords(
            Map<String, Object> params)
    {
        List<Long> phenologyIds =
                parsePhenologyIds(params.get("phenologyIds"));

        if (!phenologyIds.isEmpty())
        {
            List<AiPhenology> result =
                    new ArrayList<AiPhenology>();

            for (Long phenologyId : phenologyIds)
            {
                AiPhenology record = aiPhenologyMapper
                        .selectAiPhenologyByPhenologyId(phenologyId);

                if (record != null)
                {
                    result.add(record);
                }
            }

            return result;
        }

        AiPhenology query = new AiPhenology();
        query.setParkId(getReportLongParam(params, "parkId"));
        query.setAreaId(getReportLongParam(params, "areaId"));
        query.setDeviceId(getReportLongParam(params, "deviceId"));
        query.setLocationId(
                getReportLocationId(params.get("locationId"))
        );
        query.setPhenologyCrop(
                getReportStringParam(params, "phenologyCrop")
        );
        query.setPhenologyName(
                getReportStringParam(params, "phenologyName")
        );

        Date startDate = parseReportDate(
                params.get("startDate"),
                false,
                "startDate"
        );
        Date endDate = parseReportDate(
                params.get("endDate"),
                true,
                "endDate"
        );

        if (startDate != null
                && endDate != null
                && startDate.after(endDate))
        {
            throw new ServiceException(
                    "startDate不能晚于endDate"
            );
        }

        if (startDate != null)
        {
            query.getParams().put("beginTime", startDate);
        }
        if (endDate != null)
        {
            query.getParams().put("endTime", endDate);
        }

        List<AiPhenology> records =
                aiPhenologyMapper.selectAiPhenologyList(query);

        return records == null
                ? new ArrayList<AiPhenology>()
                : records;
    }

    private List<Long> parsePhenologyIds(
            Object value)
    {
        LinkedHashSet<Long> ids =
                new LinkedHashSet<Long>();

        if (value == null
                || value.toString().trim().isEmpty())
        {
            return new ArrayList<Long>();
        }

        List<Object> values = new ArrayList<Object>();

        if (value instanceof Iterable)
        {
            for (Object item : (Iterable<?>) value)
            {
                values.add(item);
            }
        }
        else if (value.getClass().isArray())
        {
            int length = java.lang.reflect.Array.getLength(value);
            for (int i = 0; i < length; i++)
            {
                values.add(java.lang.reflect.Array.get(value, i));
            }
        }
        else
        {
            String text = value.toString().trim();
            if (text.startsWith("[") && text.endsWith("]"))
            {
                text = text.substring(1, text.length() - 1);
            }
            if (!text.trim().isEmpty())
            {
                values.addAll(Arrays.<Object>asList(text.split(",")));
            }
        }

        for (Object item : values)
        {
            if (item == null
                    || item.toString().trim().isEmpty())
            {
                throw new ServiceException(
                        "phenologyIds包含空值"
                );
            }

            Long id;
            try
            {
                id = Long.valueOf(item.toString().trim());
            }
            catch (Exception e)
            {
                throw new ServiceException(
                        "phenologyIds格式不正确"
                );
            }

            if (id <= 0)
            {
                throw new ServiceException(
                        "phenologyIds必须为正整数"
                );
            }

            ids.add(id);
        }

        if (ids.size() > REPORT_RECORD_LIMIT)
        {
            throw new ServiceException(
                    "phenologyIds最多支持"
                            + REPORT_RECORD_LIMIT
                            + "个"
            );
        }

        return new ArrayList<Long>(ids);
    }

    private Long getReportLongParam(
            Map<String, Object> params,
            String key)
    {
        Object value = params.get(key);

        if (value == null
                || value.toString().trim().isEmpty())
        {
            return null;
        }

        try
        {
            Long result = Long.valueOf(value.toString().trim());
            if (result <= 0)
            {
                throw new NumberFormatException();
            }
            return result;
        }
        catch (Exception e)
        {
            throw new ServiceException(
                    key + "必须为正整数"
            );
        }
    }

    private String getReportLocationId(
            Object value)
    {
        if (value == null
                || value.toString().trim().isEmpty())
        {
            return null;
        }

        String locationId = value.toString().trim();
        try
        {
            if (Long.valueOf(locationId) <= 0)
            {
                throw new NumberFormatException();
            }
        }
        catch (Exception e)
        {
            throw new ServiceException(
                    "locationId必须为正整数"
            );
        }
        return locationId;
    }

    private String getReportStringParam(
            Map<String, Object> params,
            String key)
    {
        Object value = params.get(key);

        if (value == null
                || value.toString().trim().isEmpty())
        {
            return null;
        }

        String text = value.toString().trim();
        if (text.length() > 100)
        {
            throw new ServiceException(
                    key + "长度不能超过100个字符"
            );
        }
        return text;
    }

    private Date parseReportDate(
            Object value,
            boolean endOfDay,
            String key)
    {
        if (value == null
                || value.toString().trim().isEmpty())
        {
            return null;
        }

        if (value instanceof Date)
        {
            return new Date(((Date) value).getTime());
        }

        String text = value.toString().trim();
        String[] patterns = new String[] {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String pattern : patterns)
        {
            SimpleDateFormat format =
                    new SimpleDateFormat(pattern);
            format.setLenient(false);

            try
            {
                Date date = format.parse(text);
                if (!text.equals(format.format(date)))
                {
                    continue;
                }

                if (endOfDay
                        && "yyyy-MM-dd".equals(pattern))
                {
                    return new Date(
                            date.getTime() + 86399999L
                    );
                }
                return date;
            }
            catch (Exception ignored)
            {
                // 继续尝试下一种允许的日期格式。
            }
        }

        throw new ServiceException(
                key
                        + "格式不正确，支持yyyy-MM-dd或yyyy-MM-dd HH:mm:ss"
        );
    }

    private Map<String, Object> calculateReportStatistics(
            List<AiPhenology> records)
    {
        Map<String, Object> statistics =
                new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Integer> stageCounts =
                new LinkedHashMap<String, Integer>();
        Set<String> cropTypes = new LinkedHashSet<String>();
        Set<String> parks = new LinkedHashSet<String>();
        Set<String> areas = new LinkedHashSet<String>();
        Set<String> locations = new LinkedHashSet<String>();
        Set<String> devices = new LinkedHashSet<String>();
        BigDecimal confidenceTotal = BigDecimal.ZERO;
        BigDecimal minimumConfidence = null;
        int confidenceCount = 0;
        int lowConfidenceCount = 0;
        Date startTime = null;
        Date endTime = null;

        for (AiPhenology record : records)
        {
            String stage = record.getPhenologyName().trim();
            Integer count = stageCounts.get(stage);
            stageCounts.put(stage, count == null ? 1 : count + 1);

            addTextValue(cropTypes, record.getPhenologyCrop());
            addScopeValue(parks, record.getParkName(), record.getParkId());
            addScopeValue(areas, record.getAreaName(), record.getAreaId());
            addScopeValue(
                    locations,
                    record.getLocationName(),
                    record.getLocationId()
            );
            addScopeValue(
                    devices,
                    record.getDeviceName(),
                    record.getDeviceId()
            );

            Date createTime = record.getCreateTime();
            if (createTime != null)
            {
                if (startTime == null
                        || createTime.before(startTime))
                {
                    startTime = createTime;
                }
                if (endTime == null
                        || createTime.after(endTime))
                {
                    endTime = createTime;
                }
            }

            BigDecimal confidence =
                    normalizeReportConfidence(
                            record.getPhenologyConfidence()
                    );
            if (confidence != null)
            {
                confidenceTotal = confidenceTotal.add(confidence);
                confidenceCount++;

                if (minimumConfidence == null
                        || confidence.compareTo(
                        minimumConfidence) < 0)
                {
                    minimumConfidence = confidence;
                }

                if (confidence.compareTo(
                        LOW_CONFIDENCE_LIMIT) < 0)
                {
                    lowConfidenceCount++;
                }
            }
        }

        String dominantStage = null;
        int dominantCount = 0;

        for (Map.Entry<String, Integer> entry
                : stageCounts.entrySet())
        {
            if (entry.getValue() > dominantCount)
            {
                dominantStage = entry.getKey();
                dominantCount = entry.getValue();
            }
        }

        LinkedHashMap<String, String> stageDistribution =
                new LinkedHashMap<String, String>();
        for (Map.Entry<String, Integer> entry
                : stageCounts.entrySet())
        {
            stageDistribution.put(
                    entry.getKey(),
                    entry.getValue()
                            + "条（"
                            + formatRatio(
                            entry.getValue(),
                            records.size()
                    )
                            + "）"
            );
        }

        BigDecimal averageConfidence =
                confidenceCount == 0
                        ? null
                        : confidenceTotal.divide(
                        BigDecimal.valueOf(confidenceCount),
                        5,
                        RoundingMode.HALF_UP
                );

        statistics.put("validCount", records.size());
        statistics.put(
                "cropTypes",
                cropTypes.isEmpty()
                        ? "未知"
                        : joinTextValues(cropTypes)
        );
        statistics.put(
                "monitoringScope",
                buildMonitoringScope(
                        parks,
                        areas,
                        locations,
                        devices
                )
        );
        statistics.put("startDate", formatReportDate(startTime));
        statistics.put("endDate", formatReportDate(endTime));
        statistics.put("stageCounts", stageCounts);
        statistics.put("stageDistribution", stageDistribution);
        statistics.put("dominantStage", dominantStage);
        statistics.put("dominantCount", dominantCount);
        statistics.put(
                "dominantRatio",
                formatRatio(dominantCount, records.size())
        );
        statistics.put(
                "averageConfidence",
                formatConfidence(averageConfidence)
        );
        statistics.put(
                "minimumConfidence",
                formatConfidence(minimumConfidence)
        );
        statistics.put("confidenceCount", confidenceCount);
        statistics.put("lowConfidenceCount", lowConfidenceCount);
        statistics.put(
                "nextStage",
                calculateNextStage(records, dominantStage)
        );
        statistics.put(
                "timeSeries",
                buildTimeSeries(records)
        );
        return statistics;
    }

    private BigDecimal normalizeReportConfidence(
            BigDecimal confidence)
    {
        if (confidence == null
                || confidence.compareTo(BigDecimal.ZERO) < 0)
        {
            return null;
        }

        BigDecimal result = confidence;
        if (result.compareTo(BigDecimal.ONE) > 0
                && result.compareTo(
                BigDecimal.valueOf(100)) <= 0)
        {
            result = result.divide(
                    BigDecimal.valueOf(100),
                    5,
                    RoundingMode.HALF_UP
            );
        }

        return result.compareTo(BigDecimal.ONE) > 0
                ? null
                : result;
    }

    private String calculateNextStage(
            List<AiPhenology> records,
            String dominantStage)
    {
        LinkedHashMap<String, Integer> nextStageCounts =
                new LinkedHashMap<String, Integer>();

        for (AiPhenology record : records)
        {
            if (dominantStage.equals(
                    record.getPhenologyName().trim())
                    && !isBlank(record.getPhenologyNextName()))
            {
                String nextStage =
                        record.getPhenologyNextName().trim();
                Integer count = nextStageCounts.get(nextStage);
                nextStageCounts.put(
                        nextStage,
                        count == null ? 1 : count + 1
                );
            }
        }

        if (nextStageCounts.isEmpty())
        {
            for (int i = records.size() - 1; i >= 0; i--)
            {
                String nextStage =
                        records.get(i).getPhenologyNextName();
                if (!isBlank(nextStage))
                {
                    return nextStage.trim();
                }
            }
            return "未知";
        }

        String result = null;
        int maximum = 0;
        for (Map.Entry<String, Integer> entry
                : nextStageCounts.entrySet())
        {
            if (entry.getValue() > maximum)
            {
                result = entry.getKey();
                maximum = entry.getValue();
            }
        }
        return result;
    }

    private List<String> buildTimeSeries(
            List<AiPhenology> records)
    {
        List<String> timeSeries = new ArrayList<String>();
        int startIndex = Math.max(
                0,
                records.size() - REPORT_SERIES_LIMIT
        );

        for (int i = startIndex; i < records.size(); i++)
        {
            AiPhenology record = records.get(i);
            timeSeries.add(
                    formatReportDate(record.getCreateTime())
                            + "，"
                            + record.getPhenologyName().trim()
                            + "，置信度"
                            + formatConfidence(
                            normalizeReportConfidence(
                                    record.getPhenologyConfidence()
                            )
                    )
            );
        }
        return timeSeries;
    }

    private void addTextValue(
            Set<String> values,
            String value)
    {
        if (!isBlank(value))
        {
            values.add(value.trim());
        }
    }

    private void addScopeValue(
            Set<String> values,
            String name,
            Object id)
    {
        if (!isBlank(name))
        {
            values.add(name.trim());
        }
        else if (id != null)
        {
            values.add("ID=" + id.toString());
        }
    }

    private String buildMonitoringScope(
            Set<String> parks,
            Set<String> areas,
            Set<String> locations,
            Set<String> devices)
    {
        List<String> parts = new ArrayList<String>();

        if (!parks.isEmpty())
        {
            parts.add("园区：" + joinTextValues(parks));
        }
        if (!areas.isEmpty())
        {
            parts.add("地块：" + joinTextValues(areas));
        }
        if (!locations.isEmpty())
        {
            parts.add("位置：" + joinTextValues(locations));
        }
        if (!devices.isEmpty())
        {
            parts.add("设备：" + joinTextValues(devices));
        }

        return parts.isEmpty()
                ? "未知"
                : joinTextValues(parts);
    }

    private String joinTextValues(
            Iterable<String> values)
    {
        StringBuilder result = new StringBuilder();
        for (String value : values)
        {
            if (result.length() > 0)
            {
                result.append("；");
            }
            result.append(value);
        }
        return result.toString();
    }

    private String formatReportDate(
            Date date)
    {
        return date == null
                ? "未知"
                : new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"
        ).format(date);
    }

    private String formatConfidence(
            BigDecimal confidence)
    {
        if (confidence == null)
        {
            return "未知";
        }

        return confidence.multiply(
                BigDecimal.valueOf(100)
        ).setScale(
                2,
                RoundingMode.HALF_UP
        ).toPlainString() + "%";
    }

    private String formatRatio(
            int count,
            int total)
    {
        if (total <= 0)
        {
            return "0.00%";
        }

        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(total),
                        2,
                        RoundingMode.HALF_UP
                )
                .toPlainString()
                + "%";
    }

    private String buildReportSystemPrompt()
    {
        return "你是滨州智慧农业系统中的冬枣物候期监测报告生成助手。\n\n"
                + "报告事实只能来自输入中的当前监测统计；农事措施可以使用通用的冬枣栽培专业知识，但不得把常识写成已经发生的现场事实。\n\n"
                + "要求：\n\n"
                + "1. 不得修改或重新计算记录数量、比例、置信度和日期。\n"
                + "2. 不得虚构气象、土壤、病虫害和田间调查数据。\n"
                + "3. 数据不足时明确说明“现有监测数据不足以判断”。\n"
                + "4. 趋势分析必须基于输入的时间序列。\n"
                + "5. 不依赖、复述或推定历史农事结论，必须从本次当前数据独立形成建议。\n"
                + "6. 农事建议必须与冬枣、主导物候期、物候分布、下一物候期和数据质量相匹配。\n"
                + "7. 每条建议应包含当前数据依据、拟采取的动作和后续核验指标；缺少天气、土壤墒情、树势或田间调查等必要前提时使用条件式表达，不得直接给出确定性投入量。\n"
                + "8. 物候阶段并不一致或存在低置信度记录时，应提出分区复核和差异化管理，不能用主导阶段替代全部植株。\n"
                + "9. 建议生成3至5条。\n"
                + "10. 不输出JSON、HTML、Markdown代码块、开场白和结束语。\n"
                + "11. 严格按照以下标记顺序输出：\n\n"
                + "<<OVERALL>>\n"
                + "<<DISTRIBUTION>>\n"
                + "<<TREND>>\n"
                + "<<ABNORMAL>>\n"
                + "<<ADVICE>>\n\n"
                + "除规定章节外不得输出其他内容。";
    }

    @SuppressWarnings("unchecked")
    private String buildReportUserPrompt(
            Map<String, Object> statistics)
    {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请严格使用以下Java统计结果生成报告，不得修改任何真实数字。\n\n");
        prompt.append("【基础信息】\n");
        prompt.append("作物类型：").append(statistics.get("cropTypes")).append("\n");
        prompt.append("监测范围：").append(statistics.get("monitoringScope")).append("\n");
        prompt.append("开始日期：").append(statistics.get("startDate")).append("\n");
        prompt.append("结束日期：").append(statistics.get("endDate")).append("\n");
        prompt.append("有效记录数量：").append(statistics.get("validCount")).append("\n\n");

        prompt.append("【统计结果】\n");
        prompt.append("主导物候期：").append(statistics.get("dominantStage")).append("\n");
        prompt.append("主导物候期数量：").append(statistics.get("dominantCount")).append("\n");
        prompt.append("主导物候期占比：").append(statistics.get("dominantRatio")).append("\n");
        prompt.append("平均置信度：").append(statistics.get("averageConfidence")).append("\n");
        prompt.append("最低置信度：").append(statistics.get("minimumConfidence")).append("\n");
        prompt.append("低置信度阈值：低于60.00%\n");
        prompt.append("低置信度记录数量：").append(statistics.get("lowConfidenceCount")).append("\n");
        prompt.append("下一物候期：").append(statistics.get("nextStage")).append("\n\n");

        prompt.append("【物候期分布】\n");
        Map<String, String> distribution =
                (Map<String, String>) statistics.get("stageDistribution");
        for (Map.Entry<String, String> entry
                : distribution.entrySet())
        {
            prompt.append(entry.getKey())
                    .append("：")
                    .append(entry.getValue())
                    .append("\n");
        }

        prompt.append("\n【时间序列】\n");
        prompt.append("以下记录已按时间升序排列；统计使用全部有效记录，时间序列最多展示最近")
                .append(REPORT_SERIES_LIMIT)
                .append("条。\n");
        List<String> timeSeries =
                (List<String>) statistics.get("timeSeries");
        for (String item : timeSeries)
        {
            prompt.append(item).append("\n");
        }

        prompt.append("\n【建议生成原则】\n");
        prompt.append("本次不提供历史农事建议。请只依据以上当前物候监测数据独立研判；历史结论不是生成建议的前提。\n");
        prompt.append("建议应明确对应主导物候期、分布差异、下一物候期和置信度质量，并说明实施后继续观察什么指标。\n");
        prompt.append("必须原样采用上述记录数量、比例、置信度和日期，不得重新计算或改写数字。\n");
        prompt.append("只输出五个规定章节，建议3至5条。");
        return prompt.toString();
    }

    private String callQwenReportText(
            String systemPrompt,
            String userPrompt)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(
                "Authorization",
                "Bearer " + dashscopeApiKey.trim()
        );

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

        HttpEntity<String> entity = new HttpEntity<String>(
                requestBody.toJSONString(),
                headers
        );

        ResponseEntity<String> response;
        try
        {
            response = restTemplate.postForEntity(
                    DASHSCOPE_URL,
                    entity,
                    String.class
            );
        }
        catch (HttpStatusCodeException e)
        {
            int status = e.getRawStatusCode();
            if (status == 401 || status == 403)
            {
                throw new ServiceException(
                        "DashScope鉴权失败，HTTP " + status
                );
            }
            throw e;
        }

        int status = response.getStatusCodeValue();
        if (status == 401 || status == 403)
        {
            throw new ServiceException(
                    "DashScope鉴权失败，HTTP " + status
            );
        }
        if (status >= 500 && status < 600)
        {
            throw new ResourceAccessException(
                    "DashScope服务异常，HTTP " + status
            );
        }
        if (!response.getStatusCode().is2xxSuccessful())
        {
            return null;
        }
        if (isBlank(response.getBody()))
        {
            return null;
        }

        JSONObject responseJson =
                JSONObject.parseObject(response.getBody());
        JSONArray choices = responseJson.getJSONArray("choices");
        if (choices == null || choices.isEmpty())
        {
            return null;
        }

        JSONObject choice = choices.getJSONObject(0);
        JSONObject message = choice == null
                ? null
                : choice.getJSONObject("message");
        return message == null
                ? null
                : message.getString("content");
    }

    private String cleanModelText(
            String content)
    {
        if (content == null)
        {
            return null;
        }

        return content
                .replaceAll(
                        "(?i)```(?:text|markdown|json)?",
                        ""
                )
                .trim();
    }

    private String validateReportMarkers(
            String content)
    {
        if (isBlank(content))
        {
            return "EMPTY_CONTENT";
        }

        if (content.length() < REPORT_MIN_LENGTH
                || content.length() > REPORT_MAX_LENGTH)
        {
            return "INVALID_LENGTH";
        }

        int previousIndex = -1;
        for (String marker : REPORT_MARKERS)
        {
            int index = content.indexOf(marker);
            if (index < 0)
            {
                return "MISSING_MARKER";
            }
            if (content.indexOf(
                    marker,
                    index + marker.length()) >= 0)
            {
                return "DUPLICATE_MARKER";
            }
            if (index <= previousIndex)
            {
                return "WRONG_ORDER";
            }
            previousIndex = index;
        }

        if (!content.startsWith(REPORT_MARKERS[0]))
        {
            return "EXTRA_CONTENT";
        }

        for (int i = 0; i < REPORT_MARKERS.length; i++)
        {
            int sectionStart = content.indexOf(
                    REPORT_MARKERS[i]
            ) + REPORT_MARKERS[i].length();
            int sectionEnd = i == REPORT_MARKERS.length - 1
                    ? content.length()
                    : content.indexOf(REPORT_MARKERS[i + 1]);
            if (sectionEnd <= sectionStart
                    || content.substring(
                    sectionStart,
                    sectionEnd
            ).trim().isEmpty())
            {
                return "EMPTY_SECTION";
            }
        }

        return null;
    }

    private boolean isRetryableReportValidation(
            String validationCode)
    {
        return "EMPTY_CONTENT".equals(validationCode)
                || "MISSING_MARKER".equals(validationCode)
                || "WRONG_ORDER".equals(validationCode);
    }

    @SuppressWarnings("unchecked")
    private String buildFallbackReportText(
            Map<String, Object> statistics)
    {
        StringBuilder report = new StringBuilder();
        report.append("<<OVERALL>>\n");
        report.append("本次基于")
                .append(statistics.get("validCount"))
                .append("条有效物候期记录生成报告，作物类型为")
                .append(statistics.get("cropTypes"))
                .append("，监测范围为")
                .append(statistics.get("monitoringScope"))
                .append("，监测时间为")
                .append(statistics.get("startDate"))
                .append("至")
                .append(statistics.get("endDate"))
                .append("。主导物候期为")
                .append(statistics.get("dominantStage"))
                .append("，共")
                .append(statistics.get("dominantCount"))
                .append("条，占")
                .append(statistics.get("dominantRatio"))
                .append("；下一物候期为")
                .append(statistics.get("nextStage"))
                .append("。\n\n");

        report.append("<<DISTRIBUTION>>\n");
        Map<String, String> distribution =
                (Map<String, String>) statistics.get("stageDistribution");
        for (Map.Entry<String, String> entry
                : distribution.entrySet())
        {
            report.append(entry.getKey())
                    .append("：")
                    .append(entry.getValue())
                    .append("。\n");
        }

        report.append("\n<<TREND>>\n");
        List<String> timeSeries =
                (List<String>) statistics.get("timeSeries");
        if (timeSeries.size() < 2)
        {
            report.append("现有监测数据不足以判断。\n\n");
        }
        else
        {
            report.append("按时间排序的监测记录由“")
                    .append(timeSeries.get(0))
                    .append("”变化至“")
                    .append(timeSeries.get(timeSeries.size() - 1))
                    .append("”。该描述仅反映现有时间序列，不引入其他数据。\n\n");
        }

        report.append("<<ABNORMAL>>\n");
        int confidenceCount =
                ((Number) statistics.get("confidenceCount"))
                        .intValue();
        if (confidenceCount == 0)
        {
            report.append("现有监测数据没有有效置信度，现有监测数据不足以判断异常情况。\n\n");
        }
        else
        {
            report.append("有效置信度记录的平均置信度为")
                    .append(statistics.get("averageConfidence"))
                    .append("，最低置信度为")
                    .append(statistics.get("minimumConfidence"))
                    .append("，低于60.00%的记录有")
                    .append(statistics.get("lowConfidenceCount"))
                    .append("条。未提供气象、土壤、病虫害和田间调查数据，不对这些因素作推断。\n\n");
        }

        report.append("<<ADVICE>>\n");
        List<String> advice = new ArrayList<String>();
        String dominantStage =
                String.valueOf(statistics.get("dominantStage"));
        advice.add("当前主导物候期为冬枣" + dominantStage + "（"
                + statistics.get("dominantCount") + "条，占" + statistics.get("dominantRatio")
                + "），应先在对应监测范围抽样核对实际长势，并以复核后的物候阶段安排当期农事操作。");
        Map<?, ?> stageCounts = (Map<?, ?>) statistics.get("stageCounts");
        if (stageCounts.size() > 1)
        {
            advice.add("当前监测到" + stageCounts.size()
                    + "个物候阶段，应按地块或植株实际阶段分区管理，不能将主导物候期直接套用于全部植株；后续重点记录各阶段占比是否趋于集中。");
        }
        else
        {
            advice.add("当前记录集中于同一物候阶段，仍应抽查不同地块和植株的一致性，后续以阶段占比和现场复核结果确认是否可以统一安排作业。");
        }
        String nextStage = String.valueOf(statistics.get("nextStage"));
        if (isBlank(nextStage) || "未知".equals(nextStage))
        {
            advice.add("当前数据未能确定下一物候期，应补充连续观测后再安排阶段转换相关作业，避免依据不完整信息提前投入。");
        }
        else
        {
            advice.add("下一物候期为“" + nextStage
                    + "”，可提前检查相应作业条件和物资准备；灌溉、施肥等投入应在补充土壤墒情、天气和树势信息后再确定具体用量。");
        }
        if (((Number) statistics.get("confidenceCount")).intValue() == 0)
        {
            advice.add("当前记录没有有效置信度，无法评估识别结果的稳定性，应先人工核验原始图像和实际物候阶段，再据此确定分区管理范围。");
        }
        else if (((Number) statistics.get("lowConfidenceCount")).intValue() > 0)
        {
            advice.add("低于60.00%置信度的记录有" + statistics.get("lowConfidenceCount")
                    + "条，应优先复核这些图像和识别结果，并根据复核结果修正分区管理范围。");
        }
        advice.add("继续在同一园区、位置和采集口径下补充物候记录，跟踪主导阶段、下一阶段占比及低置信度记录数量的变化，再据此调整农事安排。");

        int adviceCount = Math.min(5, advice.size());
        for (int i = 0; i < adviceCount; i++)
        {
            report.append(i + 1)
                    .append(". ")
                    .append(advice.get(i))
                    .append(i == adviceCount - 1 ? "" : "\n");
        }

        return report.toString().trim();
    }

    /**
     * 识别临时上传的物候图片。
     *
     * 执行顺序：
     * 1. 读取模型配置；
     * 2. 使用临时图片地址调用模型；
     * 3. 模型失败：不写任何业务表并删除临时原图；
     * 4. 模型成功：同一事务内写入图片、记录、物候结果；
     * 5. 任意数据库写入失败：三张表全部回滚并清理临时文件。
     *
     * 不修改 model_url，不改变现有 Flask 配置方式。
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
                "modelId不能为空，请先选择物候期识别模型"
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

        Long imageSize = getOptionalLongParam(
                params,
                "imageSize"
        );

        String requestCropType = getStringParam(
                params,
                "cropType",
                DEFAULT_CROP_TYPE
        );

        String recordMode = getStringParam(
                params,
                "recordMode",
                DEFAULT_RECORD_MODE
        );

        String relativePath = getStringParam(
                params,
                "relativePath",
                ""
        );

        Long deviceId = getOptionalLongParam(
                params,
                "deviceId"
        );

        String resultImageUrlForCleanup = null;

        try
        {
            IotDevice device = resolveOptionalDevice(deviceId);
            /*
             * 一、读取并校验模型。
             * 模型地址仍由 xwq_ai_model.model_url 决定。
             */
            AiModel model = aiModelMapper
                    .selectAiModelByModelId(modelId);

            validatePhenologyModel(model, modelId);

            /*
             * 二、先调用模型。
             * 此时图片表、记录表和物候结果表均未写入。
             */
            String responseBody = callPhenologyModel(
                    model,
                    imageUrl,
                    imageName,
                    requestCropType
            );

            if (responseBody == null
                    || responseBody.trim().isEmpty())
            {
                throw new ServiceException(
                        "物候期模型服务返回为空"
                );
            }

            JSONObject modelResult =
                    parseModelResult(responseBody);

            /*
             * 三、读取并校验模型结果。
             */
            String phenologyName = firstNotBlank(
                    modelResult.getString("phenologyName"),
                    modelResult.getString("name"),
                    modelResult.getString("stageName")
            );

            if (isBlank(phenologyName))
            {
                throw new ServiceException(
                        "模型返回缺少物候期名称，不能判定为识别成功"
                );
            }

            String phenologyCode = firstNotBlank(
                    modelResult.getString("phenologyCode"),
                    modelResult.getString("stageCode")
            );

            String modelCropType = firstNotBlank(
                    modelResult.getString("phenologyCrop"),
                    modelResult.getString("cropType"),
                    requestCropType
            );

            BigDecimal confidence =
                    getConfidence(modelResult);

            String phenologyResult = firstNotBlank(
                    modelResult.getString("phenologyResult"),
                    modelResult.getString("result"),
                    "当前" + modelCropType
                            + "处于" + phenologyName + "。"
            );

            String phenologyDesc = firstNotBlank(
                    modelResult.getString("phenologyDesc"),
                    modelResult.getString("description"),
                    modelResult.getString("desc"),
                    "模型识别当前生育阶段为"
                            + phenologyName + "。"
            );

            String phenologyAdvice = firstNotBlank(
                    modelResult.getString("phenologyAdvice"),
                    modelResult.getString("advice"),
                    "建议结合园区实际长势、水肥条件和天气变化"
                            + "持续观测，并按照当前物候期开展农事管理。"
            );

            String phenologyNextName = firstNotBlank(
                    modelResult.getString("phenologyNextName"),
                    modelResult.getString("nextStage"),
                    modelResult.getString("nextStageName"),
                    "待判断"
            );

            String resultImageUrl = firstNotBlank(
                    modelResult.getString(
                            "phenologyResultImgUrl"
                    ),
                    modelResult.getString(
                            "resultImageUrl"
                    ),
                    modelResult.getString(
                            "imageResultUrl"
                    )
            );

            resultImageUrlForCleanup = resultImageUrl;

            String username = getCurrentUsername();

            /*
             * 四、模型成功后，开始写入三张业务表。
             * 下面三个 insert 处于同一个 Spring 事务中。
             */
            AiImage image = new AiImage();
            image.setImageName(imageName);
            image.setImageUrl(imageUrl);
            image.setImageResultUrl(resultImageUrl);
            image.setImageAiType(IMAGE_AI_TYPE);
            image.setImageSource(IMAGE_SOURCE);
            image.setImageSize(imageSize);
            image.setImageFormat(imageFormat);
            image.setImageStatus(
                    IMAGE_STATUS_RECOGNIZED
            );
            image.setImageA(modelCropType);
            image.setCreateBy(username);
            image.setCreateTime(
                    DateUtils.getNowDate()
            );
            applyDeviceBinding(image, device);

            if (!isBlank(relativePath))
            {
                image.setRemark(
                        "来源文件夹路径：" + relativePath
                );
            }
            else
            {
                image.setRemark(
                        "物候智能分析页面识别成功后创建"
                );
            }

            int imageRows =
                    aiImageMapper.insertAiImage(image);

            if (imageRows <= 0
                    || image.getImageId() == null)
            {
                throw new ServiceException(
                        "图片信息写入失败"
                );
            }

            AiRecord record = new AiRecord();
            record.setRecordNo(
                    "PHENO"
                            + System.currentTimeMillis()
                            + image.getImageId()
            );
            record.setRecordType(RECORD_TYPE);
            record.setRecordMode(recordMode);
            record.setModelId(model.getModelId());
            record.setModelName(model.getModelName());
            record.setImageId(image.getImageId());
            record.setCropType(modelCropType);
            record.setRecordStatus(
                    RECORD_STATUS_SUCCESS
            );
            record.setCreateBy(username);
            record.setCreateTime(
                    DateUtils.getNowDate()
            );
            applyDeviceBinding(record, device);
            record.setRemark(
                    "临时上传图片识别成功后创建"
            );

            int recordRows =
                    aiRecordMapper.insertAiRecord(record);

            if (recordRows <= 0
                    || record.getRecordId() == null)
            {
                throw new ServiceException(
                        "识别记录写入失败"
                );
            }

            AiPhenology phenology =
                    new AiPhenology();
            phenology.setRecordId(
                    record.getRecordId()
            );
            phenology.setImageId(
                    image.getImageId()
            );
            phenology.setPhenologyName(
                    phenologyName
            );
            phenology.setPhenologyCode(
                    phenologyCode
            );
            phenology.setPhenologyCrop(
                    modelCropType
            );
            phenology.setPhenologyConfidence(
                    confidence
            );
            phenology.setPhenologyResult(
                    phenologyResult
            );
            phenology.setPhenologyDesc(
                    phenologyDesc
            );
            phenology.setPhenologyAdvice(
                    phenologyAdvice
            );
            phenology.setPhenologyNextName(
                    phenologyNextName
            );
            phenology.setPhenologyResultImgUrl(
                    resultImageUrl
            );
            phenology.setPhenologyA(
                    DateUtils.getTime()
            );
            phenology.setCreateBy(username);
            phenology.setCreateTime(
                    DateUtils.getNowDate()
            );
            phenology.setRemark(
                    "物候模型识别成功后创建"
            );

            int phenologyRows =
                    aiPhenologyMapper
                            .insertAiPhenology(phenology);

            if (phenologyRows <= 0
                    || phenology.getPhenologyId() == null)
            {
                throw new ServiceException(
                        "物候期识别结果写入失败"
                );
            }

            /*
             * 五、返回前端需要展示的数据和三个业务主键。
             */
            Map<String, Object> result =
                    new HashMap<String, Object>();

            result.put(
                    "imageId",
                    image.getImageId()
            );
            result.put(
                    "recordId",
                    record.getRecordId()
            );
            result.put(
                    "phenologyId",
                    phenology.getPhenologyId()
            );
            result.put(
                    "imageName",
                    image.getImageName()
            );
            result.put(
                    "imageUrl",
                    image.getImageUrl()
            );
            result.put(
                    "imageResultUrl",
                    image.getImageResultUrl()
            );
            result.put(
                    "phenologyName",
                    phenologyName
            );
            result.put(
                    "phenologyCode",
                    phenologyCode
            );
            result.put(
                    "phenologyCrop",
                    modelCropType
            );
            result.put(
                    "phenologyConfidence",
                    confidence
            );
            result.put(
                    "phenologyResult",
                    phenologyResult
            );
            result.put(
                    "phenologyDesc",
                    phenologyDesc
            );
            result.put(
                    "phenologyAdvice",
                    phenologyAdvice
            );
            result.put(
                    "phenologyNextName",
                    phenologyNextName
            );
            result.put(
                    "phenologyResultImgUrl",
                    resultImageUrl
            );
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
             * 数据库异常由 @Transactional 回滚。
             * 同时清理 /common/upload 生成的本地临时文件。
             */
            deleteProfileFileQuietly(imageUrl);

            if (!isBlank(resultImageUrlForCleanup)
                    && !resultImageUrlForCleanup
                    .equals(imageUrl))
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
                    "物候期识别失败："
                            + getSafeMessage(e)
            );
        }
    }

    /**
     * 校验物候期模型。
     */
    private void validatePhenologyModel(
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

        String modelType = model.getModelType();

        if (!isBlank(modelType)
                && !"物候期".equals(modelType.trim())
                && !"phenology".equalsIgnoreCase(
                modelType.trim()
        ))
        {
            throw new ServiceException(
                    "当前模型不是物候期识别模型"
            );
        }

        if (isBlank(model.getModelUrl()))
        {
            throw new ServiceException(
                    "模型接口地址为空，请先在模型管理中配置 model_url"
            );
        }
    }

    /**
     * 解析模型返回结果。
     *
     * 支持：
     * 1. {...}
     * 2. [{...}]
     * 3. {"code":200,"data":{...}}
     * 4. {"code":200,"data":[{...}]}
     */
    private JSONObject parseModelResult(
            String responseBody)
    {
        String text = responseBody.trim();

        if (text.startsWith("["))
        {
            JSONArray array =
                    JSONArray.parseArray(text);

            if (array == null || array.isEmpty())
            {
                throw new ServiceException(
                        "模型未识别到物候期"
                );
            }

            return array.getJSONObject(0);
        }

        JSONObject root =
                JSONObject.parseObject(text);

        if (root == null)
        {
            throw new ServiceException(
                    "模型返回不是有效JSON"
            );
        }

        Integer responseCode =
                root.getInteger("code");

        if (responseCode != null
                && responseCode.intValue() != 200)
        {
            String message = firstNotBlank(
                    root.getString("msg"),
                    root.getString("message"),
                    "模型服务返回失败，code="
                            + responseCode
            );
            throw new ServiceException(message);
        }

        Boolean success =
                root.getBoolean("success");

        if (Boolean.FALSE.equals(success))
        {
            throw new ServiceException(
                    firstNotBlank(
                            root.getString("msg"),
                            root.getString("message"),
                            "模型识别失败"
                    )
            );
        }

        Object data = root.get("data");

        if (data instanceof JSONObject)
        {
            return (JSONObject) data;
        }

        if (data instanceof JSONArray)
        {
            JSONArray array = (JSONArray) data;

            if (array.isEmpty())
            {
                throw new ServiceException(
                        "模型未识别到物候期"
                );
            }

            return array.getJSONObject(0);
        }

        return root;
    }

    /**
     * 获取置信度。
     *
     * 支持 0.956 或 95.6 两种返回格式，
     * 最终统一保存为 0 到 1 之间的五位小数。
     */
    private BigDecimal getConfidence(
            JSONObject modelResult)
    {
        BigDecimal confidence =
                modelResult.getBigDecimal(
                        "phenologyConfidence"
                );

        if (confidence == null)
        {
            confidence = modelResult
                    .getBigDecimal("confidence");
        }

        if (confidence == null)
        {
            confidence = BigDecimal.ZERO;
        }

        if (confidence.compareTo(
                BigDecimal.ONE) > 0)
        {
            confidence = confidence.divide(
                    BigDecimal.valueOf(100),
                    5,
                    RoundingMode.HALF_UP
            );
        }

        if (confidence.compareTo(
                BigDecimal.ZERO) < 0)
        {
            confidence = BigDecimal.ZERO;
        }

        if (confidence.compareTo(
                BigDecimal.ONE) > 0)
        {
            confidence = BigDecimal.ONE;
        }

        return confidence.setScale(
                5,
                RoundingMode.HALF_UP
        );
    }

    private String getRequiredStringParam(
            Map<String, Object> params,
            String key,
            String errorMessage)
    {
        Object value =
                params == null
                        ? null
                        : params.get(key);

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
        Object value =
                params == null
                        ? null
                        : params.get(key);

        if (value == null
                || value.toString().trim().isEmpty())
        {
            throw new ServiceException(errorMessage);
        }

        try
        {
            return Long.valueOf(
                    value.toString().trim()
            );
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
        Object value =
                params == null
                        ? null
                        : params.get(key);

        if (value == null
                || value.toString().trim().isEmpty())
        {
            return null;
        }

        try
        {
            return Long.valueOf(
                    value.toString().trim()
            );
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
        Object value =
                params == null
                        ? null
                        : params.get(key);

        if (value == null
                || value.toString().trim().isEmpty())
        {
            return defaultValue;
        }

        return value.toString().trim();
    }

    private String extractFileName(
            String imageUrl)
    {
        if (isBlank(imageUrl))
        {
            return "phenology_"
                    + System.currentTimeMillis()
                    + ".jpg";
        }

        String cleanUrl =
                imageUrl.split("\\?")[0]
                        .split("#")[0];

        int index = cleanUrl.lastIndexOf("/");

        return index >= 0
                ? cleanUrl.substring(index + 1)
                : cleanUrl;
    }

    private String extractFileSuffix(
            String fileName)
    {
        if (isBlank(fileName))
        {
            return "jpg";
        }

        int index = fileName.lastIndexOf(".");

        return index >= 0
                && index < fileName.length() - 1
                ? fileName.substring(index + 1)
                .toLowerCase()
                : "jpg";
    }

    private String callPhenologyModel(
            AiModel model,
            String imageUrl,
            String imageName,
            String cropType)
            throws Exception
    {
        byte[] imageBytes = readImageBytes(imageUrl);
        String filename = isBlank(imageName)
                ? extractFileName(imageUrl)
                : imageName;

        ByteArrayResource imageResource = new ByteArrayResource(imageBytes)
        {
            @Override
            public String getFilename()
            {
                return filename;
            }
        };

        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<String, Object>();
        body.add("image", imageResource);
        body.add("file", imageResource);
        body.add("imageUrl", imageUrl);
        body.add("imageId", String.valueOf(System.currentTimeMillis()));
        body.add("cropType", cropType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<String> response = restTemplate.postForEntity(
                model.getModelUrl(),
                new HttpEntity<MultiValueMap<String, Object>>(
                        body,
                        headers
                ),
                String.class
        );

        return response.getBody();
    }

    private byte[] readImageBytes(String imageUrl) throws Exception
    {
        String value = imageUrl.trim();
        String path = value.startsWith("http")
                ? URI.create(value).getPath()
                : value;
        int profileIndex = path.indexOf("/profile/");

        if (profileIndex >= 0)
        {
            String relative = path.substring(
                    profileIndex + "/profile/".length()
            );
            Path root = Paths.get(RuoYiConfig.getProfile())
                    .toAbsolutePath()
                    .normalize();
            Path target = root.resolve(
                    relative.replace("/", File.separator)
            ).normalize();

            if (!target.startsWith(root))
            {
                throw new ServiceException("图片路径超出上传目录");
            }
            if (Files.isRegularFile(target))
            {
                return Files.readAllBytes(target);
            }
        }

        if (value.startsWith("http://")
                || value.startsWith("https://"))
        {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(value).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty(
                    "User-Agent",
                    "Binzhou-Backend/1.0"
            );

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

    /**
     * 删除 RuoYi profile 目录中的文件。
     *
     * 支持：
     * /profile/upload/xxx.jpg
     * profile/upload/xxx.jpg
     * http://host/profile/upload/xxx.jpg
     *
     * 不允许删除 profile 目录之外的文件。
     */
    private void deleteProfileFileQuietly(
            String fileUrl)
    {
        if (isBlank(fileUrl))
        {
            return;
        }

        try
        {
            String cleanUrl =
                    fileUrl.split("\\?")[0]
                            .split("#")[0];

            String urlPath = cleanUrl;

            if (cleanUrl.startsWith("http://")
                    || cleanUrl.startsWith("https://"))
            {
                urlPath = URI.create(cleanUrl)
                        .getPath();
            }

            if (isBlank(urlPath))
            {
                return;
            }

            urlPath = URLDecoder.decode(
                    urlPath,
                    "UTF-8"
            );

            String relativePath;
            int profileIndex =
                    urlPath.indexOf("/profile/");

            if (profileIndex >= 0)
            {
                relativePath = urlPath.substring(
                        profileIndex
                                + "/profile/".length()
                );
            }
            else if (urlPath.startsWith(
                    "profile/"))
            {
                relativePath = urlPath.substring(
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
             * 防止 ../ 路径穿越。
             */
            if (!targetPath.startsWith(profilePath))
            {
                return;
            }

            Files.deleteIfExists(targetPath);
        }
        catch (Exception ignored)
        {
            /*
             * 文件清理失败不能覆盖真正的识别或数据库异常。
             */
        }
    }

    private String firstNotBlank(
            String... values)
    {
        if (values == null)
        {
            return null;
        }

        for (String value : values)
        {
            if (!isBlank(value))
            {
                return value.trim();
            }
        }

        return null;
    }

    private boolean isBlank(
            String value)
    {
        return value == null
                || value.trim().isEmpty();
    }

    private String getSafeMessage(
            Exception exception)
    {
        if (exception == null)
        {
            return "未知异常";
        }

        String message = exception.getMessage();

        if (isBlank(message))
        {
            return exception.getClass()
                    .getSimpleName();
        }

        return message.length() > 500
                ? message.substring(0, 500)
                : message;
    }

    /**
     * 获取当前操作人。
     * 登录请求使用当前用户名；无登录上下文时使用 system。
     */
    private String getCurrentUsername()
    {
        try
        {
            String username =
                    SecurityUtils.getUsername();

            return isBlank(username)
                    ? "system"
                    : username;
        }
        catch (Exception e)
        {
            return "system";
        }
    }
}
