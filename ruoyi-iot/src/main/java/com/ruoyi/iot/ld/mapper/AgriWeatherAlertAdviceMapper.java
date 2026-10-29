package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.AgriWeatherAlertAdvice;

/**
 * 极端天气突发预警与防灾建议Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
public interface AgriWeatherAlertAdviceMapper 
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
     * 删除极端天气突发预警与防灾建议
     * 
     * @param id 极端天气突发预警与防灾建议主键
     * @return 结果
     */
    public int deleteAgriWeatherAlertAdviceById(Long id);

    /**
     * 批量删除极端天气突发预警与防灾建议
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAgriWeatherAlertAdviceByIds(Long[] ids);
}
