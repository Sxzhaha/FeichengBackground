package com.ruoyi.iot.ld.model;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * 统一设备消息模型
 * 屏蔽底层MQTT、HTTP、Modbus的差异，向上层业务提供统一对象
 */
@Data
public class DeviceMessage implements Serializable {
	// 设备唯一标识
	private String deviceSn;

	// 消息类型：PROPERTY(属性), EVENT(事件), COMMAND_RESP(命令回执)
	private MessageType type;

	// 消息发生时间
	private Long timestamp;

	// 数据体 (K-V结构，方便JSON处理)
	private Map<String, Object> body;

	public enum MessageType {
		CONNECT, DISCONNECT, PROPERTY_REPORT, EVENT_REPORT
	}
}