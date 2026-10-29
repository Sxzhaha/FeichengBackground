package com.ruoyi.binZhou.xwq.domain;

import java.math.BigDecimal;

/**
 * 物候期模型推理结果。
 *
 * 该对象只描述模型服务返回的数据，不直接映射数据库表。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
public class PhenologyModelResult
{
    /** 当前物候期名称 */
    private String phenologyName;

    /** 物候期标准编码 */
    private String phenologyCode;

    /** 模型识别置信度，推荐范围 0~1 */
    private BigDecimal phenologyConfidence;

    /** 识别结论 */
    private String phenologyResult;

    /** 当前作物生长状态描述 */
    private String phenologyDesc;

    /** 当前阶段对应的农事建议 */
    private String phenologyAdvice;

    /** 下一生育阶段 */
    private String phenologyNextName;

    /** 模型生成的结果图片地址 */
    private String phenologyResultImgUrl;

    public String getPhenologyName()
    {
        return phenologyName;
    }

    public void setPhenologyName(String phenologyName)
    {
        this.phenologyName = phenologyName;
    }

    public String getPhenologyCode()
    {
        return phenologyCode;
    }

    public void setPhenologyCode(String phenologyCode)
    {
        this.phenologyCode = phenologyCode;
    }

    public BigDecimal getPhenologyConfidence()
    {
        return phenologyConfidence;
    }

    public void setPhenologyConfidence(BigDecimal phenologyConfidence)
    {
        this.phenologyConfidence = phenologyConfidence;
    }

    public String getPhenologyResult()
    {
        return phenologyResult;
    }

    public void setPhenologyResult(String phenologyResult)
    {
        this.phenologyResult = phenologyResult;
    }

    public String getPhenologyDesc()
    {
        return phenologyDesc;
    }

    public void setPhenologyDesc(String phenologyDesc)
    {
        this.phenologyDesc = phenologyDesc;
    }

    public String getPhenologyAdvice()
    {
        return phenologyAdvice;
    }

    public void setPhenologyAdvice(String phenologyAdvice)
    {
        this.phenologyAdvice = phenologyAdvice;
    }

    public String getPhenologyNextName()
    {
        return phenologyNextName;
    }

    public void setPhenologyNextName(String phenologyNextName)
    {
        this.phenologyNextName = phenologyNextName;
    }

    public String getPhenologyResultImgUrl()
    {
        return phenologyResultImgUrl;
    }

    public void setPhenologyResultImgUrl(String phenologyResultImgUrl)
    {
        this.phenologyResultImgUrl = phenologyResultImgUrl;
    }
}
