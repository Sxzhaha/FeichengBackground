package com.ruoyi.iot.ld.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;
import java.util.concurrent.Executor;

@Configuration
public class MqttConfig {

	private static final String BROKER_URL = "tcp://118.195.240.92:1883";
	private static final String CLIENT_PREFIX = "ruoyi_server_";
	private static final int RECONNECT_INTERVAL_MILLIS = 5000;

	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		MqttConnectOptions options = new MqttConnectOptions();
		options.setServerURIs(new String[]{BROKER_URL});
		options.setUserName("admin");
		options.setPassword("public".toCharArray());
		// Spring Integration 的入站适配器已经有断线恢复调度。
		// 不再同时启用 Paho 自动重连，避免两个重连机制竞争同一个客户端。
		options.setAutomaticReconnect(false);
		options.setCleanSession(true);
		options.setConnectionTimeout(5);
		options.setKeepAliveInterval(20);
		factory.setConnectionOptions(options);
		return factory;
	}

	// ================== Outbound ==================

	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel")
	public MessageHandler mqttOutbound() {
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(createClientId("out"), mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic("/test");
		return messageHandler;
	}

	// ================== Inbound ==================

	@Bean(name = "mqttInboundExecutor")
	public Executor mqttInboundExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadNamePrefix("mqtt-message-");
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(500);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(10);
		executor.initialize();
		return executor;
	}

	@Bean
	public MessageChannel mqttInputChannel(@Qualifier("mqttInboundExecutor") Executor mqttInboundExecutor) {
		// MQTT 回调线程只负责投递消息，耗时的数据库/业务处理在独立线程池执行，
		// 避免消费阻塞回调线程并间接导致心跳超时或连接被 Broker 断开。
		return new ExecutorChannel(mqttInboundExecutor);
	}

	@Bean
	public MqttPahoMessageDrivenChannelAdapter inbound(
			@Qualifier("mqttInputChannel") MessageChannel mqttInputChannel) {
		MqttPahoMessageDrivenChannelAdapter adapter =
				new MqttPahoMessageDrivenChannelAdapter(createClientId("in"), mqttClientFactory(),
						"/sys/+/report");
		adapter.setCompletionTimeout(5000);
		adapter.setRecoveryInterval(RECONNECT_INTERVAL_MILLIS);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(1);
		adapter.setOutputChannel(mqttInputChannel);
		return adapter;
	}

	private String createClientId(String suffix) {
		return CLIENT_PREFIX + suffix + "_" + UUID.randomUUID().toString().replace("-", "");
	}
}
