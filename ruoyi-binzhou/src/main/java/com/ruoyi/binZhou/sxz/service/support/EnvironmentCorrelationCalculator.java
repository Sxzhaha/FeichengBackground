package com.ruoyi.binZhou.sxz.service.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.ruoyi.binZhou.sxz.domain.EnvironmentCorrelationAnalysis;

public final class EnvironmentCorrelationCalculator
{
    private static final int MIN_VALID_DAYS = 14;
    private static final int MAX_VALID_DAYS = 30;
    private static final int MIN_CORRELATION_PAIRS = 10;
    private static final long MIN_BIN_RECORDS = 3L;

    private EnvironmentCorrelationCalculator() { }

    public static EnvironmentCorrelationAnalysis calculate(List<EnvironmentCorrelationAnalysis.DailyRecord> source)
    {
        List<EnvironmentCorrelationAnalysis.DailyRecord> rows = normalize(source);
        EnvironmentCorrelationAnalysis analysis = new EnvironmentCorrelationAnalysis();
        analysis.setValidDays(rows.size());
        analysis.setRecordCount(rows.stream().mapToLong(item -> value(item.getTotalCount())).sum());
        analysis.setSufficient(rows.size() >= MIN_VALID_DAYS);
        analysis.setDailyRecords(rows);
        analysis.setFactors(Arrays.asList(
                calculateFactor(rows, factor("temperature", "温度", "℃", EnvironmentCorrelationAnalysis.DailyRecord::getTemperature,
                        new double[] { 15D, 20D, 25D, 30D }, new String[] { "<15℃", "15–20℃", "20–25℃", "25–30℃", "≥30℃" })),
                calculateFactor(rows, factor("humidity", "湿度", "%", EnvironmentCorrelationAnalysis.DailyRecord::getHumidity,
                        new double[] { 40D, 60D, 75D, 85D }, new String[] { "<40%", "40–60%", "60–75%", "75–85%", "≥85%" })),
                calculateFactor(rows, factor("rainfall", "降雨量", "mm", EnvironmentCorrelationAnalysis.DailyRecord::getRainfall,
                        new double[] { 0D, 10D, 25D, 50D }, new String[] { "无降雨", "0–10mm", "10–25mm", "25–50mm", "≥50mm" }))));
        return analysis;
    }

    private static List<EnvironmentCorrelationAnalysis.DailyRecord> normalize(List<EnvironmentCorrelationAnalysis.DailyRecord> source)
    {
        List<EnvironmentCorrelationAnalysis.DailyRecord> rows = new ArrayList<>();
        if (source != null)
        {
            for (EnvironmentCorrelationAnalysis.DailyRecord row : source)
            {
                if (row != null && row.getDate() != null && value(row.getTotalCount()) > 0
                        && row.getTemperature() != null && row.getHumidity() != null && row.getRainfall() != null)
                {
                    rows.add(row);
                }
            }
        }
        rows.sort(Comparator.comparing(item -> LocalDate.parse(item.getDate())));
        return rows.size() <= MAX_VALID_DAYS ? rows : new ArrayList<>(rows.subList(rows.size() - MAX_VALID_DAYS, rows.size()));
    }

    private static FactorDefinition factor(String key, String name, String unit,
            Function<EnvironmentCorrelationAnalysis.DailyRecord, BigDecimal> extractor,
            double[] boundaries, String[] labels)
    {
        return new FactorDefinition(key, name, unit, extractor, boundaries, labels);
    }

