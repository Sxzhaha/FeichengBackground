package com.ruoyi.iot.ld.protocol.impl;

import com.ruoyi.iot.ld.model.DeviceMessage;
import com.ruoyi.iot.ld.protocol.abstraction.IProtocolAdapter;
import com.ruoyi.iot.ld.protocol.netty.NettyTcpHandler;
import com.ruoyi.iot.ld.service.impl.MessageProcessService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class ModbusProtocolAdapter implements IProtocolAdapter {

	@Autowired
	private MessageProcessService messageProcessService;

	@PostConstruct
	public void startServer() {
		// 异步启动 Netty Server，防止阻塞 Spring 主线程
		new Thread(() -> {
			NioEventLoopGroup boss = new NioEventLoopGroup(1);
			NioEventLoopGroup worker = new NioEventLoopGroup();
			try {
				ServerBootstrap bootstrap = new ServerBootstrap();
				bootstrap.group(boss, worker)
						.channel(NioServerSocketChannel.class)
						.childHandler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel ch) {
								// 可以在这里添加 ModbusDecoder (如果用现成的库)
								ch.pipeline().addLast(new NettyTcpHandler(ModbusProtocolAdapter.this));
							}
						});
				System.out.println("Modbus/TCP 监听启动，端口: 8888");
				bootstrap.bind(8888).sync().channel().closeFuture().sync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public String getProtocolType() {
		return "MODBUS-TCP";
	}

	// 将解析后的消息透传给 Service
	public void process(DeviceMessage msg) {
		messageProcessService.processMessage(msg);
	}

	@Override
	public DeviceMessage decode(Object rawData) {
		byte[] bytes = (byte[]) rawData;
		// !!! 核心逻辑：这里需要按照 Modbus 协议手册解析 byte[]
		// 举例：假设前4位是SN，后4位是温度
		String sn = "DEV_MODBUS_01"; // 实际应从 bytes 中解析

		DeviceMessage msg = new DeviceMessage();
		msg.setDeviceSn(sn);
		msg.setType(DeviceMessage.MessageType.PROPERTY_REPORT);

		Map<String, Object> body = new HashMap<>();
		body.put("hex_data", bytesToHex(bytes)); // 暂存16进制字符串
		msg.setBody(body);

		return msg;
	}

	@Override
	public void sendMessage(String deviceSn, DeviceMessage message) {
		// 从 SessionManager 获取 Channel，写入 ByteBuf
		System.out.println("向 TCP 设备发送 Modbus 指令: " + message);
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}
}