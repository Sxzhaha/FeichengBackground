package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.AgriWeatherForecastMapper;
import com.ruoyi.iot.ld.domain.AgriWeatherForecast;
import com.ruoyi.iot.ld.service.IAgriWeatherForecastService;

/**
 * 园区未来天气预报趋势Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
@Service
public class AgriWeatherForecastServiceImpl implements IAgriWeatherForecastService 
{
    @Autowired
    private AgriWeatherForecastMapper agriWeatherForecastMapper;

    /**
     * 查询园区未来天气预报趋势
     * 
     * @param id 园区未来天气预报趋势主键
     * @return 园区未来天气预报趋势
     */
    @Override
    public AgriWeatherForecast selectAgriWeatherForecastById(Long id)
    {
        return agriWeatherForecastMapper.selectAgriWeatherForecastById(id);
    }

    /**
     * 查询园区未来天气预报趋势列表
     * 
     * @param agriWeatherForecast 园区未来天气预报趋势
     * @return 园区未来天气预报趋势
     */
    @Override
    public List<AgriWeatherForecast> selectAgriWeatherForecastList(AgriWeatherForecast agriWeatherForecast)
    {
        return agriWeatherForecastMapper.selectAgriWeatherForecastList(agriWeatherForecast);
    }

    /**
     * 新增园区未来天气预报趋势
     * 
     * @param agriWeatherForecast 园区未来天气预报趋势
     * @return 结果
     */
    @Override
    public int insertAgriWeatherForecast(AgriWeatherForecast agriWeatherForecast)
    {
        return agriWeatherForecastMapper.insertAgriWeatherForecast(agriWeatherForecast);
    }

    /**
     * 修改园区未来天气预报趋势
     * 
     * @param agriWeatherForecast 园区未来天气预报趋势
     * @return 结果
     */
    @Override
    public int updateAgriWeatherForecast(AgriWeatherForecast agriWeatherForecast)
    {
        return agriWeatherForecastMapper.updateAgriWeatherForecast(agriWeatherForecast);
    }

    /**
     * 批量删除园区未来天气预报趋势
     * 
     * @param ids 需要删除的园区未来天气预报趋势主键
     * @return 结果
     */
    @Override
    public int deleteAgriWeatherForecastByIds(Long[] ids)
    {
        return agriWeatherForecastMapper.deleteAgriWeatherForecastByIds(ids);
    }

    /**
     * 删除园区未来天气预报趋势信息
     * 
     * @param id 园区未来天气预报趋势主键
     * @return 结果
     */
    @Override
    public int deleteAgriWeatherForecastById(Long id)
    {
        return agriWeatherForecastMapper.deleteAgriWeatherForecastById(id);
    }
}
