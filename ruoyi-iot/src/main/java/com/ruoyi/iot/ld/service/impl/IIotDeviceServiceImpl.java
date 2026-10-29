package com.ruoyi.iot.ld.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.mapper.IotDeviceMapper;
import com.ruoyi.iot.ld.service.IIotDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ProjectName RuoYi-Iot
 * @ClassName IIotDeviceServiceImpl
 * @Author 刘邓
 * @Date 2026-02-02 11:12
 *
 */
@Service
public class IIotDeviceServiceImpl implements IIotDeviceService {
	@Autowired
	private IotDeviceMapper iotDeviceMapper;

	/**
	 * 查询物联网设备
	 */
	@Override
	public IotDevice selectIotDeviceByDeviceId(Long deviceId)
	{
		return iotDeviceMapper.selectIotDeviceByDeviceId(deviceId);
	}

	/**
	 * 查询物联网设备列表
	 */
	@Override
	public List<IotDevice> selectIotDeviceList(IotDevice iotDevice)
	{
		return iotDeviceMapper.selectIotDeviceList(iotDevice);
	}

	/**
	 * 新增物联网设备
	 */
	@Override
	public int insertIotDevice(IotDevice iotDevice)
	{
		iotDevice.setCreateTime(DateUtils.getNowDate());
		iotDevice.setCreatorId(SecurityUtils.getUserId());
		// 默认状态设置为离线(0)，防止插入null
		if (StringUtils.isEmpty(iotDevice.getStatus())) {
			iotDevice.setStatus("0");
		}
		return iotDeviceMapper.insertIotDevice(iotDevice);
	}

	/**
	 * 修改物联网设备
	 */
	@Override
	public int updateIotDevice(IotDevice iotDevice)
	{
		iotDevice.setUpdateTime(DateUtils.getNowDate());
		return iotDeviceMapper.updateIotDevice(iotDevice);
	}

	/**
	 * 批量删除物联网设备
	 */
	@Override
	public int deleteIotDeviceByDeviceIds(Long[] deviceIds)
	{
		return iotDeviceMapper.deleteIotDeviceByDeviceIds(deviceIds);
	}

	/**
	 * 删除物联网设备信息
	 */
	@Override
	public int deleteIotDeviceByDeviceId(Long deviceId)
	{
		return iotDeviceMapper.deleteIotDeviceByDeviceId(deviceId);
	}

	/**
	 * 校验设备SN是否唯一
	 * 逻辑：
	 * 1. 根据SN去查数据库。
	 * 2. 如果查到了设备：
	 * - 如果是新增操作（deviceId为空），说明SN重复 -> 返回 false。
	 * - 如果是修改操作（deviceId不为空），但查到的设备ID和当前修改的ID不一致，说明SN被别的设备占用了 -> 返回 false。
	 */
	@Override
	public boolean checkDeviceSnUnique(IotDevice iotDevice)
	{
		Long deviceId = StringUtils.isNull(iotDevice.getDeviceId()) ? -1L : iotDevice.getDeviceId();
		IotDevice info = iotDeviceMapper.selectIotDeviceBySn(iotDevice.getDeviceSn());

		if (StringUtils.isNotNull(info) && info.getDeviceId().longValue() != deviceId.longValue())
		{
			return false;
		}
		return true;
	}

	/**
	 * 根据SN查询设备
	 */
	@Override
	public IotDevice selectIotDeviceBySn(String deviceSn) {
		return iotDeviceMapper.selectIotDeviceBySn(deviceSn);
	}
}
