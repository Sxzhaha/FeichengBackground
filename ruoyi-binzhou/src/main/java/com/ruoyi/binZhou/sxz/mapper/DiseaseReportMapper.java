package com.ruoyi.binZhou.sxz.mapper;

import java.util.List;
import com.ruoyi.binZhou.sxz.domain.DiseaseReportSummary;

public interface DiseaseReportMapper
{
    DiseaseReportSummary selectSummary(DiseaseReportSummary.Query query);
    List<DiseaseReportSummary.CategoryStat> selectCategories(DiseaseReportSummary.Query query);
    List<DiseaseReportSummary.AdviceItem> selectAdviceList(DiseaseReportSummary.Query query);
    List<DiseaseReportSummary.FocusRecord> selectFocusRecords(DiseaseReportSummary.Query query);
    List<DiseaseReportSummary.FocusRecord> selectRecords(DiseaseReportSummary.Query query);
}
