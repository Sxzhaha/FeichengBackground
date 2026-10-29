package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.AgriAlertRecordMapper;
import com.ruoyi.iot.ld.domain.AgriAlertRecord;
import com.ruoyi.iot.ld.service.IAgriAlertRecordService;

/**
 * 产品预警触发与历史处理记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class AgriAlertRecordServiceImpl implements IAgriAlertRecordService 
{
    @Autowired
    private AgriAlertRecordMapper agriAlertRecordMapper;

    /**
     * 查询产品预警触发与历史处理记录
     * 
     * @param id 产品预警触发与历史处理记录主键
     * @return 产品预警触发与历史处理记录
     */
    @Override
    public AgriAlertRecord selectAgriAlertRecordById(Long id)
    {
        return agriAlertRecordMapper.selectAgriAlertRecordById(id);
    }

    /**
     * 查询产品预警触发与历史处理记录列表
     * 
     * @param agriAlertRecord 产品预警触发与历史处理记录
     * @return 产品预警触发与历史处理记录
     */
    @Override
    public List<AgriAlertRecord> selectAgriAlertRecordList(AgriAlertRecord agriAlertRecord)
    {
        return agriAlertRecordMapper.selectAgriAlertRecordList(agriAlertRecord);
    }

    /**
     * 新增产品预警触发与历史处理记录
     * 
     * @param agriAlertRecord 产品预警触发与历史处理记录
     * @return 结果
     */
    @Override
    public int insertAgriAlertRecord(AgriAlertRecord agriAlertRecord)
    {
        agriAlertRecord.setCreateTime(DateUtils.getNowDate());
        return agriAlertRecordMapper.insertAgriAlertRecord(agriAlertRecord);
    }

    /**
     * 修改产品预警触发与历史处理记录
     * 
     * @param agriAlertRecord 产品预警触发与历史处理记录
     * @return 结果
     */
    @Override
    public int updateAgriAlertRecord(AgriAlertRecord agriAlertRecord)
    {
        return agriAlertRecordMapper.updateAgriAlertRecord(agriAlertRecord);
    }

    /**
     * 批量删除产品预警触发与历史处理记录
     * 
     * @param ids 需要删除的产品预警触发与历史处理记录主键
     * @return 结果
     */
    @Override
    public int deleteAgriAlertRecordByIds(Long[] ids)
    {
        return agriAlertRecordMapper.deleteAgriAlertRecordByIds(ids);
    }

    /**
     * 删除产品预警触发与历史处理记录信息
     * 
     * @param id 产品预警触发与历史处理记录主键
     * @return 结果
     */
    @Override
    public int deleteAgriAlertRecordById(Long id)
    {
        return agriAlertRecordMapper.deleteAgriAlertRecordById(id);
    }
}
