package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

@Data
public class LiveWeatherDTO {
    private Boolean hasData;
    private String maxTemp;
    private String minTemp;
    private String avgHumidity;
    private String precipitation;
    private String avgWind;
}

