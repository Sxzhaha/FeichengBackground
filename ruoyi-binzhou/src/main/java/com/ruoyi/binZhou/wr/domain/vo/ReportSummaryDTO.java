package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

@Data
public class ReportSummaryDTO {
    private String weatherCountText;
    private String weatherDescription;
    private String diseaseCountText;
    private String diseaseDescription;
    private String pestCountText;
    private String pestDescription;
    private String phenologyCountText;
    private String phenologyDescription;
    private String taskCountText;
    private String taskDescription;
    private String deviceCountText;
    private String deviceDescription;
    private String overallRisk;
    private String overallRiskClass;
    private String conclusion;
    private String riskAssessment;
}