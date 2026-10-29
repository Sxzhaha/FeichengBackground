package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.domain.Area;
import com.ruoyi.iot.ld.mapper.AreaMapper;
import com.ruoyi.iot.ld.service.IAreaService;
import com.ruoyi.common.utils.StringUtils;

/**
 * 物联网园区管理-区域表 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class AreaServiceImpl implements IAreaService
{
    @Autowired
    private AreaMapper areaMapper;

    /**
     * 查询区域信息
     * 
     * @param areaId 区域ID
     * @return 区域信息
     */
    @Override
    public Area selectAreaById(Long areaId)
    {
        return areaMapper.selectAreaById(areaId);
    }

    /**
     * 根据区域编码查询区域信息
     * 
     * @param areaCode 区域编码
     * @return 区域信息
     */
    @Override
    public Area selectAreaByAreaCode(String areaCode)
    {
        return areaMapper.selectAreaByAreaCode(areaCode);
    }

    /**
     * 查询区域列表
     * 
     * @param area 区域信息
     * @return 区域集合
     */
    @Override
    public List<Area> selectAreaList(Area area)
    {
        return areaMapper.selectAreaList(area);
    }

    /**
     * 根据园区ID查询区域列表
     * 
     * @param parkId 园区ID
     * @return 区域集合
     */
    @Override
    public List<Area> selectAreaListByParkId(Long parkId)
    {
        return areaMapper.selectAreaListByParkId(parkId);
    }

    /**
     * 新增区域
     * 
     * @param area 区域信息
     * @return 结果
     */
    @Override
    public int insertArea(Area area)
    {
        return areaMapper.insertArea(area);
    }

    /**
     * 修改区域
     * 
     * @param area 区域信息
     * @return 结果
     */
    @Override
    public int updateArea(Area area)
    {
        return areaMapper.updateArea(area);
    }

    /**
     * 删除区域对象
     * 
     * @param areaId 区域ID
     * @return 结果
     */
    @Override
    public int deleteAreaById(Long areaId)
    {
        return areaMapper.deleteAreaById(areaId);
    }

    /**
     * 批量删除区域信息
     * 
     * @param areaIds 需要删除的区域ID
     * @return 结果
     */
    @Override
    public int deleteAreaByIds(Long[] areaIds)
    {
        return areaMapper.deleteAreaByIds(areaIds);
    }

    /**
     * 校验区域编码是否唯一
     * 
     * @param area 区域信息
     * @return 结果
     */
    @Override
    public boolean checkAreaCodeUnique(Area area)
    {
        Long areaId = StringUtils.isNull(area.getAreaId()) ? -1L : area.getAreaId();
        Area info = areaMapper.checkAreaCodeUnique(area.getAreaCode());
        if (StringUtils.isNotNull(info) && info.getAreaId().longValue() != areaId.longValue())
        {
            return false;
        }
        return true;
    }
}
