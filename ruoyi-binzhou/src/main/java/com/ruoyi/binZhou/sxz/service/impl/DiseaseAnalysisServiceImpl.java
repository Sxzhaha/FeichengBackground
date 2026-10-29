package com.ruoyi.binZhou.sxz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.sxz.domain.DiseaseAnalysisDashboard;
import com.ruoyi.binZhou.sxz.domain.DiseaseAnalysisQuery;
import com.ruoyi.binZhou.sxz.domain.EnvironmentCorrelationAnalysis;
import com.ruoyi.binZhou.sxz.mapper.DiseaseAnalysisMapper;
import com.ruoyi.binZhou.sxz.service.IDiseaseAnalysisService;
import com.ruoyi.binZhou.sxz.service.support.EnvironmentCorrelationCalculator;

@Service
public class DiseaseAnalysisServiceImpl implements IDiseaseAnalysisService
{
    private final DiseaseAnalysisMapper mapper;

    public DiseaseAnalysisServiceImpl(DiseaseAnalysisMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public DiseaseAnalysisDashboard getDashboard(DiseaseAnalysisQuery query)
    {
        normalizeQuery(query);
        DiseaseAnalysisDashboard dashboard = new DiseaseAnalysisDashboard();
        List<DiseaseAnalysisDashboard.CategoryStat> categories = mapper.selectCategoryStats(query);
        long total = categories.stream().mapToLong(item -> item.getCount() == null ? 0L : item.getCount()).sum();
        for (DiseaseAnalysisDashboard.CategoryStat item : categories)
        {
            BigDecimal rate = total == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(item.getCount() * 100D / total).setScale(2, RoundingMode.HALF_UP);
            item.setRate(rate);
        }
        dashboard.setTotalCount(total);
        dashboard.setCategoryCount(categories.size());
        dashboard.setCategories(categories);
        dashboard.setTrends(mapper.selectTrendStats(query));
        dashboard.setParks(mapper.selectParkStats(query));
        dashboard.setRecords(mapper.selectRecentRecords(query));
        dashboard.setParkOptions(mapper.selectParkOptions());
        dashboard.setDeviceOptions(mapper.selectDeviceOptions(query));
        dashboard.setEnvironment(mapper.selectEnvironmentStats(query));
        Map<String, EnvironmentCorrelationAnalysis> correlationByDisease = buildCorrelationByDisease(query);
        dashboard.setEnvironmentCorrelationByDisease(correlationByDisease);
        dashboard.setEnvironmentCorrelation(correlationByDisease.values().stream().findFirst().orElse(new EnvironmentCorrelationAnalysis()));
        dashboard.setGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return dashboard;
    }

    private Map<String, EnvironmentCorrelationAnalysis> buildCorrelationByDisease(DiseaseAnalysisQuery query)
    {
        List<EnvironmentCorrelationAnalysis.DailyRecord> rows = mapper.selectCorrelationDailyRecords(query);
        Map<String, List<EnvironmentCorrelationAnalysis.DailyRecord>> grouped = rows.stream()
                .filter(item -> item.getDiseaseName() != null && !item.getDiseaseName().trim().isEmpty())
                .collect(Collectors.groupingBy(EnvironmentCorrelationAnalysis.DailyRecord::getDiseaseName, LinkedHashMap::new, Collectors.toList()));
        Map<String, EnvironmentCorrelationAnalysis> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<EnvironmentCorrelationAnalysis.DailyRecord>> entry : grouped.entrySet())
        {
            result.put(entry.getKey(), EnvironmentCorrelationCalculator.calculate(new ArrayList<>(entry.getValue())));
        }
        return result;
    }

    private void normalizeQuery(DiseaseAnalysisQuery query)
    {
        if (query.getDays() == null || (query.getDays() != 7 && query.getDays() != 30 && query.getDays() != 90))
        {
            query.setDays(30);
        }
        if (query.getParkId() != null && query.getParkId() <= 0)
        {
            query.setParkId(null);
        }
        if (query.getDeviceId() != null && query.getDeviceId() <= 0)
        {
            query.setDeviceId(null);
        }
        if (query.getEnvironmentDiseaseName() != null && query.getEnvironmentDiseaseName().trim().isEmpty())
        {
            query.setEnvironmentDiseaseName(null);
        }
    }
}
