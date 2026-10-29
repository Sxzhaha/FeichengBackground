package com.ruoyi.binZhou.dxx.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.binZhou.dxx.domain.GeojsonArchive;
import com.ruoyi.binZhou.dxx.service.IGeojsonArchiveService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * GeoJSON存档Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/dxx/archive")
public class GeojsonArchiveController extends BaseController
{
    @Autowired
    private IGeojsonArchiveService geojsonArchiveService;

    /**
     * 查询GeoJSON存档列表
     */
    @PreAuthorize("@ss.hasPermi('dxx:archive:list')")
    @GetMapping("/list")
    public TableDataInfo list(GeojsonArchive geojsonArchive)
    {
        startPage();
        List<GeojsonArchive> list = geojsonArchiveService.selectGeojsonArchiveList(geojsonArchive);
        return getDataTable(list);
    }

    /**
     * 导出GeoJSON存档列表
     */
    @PreAuthorize("@ss.hasPermi('dxx:archive:export')")
    @Log(title = "GeoJSON存档", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GeojsonArchive geojsonArchive)
    {
        List<GeojsonArchive> list = geojsonArchiveService.selectGeojsonArchiveList(geojsonArchive);
        ExcelUtil<GeojsonArchive> util = new ExcelUtil<GeojsonArchive>(GeojsonArchive.class);
        util.exportExcel(response, list, "GeoJSON存档数据");
    }

    /**
     * 获取GeoJSON存档详细信息
     */
    @PreAuthorize("@ss.hasPermi('dxx:archive:query')")
    @GetMapping(value = "/{archiveId}")
    public AjaxResult getInfo(@PathVariable("archiveId") Long archiveId)
    {
        return success(geojsonArchiveService.selectGeojsonArchiveByArchiveId(archiveId));
    }

    /**
     * 新增GeoJSON存档
     */
    @PreAuthorize("@ss.hasPermi('dxx:archive:add')")
    @Log(title = "GeoJSON存档", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GeojsonArchive geojsonArchive)
    {
        return toAjax(geojsonArchiveService.insertGeojsonArchive(geojsonArchive));
    }

    /**
     * 修改GeoJSON存档
     */
    @PreAuthorize("@ss.hasPermi('dxx:archive:edit')")
    @Log(title = "GeoJSON存档", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GeojsonArchive geojsonArchive)
    {
        return toAjax(geojsonArchiveService.updateGeojsonArchive(geojsonArchive));
    }

    /**
     * 删除GeoJSON存档
     */
    @PreAuthorize("@ss.hasPermi('dxx:archive:remove')")
    @Log(title = "GeoJSON存档", businessType = BusinessType.DELETE)
	@DeleteMapping("/{archiveIds}")
    public AjaxResult remove(@PathVariable Long[] archiveIds,
                             @RequestParam(value = "deleteSourceFile", defaultValue = "false") Boolean deleteSourceFile)
    {
        return toAjax(geojsonArchiveService.deleteGeojsonArchiveByArchiveIds(archiveIds, Boolean.TRUE.equals(deleteSourceFile)));
    }
}
