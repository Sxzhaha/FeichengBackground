package com.ruoyi.iot.ld.protocol.netty;

import com.ruoyi.iot.ld.model.DeviceMessage;
import com.ruoyi.iot.ld.protocol.impl.ModbusProtocolAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class NettyTcpHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private final ModbusProtocolAdapter adapter;

	public NettyTcpHandler(ModbusProtocolAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("TCP设备连接: " + ctx.channel().remoteAddress());
		// 可以在这里做鉴权，或者等待设备发送登录包
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
		// 读取字节流
		byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);

		// 调用适配器解码
		DeviceMessage message = adapter.decode(bytes);

		// 绑定 Channel 与 DeviceSn 的关系 (用于下发指令)
		// 实际项目中需要一个 SessionManager 全局管理 Map<String, Channel>
		ctx.channel().attr(AttributeKey.valueOf("SN")).set(message.getDeviceSn());

		// 转发给业务层
		adapter.process(message);
	}
}