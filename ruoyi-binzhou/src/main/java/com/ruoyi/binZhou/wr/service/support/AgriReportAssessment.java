package com.ruoyi.binZhou.wr.service.support;

import com.ruoyi.binZhou.wr.domain.vo.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** Statistical-period evidence, not a real-time diagnosis or a weighted score. */
public final class AgriReportAssessment {
    private AgriReportAssessment() { }

    public static final class Evidence {
        private final int[] disease = new int[3];
        private final int[] pest = new int[3];
        private int normal;
        private int unresolved;
        private final List<String> weatherSuggestions = new ArrayList<>();

        public void recordWeatherSuggestion(String time, String suggestion) {
            if (suggestion == null || suggestion.trim().isEmpty() || weatherSuggestions.size() >= 3) return;
            String text = "记录处置建议（" + time + "）：" + suggestion.trim();
            if (!weatherSuggestions.contains(text)) weatherSuggestions.add(text);
        }

        public void recordDisease(String level, int warning) { record(level, warning, disease); }
        public void recordPest(String level, int warning) { record(level, warning, pest); }

        private void record(String raw, int warning, int[] counts) {
            String level = raw == null ? "" : raw.trim();
            int index = "高".equals(level) || "高风险".equals(level) ? 2
                    : "中".equals(level) || "中风险".equals(level) ? 1
                    : "低".equals(level) || "低风险".equals(level) ? 0 : -1;
            if (index >= 0 && warning == 1) counts[index]++;
            else if ("无".equals(level) && warning == 0) normal++;
            else unresolved++;
        }
    }

