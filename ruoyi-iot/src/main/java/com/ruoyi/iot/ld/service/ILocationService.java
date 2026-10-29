package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.Location;

/**
 * 物联网园区管理-位置表 服务层
 * 
 * @author ruoyi
 */
public interface ILocationService
{
    /**
     * 查询位置信息
     * 
     * @param locationId 位置ID
     * @return 位置信息
     */
    public Location selectLocationById(Long locationId);

    /**
     * 根据位置编码查询位置信息
     * 
     * @param locationCode 位置编码
     * @return 位置信息
     */
    public Location selectLocationByLocationCode(String locationCode);

    /**
     * 查询位置列表
     * 
     * @param location 位置信息
     * @return 位置集合
     */
    public List<Location> selectLocationList(Location location);

    /**
     * 根据区域ID查询位置列表
     * 
     * @param areaId 区域ID
     * @return 位置集合
     */
    public List<Location> selectLocationListByAreaId(Long areaId);

    /**
     * 新增位置
     * 
     * @param location 位置信息
     * @return 结果
     */
    public int insertLocation(Location location);

    /**
     * 修改位置
     * 
     * @param location 位置信息
     * @return 结果
     */
    public int updateLocation(Location location);

    /**
     * 删除位置信息
     * 
     * @param locationId 位置ID
     * @return 结果
     */
    public int deleteLocationById(Long locationId);
    
    /**
     * 批量删除位置信息
     * 
     * @param locationIds 需要删除的位置ID
     * @return 结果
     */
    public int deleteLocationByIds(Long[] locationIds);

    /**
     * 校验位置编码是否唯一
     * 
     * @param location 位置信息
     * @return 结果
     */
    public boolean checkLocationCodeUnique(Location location);
}
