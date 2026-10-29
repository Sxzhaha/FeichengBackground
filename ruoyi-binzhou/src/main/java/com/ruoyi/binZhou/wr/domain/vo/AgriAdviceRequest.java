package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

@Data
public class AgriAdviceRequest {
    private Long parkId;
    private String beginTime;
    private String endTime;
    private Boolean force;
}
