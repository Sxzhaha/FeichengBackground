package com.ruoyi.binZhou.sxz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.binZhou.sxz.domain.DiseaseReportSummary;
import com.ruoyi.binZhou.sxz.service.DiseaseReportService;
import com.ruoyi.common.core.domain.AjaxResult;

@RestController
@RequestMapping("/sxz/disease-report")
public class DiseaseReportController
{
    private final DiseaseReportService service;

    public DiseaseReportController(DiseaseReportService service)
    {
        this.service = service;
    }

    @GetMapping("/summary")
    public AjaxResult summary(DiseaseReportSummary.Query query)
    {
        return AjaxResult.success(service.getSummary(query));
    }
}
