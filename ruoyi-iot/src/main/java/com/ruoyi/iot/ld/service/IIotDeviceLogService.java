package com.ruoyi.iot.ld.service;


import java.util.Date;
import java.util.List;
import com.ruoyi.iot.ld.domain.IotDeviceLog;

/**
 * 设备通信日志Service接口
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
public interface IIotDeviceLogService 
{
    /**
     * 查询设备通信日志
     * 
     * @param logId 设备通信日志主键
     * @return 设备通信日志
     */
    public IotDeviceLog selectIotDeviceLogByLogId(Long logId);

    /**
     * 查询设备通信日志列表
     * 
     * @param iotDeviceLog 设备通信日志
     * @return 设备通信日志集合
     */
    public List<IotDeviceLog> selectIotDeviceLogList(IotDeviceLog iotDeviceLog);

    /**
     * 新增设备通信日志
     * 
     * @param iotDeviceLog 设备通信日志
     * @return 结果
     */
    public int insertIotDeviceLog(IotDeviceLog iotDeviceLog);

    /**
     * 修改设备通信日志
     * 
     * @param iotDeviceLog 设备通信日志
     * @return 结果
     */
    public int updateIotDeviceLog(IotDeviceLog iotDeviceLog);

    /**
     * 批量删除设备通信日志
     * 
     * @param logIds 需要删除的设备通信日志主键集合
     * @return 结果
     */
    public int deleteIotDeviceLogByLogIds(Long[] logIds);

    /**
     * 删除设备通信日志信息
     * 
     * @param logId 设备通信日志主键
     * @return 结果
     */
    public int deleteIotDeviceLogByLogId(Long logId);
    public boolean checkLogExists(String deviceSn, String payload, Date createTime);
}
