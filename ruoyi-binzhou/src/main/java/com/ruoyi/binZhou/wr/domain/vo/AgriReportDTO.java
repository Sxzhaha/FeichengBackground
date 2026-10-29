package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class AgriReportDTO {
    private ReportSummaryDTO summary;
    private WeatherReportDTO weather;
    private DiseaseReportDTO disease;
    private PestReportDTO pest;
    private PhenologyReportDTO phenology;
    private TaskReportDTO task;
    private LiveWeatherDTO live;
    private ForecastDTO forecast;
    private List<String> riskAnalysis;
    private List<FocusAreaDTO> focusAreas;
    private AdviceDTO advice;
    private List<ParkOptionDTO> parkOptions;
    private List<String> dataQualityNotes;
    private Boolean assessmentAvailable;
}
