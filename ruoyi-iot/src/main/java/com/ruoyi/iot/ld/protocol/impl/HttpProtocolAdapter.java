package com.ruoyi.iot.ld.protocol.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.redis.RedisCache; // 【重要】引入若依的Redis工具类
import com.ruoyi.iot.ld.model.DeviceMessage;
import com.ruoyi.iot.ld.protocol.abstraction.IProtocolAdapter;
import com.ruoyi.iot.ld.service.impl.MessageProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP协议接入点 (集成Redis版)
 */
@RestController
@RequestMapping("/binZhou/ld/iot/api")
public class HttpProtocolAdapter implements IProtocolAdapter {

	@Autowired
	private MessageProcessService messageProcessService;

	// 注入若依封装的 Redis 工具类
	@Autowired
	private RedisCache redisCache;

	// JSON转换工具
	private final ObjectMapper objectMapper = new ObjectMapper();

	// 定义 Redis Key 前缀
	// 格式例如: iot:device:cmd:DEV001
	private static final String CMD_CACHE_KEY_PREFIX = "iot:device:cmd:";

	@Override
	public String getProtocolType() {
		return "HTTP";
	}

	/**
	 * HTTP 上行入口
	 */
	@PostMapping("/report")
	public Map<String, Object> handleHttpRequest(@RequestParam String sn, @RequestBody String bodyStr) {
		Map<String, Object> response = new HashMap<>();
		try {
			// ================== 1. 处理上报数据 (业务逻辑) ==================
			Map<String, Object> rawData = new HashMap<>();
			rawData.put("sn", sn);
			rawData.put("payload", bodyStr);

			DeviceMessage message = decode(rawData);

			// 核心业务处理
			messageProcessService.processMessage(message);

			// ================== 2. 检查 Redis 中是否有待下发指令 ==================
			response.put("code", 200);
			response.put("msg", "操作成功");

			// 构建 Redis Key
			String redisKey = CMD_CACHE_KEY_PREFIX + sn;

			// 从 Redis 获取缓存对象
			Map<String, Object> pendingCommand = redisCache.getCacheObject(redisKey);

			if (pendingCommand != null) {
				// 将指令放入 data 字段返回给设备
				response.put("data", pendingCommand);

				// 获取后立即删除，确保指令只执行一次
				// 注意：在极高并发下可能需要使用 Lua 脚本保证原子性，但在 HTTP 轮询场景下通常由设备串行发起，deleteObject 足够
				redisCache.deleteObject(redisKey);

				System.out.println(">>> [HTTP] 已从Redis取出指令并下发给设备: " + sn);
			} else {
				response.put("data", null);
			}

		} catch (Exception e) {
			response.put("code", 500);
			response.put("msg", e.getMessage() != null ? e.getMessage() : "系统内部错误");
		}
		return response;
	}

	@Override
	public DeviceMessage decode(Object rawData) {
		Map<String, Object> map = (Map<String, Object>) rawData;
		DeviceMessage msg = new DeviceMessage();
		msg.setDeviceSn(map.get("sn") != null ? map.get("sn").toString() : "UNKNOWN");
		msg.setType(DeviceMessage.MessageType.PROPERTY_REPORT);
		String payloadStr = (String) map.get("payload");
		Map<String, Object> body = new HashMap<>();
		if (payloadStr != null && !payloadStr.isEmpty()) {
			try {
				body = objectMapper.readValue(payloadStr, Map.class);
			} catch (JsonProcessingException e) {
				 throw new RuntimeException("JSON解析失败", e);
			}
		}
		msg.setBody(body);
		return msg;
	}

	/**
	 * 下发指令实现 (写入 Redis)
	 * @param deviceSn 设备序列号
	 * @param message  包含指令内容的消息对象
	 */
	@Override
	public void sendMessage(String deviceSn, DeviceMessage message) {
		// 1. 构造符合物模型标准的下发格式
		Map<String, Object> commandPayload = new HashMap<>();
		commandPayload.put("id", System.currentTimeMillis());
		commandPayload.put("method", "thing.service.property.set");
		commandPayload.put("params", message.getBody()); // {"report_interval": 10}

		// 2. 存入 Redis
		String redisKey = CMD_CACHE_KEY_PREFIX + deviceSn;

		// setCacheObject(Key, Value, 过期时间, 时间单位)
		// 这里设置 1 天过期，避免设备长期离线后，收到已经过时的旧命令
		redisCache.setCacheObject(redisKey, commandPayload, 1, TimeUnit.DAYS);

		System.out.println(">>> [HTTP] 指令已存入Redis，Key: " + redisKey);
	}
}