package com.ruoyi.iot.ld.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.iot.ld.domain.AgriAlertRecord;
import com.ruoyi.iot.ld.domain.AgriWeatherLive;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.domain.IotDeviceLog;
import com.ruoyi.iot.ld.domain.Park;
import com.ruoyi.iot.ld.mapper.AgriAlertRecordMapper;
import com.ruoyi.iot.ld.mapper.AgriWeatherLiveMapper;
import com.ruoyi.iot.ld.mapper.IotDeviceLogMapper;
import com.ruoyi.iot.ld.mapper.ParkMapper;
import com.ruoyi.iot.ld.service.IIotDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * LD物联网移动端聚合接口。
 * 所有移动端接口统一以 /app 结尾，避免与后台管理接口冲突。
 */
@RestController
@RequestMapping("/binZhou/ld/iot")
public class IotAppController extends BaseController {
    @Autowired private IIotDeviceService deviceService;
    @Autowired private IotDeviceLogMapper deviceLogMapper;
    @Autowired private AgriWeatherLiveMapper weatherLiveMapper;
    @Autowired private AgriAlertRecordMapper alertRecordMapper;
    @Autowired private ParkMapper parkMapper;

    @GetMapping("/overview/app")
    public AjaxResult overview(@RequestParam(required = false) Long parkId) {
        IotDevice query = new IotDevice();
        query.setParkId(parkId);
        List<IotDevice> devices = deviceService.selectIotDeviceList(query);
        List<AgriAlertRecord> alertList = queryAlerts(parkId == null ? null : String.valueOf(parkId), null);
        Map<String, Object> data = new LinkedHashMap<>();
        String parkName = "全部园区";
        Park selectedPark = parkId == null ? null : parkMapper.selectParkById(parkId);
        if (selectedPark != null) {
            parkName = selectedPark.getParkName();
        } else if (!devices.isEmpty() && devices.get(0).getParkName() != null) {
            parkName = devices.get(0).getParkName();
        }
        data.put("parkName", parkName);
        data.put("total", devices.size());
        data.put("online", devices.stream().filter(d -> "1".equals(d.getStatus())).count());
        data.put("alertCount", alertList.size());
        data.put("weather", latestWeather(parkId));
        IotDevice sensor = devices.stream().filter(d -> d.getProductType() == null || d.getProductType() != 4).findFirst().orElse(null);
        data.put("device", sensor);
        Map<String, Object> latest = sensor == null ? new LinkedHashMap<>() : latestPayload(sensor.getDeviceSn());
        data.put("latest", latest);
        data.put("updateTime", latest.get("createTime"));
        return success(data);
    }

    private List<AgriAlertRecord> queryAlerts(String parkId, Long status) {
        AgriAlertRecord query = new AgriAlertRecord();
        query.setParkId(parkId);
        query.setHandleStatus(status);
        List<AgriAlertRecord> list = alertRecordMapper.selectAgriAlertRecordList(query);
        list.sort(Comparator.comparing(AgriAlertRecord::getCreateTime, Comparator.nullsLast(Date::compareTo)).reversed());
        return list;
    }

    private AgriWeatherLive latestWeather(Long parkId) {
        AgriWeatherLive query = new AgriWeatherLive();
        if (parkId != null) query.setParkId(String.valueOf(parkId));
        return weatherLiveMapper.selectAgriWeatherLiveList(query).stream()
                .max(Comparator.comparing(AgriWeatherLive::getReportTime, Comparator.nullsLast(Date::compareTo)))
                .orElse(null);
    }

    private Map<String, Object> latestPayload(String deviceSn) {
        List<IotDeviceLog> logs = deviceLogMapper.selectRecentLogsForApp(deviceSn, null, 1);
        Map<String, Object> data = new LinkedHashMap<>();
        if (logs.isEmpty()) return data;
        IotDeviceLog log = logs.get(0);
        JSONObject json = parsePayload(log.getPayload());
        data.put("temperature", number(json, "temperature", "airTemperature", "temp"));
        data.put("humidity", number(json, "humidity", "airHumidity"));
        data.put("soilHumidity", number(json, "soilHumidity", "soilMoisture"));
        data.put("light", number(json, "light", "illumination", "lightIntensity"));
        data.put("createTime", log.getCreateTime());
        return data;
    }

    private JSONObject parsePayload(String payload) {
        try {
            JSONObject json = JSON.parseObject(payload);
            Object body = json.get("body");
            if (body instanceof JSONObject) return (JSONObject) body;
            if (body instanceof String) return JSON.parseObject((String) body);
            return json;
        } catch (Exception ignored) {
            return new JSONObject();
        }
    }

    private Double number(JSONObject json, String... keys) {
        for (String key : keys) {
            Object value = json.get(key);
            if (value instanceof Number) return ((Number) value).doubleValue();
            if (value != null) try { return Double.valueOf(value.toString()); } catch (Exception ignored) { }
        }
        return null;
    }

}
