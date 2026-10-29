package com.ruoyi.binZhou.dxx.service;

import java.util.List;
import com.ruoyi.binZhou.dxx.domain.GeojsonArchive;

/**
 * GeoJSON存档Service接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IGeojsonArchiveService 
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
     * 批量删除GeoJSON存档
     * 
     * @param archiveIds 需要删除的GeoJSON存档主键集合
     * @return 结果
     */
    public int deleteGeojsonArchiveByArchiveIds(Long[] archiveIds);

    /**
     * 鎵归噺鍒犻櫎GeoJSON瀛樻。，可选同步删除磁盘源文件
     *
     * @param archiveIds 闇€瑕佸垹闄ょ殑GeoJSON瀛樻。涓婚敭闆嗗悎
     * @param deleteSourceFile 是否同步删除磁盘源文件
     * @return 缁撴灉
     */
    public int deleteGeojsonArchiveByArchiveIds(Long[] archiveIds, boolean deleteSourceFile);

    /**
     * 删除GeoJSON存档信息
     * 
     * @param archiveId GeoJSON存档主键
     * @return 结果
     */
    public int deleteGeojsonArchiveByArchiveId(Long archiveId);
}
