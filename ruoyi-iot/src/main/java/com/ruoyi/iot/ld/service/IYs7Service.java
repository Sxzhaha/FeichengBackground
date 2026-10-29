package com.ruoyi.iot.ld.service;

/**
 * @ProjectName RuoYi-camera_platform
 * @ClassName IYs7Service
 * @Author 刘邓
 * @Date 2025-12-22 11:14
 *
 */
public interface IYs7Service {
	/**
	 * 获取萤石云 AccessToken
	 * * @return Token字符串
	 */
	String getAccessToken();

	/**
	 * 执行单台设备抓拍、下载、保存本地、入库全流程
	 * * @param snCode 设备序列号
	 */
	void captureAndSave(String snCode,Long userIdOrTaskId,Short type,Integer pointIndex,Integer count);

	/**
	 * 调用萤石云添加预置点
	 * @param snCode 设备序列号
	 * @param pointIndex 预置点索引 (1-255)
	 */
	void addPreset(String snCode, Integer pointIndex);

	/**
	 * 同步所有设备的状态到数据库
	 */
	void syncIotDeviceStatus();
	/**
	 * 删除/清除萤石云设备的预置点
	 * @param snCode 设备序列号
	 * @param pointIndex 预置点索引
	 */
	void clearPreset(String snCode, Integer pointIndex);
}