    private static EnvironmentCorrelationAnalysis.FactorResult calculateFactor(
            List<EnvironmentCorrelationAnalysis.DailyRecord> rows, FactorDefinition definition)
    {
        EnvironmentCorrelationAnalysis.FactorResult result = new EnvironmentCorrelationAnalysis.FactorResult();
        result.setFactor(definition.key);
        result.setName(definition.name);
        result.setUnit(definition.unit);
        result.setBins(buildBins(rows, definition));
        result.setPeakRange(findPeakRange(result.getBins()));

        Correlation best = bestCorrelation(rows, definition.extractor);
        result.setLagDays(best.lagDays);
        result.setPairCount(best.pairCount);
        result.setCorrelationValue(decimal(best.value));
        boolean sufficient = rows.size() >= MIN_VALID_DAYS && best.pairCount >= MIN_CORRELATION_PAIRS;
        result.setRelationLevel(sufficient ? relationLevel(best.value) : "样本不足");
        result.setDirection(sufficient ? direction(best.value) : "暂不判断");
        result.setExplanation(explanation(result, sufficient));
        return result;
    }

    private static List<EnvironmentCorrelationAnalysis.BinStat> buildBins(
            List<EnvironmentCorrelationAnalysis.DailyRecord> rows, FactorDefinition definition)
    {
        List<EnvironmentCorrelationAnalysis.BinStat> bins = new ArrayList<>();
        for (String label : definition.labels)
        {
            EnvironmentCorrelationAnalysis.BinStat bin = new EnvironmentCorrelationAnalysis.BinStat();
            bin.setRange(label);
            bins.add(bin);
        }
        for (EnvironmentCorrelationAnalysis.DailyRecord row : rows)
        {
            double factorValue = definition.extractor.apply(row).doubleValue();
            int index = binIndex(factorValue, definition.boundaries, "rainfall".equals(definition.key));
            EnvironmentCorrelationAnalysis.BinStat bin = bins.get(index);
            bin.setTotalCount(bin.getTotalCount() + value(row.getTotalCount()));
            bin.setDiseaseCount(bin.getDiseaseCount() + value(row.getDiseaseCount()));
        }
        for (EnvironmentCorrelationAnalysis.BinStat bin : bins)
        {
            bin.setSufficient(bin.getTotalCount() >= MIN_BIN_RECORDS);
            BigDecimal rate = bin.getTotalCount() == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(bin.getDiseaseCount() * 100D / bin.getTotalCount()).setScale(2, RoundingMode.HALF_UP);
            bin.setDetectionRate(rate);
        }
        return bins;
    }

    private static int binIndex(double value, double[] boundaries, boolean rainfall)
    {
        if (rainfall)
        {
            if (value <= 0D) return 0;
            if (value <= boundaries[1]) return 1;
            if (value <= boundaries[2]) return 2;
            if (value <= boundaries[3]) return 3;
            return 4;
        }
        for (int index = 0; index < boundaries.length; index++)
        {
            if (value < boundaries[index]) return index;
        }
        return boundaries.length;
    }

    private static String findPeakRange(List<EnvironmentCorrelationAnalysis.BinStat> bins)
    {
        return bins.stream().filter(EnvironmentCorrelationAnalysis.BinStat::isSufficient)
                .max(Comparator.comparing(EnvironmentCorrelationAnalysis.BinStat::getDetectionRate))
                .map(EnvironmentCorrelationAnalysis.BinStat::getRange).orElse("样本不足");
    }

    private static Correlation bestCorrelation(List<EnvironmentCorrelationAnalysis.DailyRecord> rows,
            Function<EnvironmentCorrelationAnalysis.DailyRecord, BigDecimal> extractor)
    {
        Map<LocalDate, EnvironmentCorrelationAnalysis.DailyRecord> byDate = new HashMap<>();
        for (EnvironmentCorrelationAnalysis.DailyRecord row : rows)
        {
            byDate.put(LocalDate.parse(row.getDate()), row);
        }
        Correlation best = new Correlation(0, 0D, 0);
        for (int lag = 0; lag <= 2; lag++)
        {
            List<Double> environment = new ArrayList<>();
            List<Double> rates = new ArrayList<>();
            for (EnvironmentCorrelationAnalysis.DailyRecord target : rows)
            {
                EnvironmentCorrelationAnalysis.DailyRecord previous = byDate.get(LocalDate.parse(target.getDate()).minusDays(lag));
                if (previous != null)
                {
                    environment.add(extractor.apply(previous).doubleValue());
                    rates.add(value(target.getDiseaseCount()) * 100D / value(target.getTotalCount()));
                }
            }
            double coefficient = spearman(environment, rates);
            if (environment.size() > best.pairCount && Math.abs(coefficient) == Math.abs(best.value)
                    || Math.abs(coefficient) > Math.abs(best.value))
            {
                best = new Correlation(lag, coefficient, environment.size());
            }
        }
        return best;
    }

