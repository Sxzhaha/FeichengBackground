package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.AgriWeatherLiveMapper;
import com.ruoyi.iot.ld.domain.AgriWeatherLive;
import com.ruoyi.iot.ld.service.IAgriWeatherLiveService;

/**
 * 园区实时与当天天气实况记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
@Service
public class AgriWeatherLiveServiceImpl implements IAgriWeatherLiveService 
{
    @Autowired
    private AgriWeatherLiveMapper agriWeatherLiveMapper;

    /**
     * 查询园区实时与当天天气实况记录
     * 
     * @param id 园区实时与当天天气实况记录主键
     * @return 园区实时与当天天气实况记录
     */
    @Override
    public AgriWeatherLive selectAgriWeatherLiveById(Long id)
    {
        return agriWeatherLiveMapper.selectAgriWeatherLiveById(id);
    }

    /**
     * 查询园区实时与当天天气实况记录列表
     * 
     * @param agriWeatherLive 园区实时与当天天气实况记录
     * @return 园区实时与当天天气实况记录
     */
    @Override
    public List<AgriWeatherLive> selectAgriWeatherLiveList(AgriWeatherLive agriWeatherLive)
    {
        return agriWeatherLiveMapper.selectAgriWeatherLiveList(agriWeatherLive);
    }

    /**
     * 新增园区实时与当天天气实况记录
     * 
     * @param agriWeatherLive 园区实时与当天天气实况记录
     * @return 结果
     */
    @Override
    public int insertAgriWeatherLive(AgriWeatherLive agriWeatherLive)
    {
        return agriWeatherLiveMapper.insertAgriWeatherLive(agriWeatherLive);
    }

    /**
     * 修改园区实时与当天天气实况记录
     * 
     * @param agriWeatherLive 园区实时与当天天气实况记录
     * @return 结果
     */
    @Override
    public int updateAgriWeatherLive(AgriWeatherLive agriWeatherLive)
    {
        return agriWeatherLiveMapper.updateAgriWeatherLive(agriWeatherLive);
    }

    /**
     * 批量删除园区实时与当天天气实况记录
     * 
     * @param ids 需要删除的园区实时与当天天气实况记录主键
     * @return 结果
     */
    @Override
    public int deleteAgriWeatherLiveByIds(Long[] ids)
    {
        return agriWeatherLiveMapper.deleteAgriWeatherLiveByIds(ids);
    }

    /**
     * 删除园区实时与当天天气实况记录信息
     * 
     * @param id 园区实时与当天天气实况记录主键
     * @return 结果
     */
    @Override
    public int deleteAgriWeatherLiveById(Long id)
    {
        return agriWeatherLiveMapper.deleteAgriWeatherLiveById(id);
    }
}
