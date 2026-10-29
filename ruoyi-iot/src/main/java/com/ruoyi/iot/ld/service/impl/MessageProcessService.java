package com.ruoyi.iot.ld.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.domain.IotDeviceLog;
import com.ruoyi.iot.ld.domain.IotProduct;
import com.ruoyi.iot.ld.mapper.IotDeviceMapper;
import com.ruoyi.iot.ld.mapper.IotProductMapper;
import com.ruoyi.iot.ld.model.DeviceMessage;
import com.ruoyi.iot.ld.protocol.abstraction.IProtocolAdapter;
import com.ruoyi.iot.ld.protocol.factory.ProtocolAdapterFactory;
import com.ruoyi.iot.ld.service.IIotDeviceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Core device message processing service.
 */
@Service
public class MessageProcessService {

	@Autowired
	private IotDeviceMapper deviceMapper;

	@Autowired
	private IotProductMapper productMapper;

	@Autowired
	private IIotDeviceLogService iotDeviceLogService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Process upstream device messages.
	 */
	public void processMessage(DeviceMessage message) {
		IotDevice device = deviceMapper.selectIotDeviceBySn(message.getDeviceSn());

		if (device == null) {
			throw new RuntimeException("设备不存在，SN: " + message.getDeviceSn());
		}

		device.setLastOnlineTime(new Date());
		if ("0".equals(device.getStatus())) {
			device.setStatus("1");
			System.out.println("设备[" + device.getDeviceName() + "]重新激活上线");
		}
		deviceMapper.updateIotDevice(device);

		if (message.getType() != null) {
			switch (message.getType()) {
				case CONNECT:
					handleConnect(device);
					break;
				case PROPERTY_REPORT:
					handlePropertySave(device, message);
					break;
				default:
					break;
			}
		}
	}

	public void sendCommand(String deviceSn, String command) {
		IotDevice device = deviceMapper.selectIotDeviceBySn(deviceSn);
		if (device == null) {
			throw new RuntimeException("设备不存在，SN: " + deviceSn);
		}

		IotProduct product = productMapper.selectIotProductByProductId(device.getProductId());
		if (product == null || !product.isController()) {
			throw new RuntimeException("该设备所属产品不是控制终端，不能下发控制指令: " + deviceSn);
		}

		String protocolType = device.getProtocolType();
		IProtocolAdapter adapter = ProtocolAdapterFactory.getAdapter(protocolType);

		if (adapter != null) {
			DeviceMessage msg = new DeviceMessage();
			msg.setDeviceSn(deviceSn);
			adapter.sendMessage(deviceSn, msg);
		} else {
			throw new RuntimeException("不支持的协议类型: " + protocolType);
		}
	}

	private void handleConnect(IotDevice device) {
		device.setStatus("1");
		device.setLastOnlineTime(new Date());
		deviceMapper.updateIotDevice(device);
		System.out.println("设备上线: " + device.getDeviceName());

		saveLog(device.getDeviceSn(), 2, "设备上线");
	}

	private void handlePropertySave(IotDevice device, DeviceMessage message) {
		System.out.println("收到设备[" + device.getDeviceName() + "]数据: " + message.getBody());

		String payloadJson = "";
		try {
			if (message.getBody() != null) {
				payloadJson = objectMapper.writeValueAsString(message.getBody());
			}
		} catch (Exception e) {
			payloadJson = message.getBody().toString();
			e.printStackTrace();
		}

		saveLog(device.getDeviceSn(), 1, payloadJson);
	}

	private void saveLog(String sn, Integer type, String payload) {
		IotDeviceLog log = new IotDeviceLog();
		log.setDeviceSn(sn);
		log.setLogType(type);
		log.setPayload(payload);
		log.setCreateTime(new Date());
		iotDeviceLogService.insertIotDeviceLog(log);
	}
}
