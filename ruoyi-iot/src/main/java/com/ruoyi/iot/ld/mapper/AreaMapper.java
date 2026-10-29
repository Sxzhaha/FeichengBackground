package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.Area;

/**
 * 物联网园区管理-区域表 数据层
 * 
 * @author ruoyi
 */
public interface AreaMapper
{
    /**
     * 查询区域信息
     * 
     * @param areaId 区域ID
     * @return 区域信息
     */
    public Area selectAreaById(Long areaId);

    /**
     * 根据区域编码查询区域信息
     * 
     * @param areaCode 区域编码
     * @return 区域信息
     */
    public Area selectAreaByAreaCode(String areaCode);

    /**
     * 查询区域列表
     * 
     * @param area 区域信息
     * @return 区域集合
     */
    public List<Area> selectAreaList(Area area);

    /**
     * 根据园区ID查询区域列表
     * 
     * @param parkId 园区ID
     * @return 区域集合
     */
    public List<Area> selectAreaListByParkId(Long parkId);

    /**
     * 新增区域
     * 
     * @param area 区域信息
     * @return 结果
     */
    public int insertArea(Area area);

    /**
     * 修改区域
     * 
     * @param area 区域信息
     * @return 结果
     */
    public int updateArea(Area area);

    /**
     * 删除区域
     * 
     * @param areaId 区域ID
     * @return 结果
     */
    public int deleteAreaById(Long areaId);

    /**
     * 批量删除区域信息
     * 
     * @param areaIds 需要删除的区域ID
     * @return 结果
     */
    public int deleteAreaByIds(Long[] areaIds);

    /**
     * 校验区域编码是否唯一
     * 
     * @param areaCode 区域编码
     * @return 结果
     */
    public Area checkAreaCodeUnique(String areaCode);

    /**
     * 检查园区下是否存在区域
     * 
     * @param parkId 园区ID
     * @return 结果
     */
    public int checkAreaExistByParkId(Long parkId);
}
