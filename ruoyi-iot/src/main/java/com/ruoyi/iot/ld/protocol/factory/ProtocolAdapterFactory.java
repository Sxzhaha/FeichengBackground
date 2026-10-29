package com.ruoyi.iot.ld.protocol.factory;

import com.ruoyi.iot.ld.protocol.abstraction.IProtocolAdapter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProtocolAdapterFactory implements ApplicationContextAware {

	private static final Map<String, IProtocolAdapter> ADAPTER_MAP = new ConcurrentHashMap<>();

	/**
	 * 根据协议类型获取适配器
	 */
	public static IProtocolAdapter getAdapter(String protocolType) {
		return ADAPTER_MAP.get(protocolType);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// 自动扫描所有实现了IProtocolAdapter的Bean，注册到Map中
		Map<String, IProtocolAdapter> beans = applicationContext.getBeansOfType(IProtocolAdapter.class);
		beans.values().forEach(adapter -> {
			ADAPTER_MAP.put(adapter.getProtocolType(), adapter);
		});
	}
}