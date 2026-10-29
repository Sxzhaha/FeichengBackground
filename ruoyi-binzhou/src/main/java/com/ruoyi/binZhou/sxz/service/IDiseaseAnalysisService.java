package com.ruoyi.binZhou.sxz.service;

import com.ruoyi.binZhou.sxz.domain.DiseaseAnalysisDashboard;
import com.ruoyi.binZhou.sxz.domain.DiseaseAnalysisQuery;

public interface IDiseaseAnalysisService
{
    DiseaseAnalysisDashboard getDashboard(DiseaseAnalysisQuery query);
}
