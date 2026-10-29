package com.ruoyi.binZhou.ld.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.binZhou.ld.domain.CropCategory;
import com.ruoyi.binZhou.ld.service.ICropCategoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 作物分类Controller
 * 
 * @author 宋瑞智
 * @date 2025-07-22
 */
@RestController
@RequestMapping("/binZhou/ld/cropCategory")
public class CropCategoryController extends BaseController
{
    @Autowired
    private ICropCategoryService cropCategoryService;

    /**
     * 查询作物分类列表
     */
    @PreAuthorize("@ss.hasPermi('ld:cropCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(CropCategory cropCategory)
    {
        startPage();
        List<CropCategory> list = cropCategoryService.selectCropCategoryList(cropCategory);
        return getDataTable(list);
    }

    /**
     * 导出作物分类列表
     */
    @PreAuthorize("@ss.hasPermi('ld:cropCategory:export')")
    @Log(title = "作物分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CropCategory cropCategory)
    {
        List<CropCategory> list = cropCategoryService.selectCropCategoryList(cropCategory);
        ExcelUtil<CropCategory> util = new ExcelUtil<CropCategory>(CropCategory.class);
        util.exportExcel(response, list, "作物分类数据");
    }

    /**
     * 获取作物分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:cropCategory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(cropCategoryService.selectCropCategoryById(id));
    }

    /**
     * 新增作物分类
     */
    @PreAuthorize("@ss.hasPermi('ld:cropCategory:add')")
    @Log(title = "作物分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CropCategory cropCategory)
    {
        return toAjax(cropCategoryService.insertCropCategory(cropCategory));
    }

    /**
     * 修改作物分类
     */
    @PreAuthorize("@ss.hasPermi('ld:cropCategory:edit')")
    @Log(title = "作物分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CropCategory cropCategory)
    {
        return toAjax(cropCategoryService.updateCropCategory(cropCategory));
    }

    /**
     * 删除作物分类
     */
    @PreAuthorize("@ss.hasPermi('ld:cropCategory:remove')")
    @Log(title = "作物分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(cropCategoryService.deleteCropCategoryByIds(ids));
    }
}
