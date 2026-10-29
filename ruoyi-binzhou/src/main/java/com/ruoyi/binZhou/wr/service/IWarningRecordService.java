package com.ruoyi.binZhou.wr.service;

import java.util.List;
import com.ruoyi.binZhou.wr.domain.WarningRecord;

/**
 * 预警记录Service接口
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
public interface IWarningRecordService 
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
     * 批量删除预警记录
     * 
     * @param ids 需要删除的预警记录主键集合
     * @return 结果
     */
    public int deleteWarningRecordByIds(Long[] ids);

    /**
     * 删除预警记录信息
     * 
     * @param id 预警记录主键
     * @return 结果
     */
    public int deleteWarningRecordById(Long id);
}
