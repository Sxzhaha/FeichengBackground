package com.ruoyi.iot.ld.utils;

import com.ruoyi.iot.ld.service.IYs7Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ProjectName RuoYi-camera_platform
 * @ClassName DeviceStatusScheduler
 * @Author 刘邓
 * @Date 2025-12-24 10:09
 *
 */
@Component
@EnableScheduling
public class DeviceStatusScheduler {
	@Autowired
	private IYs7Service ys7Service;

	/**
	 * 每隔 5 分钟同步一次设备在线状态
	 */
	@Scheduled(cron = "0 0/5 * * * ?")
	public void syncStatus() {
		ys7Service.syncIotDeviceStatus();
	}
}
