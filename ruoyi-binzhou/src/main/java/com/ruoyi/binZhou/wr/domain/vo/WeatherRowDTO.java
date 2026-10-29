package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

@Data
public class WeatherRowDTO {
    private Long id;
    private String time;
    private String device;
    private String indicator;
    private String value;
    private String levelText;
    private String levelClass;
    private String suggestion;
}
