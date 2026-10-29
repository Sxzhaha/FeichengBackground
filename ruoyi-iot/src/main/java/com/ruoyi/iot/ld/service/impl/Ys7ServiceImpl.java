package com.ruoyi.iot.ld.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.iot.ld.domain.CameraTask;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.domain.ImageRecord;
import com.ruoyi.iot.ld.mapper.CameraTaskMapper;
import com.ruoyi.iot.ld.mapper.IotDeviceMapper;
import com.ruoyi.iot.ld.mapper.ImageRecordMapper;
import com.ruoyi.iot.ld.service.IYs7Service;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 萤石云业务服务实现类
 * @author ruoyi
 */
@Service
public class Ys7ServiceImpl implements IYs7Service {

	private static final Logger log = LoggerFactory.getLogger(Ys7ServiceImpl.class);

	@Value("${ys7.app-key}")
	private String appKey;

	@Value("${ys7.app-secret}")
	private String appSecret;

	@Value("${ys7.url}")
	private String baseUrl;
	@Value("${ruoyi.capture-server-url:${ruoyi.domain:}}")
	private String captureServerUrl;

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private IotDeviceMapper deviceMapper;

	@Autowired
	private ImageRecordMapper imageRecordMapper;
	@Autowired
	private CameraTaskMapper cameraTaskMapper;

	@Autowired
	private ServerConfig serverConfig;

	private static final String YS7_TOKEN_KEY = "ys7:token";

