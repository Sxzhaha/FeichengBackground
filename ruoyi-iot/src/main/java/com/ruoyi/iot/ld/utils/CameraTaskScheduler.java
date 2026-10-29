package com.ruoyi.iot.ld.utils;

import com.ruoyi.iot.ld.domain.CameraPreset;
import com.ruoyi.iot.ld.domain.CameraTask;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.mapper.CameraPresetMapper;
import com.ruoyi.iot.ld.mapper.CameraTaskMapper;
import com.ruoyi.iot.ld.mapper.IotDeviceMapper;
import com.ruoyi.iot.ld.service.IYs7Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName RuoYi-camera_platform
 * @ClassName CameraTaskScheduler
 * @Author 刘邓
 * @Date 2025-12-22 16:07
 */
@Component
@EnableScheduling
public class CameraTaskScheduler {

	private static final Logger log = LoggerFactory.getLogger(CameraTaskScheduler.class);

	@Autowired
	private CameraPresetMapper cameraPresetMapper;

	@Autowired
	private IotDeviceMapper deviceMapper;

	@Autowired
	private CameraTaskMapper cameraTaskMapper;

	@Autowired
	private IYs7Service ys7Service;

	/**
	 * 核心调度逻辑：每隔 1 分钟执行一次
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	public void processTasks() {
		// ... (保持原有的 processTasks 逻辑不变) ...
		CameraTask query = new CameraTask();
		query.setStatus("0");
		List<CameraTask> taskList = cameraTaskMapper.selectCameraTaskList(query);

		if (taskList == null || taskList.isEmpty()) {
			return;
		}

		Date now = new Date();
		log.info("定时任务扫描开始，当前待处理任务数：{}", taskList.size());

		for (CameraTask task : taskList) {
			try {
				boolean shouldExecute = false;

				if ("ONCE".equals(task.getJobType())) {
					Date executeTime = task.getExecuteTime();
					if (executeTime != null && !now.before(executeTime)) {
						shouldExecute = true;
						task.setStatus("2");
						cameraTaskMapper.updateCameraTask(task);
					}
				} else if ("CRON".equals(task.getJobType())) {
					String cron = task.getCronExpression();
					if (isValidCron(cron)) {
						Date oneMinuteAgo = new Date(now.getTime() - 60 * 1000);
						CronExpression expression = CronExpression.parse(cron);
						LocalDateTime nextTimeLocal = expression.next(
								LocalDateTime.ofInstant(oneMinuteAgo.toInstant(), ZoneId.systemDefault())
						);

						if (nextTimeLocal != null) {
							Date nextTime = Date.from(nextTimeLocal.atZone(ZoneId.systemDefault()).toInstant());
							if (!nextTime.after(now)) {
								shouldExecute = true;
							}
						}
					}
				}

				if (shouldExecute) {
					executeCapture(task);
				}

			} catch (Exception e) {
				log.error("处理任务ID[{}]异常", task.getTaskId(), e);
			}
		}
	}

	/**
	 * 执行具体的业务逻辑：按预置点抓拍
	 */
	private void executeCapture(CameraTask task) {
		// 1. 校验预置点 ID
		Long presetId = task.getPresetId();
		if (presetId == null) {
			log.warn("任务[{}]没有配置 presetId，跳过", task.getTaskName());
			return;
		}

		// 2. 查询预置点信息
		// 注意：这里需要你确保 Mapper 中有根据主键 ID 查询的方法 (通常是 selectCameraPresetById)
		// 之前的 selectCameraPresetByDeviceId 可能查出列表，这里需要单条查询
		CameraPreset preset = cameraPresetMapper.selectCameraPresetById(presetId);

		if (preset == null) {
			log.warn("任务[{}]关联的预置点ID[{}]在数据库中不存在，跳过", task.getTaskName(), presetId);
			return;
		}

		// 3. 查询设备信息 (通过预置点关联的 deviceId)
		IotDevice device = deviceMapper.selectIotDeviceByDeviceId(preset.getDeviceId());
		if (device == null) {
			log.warn("预置点[{}]关联的设备ID[{}]不存在，跳过", presetId, preset.getDeviceId());
			return;
		}

		log.info(">>> 触发定时抓拍: 任务[{}], 设备[{}], 预置点索引[{}]",
				task.getTaskName(), device.getDeviceSn(), preset.getPointIndex());

		// 4. 调用抓拍服务
		try {
			// 【关键修改】：将预置点索引 pointIndex 传给 Service
			// 假设 pointIndex 是 Integer 类型，可能需要转为 int 或 short，取决于 Service 定义
			Integer pointIndex = preset.getPointIndex();

			// 这里的 (short)1 表示任务抓拍类型
			ys7Service.captureAndSave(device.getDeviceSn(), task.getTaskId(), (short)1, pointIndex, task.getCount());

			log.info("<<< 抓拍完成");
		} catch (Exception e) {
			log.error("抓拍失败: {}", e.getMessage());
		}
	}

	/**
	 * 校验 Cron 表达式是否有效
	 */
	private boolean isValidCron(String cron) {
		return cron != null && CronExpression.isValidExpression(cron);
	}
}