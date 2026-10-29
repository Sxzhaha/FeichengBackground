package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PhenologyReportDTO {

    private Boolean hasData;

    private Integer total;

    /**
     * 当前主导物候期
     */
    private String dominant;

    /**
     * 主导物候期占比
     */
    private String dominantRate;

    /**
     * 平均识别置信度
     */
    private String avgConfidence;

    /**
     * 各物候阶段统计
     * stage: 物候阶段名称
     * count: 识别次数
     * rate: 占比
     */
    private List<Map<String, Object>> stageStats;

    /**
     * 生长阶段研判
     */
    private String growthJudgement;
}
