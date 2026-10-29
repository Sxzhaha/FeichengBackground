package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class PestReportDTO {
    private Boolean hasData;
    private Integer total;
    private Integer totalCount;
    private Integer warningCount;
    private String avgConfidence;
    private LevelStatsDTO levelStats;
    private List<TypeStatDTO> topSpecies;
}