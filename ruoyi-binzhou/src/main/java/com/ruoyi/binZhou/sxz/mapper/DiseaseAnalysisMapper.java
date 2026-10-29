package com.ruoyi.binZhou.sxz.mapper;

import java.util.List;
import com.ruoyi.binZhou.sxz.domain.DiseaseAnalysisDashboard;
import com.ruoyi.binZhou.sxz.domain.DiseaseAnalysisQuery;
import com.ruoyi.binZhou.sxz.domain.EnvironmentCorrelationAnalysis;

public interface DiseaseAnalysisMapper
{
    List<DiseaseAnalysisDashboard.CategoryStat> selectCategoryStats(DiseaseAnalysisQuery query);
    List<DiseaseAnalysisDashboard.TrendStat> selectTrendStats(DiseaseAnalysisQuery query);
    List<DiseaseAnalysisDashboard.ParkStat> selectParkStats(DiseaseAnalysisQuery query);
    List<DiseaseAnalysisDashboard.RecentRecord> selectRecentRecords(DiseaseAnalysisQuery query);
    List<DiseaseAnalysisDashboard.ParkOption> selectParkOptions();
    List<DiseaseAnalysisDashboard.DeviceOption> selectDeviceOptions(DiseaseAnalysisQuery query);
    List<DiseaseAnalysisDashboard.EnvironmentStat> selectEnvironmentStats(DiseaseAnalysisQuery query);
    List<EnvironmentCorrelationAnalysis.DailyRecord> selectCorrelationDailyRecords(DiseaseAnalysisQuery query);
}
