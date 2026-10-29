package com.ruoyi.binZhou.wr.service;

import com.ruoyi.binZhou.wr.domain.FarmerReportDTO;

public interface IFarmerReportService {
    /**
     * 根据用户ID和角色，动态聚合物联网、虫情、病虫害、物候期数据生成报表
     */
    FarmerReportDTO generateReportData(Long userId, String role);

    /**
     * 触发定时任务推送（供若依 quartz 模块调用）
     */
    void executeScheduledPush(String frequency);
}