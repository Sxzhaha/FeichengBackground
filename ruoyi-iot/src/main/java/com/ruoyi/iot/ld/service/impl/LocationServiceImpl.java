package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.domain.Location;
import com.ruoyi.iot.ld.mapper.LocationMapper;
import com.ruoyi.iot.ld.service.ILocationService;
import com.ruoyi.common.utils.StringUtils;

/**
 * 物联网园区管理-位置表 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class LocationServiceImpl implements ILocationService
{
    @Autowired
    private LocationMapper locationMapper;

    /**
     * 查询位置信息
     * 
     * @param locationId 位置ID
     * @return 位置信息
     */
    @Override
    public Location selectLocationById(Long locationId)
    {
        return locationMapper.selectLocationById(locationId);
    }

    /**
     * 根据位置编码查询位置信息
     * 
     * @param locationCode 位置编码
     * @return 位置信息
     */
    @Override
    public Location selectLocationByLocationCode(String locationCode)
    {
        return locationMapper.selectLocationByLocationCode(locationCode);
    }

    /**
     * 查询位置列表
     * 
     * @param location 位置信息
     * @return 位置集合
     */
    @Override
    public List<Location> selectLocationList(Location location)
    {
        return locationMapper.selectLocationList(location);
    }

    /**
     * 根据区域ID查询位置列表
     * 
     * @param areaId 区域ID
     * @return 位置集合
     */
    @Override
    public List<Location> selectLocationListByAreaId(Long areaId)
    {
        return locationMapper.selectLocationListByAreaId(areaId);
    }

    /**
     * 新增位置
     * 
     * @param location 位置信息
     * @return 结果
     */
    @Override
    public int insertLocation(Location location)
    {
        return locationMapper.insertLocation(location);
    }

    /**
     * 修改位置
     * 
     * @param location 位置信息
     * @return 结果
     */
    @Override
    public int updateLocation(Location location)
    {
        return locationMapper.updateLocation(location);
    }

    /**
     * 删除位置对象
     * 
     * @param locationId 位置ID
     * @return 结果
     */
    @Override
    public int deleteLocationById(Long locationId)
    {
        return locationMapper.deleteLocationById(locationId);
    }

    /**
     * 批量删除位置信息
     * 
     * @param locationIds 需要删除的位置ID
     * @return 结果
     */
    @Override
    public int deleteLocationByIds(Long[] locationIds)
    {
        return locationMapper.deleteLocationByIds(locationIds);
    }

    /**
     * 校验位置编码是否唯一
     * 
     * @param location 位置信息
     * @return 结果
     */
    @Override
    public boolean checkLocationCodeUnique(Location location)
    {
        Long locationId = StringUtils.isNull(location.getLocationId()) ? -1L : location.getLocationId();
        Location info = locationMapper.checkLocationCodeUnique(location.getLocationCode());
        if (StringUtils.isNotNull(info) && info.getLocationId().longValue() != locationId.longValue())
        {
            return false;
        }
        return true;
    }
}
