package com.ruoyi.iot.ld.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.domain.IotDeviceLog;
import com.ruoyi.iot.ld.domain.IotThingModel;
import com.ruoyi.iot.ld.mapper.IotDeviceLogMapper;
import com.ruoyi.iot.ld.mapper.IotDeviceMapper;
import com.ruoyi.iot.ld.mapper.IotThingModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DeviceStatusTask {

    private static final Logger log = LoggerFactory.getLogger(DeviceStatusTask.class);

    private static final long CONTROL_DEVICE_OFFLINE_MILLIS = 2 * 60 * 1000L;

    @Value("${iot.device-status-url:https://yun.taianmh.cn/api/Device/SELECT?adUserid=28}")
    private String deviceStatusUrl;

    @Autowired
    private IotDeviceMapper deviceMapper;

    @Autowired
    private IotThingModelMapper iotThingModelMapper;

    @Autowired
    private IotDeviceLogMapper iotDeviceLogMapper;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(cron = "0 */1 * * * ?")
    public void checkDeviceTimeout() {
        log.info("【设备状态同步】任务开始执行...");

        List<IotDevice> deviceList = deviceMapper.selectIotDeviceList(new IotDevice());
        if (deviceList == null || deviceList.isEmpty()) {
            log.info("【设备状态同步】本地未查询到设备");
            return;
        }

        int acquisitionCount = syncAcquisitionDeviceStatus(deviceList);
        int controlCount = syncControlDeviceStatus(deviceList);

        log.info("【设备状态同步】执行完成，采集终端更新 {} 台，控制终端更新 {} 台", acquisitionCount, controlCount);
    }

    private int syncAcquisitionDeviceStatus(List<IotDevice> deviceList) {
        Map<Long, IotDevice> acquisitionDeviceMap = new HashMap<>();
        for (IotDevice device : deviceList) {
            if (device != null && Integer.valueOf(0).equals(device.getProductType()) && device.getDeviceId() != null) {
                acquisitionDeviceMap.put(device.getDeviceId(), device);
            }
        }
        if (acquisitionDeviceMap.isEmpty()) {
            return 0;
        }

        try {
            String responseStr = restTemplate.getForObject(deviceStatusUrl, String.class);
            JsonNode rootNode = objectMapper.readTree(responseStr);

            if (!rootNode.path("status").asBoolean()) {
                log.warn("【设备状态同步】采集终端HTTP接口返回失败: {}", responseStr);
                return 0;
            }

            JsonNode dataArray = rootNode.path("data");
            if (!dataArray.isArray() || dataArray.size() == 0) {
                log.info("【设备状态同步】采集终端HTTP接口未返回设备数据");
                return 0;
            }

            int count = 0;
            for (JsonNode item : dataArray) {
                if (item == null || item.isNull()) {
                    continue;
                }
                JsonNode idNode = item.path("id");
                JsonNode statusNode = item.path("status");
                if (idNode.isMissingNode() || idNode.isNull() || statusNode.isMissingNode() || statusNode.isNull()) {
                    continue;
                }

                Long deviceId = idNode.asLong();
                if (!acquisitionDeviceMap.containsKey(deviceId)) {
                    continue;
                }

                String status = statusNode.asText();
                count += deviceMapper.updateIotDeviceStatusByDeviceId(deviceId, status);
            }
            return count;
        } catch (Exception e) {
            log.error("【设备状态同步】采集终端HTTP状态同步异常", e);
            return 0;
        }
    }

    private int syncControlDeviceStatus(List<IotDevice> deviceList) {
        int count = 0;
        for (IotDevice device : deviceList) {
            if (device == null || !Integer.valueOf(1).equals(device.getProductType()) || device.getDeviceId() == null) {
                continue;
            }

            String status = resolveControlDeviceStatus(device);
            count += deviceMapper.updateIotDeviceStatusByDeviceId(device.getDeviceId(), status);
        }
        return count;
    }

    private String resolveControlDeviceStatus(IotDevice device) {
        String runningStatusIdentifier = getRunningStatusIdentifier(device);
        if (StringUtils.isEmpty(runningStatusIdentifier)) {
            log.warn("【设备状态同步】控制终端未找到中文名为“运行状态”的物模型，设备SN: {}", device.getDeviceSn());
            return "0";
        }

        IotDeviceLog latestLog = iotDeviceLogMapper.selectLatestIotDeviceLogBySnAndIdentifier(
                device.getDeviceSn(),
                runningStatusIdentifier);
        if (latestLog == null || latestLog.getCreateTime() == null || StringUtils.isEmpty(latestLog.getPayload())) {
            return "0";
        }

        if (System.currentTimeMillis() - latestLog.getCreateTime().getTime() > CONTROL_DEVICE_OFFLINE_MILLIS) {
            return "0";
        }

        try {
            JsonNode payloadNode = objectMapper.readTree(latestLog.getPayload());
            JsonNode statusNode = payloadNode.path(runningStatusIdentifier);
            if (statusNode.isMissingNode() || statusNode.isNull()) {
                return "0";
            }

            String runningStatus = statusNode.asText();
            return "正常运行".equals(runningStatus != null ? runningStatus.trim() : null) ? "1" : "0";
        } catch (Exception e) {
            log.warn("【设备状态同步】控制终端运行状态日志解析失败，设备SN: {}, payload: {}", device.getDeviceSn(), latestLog.getPayload());
            return "0";
        }
    }

    private String getRunningStatusIdentifier(IotDevice device) {
        if (device.getProductId() == null) {
            return null;
        }

        IotThingModel query = new IotThingModel();
        query.setProductId(device.getProductId());
        query.setModelName("运行状态");
        List<IotThingModel> models = iotThingModelMapper.selectIotThingModelList(query);
        if (models == null || models.isEmpty()) {
            return null;
        }

        for (IotThingModel model : models) {
            if (model != null && "运行状态".equals(model.getModelName()) && StringUtils.isNotEmpty(model.getIdentifier())) {
                return model.getIdentifier();
            }
        }

        IotThingModel first = models.get(0);
        return first != null ? first.getIdentifier() : null;
    }
}