    private static double spearman(List<Double> left, List<Double> right)
    {
        if (left.size() < 2 || right.size() != left.size()) return 0D;
        return pearson(ranks(left), ranks(right));
    }

    private static double[] ranks(List<Double> values)
    {
        Integer[] indexes = new Integer[values.size()];
        for (int i = 0; i < indexes.length; i++) indexes[i] = i;
        Arrays.sort(indexes, Comparator.comparingDouble(values::get));
        double[] ranks = new double[values.size()];
        int start = 0;
        while (start < indexes.length)
        {
            int end = start;
            while (end + 1 < indexes.length && Double.compare(values.get(indexes[start]), values.get(indexes[end + 1])) == 0) end++;
            double rank = (start + end + 2D) / 2D;
            for (int i = start; i <= end; i++) ranks[indexes[i]] = rank;
            start = end + 1;
        }
        return ranks;
    }

    private static double pearson(double[] left, double[] right)
    {
        double leftMean = Arrays.stream(left).average().orElse(0D);
        double rightMean = Arrays.stream(right).average().orElse(0D);
        double numerator = 0D;
        double leftSquare = 0D;
        double rightSquare = 0D;
        for (int i = 0; i < left.length; i++)
        {
            double leftDelta = left[i] - leftMean;
            double rightDelta = right[i] - rightMean;
            numerator += leftDelta * rightDelta;
            leftSquare += leftDelta * leftDelta;
            rightSquare += rightDelta * rightDelta;
        }
        double denominator = Math.sqrt(leftSquare * rightSquare);
        return denominator == 0D ? 0D : numerator / denominator;
    }

    private static String relationLevel(double value)
    {
        double absolute = Math.abs(value);
        if (absolute < 0.2D) return "很弱";
        if (absolute < 0.4D) return "较弱";
        if (absolute < 0.6D) return "中等";
        if (absolute < 0.8D) return "较强";
        return "强";
    }

    private static String direction(double value)
    {
        if (Math.abs(value) < 0.2D) return "无明显变化";
        return value > 0D ? "同向变化" : "反向变化";
    }

    private static String explanation(EnvironmentCorrelationAnalysis.FactorResult result, boolean sufficient)
    {
        if (!sufficient)
        {
            return "当前有效数据不足，暂不判断" + result.getName() + "与病害检出率的关联程度。";
        }
        String timing = result.getLagDays() == 0 ? "当天" : result.getLagDays() + "天后";
        return result.getName() + "与病害检出率呈" + result.getRelationLevel() + result.getDirection()
                + "，这种关联在" + timing + "表现得更明显。";
    }

    private static BigDecimal decimal(double value)
    {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }

    private static long value(Long value)
    {
        return value == null ? 0L : value;
    }

    private static class FactorDefinition
    {
        private final String key;
        private final String name;
        private final String unit;
        private final Function<EnvironmentCorrelationAnalysis.DailyRecord, BigDecimal> extractor;
        private final double[] boundaries;
        private final String[] labels;

        private FactorDefinition(String key, String name, String unit,
                Function<EnvironmentCorrelationAnalysis.DailyRecord, BigDecimal> extractor,
                double[] boundaries, String[] labels)
        {
            this.key = key;
            this.name = name;
            this.unit = unit;
            this.extractor = extractor;
            this.boundaries = boundaries;
            this.labels = labels;
        }
    }

    private static class Correlation
    {
        private final int lagDays;
        private final double value;
        private final int pairCount;

        private Correlation(int lagDays, double value, int pairCount)
        {
            this.lagDays = lagDays;
            this.value = value;
            this.pairCount = pairCount;
        }
    }
}
