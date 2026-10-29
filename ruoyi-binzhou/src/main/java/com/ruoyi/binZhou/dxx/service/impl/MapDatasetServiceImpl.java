package com.ruoyi.binZhou.dxx.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.dxx.mapper.MapDatasetMapper;
import com.ruoyi.binZhou.dxx.domain.MapDataset;
import com.ruoyi.binZhou.dxx.service.IMapDatasetService;

/**
 * 地图图层Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class MapDatasetServiceImpl implements IMapDatasetService 
{
    @Autowired
    private MapDatasetMapper mapDatasetMapper;

    /**
     * 查询地图图层
     * 
     * @param datasetId 地图图层主键
     * @return 地图图层
     */
    @Override
    public MapDataset selectMapDatasetByDatasetId(Long datasetId)
    {
        return mapDatasetMapper.selectMapDatasetByDatasetId(datasetId);
    }

    /**
     * 查询地图图层列表
     * 
     * @param mapDataset 地图图层
     * @return 地图图层
     */
    @Override
    public List<MapDataset> selectMapDatasetList(MapDataset mapDataset)
    {
        return mapDatasetMapper.selectMapDatasetList(mapDataset);
    }

    /**
     * 新增地图图层
     * 
     * @param mapDataset 地图图层
     * @return 结果
     */
    @Override
    public int insertMapDataset(MapDataset mapDataset)
    {
        mapDataset.setCreateTime(DateUtils.getNowDate());
        return mapDatasetMapper.insertMapDataset(mapDataset);
    }

    /**
     * 修改地图图层
     * 
     * @param mapDataset 地图图层
     * @return 结果
     */
    @Override
    public int updateMapDataset(MapDataset mapDataset)
    {
        mapDataset.setUpdateTime(DateUtils.getNowDate());
        return mapDatasetMapper.updateMapDataset(mapDataset);
    }

    /**
     * 批量删除地图图层
     * 
     * @param datasetIds 需要删除的地图图层主键
     * @return 结果
     */
    @Override
    public int deleteMapDatasetByDatasetIds(Long[] datasetIds)
    {
        return mapDatasetMapper.deleteMapDatasetByDatasetIds(datasetIds);
    }

    /**
     * 删除地图图层信息
     * 
     * @param datasetId 地图图层主键
     * @return 结果
     */
    @Override
    public int deleteMapDatasetByDatasetId(Long datasetId)
    {
        return mapDatasetMapper.deleteMapDatasetByDatasetId(datasetId);
    }
}
