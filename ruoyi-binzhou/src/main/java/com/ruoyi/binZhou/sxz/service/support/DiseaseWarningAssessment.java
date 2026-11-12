package com.ruoyi.binZhou.sxz.service.support;

/**
 * 病虫害识别后的风险判定结果。
 *
 * 风险判定与模型识别解耦，便于后续由植保专家调整规则，而不修改识别主流程。
 */
public class DiseaseWarningAssessment
{
    private final long isWarning;
    private final String warningLevel;
    private final boolean reviewRequired;
    private final String reason;

    public DiseaseWarningAssessment(long isWarning, String warningLevel,
            boolean reviewRequired, String reason)
    {
        this.isWarning = isWarning;
        this.warningLevel = warningLevel;
        this.reviewRequired = reviewRequired;
        this.reason = reason;
    }

    public long getIsWarning()
    {
        return isWarning;
    }

    public String getWarningLevel()
    {
        return warningLevel;
    }

    public boolean isReviewRequired()
    {
        return reviewRequired;
    }

    public String getReason()
    {
        return reason;
    }
}
