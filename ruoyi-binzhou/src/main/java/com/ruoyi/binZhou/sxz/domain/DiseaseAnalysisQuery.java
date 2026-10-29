package com.ruoyi.binZhou.sxz.domain;

public class DiseaseAnalysisQuery
{
    private Long parkId;
    private Long deviceId;
    private String environmentDiseaseName;
    private Integer days = 30;

    public Long getParkId()
    {
        return parkId;
    }

    public void setParkId(Long parkId)
    {
        this.parkId = parkId;
    }

    public Long getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getEnvironmentDiseaseName()
    {
        return environmentDiseaseName;
    }

    public void setEnvironmentDiseaseName(String environmentDiseaseName)
    {
        this.environmentDiseaseName = environmentDiseaseName;
    }

    public Integer getDays()
    {
        return days;
    }

    public void setDays(Integer days)
    {
        this.days = days;
    }
}
