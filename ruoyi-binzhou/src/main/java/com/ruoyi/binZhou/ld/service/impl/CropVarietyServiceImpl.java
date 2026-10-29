package com.ruoyi.binZhou.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.ld.mapper.CropVarietyMapper;
import com.ruoyi.binZhou.ld.domain.CropVariety;
import com.ruoyi.binZhou.ld.service.ICropVarietyService;

/**
 * 品种信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-07-22
 */
@Service
public class CropVarietyServiceImpl implements ICropVarietyService
{
    @Autowired
    private CropVarietyMapper cropVarietyMapper;

    /**
     * 查询品种信息
     *
     * @param id 品种信息主键
     * @return 品种信息
     */
    @Override
    public CropVariety selectCropVarietyById(Long id)
    {
        return cropVarietyMapper.selectCropVarietyById(id);
    }

    /**
     * 🔥 新增：根据品种名称查询品种信息
     *
     * @param varietyName 品种名称
     * @return 品种信息
     */
    @Override
    public CropVariety selectCropVarietyByName(String varietyName)
    {
        return cropVarietyMapper.selectCropVarietyByName(varietyName);
    }

    /**
     * 查询品种信息列表
     *
     * @param cropVariety 品种信息
     * @return 品种信息
     */
    @Override
    public List<CropVariety> selectCropVarietyList(CropVariety cropVariety)
    {
        return cropVarietyMapper.selectCropVarietyList(cropVariety);
    }

    /**
     * 🔥 新增：搜索品种信息
     *
     * @param keyword 搜索关键词
     * @return 品种信息集合
     */
    @Override
    public List<CropVariety> searchCropVariety(String keyword)
    {
        return cropVarietyMapper.searchCropVariety(keyword);
    }

    /**
     * 新增品种信息
     *
     * @param cropVariety 品种信息
     * @return 结果
     */
    @Override
    public int insertCropVariety(CropVariety cropVariety)
    {
        cropVariety.setCreateTime(DateUtils.getNowDate());
        return cropVarietyMapper.insertCropVariety(cropVariety);
    }

    /**
     * 修改品种信息
     *
     * @param cropVariety 品种信息
     * @return 结果
     */
    @Override
    public int updateCropVariety(CropVariety cropVariety)
    {
        cropVariety.setUpdateTime(DateUtils.getNowDate());
        return cropVarietyMapper.updateCropVariety(cropVariety);
    }

    /**
     * 批量删除品种信息
     *
     * @param ids 需要删除的品种信息主键
     * @return 结果
     */
    @Override
    public int deleteCropVarietyByIds(Long[] ids)
    {
        return cropVarietyMapper.deleteCropVarietyByIds(ids);
    }

    /**
     * 删除品种信息信息
     *
     * @param id 品种信息主键
     * @return 结果
     */
    @Override
    public int deleteCropVarietyById(Long id)
    {
        return cropVarietyMapper.deleteCropVarietyById(id);
    }
}