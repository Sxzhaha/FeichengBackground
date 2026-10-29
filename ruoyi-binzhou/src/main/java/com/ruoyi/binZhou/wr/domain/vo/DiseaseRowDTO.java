package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

@Data
public class DiseaseRowDTO {
    private Long id;
    private String name;
    private String confidence;
    private String levelText;
    private String levelClass;
    private String time;
}