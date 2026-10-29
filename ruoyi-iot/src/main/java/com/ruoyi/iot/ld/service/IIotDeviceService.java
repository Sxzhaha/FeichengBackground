package com.ruoyi.iot.ld.service;

import com.ruoyi.iot.ld.domain.IotDevice;

import java.util.List;

/**
 * @ProjectName RuoYi-Iot
 * @ClassName IIotService
 * @Author 刘邓
 * @Date 2026-02-02 11:10
 *
 */
public interface IIotDeviceService {
	/**
	 * 查询物联网设备
	 * * @param deviceId 物联网设备主键
	 * @return 物联网设备
	 */
	public IotDevice selectIotDeviceByDeviceId(Long deviceId);

	/**
	 * 查询物联网设备列表
	 * * @param iotDevice 物联网设备
	 * @return 物联网设备集合
	 */
	public List<IotDevice> selectIotDeviceList(IotDevice iotDevice);

	/**
	 * 新增物联网设备
	 * * @param iotDevice 物联网设备
	 * @return 结果
	 */
	public int insertIotDevice(IotDevice iotDevice);

	/**
	 * 修改物联网设备
	 * * @param iotDevice 物联网设备
	 * @return 结果
	 */
	public int updateIotDevice(IotDevice iotDevice);

	/**
	 * 批量删除物联网设备
	 * * @param deviceIds 需要删除的物联网设备主键集合
	 * @return 结果
	 */
	public int deleteIotDeviceByDeviceIds(Long[] deviceIds);

	/**
	 * 删除物联网设备信息
	 * * @param deviceId 物联网设备主键
	 * @return 结果
	 */
	public int deleteIotDeviceByDeviceId(Long deviceId);

	/**
	 * 校验设备SN是否唯一
	 * * @param iotDevice 设备信息
	 * @return 结果 true 唯一 / false 不唯一
	 */
	public boolean checkDeviceSnUnique(IotDevice iotDevice);

	/**
	 * 根据SN查询设备（用于核心业务层调用）
	 * * @param deviceSn 设备编号
	 * @return 设备对象
	 */
	public IotDevice selectIotDeviceBySn(String deviceSn);
}
