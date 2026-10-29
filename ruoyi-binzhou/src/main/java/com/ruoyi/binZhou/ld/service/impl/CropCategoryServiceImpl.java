package com.ruoyi.binZhou.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.ld.mapper.CropCategoryMapper;
import com.ruoyi.binZhou.ld.domain.CropCategory;
import com.ruoyi.binZhou.ld.service.ICropCategoryService;

/**
 * 作物分类Service业务层处理
 * 
 * @author 宋瑞智
 * @date 2025-07-22
 */
@Service
public class CropCategoryServiceImpl implements ICropCategoryService
{
    @Autowired
    private CropCategoryMapper cropCategoryMapper;

    /**
     * 查询作物分类
     * 
     * @param id 作物分类主键
     * @return 作物分类
     */
    @Override
    public CropCategory selectCropCategoryById(Long id)
    {
        return cropCategoryMapper.selectCropCategoryById(id);
    }

    /**
     * 查询作物分类列表
     * 
     * @param cropCategory 作物分类
     * @return 作物分类
     */
    @Override
    public List<CropCategory> selectCropCategoryList(CropCategory cropCategory)
    {
        return cropCategoryMapper.selectCropCategoryList(cropCategory);
    }

    /**
     * 新增作物分类
     * 
     * @param cropCategory 作物分类
     * @return 结果
     */
    @Override
    public int insertCropCategory(CropCategory cropCategory)
    {
        cropCategory.setCreateTime(DateUtils.getNowDate());
        return cropCategoryMapper.insertCropCategory(cropCategory);
    }

    /**
     * 修改作物分类
     * 
     * @param cropCategory 作物分类
     * @return 结果
     */
    @Override
    public int updateCropCategory(CropCategory cropCategory)
    {
        return cropCategoryMapper.updateCropCategory(cropCategory);
    }

    /**
     * 批量删除作物分类
     * 
     * @param ids 需要删除的作物分类主键
     * @return 结果
     */
    @Override
    public int deleteCropCategoryByIds(Long[] ids)
    {
        return cropCategoryMapper.deleteCropCategoryByIds(ids);
    }

    /**
     * 删除作物分类信息
     * 
     * @param id 作物分类主键
     * @return 结果
     */
    @Override
    public int deleteCropCategoryById(Long id)
    {
        return cropCategoryMapper.deleteCropCategoryById(id);
    }
}
