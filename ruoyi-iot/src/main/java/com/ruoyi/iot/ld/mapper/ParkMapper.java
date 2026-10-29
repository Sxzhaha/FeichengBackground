package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.Park;

/**
 * 物联网园区管理-园区表 数据层
 * 
 * @author ruoyi
 */
public interface ParkMapper
{
    /**
     * 查询园区信息
     * 
     * @param parkId 园区ID
     * @return 园区信息
     */
    public Park selectParkById(Long parkId);

    /**
     * 根据园区编码查询园区信息
     * 
     * @param parkCode 园区编码
     * @return 园区信息
     */
    public Park selectParkByParkCode(String parkCode);

    /**
     * 查询园区列表
     * 
     * @param park 园区信息
     * @return 园区集合
     */
    public List<Park> selectParkList(Park park);

    /**
     * 新增园区
     * 
     * @param park 园区信息
     * @return 结果
     */
    public int insertPark(Park park);

    /**
     * 修改园区
     * 
     * @param park 园区信息
     * @return 结果
     */
    public int updatePark(Park park);

    /**
     * 删除园区
     * 
     * @param parkId 园区ID
     * @return 结果
     */
    public int deleteParkById(Long parkId);

    /**
     * 批量删除园区信息
     * 
     * @param parkIds 需要删除的园区ID
     * @return 结果
     */
    public int deleteParkByIds(Long[] parkIds);

    /**
     * 校验园区名称是否唯一
     * 
     * @param parkName 园区名称
     * @return 结果
     */
    public Park checkParkNameUnique(String parkName);

    /**
     * 校验园区编码是否唯一
     * 
     * @param parkCode 园区编码
     * @return 结果
     */
    public Park checkParkCodeUnique(String parkCode);
}