	@Override
	public String getAccessToken() {
		String token = redisCache.getCacheObject(YS7_TOKEN_KEY);
		if (StrUtil.isNotEmpty(token)) {
			return token;
		}

		String api = baseUrl + "/token/get";
		Map<String, Object> param = new HashMap<>();
		param.put("appKey", appKey);
		param.put("appSecret", appSecret);

		try {
			String result = HttpUtil.post(api, param);
			JSONObject json = JSON.parseObject(result);

			if ("200".equals(json.getString("code"))) {
				JSONObject data = json.getJSONObject("data");
				String accessToken = data.getString("accessToken");
				long expireTime = data.getLongValue("expireTime");

				// 提前10分钟过期
				long current = System.currentTimeMillis();
				long ttl = (expireTime - current) / 1000 - 600;

				if (ttl > 0) {
					redisCache.setCacheObject(YS7_TOKEN_KEY, accessToken, (int) ttl, TimeUnit.SECONDS);
				}
				return accessToken;
			} else {
				throw new RuntimeException("萤石云Token获取失败: " + json.getString("msg"));
			}
		} catch (Exception e) {
			log.error("获取Token异常", e);
			throw new RuntimeException("连接萤石云服务失败");
		}
	}
	/**
	 * 移动预置点 (新增辅助方法)
	 */
	private void movePreset(String snCode, Integer pointIndex, String token) {
		if (pointIndex == null || pointIndex < 0) return;

		String moveApi = baseUrl + "/device/preset/move";
		Map<String, Object> param = new HashMap<>();
		param.put("accessToken", token);
		param.put("deviceSerial", snCode);
		param.put("channelNo", 1);
		param.put("index", pointIndex);

		try {
			HttpUtil.post(moveApi, param);
			// 这里不要 sleep 了，在外面统一 sleep
		} catch (Exception e) {
			log.error("移动预置点请求异常", e);
		}
	}
	@Override
	public void captureAndSave(String snCode, Long userIdOrtaskId, Short type, Integer pointIndex, Integer count) {
		IotDevice device = deviceMapper.selectIotDeviceBySnCode(snCode);
		if (device == null) {
			throw new RuntimeException("设备不存在: " + snCode);
		}

		String token = getAccessToken();

		// 如果是定时任务，预先查询任务信息，避免在循环中重复查询
		Long taskCreatorId = null;
		if (type == 1) {
			CameraTask task = cameraTaskMapper.selectCameraTaskByTaskId(userIdOrtaskId);
			if (task != null) {
				taskCreatorId = task.getCreatorId();
			}
		}

		// 预置点移动逻辑
		if (type == 1 && pointIndex != null) {
			log.info("设备[{}]移动至预置点[{}]", snCode, pointIndex);
			movePreset(snCode, pointIndex, token);
			try {
				Thread.sleep(10000); // 等待转动聚焦
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("预置点移动等待被中断", e);
				return;
			}
		}

		int executeCount = (count == null || count < 1) ? 1 : count;

		for (int i = 0; i < executeCount; i++) {
			// 多次抓拍的间隔等待
			if (i > 0) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}

			try {
				// 1. 调用抓拍接口
				String captureApi = baseUrl + "/device/capture";
				Map<String, Object> param = new HashMap<>();
				param.put("accessToken", token);
				param.put("deviceSerial", snCode);
				param.put("channelNo", 1);

				String res = HttpUtil.post(captureApi, param);
				JSONObject resJson = JSON.parseObject(res);

				// 接口返回非200时，抛出异常
				if (resJson == null || !"200".equals(resJson.getString("code"))) {
					String msg = resJson != null ? resJson.getString("msg") : "萤石云接口无响应";
					log.error("设备[{}] 第 {} 次抓拍失败: {}", snCode, (i + 1), msg);
					// 抛出异常供 Controller 捕获
					throw new RuntimeException(msg);
				}
				String picUrl = resJson.getJSONObject("data").getString("picUrl");

				// 2. 下载图片
				byte[] imgBytes = HttpUtil.downloadBytes(picUrl);
				// 【修改点 2】下载失败抛出异常
				if (imgBytes == null || imgBytes.length == 0) {
					throw new RuntimeException("图片流下载失败或为空");
				}

				// 3. 保存文件 (命名格式：时间戳_序号.jpg)
				Date now = new Date();
				// 修改点：使用系统毫秒时间戳 + 当前循环序号
				String fileName = System.currentTimeMillis() + "_" + (i + 1) + ".jpg";

				String datePath = DateUtils.datePath();
				String timePath = DateUtils.parseDateToStr("HH/mm", now);
				String relativePath = "/haikang/" + snCode + "/" + datePath + "/" + timePath + "/" + fileName;
				String absolutePath = RuoYiConfig.getProfile() + relativePath;

				File file = new File(absolutePath);
				FileUtil.writeBytes(imgBytes, file);

				String url = buildCaptureUrl(relativePath);

				// 4. 入库记录
				ImageRecord record = new ImageRecord();
				record.setDeviceId(device.getDeviceId());
				record.setUrl(url);
				record.setFileSize((long) imgBytes.length);
				record.setCaptureTime(now);
				record.setCreateTime(now);

				if (type == 1) {
					record.setTaskId(userIdOrtaskId);
					record.setUserId(taskCreatorId); // 使用循环外查询的结果
					String countInfo = executeCount > 1 ? String.format(" [%d/%d]", (i + 1), executeCount) : "";
					record.setRemark("自动抓拍 (预置点:" + pointIndex + ")" + countInfo);
				} else {
					record.setUserId(userIdOrtaskId);
					record.setRemark("手动抓拍");
				}

				// 建议：此处单独调用一个带事务的小方法进行插入，或者直接插入（单条insert通常不需要显式事务）
				imageRecordMapper.insertImageRecord(record);
				log.info("设备[{}] 抓拍成功 [{}/{}]", snCode, (i + 1), executeCount);

			} catch (Exception e) {
				log.error("第 {} 次抓拍异常: {}", (i + 1), e.getMessage());
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	@Override
	public void addPreset(String snCode, Integer pointIndex) {
		// 1. 获取 Token
		String token = getAccessToken();

		// 2. 构造请求参数
		// 萤石云增加预置点接口地址
		String api = baseUrl + "/device/preset/add";

		Map<String, Object> param = new HashMap<>();
		param.put("accessToken", token);
		param.put("deviceSerial", snCode);
		param.put("channelNo", 1);        // 通道号，通常是 1
		param.put("index", pointIndex);   // 预置点编号

		try {
			// 3. 发送请求
			String result = HttpUtil.post(api, param);
			JSONObject json = JSON.parseObject(result);

			// 4. 校验结果
			if (!"200".equals(json.getString("code"))) {
				// 如果萤石云返回错误（比如编号超出范围、设备不支持云台等）
				throw new RuntimeException("萤石云设置失败: " + json.getString("msg"));
			}

			log.info("设备[{}] 预置点[{}] 设置成功", snCode, pointIndex);

		} catch (Exception e) {
			log.error("设置预置点异常", e);
			throw new RuntimeException(e.getMessage()); // 抛出异常中断 Controller 流程
		}
	}

	private String buildCaptureUrl(String relativePath) {
		String resourcePath = Constants.RESOURCE_PREFIX + relativePath;
		if (StrUtil.isBlank(captureServerUrl)) {
			return resourcePath;
		}
		return StrUtil.removeSuffix(captureServerUrl, "/") + resourcePath;
	}

	@Override
	public void syncIotDeviceStatus() {
		String token = getAccessToken();
		String api = baseUrl + "/device/list";

		// 分页获取萤石云设备（假设设备不超过50台，如果多需要写循环分页）
		Map<String, Object> param = new HashMap<>();
		param.put("accessToken", token);
		param.put("pageStart", 0);
		param.put("pageSize", 50);

		try {
			String result = HttpUtil.post(api, param);
			JSONObject json = JSON.parseObject(result);

			if ("200".equals(json.getString("code"))) {
				JSONArray dataList = json.getJSONArray("data");
				if (dataList == null) return;

				for (int i = 0; i < dataList.size(); i++) {
					JSONObject item = dataList.getJSONObject(i);
					String deviceSerial = item.getString("deviceSerial");
					// 萤石云返回：1-在线，0-不在线
					Integer onlineStatus = item.getInteger("status");

					// 更新本地数据库
					// 这里建议写一个只更新状态的SQL，性能更好
					deviceMapper.updateIotDeviceStatusBySn(deviceSerial, String.valueOf(onlineStatus));
				}
				log.info("设备状态同步完成，共同步 {} 台设备", dataList.size());
			}
		} catch (Exception e) {
			log.error("同步设备状态失败", e);
		}
	}
	@Override
	public void clearPreset(String snCode, Integer pointIndex) {
		String token = getAccessToken();
		// 萤石云清除预置点接口
		String api = baseUrl + "/device/preset/clear";

		Map<String, Object> param = new HashMap<>();
		param.put("accessToken", token);
		param.put("deviceSerial", snCode);
		param.put("channelNo", 1);
		param.put("index", pointIndex);

		try {
			String result = HttpUtil.post(api, param);
			JSONObject json = JSON.parseObject(result);

			// 200代表成功，部分设备如果该点位本来就不存在也可能返回200，具体视固件而定
			// 如果返回错误码，则抛出异常阻止本地删除
			if (!"200".equals(json.getString("code"))) {
				throw new RuntimeException("萤石云删除失败: " + json.getString("msg"));
			}

			log.info("设备[{}] 预置点[{}] 清除指令下发成功", snCode, pointIndex);
		} catch (Exception e) {
			log.error("清除预置点异常", e);
			throw new RuntimeException(e.getMessage());
		}
	}
}
