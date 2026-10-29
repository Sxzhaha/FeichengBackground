package com.ruoyi.binZhou.wr.controller;

import com.ruoyi.binZhou.wr.domain.vo.AgriReportDTO;
import com.ruoyi.binZhou.wr.service.AgriReportService;
import com.ruoyi.binZhou.wr.service.AgriAdviceService;
import com.ruoyi.binZhou.wr.domain.vo.AgriAdviceRequest;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 滨州冬枣园区综合农情监测报告
 */
@RestController
@RequestMapping("/binZhou/wr/report")
public class AgriReportController {

    @Autowired
    private AgriReportService agriReportService;

    @Autowired
    private AgriAdviceService agriAdviceService;

    @PostMapping("/agriAdvice")
    public AjaxResult agriAdvice(@RequestBody AgriAdviceRequest request) {
        return AjaxResult.success(agriAdviceService.generate(SecurityUtils.getUserId(), request));
    }

    @GetMapping("/agri")
    public AjaxResult agriReport(
            @RequestParam(required = false) Long parkId,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime) {
        AgriReportDTO report = agriReportService.buildAgriReport(parkId, beginTime, endTime);
        return AjaxResult.success(report);
    }
}
