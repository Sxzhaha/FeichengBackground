package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.AgriWeatherLive;

/**
 * 园区实时与当天天气实况记录Service接口
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
public interface IAgriWeatherLiveService 
{
    /**
     * 查询园区实时与当天天气实况记录
     * 
     * @param id 园区实时与当天天气实况记录主键
     * @return 园区实时与当天天气实况记录
     */
    public AgriWeatherLive selectAgriWeatherLiveById(Long id);

    /**
     * 查询园区实时与当天天气实况记录列表
     * 
     * @param agriWeatherLive 园区实时与当天天气实况记录
     * @return 园区实时与当天天气实况记录集合
     */
    public List<AgriWeatherLive> selectAgriWeatherLiveList(AgriWeatherLive agriWeatherLive);

    /**
     * 新增园区实时与当天天气实况记录
     * 
     * @param agriWeatherLive 园区实时与当天天气实况记录
     * @return 结果
     */
    public int insertAgriWeatherLive(AgriWeatherLive agriWeatherLive);

    /**
     * 修改园区实时与当天天气实况记录
     * 
     * @param agriWeatherLive 园区实时与当天天气实况记录
     * @return 结果
     */
    public int updateAgriWeatherLive(AgriWeatherLive agriWeatherLive);

    /**
     * 批量删除园区实时与当天天气实况记录
     * 
     * @param ids 需要删除的园区实时与当天天气实况记录主键集合
     * @return 结果
     */
    public int deleteAgriWeatherLiveByIds(Long[] ids);

    /**
     * 删除园区实时与当天天气实况记录信息
     * 
     * @param id 园区实时与当天天气实况记录主键
     * @return 结果
     */
    public int deleteAgriWeatherLiveById(Long id);
}
