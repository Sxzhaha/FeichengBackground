package com.ruoyi.binZhou.wr.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdviceDTO {
    private List<String> emergency;
    private List<String> midTerm;
    private List<String> longTerm;
}
