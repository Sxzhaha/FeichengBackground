package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class TaskReportDTO {
    private Boolean hasData;
    private Integer total;
    private Integer finished;
    private Integer running;
    private Integer pending;
    private Integer overdue;
    private String finishRate;
    private List<TypeStatDTO> typeStats;
    private List<TaskRowDTO> overdueRows;
}