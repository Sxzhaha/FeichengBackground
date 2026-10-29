package com.ruoyi.iot.ld.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * MQTT 消息发送网关
 * 调用这个接口的方法，Spring就会自动把消息发到 MQTT Broker
 */
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {

	/**
	 * 发送消息到指定 Topic
	 * @param topic   主题
	 * @param payload 消息内容 (String 或 byte[])
	 */
	void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);
}