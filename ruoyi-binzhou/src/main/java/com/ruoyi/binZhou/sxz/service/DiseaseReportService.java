package com.ruoyi.binZhou.sxz.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.sxz.domain.DiseaseReportSummary;
import com.ruoyi.binZhou.sxz.mapper.DiseaseReportMapper;
import com.ruoyi.common.exception.ServiceException;

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
        double rate = summary.getAbnormalRate() == null
                ? 0D : summary.getAbnormalRate().doubleValue();
        if (rate >= 70D) return "高风险";
        if (rate >= 40D) return "中风险";
        return "低风险";
    }

    private String buildRiskText(DiseaseReportSummary summary)
    {
        if (value(summary.getRecordCount()) == 0)
        {
            return "当前统计范围内暂无病虫害识别记录，建议调整查询时间范围或先完成病虫害识别。";
        }
        String dominant = summary.getCategories().isEmpty()
                ? "暂无明显优势种类" : summary.getCategories().get(0).getName();
        BigDecimal dominantPercent = summary.getCategories().isEmpty()
                ? BigDecimal.ZERO : summary.getCategories().get(0).getPercent();
        if ("高风险".equals(summary.getRiskLevel()))
        {
            return String.format("本期共识别 %d 条记录，其中病虫害异常记录 %d 条，异常识别占比为 %s%%，处于 70%%—100%% 区间，综合风险等级判定为高风险。主要识别结果为 %s，占全部记录的 %s%%。建议立即开展田间复核，确认病虫害发生范围和危害程度，并及时实施针对性防治。",
                    value(summary.getRecordCount()), value(summary.getAbnormalCount()),
                    summary.getAbnormalRate(), dominant, dominantPercent);
        }
        if ("中风险".equals(summary.getRiskLevel()))
        {
            return String.format("本期共识别 %d 条记录，其中病虫害异常记录 %d 条，异常识别占比为 %s%%，处于 40%%—70%% 区间，综合风险等级判定为中风险。主要识别结果为 %s。建议提高重点区域图像采集和巡查频次，持续关注病虫害数量及置信度变化。",
                    value(summary.getRecordCount()), value(summary.getAbnormalCount()),
                    summary.getAbnormalRate(), dominant);
        }
        return String.format("本期共识别 %d 条记录，其中病虫害异常记录 %d 条，异常识别占比为 %s%%，处于 0%%—40%% 区间，综合风险等级判定为低风险。建议保持常态化监测，重点关注 %s 等主要识别结果的后续变化。",
                value(summary.getRecordCount()), value(summary.getAbnormalCount()),
                summary.getAbnormalRate(), dominant);
    }

    private long value(Long number)
    {
        return number == null ? 0L : number;
    }
}
