package com.ruoyi.binZhou.wr.domain;

import java.util.List;
import java.util.Map;

/**
 * 农企/农户综合农情报表数据传输对象
 */
public class FarmerReportDTO {
    private String title;
    private String reportDate;
    private String targetUser;
    private String summary;

    // 物联网环境预警列表
    private List<Map<String, Object>> iotAlerts;

    // 虫情预警列表
    private List<Map<String, Object>> pestAlerts;

    // AI 综合农事建议
    private List<String> aiSuggestions;

    // --- Getters and Setters ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }

    public String getTargetUser() { return targetUser; }
    public void setTargetUser(String targetUser) { this.targetUser = targetUser; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<Map<String, Object>> getIotAlerts() { return iotAlerts; }
    public void setIotAlerts(List<Map<String, Object>> iotAlerts) { this.iotAlerts = iotAlerts; }

    public List<Map<String, Object>> getPestAlerts() { return pestAlerts; }
    public void setPestAlerts(List<Map<String, Object>> pestAlerts) { this.pestAlerts = pestAlerts; }

    public List<String> getAiSuggestions() { return aiSuggestions; }
    public void setAiSuggestions(List<String> aiSuggestions) { this.aiSuggestions = aiSuggestions; }
}
