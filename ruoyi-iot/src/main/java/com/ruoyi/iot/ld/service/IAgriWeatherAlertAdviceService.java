package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.AgriWeatherAlertAdvice;

/**
 * 极端天气突发预警与防灾建议Service接口
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
public interface IAgriWeatherAlertAdviceService 
{
    /**
     * 查询极端天气突发预警与防灾建议
     * 
     * @param id 极端天气突发预警与防灾建议主键
     * @return 极端天气突发预警与防灾建议
     */
    public AgriWeatherAlertAdvice selectAgriWeatherAlertAdviceById(Long id);

    /**
     * 查询极端天气突发预警与防灾建议列表
     * 
     * @param agriWeatherAlertAdvice 极端天气突发预警与防灾建议
     * @return 极端天气突发预警与防灾建议集合
     */
    public List<AgriWeatherAlertAdvice> selectAgriWeatherAlertAdviceList(AgriWeatherAlertAdvice agriWeatherAlertAdvice);

    /**
     * 新增极端天气突发预警与防灾建议
     * 
     * @param agriWeatherAlertAdvice 极端天气突发预警与防灾建议
     * @return 结果
     */
    public int insertAgriWeatherAlertAdvice(AgriWeatherAlertAdvice agriWeatherAlertAdvice);

    /**
     * 修改极端天气突发预警与防灾建议
     * 
     * @param agriWeatherAlertAdvice 极端天气突发预警与防灾建议
     * @return 结果
     */
    public int updateAgriWeatherAlertAdvice(AgriWeatherAlertAdvice agriWeatherAlertAdvice);

    /**
     * 批量删除极端天气突发预警与防灾建议
     * 
     * @param ids 需要删除的极端天气突发预警与防灾建议主键集合
     * @return 结果
     */
    public int deleteAgriWeatherAlertAdviceByIds(Long[] ids);

    /**
     * 删除极端天气突发预警与防灾建议信息
     * 
     * @param id 极端天气突发预警与防灾建议主键
     * @return 结果
     */
    public int deleteAgriWeatherAlertAdviceById(Long id);
}
