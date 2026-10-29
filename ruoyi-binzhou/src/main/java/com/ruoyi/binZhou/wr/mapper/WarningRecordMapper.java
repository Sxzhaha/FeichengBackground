package com.ruoyi.binZhou.wr.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.binZhou.wr.domain.WarningRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 预警记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
public interface WarningRecordMapper 
{
    /**
     * 查询预警记录
     * 
     * @param id 预警记录主键
     * @return 预警记录
     */
    public WarningRecord selectWarningRecordById(Long id);

    /**
     * 查询预警记录列表
     * 
     * @param warningRecord 预警记录
     * @return 预警记录集合
     */
    public List<WarningRecord> selectWarningRecordList(WarningRecord warningRecord);


    /**
     * 查询任务所属园区的最新实时气象指标值
     *
     * @param templateId 模板ID，用于设备关系无效时回退到模板园区
     * @param deviceId 设备ID
     * @param indicatorCode 规范化后的指标编码
     * @return 最新指标值
     */
    public BigDecimal selectLatestWeatherLiveValue(@Param("templateId") Long templateId,
            @Param("deviceId") Long deviceId,
            @Param("indicatorCode") String indicatorCode);

    /**
     * 新增预警记录
     * 
     * @param warningRecord 预警记录
     * @return 结果
     */
    public int insertWarningRecord(WarningRecord warningRecord);

    /**
     * 修改预警记录
     * 
     * @param warningRecord 预警记录
     * @return 结果
     */
    public int updateWarningRecord(WarningRecord warningRecord);

    /**
     * 删除预警记录
     * 
     * @param id 预警记录主键
     * @return 结果
     */
    public int deleteWarningRecordById(Long id);

    /**
     * 批量删除预警记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWarningRecordByIds(Long[] ids);
}
