package com.ruoyi.binZhou.sxz.service.support;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.xwq.mapper.AiDiseaseMapper;

/** 肥城桃病虫害识别结果的保守风险判定规则。 */
@Service
public class DiseaseWarningRuleService
{
    private final AiDiseaseMapper diseaseMapper;

    @Value("${sxz.disease-warning.review-confidence:0.70}")
    private BigDecimal reviewConfidence;

    @Value("${sxz.disease-warning.reliable-confidence:0.85}")
    private BigDecimal reliableConfidence;

    @Value("${sxz.disease-warning.lookback-days:7}")
    private int lookbackDays;

    @Value("${sxz.disease-warning.continuous-count:3}")
    private int continuousCount;

    @Value("${sxz.disease-warning.multi-device-count:2}")
    private int multiDeviceCount;

    public DiseaseWarningRuleService(AiDiseaseMapper diseaseMapper)
    {
        this.diseaseMapper = diseaseMapper;
    }

    public DiseaseWarningAssessment assess(String diseaseName, BigDecimal confidence,
            String recordMode, Long parkId, Long deviceId, Date recognitionTime)
    {
        BigDecimal normalizedConfidence = confidence == null ? BigDecimal.ZERO : confidence;
        if (isUnknown(diseaseName))
        {
            return review("模型未返回可确认的病虫害类别，需要人工复核");
        }
        if (normalizedConfidence.compareTo(reviewConfidence) < 0)
        {
            return review(String.format("模型置信度低于人工复核阈值 %s", reviewConfidence));
        }

        boolean automatic = "自动定时".equals(recordMode);
        if (automatic && parkId == null)
        {
            return review("自动监测结果未关联园区，不能生成园区风险");
        }
        if (isHealthy(diseaseName))
        {
            return new DiseaseWarningAssessment(0L, "无风险", false,
                    "模型未识别到明显病虫害，保持常态化监测");
        }
        if (!automatic)
        {
            return new DiseaseWarningAssessment(0L, "辅助诊断", false,
                    "手动上传结果仅作辅助诊断，不纳入园区自动监测风险统计");
        }

        if (normalizedConfidence.compareTo(reliableConfidence) < 0)
        {
            return new DiseaseWarningAssessment(1L, "低风险", false,
                    String.format("自动监测发现疑似病虫害，但置信度未达到风险升级阈值 %s", reliableConfidence));
        }

        Date since = new Date((recognitionTime == null ? System.currentTimeMillis() : recognitionTime.getTime())
                - Math.max(1, lookbackDays) * 24L * 60L * 60L * 1000L);
        int historicalCount = diseaseMapper.countRecentReliableAutomaticDetections(
                parkId, diseaseName, reliableConfidence, since);
        int affectedDevices = diseaseMapper.countRecentReliableAutomaticDevices(
                parkId, diseaseName, reliableConfidence, since);
        if (deviceId != null && diseaseMapper.countRecentReliableAutomaticDetectionsForDevice(
                parkId, deviceId, diseaseName, reliableConfidence, since) == 0)
        {
            affectedDevices++;
        }

        int totalCount = historicalCount + 1;
        if (totalCount >= Math.max(2, continuousCount)
                && affectedDevices >= Math.max(2, multiDeviceCount))
        {
            return new DiseaseWarningAssessment(1L, "中风险", false,
                    String.format("近%d天同园区累计%d次可靠识别，涉及%d台设备，建议开展田间复核",
                            Math.max(1, lookbackDays), totalCount, affectedDevices));
        }
        return new DiseaseWarningAssessment(1L, "低风险", false,
                String.format("自动监测首次或零散发现，近%d天同园区累计%d次可靠识别",
                        Math.max(1, lookbackDays), totalCount));
    }

    public String prependSafetyAdvice(String advice, DiseaseWarningAssessment assessment)
    {
        String original = advice == null ? "" : advice.trim();
        if (assessment.isReviewRequired())
        {
            return "【待人工复核】当前结果不得直接作为施药或园区风险判定依据。" + original;
        }
        if ("辅助诊断".equals(assessment.getWarningLevel()))
        {
            return "【辅助诊断】手动上传结果不纳入园区自动监测风险统计，请结合田间调查复核。" + original;
        }
        return original;
    }

    private DiseaseWarningAssessment review(String reason)
    {
        return new DiseaseWarningAssessment(0L, "待人工复核", true, reason);
    }

    private boolean isHealthy(String diseaseName)
    {
        String value = diseaseName == null ? "" : diseaseName.trim();
        return value.contains("健康") || value.contains("正常") || value.contains("无病虫害");
    }

    private boolean isUnknown(String diseaseName)
    {
        String value = diseaseName == null ? "" : diseaseName.trim();
        String lower = value.toLowerCase(Locale.ROOT);
        return value.isEmpty() || value.contains("未知") || value.contains("未识别")
                || value.contains("无法识别") || value.contains("其他") || value.contains("其它")
                || "unknown".equals(lower) || "other".equals(lower);
    }
}