    public static void apply(AgriReportDTO report, Evidence evidence, boolean allHistory) {
        WeatherReportDTO weather = report.getWeather();
        DiseaseReportDTO disease = report.getDisease();
        PestReportDTO pest = report.getPest();
        PhenologyReportDTO phenology = report.getPhenology();
        TaskReportDTO task = report.getTask();
        LevelStatsDTO levels = weather.getLevelStats();
        int high = count(levels.getHigh()) + evidence.disease[2] + evidence.pest[2];
        int medium = count(levels.getMedium()) + evidence.disease[1] + evidence.pest[1];
        int low = count(levels.getLow()) + evidence.disease[0] + evidence.pest[0];
        int unresolved = count(levels.getUnmarked()) + evidence.unresolved;
        boolean available = high + medium + low > 0 || (evidence.normal > 0 && unresolved == 0);
        String scope = allHistory ? "历史统计范围内" : "所选统计期内";
        String risk = high > 0 ? "高风险" : medium > 0 ? "中风险" : low > 0 ? "低风险"
                : available ? "暂未发现明显风险" : "数据不足";
        String riskClass = high > 0 ? "danger" : medium > 0 ? "warning" : low > 0 ? "green" : "info";

        ReportSummaryDTO summary = new ReportSummaryDTO();
        summary.setWeatherCountText(count(weather.getTotal()) + "条");
        summary.setWeatherDescription("高风险" + count(levels.getHigh()) + "条，中风险"
                + count(levels.getMedium()) + "条，低风险" + count(levels.getLow()) + "条。");
        summary.setDiseaseCountText(count(disease.getTotal()) + "条");
        summary.setDiseaseDescription("预警记录" + count(disease.getWarningCount()) + "条，预警比例" + disease.getWarningRate() + "%。");
        summary.setPestCountText(count(pest.getTotal()) + "条");
        summary.setPestDescription("累计监测虫数" + count(pest.getTotalCount()) + "头；虫数不作为风险等级阈值。");
        summary.setPhenologyCountText(count(phenology.getTotal()) + "条");
        summary.setPhenologyDescription("统计结果主导物候期：" + phenology.getDominant() + "；仅作为管理背景。");
        summary.setTaskCountText(count(task.getTotal()) + "项");
        summary.setTaskDescription("任务总数来自真实记录，执行状态暂不可可靠统计，不参与风险判定。");
        summary.setDeviceCountText("数据待接入");
        summary.setDeviceDescription("暂无可靠设备在线统计，不参与风险判定。");
        summary.setOverallRisk(risk);
        summary.setOverallRiskClass(available ? riskClass : "info");
        summary.setConclusion(scope + "，基于当前可用且可核实的记录，统计期综合风险为“" + risk
                + "”。此结论不代表园区当前实时风险，也不能用于判断历史预警是否已解除。");
        summary.setRiskAssessment(available
                ? scope + "，有效风险证据按高、中、低依次优先判定：高风险" + high + "条，中风险" + medium
                    + "条，低风险" + low + "条（按模块记录计数，不代表去重后的事件数）；未标记或不一致记录"
                    + unresolved + "条不参与定级。未采用综合评分。"
                : "缺少可核实的分级风险证据，或未分级记录尚待核实；物候、农事总量和天气预报不能单独证明无风险。");
        report.setAssessmentAvailable(available);
        report.setSummary(summary);

        List<String> analysis = new ArrayList<>();
        analysis.add(scope + "共记录" + count(weather.getTotal()) + "条气象环境预警，其中高风险"
                + count(levels.getHigh()) + "条，占" + percent(count(levels.getHigh()), count(weather.getTotal()))
                + "%；中风险" + count(levels.getMedium()) + "条，低风险" + count(levels.getLow()) + "条。");
        analysis.add(scope + "病害识别" + count(disease.getTotal()) + "条，预警" + count(disease.getWarningCount())
                + "条，预警比例" + disease.getWarningRate() + "%；其中可核实高/中/低等级分别为"
                + evidence.disease[2] + "/" + evidence.disease[1] + "/" + evidence.disease[0] + "条，仅覆盖已关联本园区的记录。");
        analysis.add(scope + "虫情监测" + count(pest.getTotal()) + "条，累计虫数" + count(pest.getTotalCount())
                + "头；可核实高/中/低标记分别为" + evidence.pest[2] + "/" + evidence.pest[1] + "/" + evidence.pest[0]
                + "条，未使用虫数或优势度推断风险。");
        analysis.add(scope + "物候识别" + count(phenology.getTotal()) + "条，统计主导阶段为“"
                + phenology.getDominant() + "”；物候阶段不参与风险定级，历史模型来源待核实。");
        analysis.add(scope + "农事任务" + count(task.getTotal()) + "项；无可靠状态依据，不推断执行情况。");
        report.setRiskAnalysis(analysis);
        report.setFocusAreas(new ArrayList<>());

        AdviceDTO advice = new AdviceDTO();
        List<String> emergency = new ArrayList<>();
        List<String> midTerm = new ArrayList<>();
        List<String> longTerm = new ArrayList<>();
        if (count(weather.getTotal()) > 0) {
            emergency.add("规则建议：复核统计期气象环境预警记录及其处置建议，确认是否仍需现场处置；历史预警不等于当前告警。");
            // Quote raw suggestions only, never the display DTO's fallback text.
            emergency.addAll(evidence.weatherSuggestions);
        }
        if (count(disease.getWarningCount()) > 0) {
            midTerm.add("规则建议：复核统计期病害预警识别结果，加强对应病害巡查；未归属记录不纳入本园区处置依据。");
        }
        if (count(pest.getTotal()) > 0) midTerm.add("规则建议：结合田间巡查复核虫情监测结果，不仅凭累计虫数判断防治强度。");
        if (count(phenology.getTotal()) > 0) midTerm.add("规则建议：核实统计主导物候期“" + phenology.getDominant()
                + "”与现场当前阶段是否一致，再安排相应管理；不据此预测下一生育期。");
        if (available) longTerm.add("规则建议：保留巡查和处置记录，复核缺失园区关联、未标记等级和历史数据来源，定期更新统计期报告。");
        advice.setEmergency(emergency);
        advice.setMidTerm(midTerm);
        advice.setLongTerm(longTerm);
        report.setAdvice(advice);
        List<String> notes = new ArrayList<>(report.getDataQualityNotes());
        notes.add("综合研判是统计范围内的证据分级，不是当前实时风险或发生概率；缺失、数字编码和不一致识别标记不参与定级。");
        if (unresolved > 0) notes.add("本统计范围有" + unresolved + "条未分级或标记不一致记录，需复核；综合结论仅覆盖可核实证据。");
        report.setDataQualityNotes(notes);
    }

    private static int count(Integer value) { return value == null ? 0 : Math.max(0, value); }
    private static String percent(int value, int total) {
        return total <= 0 ? "0.0" : BigDecimal.valueOf(value).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP).toPlainString();
    }
}
