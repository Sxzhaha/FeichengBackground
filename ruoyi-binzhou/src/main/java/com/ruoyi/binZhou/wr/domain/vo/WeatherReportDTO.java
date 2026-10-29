package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;
import java.util.List;

@Data
public class WeatherReportDTO {
    private Boolean hasData;
    private Integer total;
    private LevelStatsDTO levelStats;
    private WeatherExtremesDTO extremes;
    private List<WeatherRowDTO> topRows;
}
