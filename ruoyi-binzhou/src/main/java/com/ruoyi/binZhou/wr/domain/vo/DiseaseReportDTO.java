package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class DiseaseReportDTO {
    private Boolean hasData;
    private Integer total;
    private Integer warningCount;
    private String warningRate;
    private String avgConfidence;
    private List<TypeStatDTO> typeStats;
    private LevelStatsDTO levelStats;
    private List<DiseaseRowDTO> topRows;
}
