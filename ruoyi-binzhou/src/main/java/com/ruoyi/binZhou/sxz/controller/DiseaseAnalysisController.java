package com.ruoyi.binZhou.sxz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.binZhou.sxz.domain.DiseaseAnalysisQuery;
import com.ruoyi.binZhou.sxz.service.IDiseaseAnalysisService;
import com.ruoyi.common.core.domain.AjaxResult;

@RestController
@RequestMapping("/sxz/disease-analysis")
public class DiseaseAnalysisController
{
    private final IDiseaseAnalysisService service;

    public DiseaseAnalysisController(IDiseaseAnalysisService service)
    {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public AjaxResult dashboard(DiseaseAnalysisQuery query)
    {
        return AjaxResult.success(service.getDashboard(query));
    }
}
