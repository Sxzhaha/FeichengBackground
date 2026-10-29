package com.ruoyi.binZhou.xwq.domain;

import java.util.Map;

/**
 * 当前登录用户的默认监测范围和模型选择请求。
 * userId 不由客户端传递，控制器始终从登录 Token 获取。
 */
public class UserDefaultPreferencesRequest
{
    private Long parkId;
    private Long areaId;
    private Long deviceId;
    private Map<String, Long> models;

    public Long getParkId() { return parkId; }
    public void setParkId(Long parkId) { this.parkId = parkId; }

    public Long getAreaId() { return areaId; }
    public void setAreaId(Long areaId) { this.areaId = areaId; }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public Map<String, Long> getModels() { return models; }
    public void setModels(Map<String, Long> models) { this.models = models; }
}
