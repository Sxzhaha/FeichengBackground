package com.ruoyi.binZhou.sxz.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.common.FeichengDataScopeUtils;
import com.ruoyi.binZhou.sxz.domain.DiseaseReportSummary;
import com.ruoyi.binZhou.sxz.mapper.DiseaseReportMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

@Service
public class DiseaseReportService
{
    private final DiseaseReportMapper mapper;

    public DiseaseReportService(DiseaseReportMapper mapper)
    {
        this.mapper = mapper;
    }

    public DiseaseReportSummary getSummary(DiseaseReportSummary.Query query)
    {
        if (query == null)
        {
            query = new DiseaseReportSummary.Query();
        }
        Long scopeUserId = FeichengDataScopeUtils.currentParkOwnerUserId();
        query.setScopeUserId(scopeUserId);
        if (scopeUserId != null) query.setScopeUsername(SecurityUtils.getUsername());
        query.setDiseaseIdList(parseDiseaseIds(query.getDiseaseIds()));
        DiseaseReportSummary summary = mapper.selectSummary(query);
        if (summary == null)
        {
            summary = new DiseaseReportSummary();
        }
        long records = value(summary.getRecordCount());
        long abnormal = value(summary.getAbnormalCount());
        BigDecimal rate = records == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(abnormal * 100D / records)
                        .setScale(2, RoundingMode.HALF_UP);
        summary.setAbnormalRate(rate);
        List<DiseaseReportSummary.CategoryStat> categories = mapper.selectCategories(query);
        for (DiseaseReportSummary.CategoryStat category : categories)
        {
            BigDecimal percent = records == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(value(category.getCount()) * 100D / records)
                            .setScale(2, RoundingMode.HALF_UP);
            category.setPercent(percent);
        }
        summary.setCategories(categories);
        summary.setAdviceList(mapper.selectAdviceList(query));
        summary.setFocusRecords(mapper.selectFocusRecords(query));
        summary.setRecords(mapper.selectRecords(query));
        summary.setQualityText(buildQualityText(summary));
        summary.setRiskLevel(buildRiskLevel(summary));
        summary.setRiskText(buildRiskText(summary));
        return summary;
    }

    private List<Long> parseDiseaseIds(String value)
    {
        if (value == null || value.trim().isEmpty())
        {
            return null;
        }
        Set<Long> ids = new LinkedHashSet<>();
        for (String token : value.split(","))
        {
            try
            {
                long id = Long.parseLong(token.trim());
                if (id > 0) ids.add(id);
            }
            catch (NumberFormatException e)
            {
                throw new ServiceException("选中的病虫害记录编号格式不正确");
            }
        }
        if (ids.isEmpty())
        {
            throw new ServiceException("请选择有效的病虫害识别记录");
        }
        if (ids.size() > 1000)
        {
            throw new ServiceException("单次最多选择1000条病虫害识别记录");
        }
        return new ArrayList<>(ids);
    }

    private String buildQualityText(DiseaseReportSummary summary)
    {
        if (value(summary.getRecordCount()) == 0)
        {
            return "暂无识别数据，暂时无法开展识别质量分析。";
        }
        double average = summary.getAvgConfidence() == null
                ? 0D : summary.getAvgConfidence().doubleValue();
        if (average >= 90D)
        {
            return "整体识别置信度较高，模型输出结果较为稳定，可结合人工抽检对重点风险记录进行复核。";
        }
        if (average >= 75D)
        {
            return "整体识别置信度处于可用水平，但部分记录仍存在不确定性，建议对中低置信度图片进行人工复核。";
        }
        return "整体识别置信度偏低，建议检查图像清晰度、拍摄角度和模型适用范围，并补充相关病虫害训练样本。";
    }

    private String buildRiskLevel(DiseaseReportSummary summary)
    {
        if (value(summary.getRecordCount()) == 0) return "暂无数据";
        if (value(summary.getHighRiskCount()) > 0) return "高风险";
        if (value(summary.getMiddleRiskCount()) > 0) return "中风险";
        if (value(summary.getLowRiskCount()) > 0) return "低风险";
        if (value(summary.getReviewRequiredCount()) > 0) return "待人工复核";
        return "无风险";
    }

    private String buildRiskText(DiseaseReportSummary summary)
    {
        if (value(summary.getRecordCount()) == 0)
        {
            return "当前统计范围内暂无病虫害识别记录，建议调整查询时间范围或先完成病虫害识别。";
        }
        String dominant = summary.getCategories() == null || summary.getCategories().isEmpty()
                ? "暂无明显优势种类" : summary.getCategories().get(0).getName();
        BigDecimal dominantPercent = summary.getCategories() == null || summary.getCategories().isEmpty()
                ? BigDecimal.ZERO : summary.getCategories().get(0).getPercent();
        if ("高风险".equals(summary.getRiskLevel()))
        {
            return String.format("本期自动监测存在 %d 条高风险记录。主要识别结果为 %s，占全部记录的 %s%%。高风险仅来源于已确认或后续专家规则，不由单张图片自动生成；建议立即开展田间复核。",
                    value(summary.getHighRiskCount()), dominant, dominantPercent);
        }
        if ("中风险".equals(summary.getRiskLevel()))
        {
            return String.format("本期自动监测存在 %d 条中风险记录，表示同园区、同类病虫害在统计窗口内被多次可靠识别且涉及多台设备。主要识别结果为 %s，建议提高巡查频次并开展田间核验。",
                    value(summary.getMiddleRiskCount()), dominant);
        }
        if ("低风险".equals(summary.getRiskLevel()))
        {
            return String.format("本期自动监测存在 %d 条低风险记录，属于首次、零散或尚未达到升级条件的发现。建议保持常态化监测，重点关注 %s 的后续变化。异常识别占比仅表示样本构成，不等同于果园发病率。",
                    value(summary.getLowRiskCount()), dominant);
        }
        if ("待人工复核".equals(summary.getRiskLevel()))
        {
            return String.format("本期有 %d 条结果因类别未知、置信度不足或缺少园区关联而待人工复核，复核前不纳入园区风险，也不能直接作为施药依据。",
                    value(summary.getReviewRequiredCount()));
        }
        return String.format("本期未产生自动监测风险记录；其中手动辅助诊断 %d 条、无风险 %d 条。请保持常态化监测。异常识别占比仅表示样本构成，不等同于果园发病率。",
                value(summary.getAuxiliaryDiagnosisCount()), value(summary.getNoRiskCount()));
    }

    private long value(Long number)
    {
        return number == null ? 0L : number;
    }
}
