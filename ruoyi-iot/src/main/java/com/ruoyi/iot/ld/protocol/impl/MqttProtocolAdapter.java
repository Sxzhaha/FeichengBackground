package com.ruoyi.iot.ld.protocol.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.iot.ld.model.DeviceMessage;
import com.ruoyi.iot.ld.mqtt.MqttGateway; // 引入刚才定义的接口
import com.ruoyi.iot.ld.protocol.abstraction.IProtocolAdapter;
import com.ruoyi.iot.ld.service.impl.MessageProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * MQTT 协议适配器
 * 负责：
 * 1. 监听 MQTT 消息 -> 解码 -> 传给业务层
 * 2. 业务层调用 -> 编码 -> 发送 MQTT 消息
 */
@Component
public class MqttProtocolAdapter implements IProtocolAdapter {

	@Autowired
	private MessageProcessService messageProcessService;

	@Autowired
	private MqttGateway mqttGateway; // 注入发送网关

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String getProtocolType() {
		return "MQTT";
	}

	// ================== 下行：发送指令 (立即执行) ==================

	/**
	 * 服务器主动下发指令给设备
	 * MQTT 是长连接，这里发送后设备会立即收到
	 * @param deviceSn 设备SN
	 * @param message  指令内容
	 */
	@Override
	public void sendMessage(String deviceSn, DeviceMessage message) {
		try {
			// 1. 构造下行 Topic
			// 例如: /sys/DEV001/cmd
			String topic = "/sys/" + deviceSn + "/cmd";

			// 2. 构造符合物模型的 JSON Payload
			Map<String, Object> payloadMap = new HashMap<>();
			payloadMap.put("id", System.currentTimeMillis());
			payloadMap.put("method", "thing.service.property.set");
			payloadMap.put("params", message.getBody()); // {"report_interval": 5}

			String jsonPayload = objectMapper.writeValueAsString(payloadMap);

			// 3. 调用网关发送
			mqttGateway.sendToMqtt(topic, jsonPayload);

			System.out.println(">>> [MQTT] 指令已下发: " + topic + " 内容: " + jsonPayload);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("发送MQTT指令失败: " + e.getMessage());
		}
	}

	// ================== 上行：接收数据 (实时监听) ==================

	/**
	 * 通过 Spring Integration 接收 MQTT 消息
	 * inputChannel 必须对应 MQTT 配置中的输入通道名称
	 */
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public void handleMqttMessage(Message<String> message) {
		try {
			// 1. 获取 Topic 和 Payload
			System.out.println(message);
			String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
			String payload = message.getPayload();

			System.out.println("收到MQTT消息 Topic: " + topic + " Payload: " + payload);

			// 2. 构造原始数据对象传给 decode
			Map<String, Object> rawData = new HashMap<>();
			rawData.put("topic", topic);
			rawData.put("payload", payload);

			// 3. 解码
			DeviceMessage deviceMessage = decode(rawData);

			// 4. 业务处理
			if (deviceMessage != null) {
				messageProcessService.processMessage(deviceMessage);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public DeviceMessage decode(Object rawData) {
		try {
			Map<String, Object> map = (Map<String, Object>) rawData;
			String topic = (String) map.get("topic");
			String payload = (String) map.get("payload");
			// 1. 从 Topic 解析设备 SN
			// 假设 Topic 格式: /sys/{sn}/report
			// split("/") 结果: ["", "sys", "DEV001", "report"] -> index 2 是 SN
			String[] parts = topic.split("/");
			if (parts.length < 3) return null; // Topic 格式不对
			String sn = parts[2];
			System.out.println(sn);
			// 2. 解析 Payload JSON
			Map<String, Object> body = objectMapper.readValue(payload, Map.class);

			// 3. 封装 DeviceMessage
			DeviceMessage msg = new DeviceMessage();
			msg.setDeviceSn(sn);
			msg.setType(DeviceMessage.MessageType.PROPERTY_REPORT);
			msg.setBody(body); // {"temp": 25.5, "humi": 60}

			return msg;
		} catch (Exception e) {
			System.err.println("MQTT消息解析失败: " + e.getMessage());
			return null;
		}
	}
}