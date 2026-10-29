package com.ruoyi.binZhou.dxx.service;

import java.util.List;
import com.ruoyi.binZhou.dxx.domain.MapDataset;

/**
 * 地图图层Service接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IMapDatasetService 
{
    /**
     * 查询地图图层
     * 
     * @param datasetId 地图图层主键
     * @return 地图图层
     */
    public MapDataset selectMapDatasetByDatasetId(Long datasetId);

    /**
     * 查询地图图层列表
     * 
     * @param mapDataset 地图图层
     * @return 地图图层集合
     */
    public List<MapDataset> selectMapDatasetList(MapDataset mapDataset);

    /**
     * 新增地图图层
     * 
     * @param mapDataset 地图图层
     * @return 结果
     */
    public int insertMapDataset(MapDataset mapDataset);

    /**
     * 修改地图图层
     * 
     * @param mapDataset 地图图层
     * @return 结果
     */
    public int updateMapDataset(MapDataset mapDataset);

    /**
     * 批量删除地图图层
     * 
     * @param datasetIds 需要删除的地图图层主键集合
     * @return 结果
     */
    public int deleteMapDatasetByDatasetIds(Long[] datasetIds);

    /**
     * 删除地图图层信息
     * 
     * @param datasetId 地图图层主键
     * @return 结果
     */
    public int deleteMapDatasetByDatasetId(Long datasetId);
}
