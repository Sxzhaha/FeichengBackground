package com.ruoyi.iot.ld.mapper;

import java.util.Date;
import java.util.List;
import com.ruoyi.iot.ld.domain.IotDeviceLog;
import org.apache.ibatis.annotations.Param;

/**
 * 设备通信日志Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
public interface IotDeviceLogMapper 
{
    /**
     * 查询设备通信日志
     * 
     * @param logId 设备通信日志主键
     * @return 设备通信日志
     */
    public IotDeviceLog selectIotDeviceLogByLogId(Long logId);

    public IotDeviceLog selectLatestIotDeviceLogBySnAndIdentifier(@Param("deviceSn") String deviceSn,
                                                                  @Param("identifier") String identifier);

    /**
     * 查询设备通信日志列表
     * 
     * @param iotDeviceLog 设备通信日志
     * @return 设备通信日志集合
     */
    public List<IotDeviceLog> selectIotDeviceLogList(IotDeviceLog iotDeviceLog);

    /** App端按设备和时间查询最近属性上报日志 */
    public List<IotDeviceLog> selectRecentLogsForApp(@Param("deviceSn") String deviceSn,
                                                     @Param("beginTime") Date beginTime,
                                                     @Param("limit") Integer limit);

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
     * 删除设备通信日志
     * 
     * @param logId 设备通信日志主键
     * @return 结果
     */
    public int deleteIotDeviceLogByLogId(Long logId);

    /**
     * 批量删除设备通信日志
     * 
     * @param logIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIotDeviceLogByLogIds(Long[] logIds);
    /**
     * 校验是否存在完全相同的日志记录
     */
    public int checkLogExists(@Param("deviceSn") String deviceSn,
                       @Param("payload") String payload,
                       @Param("createTime") Date createTime);
}
