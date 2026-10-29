package com.ruoyi.binZhou.sxz.domain;

import java.math.BigDecimal;
import java.util.List;

/** 病虫害识别报告总体统计。 */
public class DiseaseReportSummary
{
    private Long imageCount;
    private Long recordCount;
    private Long categoryCount;
    private Long healthyCount;
    private Long abnormalCount;
    private BigDecimal abnormalRate;
    private BigDecimal avgConfidence;
    private BigDecimal maxConfidence;
    private BigDecimal minConfidence;
    private Long highConfidenceCount;
    private Long middleConfidenceCount;
    private Long lowConfidenceCount;
    private Long highRiskCount;
    private Long middleRiskCount;
    private Long lowRiskCount;
    private Long unmarkedRiskCount;
    private String riskLevel;
    private String riskText;
    private String qualityText;
    private List<CategoryStat> categories;
    private List<AdviceItem> adviceList;
    private List<FocusRecord> focusRecords;
    private List<FocusRecord> records;

    public Long getImageCount() { return imageCount; }
    public void setImageCount(Long imageCount) { this.imageCount = imageCount; }
    public Long getRecordCount() { return recordCount; }
    public void setRecordCount(Long recordCount) { this.recordCount = recordCount; }
    public Long getCategoryCount() { return categoryCount; }
    public void setCategoryCount(Long categoryCount) { this.categoryCount = categoryCount; }
    public Long getHealthyCount() { return healthyCount; }
    public void setHealthyCount(Long healthyCount) { this.healthyCount = healthyCount; }
    public Long getAbnormalCount() { return abnormalCount; }
    public void setAbnormalCount(Long abnormalCount) { this.abnormalCount = abnormalCount; }
    public BigDecimal getAbnormalRate() { return abnormalRate; }
    public void setAbnormalRate(BigDecimal abnormalRate) { this.abnormalRate = abnormalRate; }
    public BigDecimal getAvgConfidence() { return avgConfidence; }
    public void setAvgConfidence(BigDecimal avgConfidence) { this.avgConfidence = avgConfidence; }
    public BigDecimal getMaxConfidence() { return maxConfidence; }
    public void setMaxConfidence(BigDecimal maxConfidence) { this.maxConfidence = maxConfidence; }
    public BigDecimal getMinConfidence() { return minConfidence; }
    public void setMinConfidence(BigDecimal minConfidence) { this.minConfidence = minConfidence; }
    public Long getHighConfidenceCount() { return highConfidenceCount; }
    public void setHighConfidenceCount(Long highConfidenceCount) { this.highConfidenceCount = highConfidenceCount; }
    public Long getMiddleConfidenceCount() { return middleConfidenceCount; }
    public void setMiddleConfidenceCount(Long middleConfidenceCount) { this.middleConfidenceCount = middleConfidenceCount; }
    public Long getLowConfidenceCount() { return lowConfidenceCount; }
    public void setLowConfidenceCount(Long lowConfidenceCount) { this.lowConfidenceCount = lowConfidenceCount; }
    public Long getHighRiskCount() { return highRiskCount; }
    public void setHighRiskCount(Long highRiskCount) { this.highRiskCount = highRiskCount; }
    public Long getMiddleRiskCount() { return middleRiskCount; }
    public void setMiddleRiskCount(Long middleRiskCount) { this.middleRiskCount = middleRiskCount; }
    public Long getLowRiskCount() { return lowRiskCount; }
    public void setLowRiskCount(Long lowRiskCount) { this.lowRiskCount = lowRiskCount; }
    public Long getUnmarkedRiskCount() { return unmarkedRiskCount; }
    public void setUnmarkedRiskCount(Long unmarkedRiskCount) { this.unmarkedRiskCount = unmarkedRiskCount; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getRiskText() { return riskText; }
    public void setRiskText(String riskText) { this.riskText = riskText; }
    public String getQualityText() { return qualityText; }
    public void setQualityText(String qualityText) { this.qualityText = qualityText; }
    public List<CategoryStat> getCategories() { return categories; }
    public void setCategories(List<CategoryStat> categories) { this.categories = categories; }
    public List<AdviceItem> getAdviceList() { return adviceList; }
    public void setAdviceList(List<AdviceItem> adviceList) { this.adviceList = adviceList; }
    public List<FocusRecord> getFocusRecords() { return focusRecords; }
    public void setFocusRecords(List<FocusRecord> focusRecords) { this.focusRecords = focusRecords; }
    public List<FocusRecord> getRecords() { return records; }
    public void setRecords(List<FocusRecord> records) { this.records = records; }

    public static class CategoryStat
    {
        private String name;
        private Long count;
        private BigDecimal percent;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
        public BigDecimal getPercent() { return percent; }
        public void setPercent(BigDecimal percent) { this.percent = percent; }
    }

    public static class AdviceItem
    {
        private String diseaseName;
        private String content;

        public String getDiseaseName() { return diseaseName; }
        public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class FocusRecord
    {
        private Long diseaseId;
        private Long recordId;
        private Long imageId;
        private String recordNo;
        private String imageUrl;
        private String categoryName;
        private BigDecimal confidence;
        private String riskLevel;
        private String resultText;
        private String advice;
        private String modelName;
        private String createTime;

        public Long getDiseaseId() { return diseaseId; }
        public void setDiseaseId(Long diseaseId) { this.diseaseId = diseaseId; }
        public Long getRecordId() { return recordId; }
        public void setRecordId(Long recordId) { this.recordId = recordId; }
        public Long getImageId() { return imageId; }
        public void setImageId(Long imageId) { this.imageId = imageId; }
        public String getRecordNo() { return recordNo; }
        public void setRecordNo(String recordNo) { this.recordNo = recordNo; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public BigDecimal getConfidence() { return confidence; }
        public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public String getResultText() { return resultText; }
        public void setResultText(String resultText) { this.resultText = resultText; }
        public String getAdvice() { return advice; }
        public void setAdvice(String advice) { this.advice = advice; }
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }
    }

    public static class Query
    {
        private String beginDate;
        private String endDate;
        private String categoryName;
        private String riskLevel;
        private String recognitionMode;
        private String diseaseIds;
        private List<Long> diseaseIdList;

        public String getBeginDate() { return beginDate; }
        public void setBeginDate(String beginDate) { this.beginDate = beginDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public String getRecognitionMode() { return recognitionMode; }
        public void setRecognitionMode(String recognitionMode) { this.recognitionMode = recognitionMode; }
        public String getDiseaseIds() { return diseaseIds; }
        public void setDiseaseIds(String diseaseIds) { this.diseaseIds = diseaseIds; }
        public List<Long> getDiseaseIdList() { return diseaseIdList; }
        public void setDiseaseIdList(List<Long> diseaseIdList) { this.diseaseIdList = diseaseIdList; }
    }
}
