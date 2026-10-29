package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

@Data
public class LevelStatsDTO {
    private Integer high;
    private Integer medium;
    private Integer low;
    private Integer unmarked;
    private String highRate;
    private String mediumRate;
    private String lowRate;
    private String unmarkedRate;
}