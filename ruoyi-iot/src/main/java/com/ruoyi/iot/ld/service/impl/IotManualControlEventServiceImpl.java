package com.ruoyi.iot.ld.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.domain.IotDeviceLog;
import com.ruoyi.iot.ld.domain.IotManualControlEvent;
import com.ruoyi.iot.ld.mapper.IotDeviceMapper;
import com.ruoyi.iot.ld.mapper.IotDeviceLogMapper;
import com.ruoyi.iot.ld.mapper.IotManualControlEventMapper;
import com.ruoyi.iot.ld.mqtt.MqttGateway;
import com.ruoyi.iot.ld.service.IIotManualControlEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manual control event Service implementation.
 */
@Service
public class IotManualControlEventServiceImpl implements IIotManualControlEventService
{
    @org.springframework.beans.factory.annotation.Value("${iot.control-res.manual-prefix:M}")
    private String manualControlResPrefix;

    @Autowired
    private IotManualControlEventMapper iotManualControlEventMapper;

    @Autowired
    private IotDeviceMapper iotDeviceMapper;

    @Autowired
    private IotDeviceLogMapper iotDeviceLogMapper;

    @Autowired
    private MqttGateway mqttGateway;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public IotManualControlEvent selectIotManualControlEventByEventId(Long eventId)
    {
        return iotManualControlEventMapper.selectIotManualControlEventByEventId(eventId);
    }

    @Override
    public List<IotManualControlEvent> selectIotManualControlEventList(IotManualControlEvent iotManualControlEvent)
    {
        return iotManualControlEventMapper.selectIotManualControlEventList(iotManualControlEvent);
    }

    @Override
    public int insertIotManualControlEvent(IotManualControlEvent iotManualControlEvent)
    {
        fillCreateInfo(iotManualControlEvent);
        return iotManualControlEventMapper.insertIotManualControlEvent(iotManualControlEvent);
    }

    @Override
    public int sendIotManualControlEvent(IotManualControlEvent iotManualControlEvent)
    {
        IotDevice device = resolveControlDevice(iotManualControlEvent);
        prepareManualControlPayload(iotManualControlEvent, device);
        fillCreateInfo(iotManualControlEvent);
        iotManualControlEvent.setStatus(0);
        iotManualControlEventMapper.insertIotManualControlEvent(iotManualControlEvent);

        try {
            mqttGateway.sendToMqtt(iotManualControlEvent.getTopic(), iotManualControlEvent.getPayload());
            java.util.Date sendTime = DateUtils.getNowDate();
            saveDownlinkDeviceLog(iotManualControlEvent.getDeviceSn(), 2, iotManualControlEvent.getPayload(), sendTime);
            iotManualControlEvent.setStatus(1);
            iotManualControlEvent.setSendTime(sendTime);
            iotManualControlEvent.setResponseDesc("已下发，等待设备响应");
            return updateIotManualControlEvent(iotManualControlEvent);
        } catch (Exception e) {
            iotManualControlEvent.setStatus(3);
            iotManualControlEvent.setResponseDesc("下发失败: " + e.getMessage());
            updateIotManualControlEvent(iotManualControlEvent);
            throw new ServiceException("手动控制下发失败: " + e.getMessage());
        }
    }

    @Override
    public int updateIotManualControlEvent(IotManualControlEvent iotManualControlEvent)
    {
        try {
            iotManualControlEvent.setUpdateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            // System-triggered updates may not have a login context.
        }
        iotManualControlEvent.setUpdateTime(DateUtils.getNowDate());
        return iotManualControlEventMapper.updateIotManualControlEvent(iotManualControlEvent);
    }

    @Override
    public int updateIotManualControlEventResponseByRes(String deviceSn, String res, String responsePayload, String responseDesc)
    {
        return iotManualControlEventMapper.updateIotManualControlEventResponseByRes(deviceSn, res, responsePayload, responseDesc);
    }

    @Override
    public int deleteIotManualControlEventByEventIds(Long[] eventIds)
    {
        return iotManualControlEventMapper.deleteIotManualControlEventByEventIds(eventIds);
    }

    @Override
    public int deleteIotManualControlEventByEventId(Long eventId)
    {
        return iotManualControlEventMapper.deleteIotManualControlEventByEventId(eventId);
    }

    private void fillCreateInfo(IotManualControlEvent event)
    {
        try {
            event.setCreateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            // System-triggered inserts may not have a login context.
        }
        event.setCreateTime(DateUtils.getNowDate());
    }

    private IotDevice resolveControlDevice(IotManualControlEvent event)
    {
        IotDevice device = null;
        if (event.getDeviceId() != null) {
            device = iotDeviceMapper.selectIotDeviceByDeviceId(event.getDeviceId());
        } else if (StringUtils.isNotEmpty(event.getDeviceSn())) {
            device = iotDeviceMapper.selectIotDeviceBySn(event.getDeviceSn());
        }
        if (device == null) {
            throw new ServiceException("控制终端设备不存在");
        }
        if (device.getProductType() != null && device.getProductType() != 1) {
            throw new ServiceException("目标设备不是控制终端");
        }
        if (StringUtils.isEmpty(device.getDeviceSn())) {
            throw new ServiceException("控制终端SN为空");
        }
        return device;
    }

    private void saveDownlinkDeviceLog(String deviceSn, Integer logType, String payload, java.util.Date createTime) {
        IotDeviceLog log = new IotDeviceLog();
        log.setDeviceSn(deviceSn);
        log.setLogType(logType);
        log.setPayload(payload);
        log.setCreateTime(createTime);
        iotDeviceLogMapper.insertIotDeviceLog(log);
    }

    private void prepareManualControlPayload(IotManualControlEvent event, IotDevice device)
    {
        Map<String, Object> payloadMap = new LinkedHashMap<>();
        if (event.getCommands() != null && !event.getCommands().isEmpty()) {
            payloadMap.putAll(event.getCommands());
        } else if (StringUtils.isNotEmpty(event.getPayload())) {
            try {
                payloadMap.putAll(objectMapper.readValue(event.getPayload(), new TypeReference<Map<String, Object>>() {}));
            } catch (Exception e) {
                throw new ServiceException("下发payload必须是JSON对象");
            }
        } else {
            throw new ServiceException("下发内容不能为空");
        }

        String res = buildControlRes(manualControlResPrefix, "M");
        if (res.length() > 15) {
            throw new ServiceException("res长度不能超过15个字符");
        }
        payloadMap.put("res", res);

        event.setDeviceId(device.getDeviceId());
        event.setDeviceSn(device.getDeviceSn());
        event.setTopic("/set/" + device.getDeviceSn());
        event.setRes(res);
        event.setTargetIdentifiers(payloadMap.keySet().stream()
                .filter(key -> !"res".equalsIgnoreCase(key))
                .collect(Collectors.joining(",")));
        try {
            event.setPayload(objectMapper.writeValueAsString(payloadMap));
        } catch (Exception e) {
            throw new ServiceException("下发payload生成失败");
        }
    }

    private String buildControlRes(String configuredPrefix, String defaultPrefix) {
        String prefix = StringUtils.isNotEmpty(configuredPrefix) ? configuredPrefix.trim() : defaultPrefix;
        if (prefix.length() > 5) {
            prefix = prefix.substring(0, 5);
        }
        int suffixLength = Math.max(1, 15 - prefix.length());
        String suffix = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        if (suffix.length() > suffixLength) {
            suffix = suffix.substring(suffix.length() - suffixLength);
        }
        return prefix + suffix;
    }
}
