package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.AgriWeatherAlertAdviceMapper;
import com.ruoyi.iot.ld.domain.AgriWeatherAlertAdvice;
import com.ruoyi.iot.ld.service.IAgriWeatherAlertAdviceService;

/**
 * 极端天气突发预警与防灾建议Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
@Service
public class AgriWeatherAlertAdviceServiceImpl implements IAgriWeatherAlertAdviceService 
{
    @Autowired
    private AgriWeatherAlertAdviceMapper agriWeatherAlertAdviceMapper;

    /**
     * 查询极端天气突发预警与防灾建议
     * 
     * @param id 极端天气突发预警与防灾建议主键
     * @return 极端天气突发预警与防灾建议
     */
    @Override
    public AgriWeatherAlertAdvice selectAgriWeatherAlertAdviceById(Long id)
    {
        return agriWeatherAlertAdviceMapper.selectAgriWeatherAlertAdviceById(id);
    }

    /**
     * 查询极端天气突发预警与防灾建议列表
     * 
     * @param agriWeatherAlertAdvice 极端天气突发预警与防灾建议
     * @return 极端天气突发预警与防灾建议
     */
    @Override
    public List<AgriWeatherAlertAdvice> selectAgriWeatherAlertAdviceList(AgriWeatherAlertAdvice agriWeatherAlertAdvice)
    {
        return agriWeatherAlertAdviceMapper.selectAgriWeatherAlertAdviceList(agriWeatherAlertAdvice);
    }

    /**
     * 新增极端天气突发预警与防灾建议
     * 
     * @param agriWeatherAlertAdvice 极端天气突发预警与防灾建议
     * @return 结果
     */
    @Override
    public int insertAgriWeatherAlertAdvice(AgriWeatherAlertAdvice agriWeatherAlertAdvice)
    {
        agriWeatherAlertAdvice.setCreateTime(DateUtils.getNowDate());
        return agriWeatherAlertAdviceMapper.insertAgriWeatherAlertAdvice(agriWeatherAlertAdvice);
    }

    /**
     * 修改极端天气突发预警与防灾建议
     * 
     * @param agriWeatherAlertAdvice 极端天气突发预警与防灾建议
     * @return 结果
     */
    @Override
    public int updateAgriWeatherAlertAdvice(AgriWeatherAlertAdvice agriWeatherAlertAdvice)
    {
        return agriWeatherAlertAdviceMapper.updateAgriWeatherAlertAdvice(agriWeatherAlertAdvice);
    }

    /**
     * 批量删除极端天气突发预警与防灾建议
     * 
     * @param ids 需要删除的极端天气突发预警与防灾建议主键
     * @return 结果
     */
    @Override
    public int deleteAgriWeatherAlertAdviceByIds(Long[] ids)
    {
        return agriWeatherAlertAdviceMapper.deleteAgriWeatherAlertAdviceByIds(ids);
    }

    /**
     * 删除极端天气突发预警与防灾建议信息
     * 
     * @param id 极端天气突发预警与防灾建议主键
     * @return 结果
     */
    @Override
    public int deleteAgriWeatherAlertAdviceById(Long id)
    {
        return agriWeatherAlertAdviceMapper.deleteAgriWeatherAlertAdviceById(id);
    }
}
