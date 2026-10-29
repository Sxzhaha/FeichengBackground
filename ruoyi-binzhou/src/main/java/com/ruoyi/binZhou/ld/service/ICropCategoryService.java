package com.ruoyi.binZhou.ld.service;

import java.util.List;
import com.ruoyi.binZhou.ld.domain.CropCategory;

/**
 * 作物分类Service接口
 * 
 * @author 宋瑞智
 * @date 2025-07-22
 */
public interface ICropCategoryService
{
    /**
     * 查询作物分类
     * 
     * @param id 作物分类主键
     * @return 作物分类
     */
    public CropCategory selectCropCategoryById(Long id);

    /**
     * 查询作物分类列表
     * 
     * @param cropCategory 作物分类
     * @return 作物分类集合
     */
    public List<CropCategory> selectCropCategoryList(CropCategory cropCategory);

    /**
     * 新增作物分类
     * 
     * @param cropCategory 作物分类
     * @return 结果
     */
    public int insertCropCategory(CropCategory cropCategory);

    /**
     * 修改作物分类
     * 
     * @param cropCategory 作物分类
     * @return 结果
     */
    public int updateCropCategory(CropCategory cropCategory);

    /**
     * 批量删除作物分类
     * 
     * @param ids 需要删除的作物分类主键集合
     * @return 结果
     */
    public int deleteCropCategoryByIds(Long[] ids);

    /**
     * 删除作物分类信息
     * 
     * @param id 作物分类主键
     * @return 结果
     */
    public int deleteCropCategoryById(Long id);
}
