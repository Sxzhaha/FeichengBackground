package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

@Data
public class WeatherExtremesDTO {
    private String maxTemp;
    private String minSoilHum;
    private String precipitation;
    private String maxWind;
}