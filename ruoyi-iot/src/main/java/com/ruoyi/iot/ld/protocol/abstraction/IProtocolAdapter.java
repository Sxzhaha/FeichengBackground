package com.ruoyi.iot.ld.protocol.abstraction;

import com.ruoyi.iot.ld.model.DeviceMessage;

/**
 * @ProjectName RuoYi-Iot
 * @ClassName IProtocolAdapter
 * @Author 刘邓
 * @Date 2026-02-02 10:24
 *
 */
public interface IProtocolAdapter {

	/**
	 * 获取协议类型标识
	 * @return e.g., "MQTT", "HTTP", "MODBUS-RTU"
	 */
	String getProtocolType();

	/**
	 * 下行指令：发送消息到设备
	 * @param deviceSn 设备SN
	 * @param message 消息内容
	 */
	void sendMessage(String deviceSn, DeviceMessage message);

	/**
	 * 上行解析：将原始报文(byte[]/String)解析为标准DeviceMessage
	 * @param rawData 原始数据
	 * @return 标准消息对象
	 */
	DeviceMessage decode(Object rawData);
}
