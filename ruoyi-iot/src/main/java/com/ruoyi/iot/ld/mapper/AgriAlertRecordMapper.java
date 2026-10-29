package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.AgriAlertRecord;

/**
 * 产品预警触发与历史处理记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface AgriAlertRecordMapper 
{
    /**
     * 查询产品预警触发与历史处理记录
     * 
     * @param id 产品预警触发与历史处理记录主键
     * @return 产品预警触发与历史处理记录
     */
    public AgriAlertRecord selectAgriAlertRecordById(Long id);

    /**
     * 查询产品预警触发与历史处理记录列表
     * 
     * @param agriAlertRecord 产品预警触发与历史处理记录
     * @return 产品预警触发与历史处理记录集合
     */
    public List<AgriAlertRecord> selectAgriAlertRecordList(AgriAlertRecord agriAlertRecord);

    /**
     * 新增产品预警触发与历史处理记录
     * 
     * @param agriAlertRecord 产品预警触发与历史处理记录
     * @return 结果
     */
    public int insertAgriAlertRecord(AgriAlertRecord agriAlertRecord);

    /**
     * 修改产品预警触发与历史处理记录
     * 
     * @param agriAlertRecord 产品预警触发与历史处理记录
     * @return 结果
     */
    public int updateAgriAlertRecord(AgriAlertRecord agriAlertRecord);

    /**
     * 删除产品预警触发与历史处理记录
     * 
     * @param id 产品预警触发与历史处理记录主键
     * @return 结果
     */
    public int deleteAgriAlertRecordById(Long id);

    /**
     * 批量删除产品预警触发与历史处理记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAgriAlertRecordByIds(Long[] ids);
}
