package com.ruoyi.binZhou.ld.service;

import java.util.List;
import com.ruoyi.binZhou.ld.domain.CropVariety;

/**
 * 品种信息Service接口
 *
 * @author ruoyi
 * @date 2025-07-22
 */
public interface ICropVarietyService
{
    /**
     * 查询品种信息
     *
     * @param id 品种信息主键
     * @return 品种信息
     */
    public CropVariety selectCropVarietyById(Long id);

    /**
     * 🔥 新增：根据品种名称查询品种信息
     *
     * @param varietyName 品种名称
     * @return 品种信息
     */
    public CropVariety selectCropVarietyByName(String varietyName);

    /**
     * 查询品种信息列表
     *
     * @param cropVariety 品种信息
     * @return 品种信息集合
     */
    public List<CropVariety> selectCropVarietyList(CropVariety cropVariety);

    /**
     * 🔥 新增：搜索品种信息
     *
     * @param keyword 搜索关键词
     * @return 品种信息集合
     */
    public List<CropVariety> searchCropVariety(String keyword);

    /**
     * 新增品种信息
     *
     * @param cropVariety 品种信息
     * @return 结果
     */
    public int insertCropVariety(CropVariety cropVariety);

    /**
     * 修改品种信息
     *
     * @param cropVariety 品种信息
     * @return 结果
     */
    public int updateCropVariety(CropVariety cropVariety);

    /**
     * 批量删除品种信息
     *
     * @param ids 需要删除的品种信息主键集合
     * @return 结果
     */
    public int deleteCropVarietyByIds(Long[] ids);

    /**
     * 删除品种信息信息
     *
     * @param id 品种信息主键
     * @return 结果
     */
    public int deleteCropVarietyById(Long id);
}