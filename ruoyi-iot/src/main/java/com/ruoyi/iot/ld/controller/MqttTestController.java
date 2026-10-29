package com.ruoyi.iot.ld.controller;

import com.ruoyi.iot.ld.model.DeviceMessage;
import com.ruoyi.iot.ld.protocol.impl.MqttProtocolAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName RuoYi-Iot
 * @ClassName TestMqttController
 * @Author 刘邓
 * @Date 2026-02-03 11:20
 *
 */
@RestController
@RequestMapping("/binZhou/ld/test/mqtt")
public class MqttTestController {

	@Autowired
	private MqttProtocolAdapter mqttProtocolAdapter;

	@GetMapping("/send")
	public String testSend(@RequestParam String sn) {
		DeviceMessage msg = new DeviceMessage();
		Map<String, Object> body = new HashMap<>();
		body.put("humidity", 10);
		msg.setBody(body);

		// 触发发送
		mqttProtocolAdapter.sendMessage(sn, msg);
		return "指令已发送";
	}
}
