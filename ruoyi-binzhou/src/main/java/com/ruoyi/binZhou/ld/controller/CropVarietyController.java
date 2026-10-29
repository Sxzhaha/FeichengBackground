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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.binZhou.ld.domain.CropVariety;
import com.ruoyi.binZhou.ld.service.ICropVarietyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 品种信息Controller
 *
 * @author ruoyi
 * @date 2025-07-22
 */
@RestController
@RequestMapping("/binZhou/ld/cropVariety")
public class CropVarietyController extends BaseController
{
    @Autowired
    private ICropVarietyService cropVarietyService;

    /**
     * 查询品种信息列表
     */
    @PreAuthorize("@ss.hasPermi('ld:cropVariety:list')")
    @GetMapping("/list")
    public TableDataInfo list(CropVariety cropVariety)
    {
        startPage();
        List<CropVariety> list = cropVarietyService.selectCropVarietyList(cropVariety);
        return getDataTable(list);
    }

    /**
     * 🔥 新增：根据品种名称查询详细信息 - 专门为详情页提供
     */
    @GetMapping("/query")
    public AjaxResult getByName(@RequestParam("varietyName") String varietyName)
    {
        try {
            logger.info("🔍 查询品种详情，名称: {}", varietyName);

            CropVariety variety = cropVarietyService.selectCropVarietyByName(varietyName);

            if (variety != null) {
                logger.info("✅ 查询成功: {}", variety.getVarietyName());
                return success(variety);
            } else {
                logger.warn("⚠️ 未找到品种: {}", varietyName);
                return error("未找到相关品种信息");
            }
        } catch (Exception e) {
            logger.error("💥 查询品种详情失败: {}", e.getMessage(), e);
            return error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 🔥 新增：搜索品种信息
     */
    @GetMapping("/search")
    public AjaxResult search(@RequestParam("keyword") String keyword)
    {
        try {
            List<CropVariety> list = cropVarietyService.searchCropVariety(keyword);
            return success(list);
        } catch (Exception e) {
            logger.error("搜索品种失败: {}", e.getMessage(), e);
            return error("搜索失败");
        }
    }

    /**
     * 导出品种信息列表
     */
    @PreAuthorize("@ss.hasPermi('ld:cropVariety:export')")
    @Log(title = "品种信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CropVariety cropVariety)
    {
        List<CropVariety> list = cropVarietyService.selectCropVarietyList(cropVariety);
        ExcelUtil<CropVariety> util = new ExcelUtil<CropVariety>(CropVariety.class);
        util.exportExcel(response, list, "品种信息数据");
    }

    /**
     * 获取品种信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:cropVariety:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(cropVarietyService.selectCropVarietyById(id));
    }

    /**
     * 新增品种信息
     */
    @PreAuthorize("@ss.hasPermi('ld:cropVariety:add')")
    @Log(title = "品种信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CropVariety cropVariety)
    {
        return toAjax(cropVarietyService.insertCropVariety(cropVariety));
    }

    /**
     * 修改品种信息
     */
    @PreAuthorize("@ss.hasPermi('ld:cropVariety:edit')")
    @Log(title = "品种信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CropVariety cropVariety)
    {
        return toAjax(cropVarietyService.updateCropVariety(cropVariety));
    }

    /**
     * 删除品种信息
     */
    @PreAuthorize("@ss.hasPermi('ld:cropVariety:remove')")
    @Log(title = "品种信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(cropVarietyService.deleteCropVarietyByIds(ids));
    }
}