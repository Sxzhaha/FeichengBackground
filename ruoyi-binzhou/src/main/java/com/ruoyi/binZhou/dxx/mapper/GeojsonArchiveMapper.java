package com.ruoyi.binZhou.dxx.mapper;

import java.util.List;
import com.ruoyi.binZhou.dxx.domain.GeojsonArchive;

/**
 * GeoJSON存档Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface GeojsonArchiveMapper 
{
    /**
     * 查询GeoJSON存档
     * 
     * @param archiveId GeoJSON存档主键
     * @return GeoJSON存档
     */
    public GeojsonArchive selectGeojsonArchiveByArchiveId(Long archiveId);

    /**
     * 查询GeoJSON存档列表
     * 
     * @param geojsonArchive GeoJSON存档
     * @return GeoJSON存档集合
     */
    public List<GeojsonArchive> selectGeojsonArchiveList(GeojsonArchive geojsonArchive);

    /**
     * 新增GeoJSON存档
     * 
     * @param geojsonArchive GeoJSON存档
     * @return 结果
     */
    public int insertGeojsonArchive(GeojsonArchive geojsonArchive);

    /**
     * 修改GeoJSON存档
     * 
     * @param geojsonArchive GeoJSON存档
     * @return 结果
     */
    public int updateGeojsonArchive(GeojsonArchive geojsonArchive);

    /**
     * 取消同一区域下其它默认存档
     *
     * @param geojsonArchive GeoJSON存档
     * @return 结果
     */
    public int clearOtherDefaultArchive(GeojsonArchive geojsonArchive);

    /**
     * 删除GeoJSON存档
     * 
     * @param archiveId GeoJSON存档主键
     * @return 结果
     */
    public int deleteGeojsonArchiveByArchiveId(Long archiveId);

    /**
     * 批量删除GeoJSON存档
     * 
     * @param archiveIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGeojsonArchiveByArchiveIds(Long[] archiveIds);
}
