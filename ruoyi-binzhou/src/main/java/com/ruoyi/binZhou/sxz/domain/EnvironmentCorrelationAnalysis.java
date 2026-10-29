package com.ruoyi.binZhou.sxz.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentCorrelationAnalysis
{
    private Integer validDays = 0;
    private Long recordCount = 0L;
    private boolean sufficient;
    private List<FactorResult> factors = new ArrayList<>();
    private List<DailyRecord> dailyRecords = new ArrayList<>();

    public Integer getValidDays() { return validDays; }
    public void setValidDays(Integer validDays) { this.validDays = validDays; }
    public Long getRecordCount() { return recordCount; }
    public void setRecordCount(Long recordCount) { this.recordCount = recordCount; }
    public boolean isSufficient() { return sufficient; }
    public void setSufficient(boolean sufficient) { this.sufficient = sufficient; }
    public List<FactorResult> getFactors() { return factors; }
    public void setFactors(List<FactorResult> factors) { this.factors = factors; }
    public List<DailyRecord> getDailyRecords() { return dailyRecords; }
    public void setDailyRecords(List<DailyRecord> dailyRecords) { this.dailyRecords = dailyRecords; }

    public FactorResult findFactor(String factor)
    {
        return factors.stream().filter(item -> factor.equals(item.getFactor())).findFirst().orElse(null);
    }

    public static class DailyRecord
    {
        private String date;
        private String diseaseName;
        private Long totalCount;
        private Long diseaseCount;
        private BigDecimal temperature;
        private BigDecimal humidity;
        private BigDecimal rainfall;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getDiseaseName() { return diseaseName; }
        public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Long getDiseaseCount() { return diseaseCount; }
        public void setDiseaseCount(Long diseaseCount) { this.diseaseCount = diseaseCount; }
        public BigDecimal getTemperature() { return temperature; }
        public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
        public BigDecimal getHumidity() { return humidity; }
        public void setHumidity(BigDecimal humidity) { this.humidity = humidity; }
        public BigDecimal getRainfall() { return rainfall; }
        public void setRainfall(BigDecimal rainfall) { this.rainfall = rainfall; }
    }

    public static class FactorResult
    {
        private String factor;
        private String name;
        private String unit;
        private String relationLevel;
        private String direction;
        private Integer lagDays = 0;
        private BigDecimal correlationValue = BigDecimal.ZERO;
        private String peakRange;
        private String explanation;
        private Integer pairCount = 0;
        private List<BinStat> bins = new ArrayList<>();

        public String getFactor() { return factor; }
        public void setFactor(String factor) { this.factor = factor; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public String getRelationLevel() { return relationLevel; }
        public void setRelationLevel(String relationLevel) { this.relationLevel = relationLevel; }
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }
        public Integer getLagDays() { return lagDays; }
        public void setLagDays(Integer lagDays) { this.lagDays = lagDays; }
        public BigDecimal getCorrelationValue() { return correlationValue; }
        public void setCorrelationValue(BigDecimal correlationValue) { this.correlationValue = correlationValue; }
        public String getPeakRange() { return peakRange; }
        public void setPeakRange(String peakRange) { this.peakRange = peakRange; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
        public Integer getPairCount() { return pairCount; }
        public void setPairCount(Integer pairCount) { this.pairCount = pairCount; }
        public List<BinStat> getBins() { return bins; }
        public void setBins(List<BinStat> bins) { this.bins = bins; }
    }

    public static class BinStat
    {
        private String range;
        private Long totalCount = 0L;
        private Long diseaseCount = 0L;
        private BigDecimal detectionRate = BigDecimal.ZERO;
        private boolean sufficient;

        public String getRange() { return range; }
        public void setRange(String range) { this.range = range; }
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Long getDiseaseCount() { return diseaseCount; }
        public void setDiseaseCount(Long diseaseCount) { this.diseaseCount = diseaseCount; }
        public BigDecimal getDetectionRate() { return detectionRate; }
        public void setDetectionRate(BigDecimal detectionRate) { this.detectionRate = detectionRate; }
        public boolean isSufficient() { return sufficient; }
        public void setSufficient(boolean sufficient) { this.sufficient = sufficient; }
    }
}
