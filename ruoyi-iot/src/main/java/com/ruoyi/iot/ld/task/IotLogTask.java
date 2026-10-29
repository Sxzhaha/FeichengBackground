package com.ruoyi.iot.ld.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.domain.IotDeviceLog;
import com.ruoyi.iot.ld.service.IIotDeviceLogService;
import com.ruoyi.iot.ld.service.IIotDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Component
public class IotLogTask {

	private static final Logger log = LoggerFactory.getLogger(IotLogTask.class);

	@Value("${iot.data-url:https://yun.taianmh.cn/api/Data/SELECT}")
	private String dataUrl;

	@Autowired
	private IIotDeviceService iotDeviceService;

	@Autowired
	private IIotDeviceLogService iotDeviceLogService;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Scheduled(cron = "0 */1 * * * ?")
	public void fetchIotData() {
		log.info("【IoT数据同步】定时任务开始触发执行...");

		try {
			List<IotDevice> deviceList = iotDeviceService.selectIotDeviceList(new IotDevice());
			if (deviceList == null || deviceList.isEmpty()) {
				return;
			}

			for (IotDevice device : deviceList) {
				Long deviceId = device.getDeviceId();
				if (deviceId == null) continue;

				String url = dataUrl + "?device_id=" + deviceId;
				String responseStr = restTemplate.getForObject(url, String.class);

				JsonNode rootNode = objectMapper.readTree(responseStr);
				if (!rootNode.path("status").asBoolean()) continue;

				JsonNode dataArray = rootNode.path("data");
				if (dataArray.isArray() && dataArray.size() > 0) {

					for (JsonNode item : dataArray) {
						// 1. 提取 SN 和组装 Payload
						String sn = item.path("device").path("sn").asText();
						String sensorType = item.path("sensor").path("type").asText();
						String dataVal = item.path("data").asText();

						ObjectNode payloadNode = objectMapper.createObjectNode();
						payloadNode.put(sensorType, dataVal);
						String payloadJson = payloadNode.toString();

						// 2. 解析得到 createTime
						String updatedTimeStr = item.path("updated_time").asText();
						Date updatedTime = DateUtils.parseDate(updatedTimeStr);
						IotDevice updateDevice = new IotDevice();
						updateDevice.setDeviceId(deviceId);
						updateDevice.setLastOnlineTime(updatedTime);
						iotDeviceService.updateIotDevice(updateDevice);

						// ================== 【核心新增：三重唯一性校验】 ==================
						boolean isExists = iotDeviceLogService.checkLogExists(sn, payloadJson, updatedTime);
						if (isExists) {
							// 如果数据库里已经有了，直接跳过此条数据，不执行插入
							log.debug("【IoT数据同步】检测到相同日志已存在，跳过插入。SN: {}, Time: {}", sn, updatedTimeStr);
							continue;
						}
						// ==================================================================

						// 3. 校验通过，执行插入
						IotDeviceLog deviceLog = new IotDeviceLog();
						deviceLog.setDeviceSn(sn);
						deviceLog.setLogType(1);
						deviceLog.setPayload(payloadJson);
						deviceLog.setCreateTime(updatedTime);

						iotDeviceLogService.insertIotDeviceLog(deviceLog);
					}
				}
			}
			log.info("【IoT数据同步】本轮数据同步及去重校验完成。");
		} catch (Exception e) {
			log.error("【IoT数据同步】任务执行期间发生严重异常：", e);
		}
	}
}
