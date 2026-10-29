package com.ruoyi.binZhou.wr.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.binZhou.wr.domain.FarmerReportDTO;
import com.ruoyi.binZhou.wr.service.IFarmerReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能集成农情报表控制层
 */
@RestController
@RequestMapping("/binZhou/wr/report")
public class FarmerReportController {

    @Autowired
    private IFarmerReportService reportService;

    /**
     * 获取最新多维报表数据（支持按角色进行数据隔离和聚合）
     * @param role 角色类型 (farmer:农户, county:县级, city:市级)
     */
    @GetMapping("/getLatest")
    public AjaxResult getLatestReport(@RequestParam(value = "role", defaultValue = "farmer") String role) {
        // 临时模拟当前登录用户ID为 100
        Long mockUserId = 100L;

        // 调用业务层进行物联网+虫情+病虫害+物候期的四维深度数据聚合
        FarmerReportDTO reportData = reportService.generateReportData(mockUserId, role);

        return AjaxResult.success(reportData);
    }
}