package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.AgriWeatherForecast;

/**
 * 园区未来天气预报趋势Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
public interface AgriWeatherForecastMapper 
{
    /**
     * 查询园区未来天气预报趋势
     * 
     * @param id 园区未来天气预报趋势主键
     * @return 园区未来天气预报趋势
     */
    public AgriWeatherForecast selectAgriWeatherForecastById(Long id);

    /**
     * 查询园区未来天气预报趋势列表
     * 
     * @param agriWeatherForecast 园区未来天气预报趋势
     * @return 园区未来天气预报趋势集合
     */
    public List<AgriWeatherForecast> selectAgriWeatherForecastList(AgriWeatherForecast agriWeatherForecast);

    /**
     * 新增园区未来天气预报趋势
     * 
     * @param agriWeatherForecast 园区未来天气预报趋势
     * @return 结果
     */
    public int insertAgriWeatherForecast(AgriWeatherForecast agriWeatherForecast);

    /**
     * 修改园区未来天气预报趋势
     * 
     * @param agriWeatherForecast 园区未来天气预报趋势
     * @return 结果
     */
    public int updateAgriWeatherForecast(AgriWeatherForecast agriWeatherForecast);

    /**
     * 删除园区未来天气预报趋势
     * 
     * @param id 园区未来天气预报趋势主键
     * @return 结果
     */
    public int deleteAgriWeatherForecastById(Long id);

    /**
     * 批量删除园区未来天气预报趋势
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAgriWeatherForecastByIds(Long[] ids);
}
