package com.ruoyi.binZhou.sxz.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiseaseAnalysisDashboard
{
    private Long totalCount = 0L;
    private Integer categoryCount = 0;
    private String generatedAt;
    private List<CategoryStat> categories = new ArrayList<>();
    private List<TrendStat> trends = new ArrayList<>();
    private List<ParkStat> parks = new ArrayList<>();
    private List<RecentRecord> records = new ArrayList<>();
    private List<ParkOption> parkOptions = new ArrayList<>();
    private List<DeviceOption> deviceOptions = new ArrayList<>();
    private List<EnvironmentStat> environment = new ArrayList<>();
    private EnvironmentCorrelationAnalysis environmentCorrelation;
    private Map<String, EnvironmentCorrelationAnalysis> environmentCorrelationByDisease = new LinkedHashMap<>();

    public Long getTotalCount() { return totalCount; }
    public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
    public Integer getCategoryCount() { return categoryCount; }
    public void setCategoryCount(Integer categoryCount) { this.categoryCount = categoryCount; }
    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
    public List<CategoryStat> getCategories() { return categories; }
    public void setCategories(List<CategoryStat> categories) { this.categories = categories; }
    public List<TrendStat> getTrends() { return trends; }
    public void setTrends(List<TrendStat> trends) { this.trends = trends; }
    public List<ParkStat> getParks() { return parks; }
    public void setParks(List<ParkStat> parks) { this.parks = parks; }
    public List<RecentRecord> getRecords() { return records; }
    public void setRecords(List<RecentRecord> records) { this.records = records; }
    public List<ParkOption> getParkOptions() { return parkOptions; }
    public void setParkOptions(List<ParkOption> parkOptions) { this.parkOptions = parkOptions; }
    public List<DeviceOption> getDeviceOptions() { return deviceOptions; }
    public void setDeviceOptions(List<DeviceOption> deviceOptions) { this.deviceOptions = deviceOptions; }
    public List<EnvironmentStat> getEnvironment() { return environment; }
    public void setEnvironment(List<EnvironmentStat> environment) { this.environment = environment; }
    public EnvironmentCorrelationAnalysis getEnvironmentCorrelation() { return environmentCorrelation; }
    public void setEnvironmentCorrelation(EnvironmentCorrelationAnalysis environmentCorrelation) { this.environmentCorrelation = environmentCorrelation; }
    public Map<String, EnvironmentCorrelationAnalysis> getEnvironmentCorrelationByDisease() { return environmentCorrelationByDisease; }
    public void setEnvironmentCorrelationByDisease(Map<String, EnvironmentCorrelationAnalysis> environmentCorrelationByDisease) { this.environmentCorrelationByDisease = environmentCorrelationByDisease; }

    public static class CategoryStat
    {
        private String name;
        private Long count;
        private BigDecimal rate;
        private BigDecimal avgConfidence;
        private BigDecimal maxConfidence;
        private String firstTime;
        private String latestTime;
        private String parkName;
        private String result;
        private String description;
        private String advice;
        private String imageUrl;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
        public BigDecimal getRate() { return rate; }
        public void setRate(BigDecimal rate) { this.rate = rate; }
        public BigDecimal getAvgConfidence() { return avgConfidence; }
        public void setAvgConfidence(BigDecimal avgConfidence) { this.avgConfidence = avgConfidence; }
        public BigDecimal getMaxConfidence() { return maxConfidence; }
        public void setMaxConfidence(BigDecimal maxConfidence) { this.maxConfidence = maxConfidence; }
        public String getFirstTime() { return firstTime; }
        public void setFirstTime(String firstTime) { this.firstTime = firstTime; }
        public String getLatestTime() { return latestTime; }
        public void setLatestTime(String latestTime) { this.latestTime = latestTime; }
        public String getParkName() { return parkName; }
        public void setParkName(String parkName) { this.parkName = parkName; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAdvice() { return advice; }
        public void setAdvice(String advice) { this.advice = advice; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class TrendStat
    {
        private String date;
        private String name;
        private Long count;
        private BigDecimal avgConfidence;
        private Long totalCount;
        private Long diseaseCount;
        private BigDecimal detectionRate;
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
        public BigDecimal getAvgConfidence() { return avgConfidence; }
        public void setAvgConfidence(BigDecimal avgConfidence) { this.avgConfidence = avgConfidence; }
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Long getDiseaseCount() { return diseaseCount; }
        public void setDiseaseCount(Long diseaseCount) { this.diseaseCount = diseaseCount; }
        public BigDecimal getDetectionRate() { return detectionRate; }
        public void setDetectionRate(BigDecimal detectionRate) { this.detectionRate = detectionRate; }
    }

    public static class ParkStat
    {
        private Long parkId;
        private String parkName;
        private Long count;
        private Long warningCount;
        private BigDecimal avgConfidence;
        public Long getParkId() { return parkId; }
        public void setParkId(Long parkId) { this.parkId = parkId; }
        public String getParkName() { return parkName; }
        public void setParkName(String parkName) { this.parkName = parkName; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
        public Long getWarningCount() { return warningCount; }
        public void setWarningCount(Long warningCount) { this.warningCount = warningCount; }
        public BigDecimal getAvgConfidence() { return avgConfidence; }
        public void setAvgConfidence(BigDecimal avgConfidence) { this.avgConfidence = avgConfidence; }
    }

    public static class RecentRecord
    {
        private Long diseaseId;
        private String name;
        private BigDecimal confidence;
        private String time;
        private String model;
        private String parkName;
        private String plotName;
        private String imageName;
        private String imageUrl;
        public Long getDiseaseId() { return diseaseId; }
        public void setDiseaseId(Long diseaseId) { this.diseaseId = diseaseId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getConfidence() { return confidence; }
        public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getParkName() { return parkName; }
        public void setParkName(String parkName) { this.parkName = parkName; }
        public String getPlotName() { return plotName; }
        public void setPlotName(String plotName) { this.plotName = plotName; }
        public String getImageName() { return imageName; }
        public void setImageName(String imageName) { this.imageName = imageName; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class ParkOption
    {
        private Long parkId;
        private String parkName;
        public Long getParkId() { return parkId; }
        public void setParkId(Long parkId) { this.parkId = parkId; }
        public String getParkName() { return parkName; }
        public void setParkName(String parkName) { this.parkName = parkName; }
    }

    public static class DeviceOption
    {
        private Long deviceId;
        private String deviceName;
        private Long parkId;
        public Long getDeviceId() { return deviceId; }
        public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public Long getParkId() { return parkId; }
        public void setParkId(Long parkId) { this.parkId = parkId; }
    }

    public static class EnvironmentStat
    {
        private String time;
        private Long totalCount;
        private Long diseaseCount;
        private BigDecimal detectionRate;
        private BigDecimal temperature;
        private BigDecimal humidity;
        private BigDecimal precipitation;
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Long getDiseaseCount() { return diseaseCount; }
        public void setDiseaseCount(Long diseaseCount) { this.diseaseCount = diseaseCount; }
        public BigDecimal getDetectionRate() { return detectionRate; }
        public void setDetectionRate(BigDecimal detectionRate) { this.detectionRate = detectionRate; }
        public BigDecimal getTemperature() { return temperature; }
        public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
        public BigDecimal getHumidity() { return humidity; }
        public void setHumidity(BigDecimal humidity) { this.humidity = humidity; }
        public BigDecimal getPrecipitation() { return precipitation; }
        public void setPrecipitation(BigDecimal precipitation) { this.precipitation = precipitation; }
    }
}
