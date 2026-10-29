package com.ruoyi.binZhou.dxx.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.dxx.mapper.GeojsonArchiveMapper;
import com.ruoyi.binZhou.dxx.domain.GeojsonArchive;
import com.ruoyi.binZhou.dxx.service.IGeojsonArchiveService;

/**
 * GeoJSON存档Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class GeojsonArchiveServiceImpl implements IGeojsonArchiveService 
{
    private static final Logger log = LoggerFactory.getLogger(GeojsonArchiveServiceImpl.class);

    @Autowired
    private GeojsonArchiveMapper geojsonArchiveMapper;

    /**
     * 查询GeoJSON存档
     * 
     * @param archiveId GeoJSON存档主键
     * @return GeoJSON存档
     */
    @Override
    public GeojsonArchive selectGeojsonArchiveByArchiveId(Long archiveId)
    {
        return geojsonArchiveMapper.selectGeojsonArchiveByArchiveId(archiveId);
    }

    /**
     * 查询GeoJSON存档列表
     * 
     * @param geojsonArchive GeoJSON存档
     * @return GeoJSON存档
     */
    @Override
    public List<GeojsonArchive> selectGeojsonArchiveList(GeojsonArchive geojsonArchive)
    {
        return geojsonArchiveMapper.selectGeojsonArchiveList(geojsonArchive);
    }

    /**
     * 新增GeoJSON存档
     * 
     * @param geojsonArchive GeoJSON存档
     * @return 结果
     */
    @Override
    public int insertGeojsonArchive(GeojsonArchive geojsonArchive)
    {
        geojsonArchive.setCreateTime(DateUtils.getNowDate());
        int rows = geojsonArchiveMapper.insertGeojsonArchive(geojsonArchive);
        clearOtherDefaultIfNeeded(geojsonArchive);
        return rows;
    }

    /**
     * 修改GeoJSON存档
     * 
     * @param geojsonArchive GeoJSON存档
     * @return 结果
     */
    @Override
    public int updateGeojsonArchive(GeojsonArchive geojsonArchive)
    {
        geojsonArchive.setUpdateTime(DateUtils.getNowDate());
        int rows = geojsonArchiveMapper.updateGeojsonArchive(geojsonArchive);
        clearOtherDefaultIfNeeded(geojsonArchive);
        return rows;
    }

    /**
     * 当当前存档设为默认时，自动取消同一区域下其它默认存档
     *
     * @param geojsonArchive GeoJSON存档
     */
    private void clearOtherDefaultIfNeeded(GeojsonArchive geojsonArchive)
    {
        if ("1".equals(geojsonArchive.getIsDefault()) && geojsonArchive.getDatasetId() != null)
        {
            geojsonArchiveMapper.clearOtherDefaultArchive(geojsonArchive);
        }
    }

    /**
     * 批量删除GeoJSON存档
     * 
     * @param archiveIds 需要删除的GeoJSON存档主键
     * @return 结果
     */
    @Override
    public int deleteGeojsonArchiveByArchiveIds(Long[] archiveIds)
    {
        return geojsonArchiveMapper.deleteGeojsonArchiveByArchiveIds(archiveIds);
    }

    /**
     * 鎵归噺鍒犻櫎GeoJSON瀛樻。，可选同步删除磁盘源文件
     *
     * @param archiveIds 闇€瑕佸垹闄ょ殑GeoJSON瀛樻。涓婚敭
     * @param deleteSourceFile 是否同步删除磁盘源文件
     * @return 缁撴灉
     */
    @Override
    public int deleteGeojsonArchiveByArchiveIds(Long[] archiveIds, boolean deleteSourceFile)
    {
        Set<String> deletableFilePaths = deleteSourceFile ? collectDeletableSourceFiles(archiveIds) : new HashSet<>();
        int rows = geojsonArchiveMapper.deleteGeojsonArchiveByArchiveIds(archiveIds);
        if (deleteSourceFile)
        {
            deletableFilePaths.forEach(this::deleteUploadFile);
        }
        return rows;
    }

    /**
     * 删除GeoJSON存档信息
     * 
     * @param archiveId GeoJSON存档主键
     * @return 结果
     */
    @Override
    public int deleteGeojsonArchiveByArchiveId(Long archiveId)
    {
        return geojsonArchiveMapper.deleteGeojsonArchiveByArchiveId(archiveId);
    }

    /**
     * 删除源文件。仅删除 /profile/upload/ 下的文件，并且不会删除仍被其它存档引用的文件。
     */
    private Set<String> collectDeletableSourceFiles(Long[] archiveIds)
    {
        Set<Long> deletingIdSet = new HashSet<>(Arrays.asList(archiveIds));
        Set<String> filePaths = Arrays.stream(archiveIds)
                .map(geojsonArchiveMapper::selectGeojsonArchiveByArchiveId)
                .filter(archive -> archive != null && StringUtils.isNotEmpty(archive.getFilePath()))
                .map(GeojsonArchive::getFilePath)
                .collect(Collectors.toSet());

        filePaths.removeIf(filePath -> {
            boolean referenced = isReferencedByOtherArchive(filePath, deletingIdSet);
            if (referenced)
            {
                log.info("GeoJSON源文件仍被其它存档引用，跳过删除：{}", filePath);
            }
            return referenced;
        });
        return filePaths;
    }

    private boolean isReferencedByOtherArchive(String filePath, Set<Long> deletingIdSet)
    {
        GeojsonArchive query = new GeojsonArchive();
        query.setFilePath(filePath);
        List<GeojsonArchive> references = geojsonArchiveMapper.selectGeojsonArchiveList(query);
        for (GeojsonArchive reference : references)
        {
            if (!deletingIdSet.contains(reference.getArchiveId()))
            {
                return true;
            }
        }
        return false;
    }

    private void deleteUploadFile(String filePath)
    {
        try
        {
            if (StringUtils.isEmpty(filePath) || !filePath.startsWith(Constants.RESOURCE_PREFIX + "/upload/"))
            {
                log.warn("GeoJSON源文件路径不在上传目录下，跳过删除：{}", filePath);
                return;
            }

            String relativePath = StringUtils.substringAfter(filePath, Constants.RESOURCE_PREFIX)
                    .replaceFirst("^/+", "");
            Path uploadRoot = Paths.get(RuoYiConfig.getUploadPath()).toAbsolutePath().normalize();
            Path targetPath = Paths.get(RuoYiConfig.getProfile(), relativePath).toAbsolutePath().normalize();

            if (!targetPath.startsWith(uploadRoot))
            {
                log.warn("GeoJSON源文件路径越界，跳过删除：{}", filePath);
                return;
            }

            Files.deleteIfExists(targetPath);
        }
        catch (Exception e)
        {
            log.warn("删除GeoJSON源文件失败：{}", filePath, e);
        }
    }
}
