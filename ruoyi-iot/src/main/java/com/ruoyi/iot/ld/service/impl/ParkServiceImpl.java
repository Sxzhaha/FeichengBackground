package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.domain.Park;
import com.ruoyi.iot.ld.mapper.ParkMapper;
import com.ruoyi.iot.ld.service.IParkService;
import com.ruoyi.common.utils.StringUtils;

/**
 * 物联网园区管理-园区表 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class ParkServiceImpl implements IParkService
{
    @Autowired
    private ParkMapper parkMapper;

    /**
     * 查询园区信息
     * 
     * @param parkId 园区ID
     * @return 园区信息
     */
    @Override
    public Park selectParkById(Long parkId)
    {
        return parkMapper.selectParkById(parkId);
    }

    /**
     * 根据园区编码查询园区信息
     * 
     * @param parkCode 园区编码
     * @return 园区信息
     */
    @Override
    public Park selectParkByParkCode(String parkCode)
    {
        return parkMapper.selectParkByParkCode(parkCode);
    }

    /**
     * 查询园区列表
     * 
     * @param park 园区信息
     * @return 园区集合
     */
    @Override
    public List<Park> selectParkList(Park park)
    {
        return parkMapper.selectParkList(park);
    }

    /**
     * 新增园区
     * 
     * @param park 园区信息
     * @return 结果
     */
    @Override
    public int insertPark(Park park)
    {
        return parkMapper.insertPark(park);
    }

    /**
     * 修改园区
     * 
     * @param park 园区信息
     * @return 结果
     */
    @Override
    public int updatePark(Park park)
    {
        return parkMapper.updatePark(park);
    }

    /**
     * 删除园区对象
     * 
     * @param parkId 园区ID
     * @return 结果
     */
    @Override
    public int deleteParkById(Long parkId)
    {
        return parkMapper.deleteParkById(parkId);
    }

    /**
     * 批量删除园区信息
     * 
     * @param parkIds 需要删除的园区ID
     * @return 结果
     */
    @Override
    public int deleteParkByIds(Long[] parkIds)
    {
        return parkMapper.deleteParkByIds(parkIds);
    }

    /**
     * 校验园区名称是否唯一
     * 
     * @param park 园区信息
     * @return 结果
     */
    @Override
    public boolean checkParkNameUnique(Park park)
    {
        Long parkId = StringUtils.isNull(park.getParkId()) ? -1L : park.getParkId();
        Park info = parkMapper.checkParkNameUnique(park.getParkName());
        if (StringUtils.isNotNull(info) && info.getParkId().longValue() != parkId.longValue())
        {
            return false;
        }
        return true;
    }

    /**
     * 校验园区编码是否唯一
     * 
     * @param park 园区信息
     * @return 结果
     */
    @Override
    public boolean checkParkCodeUnique(Park park)
    {
        Long parkId = StringUtils.isNull(park.getParkId()) ? -1L : park.getParkId();
        Park info = parkMapper.checkParkCodeUnique(park.getParkCode());
        if (StringUtils.isNotNull(info) && info.getParkId().longValue() != parkId.longValue())
        {
            return false;
        }
        return true;
    }
}
