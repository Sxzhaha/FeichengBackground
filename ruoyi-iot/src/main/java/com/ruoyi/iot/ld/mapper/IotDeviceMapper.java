package com.ruoyi.iot.ld.mapper;

import com.ruoyi.iot.ld.domain.IotDevice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ProjectName RuoYi-Iot
 * @ClassName IotMapper
 * @Author 刘邓
 * @Date 2026-02-02 10:32
 *
 */
public interface IotDeviceMapper {
	/**
	 * 查询物联网设备
	 * * @param deviceId 物联网设备主键
	 * @return 物联网设备
	 */
	public IotDevice selectIotDeviceByDeviceId(Long deviceId);

	/**
	 * 根据SN查询设备（核心业务使用）
	 * * @param deviceSn 设备序列号
	 * @return 物联网设备
	 */
	public IotDevice selectIotDeviceBySn(String deviceSn);

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
	 * 根据设备主键更新状态
	 * @param deviceId 设备ID
	 * @param status 状态
	 * @return 结果
	 */
	public int updateIotDeviceStatusByDeviceId(@Param("deviceId") Long deviceId, @Param("status") String status);

	/**
	 * 删除物联网设备
	 * * @param deviceId 物联网设备主键
	 * @return 结果
	 */
	public int deleteIotDeviceByDeviceId(Long deviceId);

	/**
	 * 批量删除物联网设备
	 * * @param deviceIds 需要删除的数据主键集合
	 * @return 结果
	 */
	public int deleteIotDeviceByDeviceIds(Long[] deviceIds);
	/**
	 * 批量更新超时设备为离线
	 * @param timeoutSeconds 超时时间（秒）
	 * @return 影响行数
	 */
	public int updateDeviceStatusTimeout(int timeoutSeconds);
	/**
	 * 根据序列号查询设备 (新增这个方法)
	 * * @param snCode 设备序列号
	 * @return 设备信息
	 */
	public IotDevice selectIotDeviceBySnCode(String snCode);
	public int updateIotDeviceStatusBySn(@Param("snCode") String snCode, @Param("status") String status);
}
