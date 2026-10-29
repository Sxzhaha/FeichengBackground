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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.binZhou.dxx.domain.MapDataset;
import com.ruoyi.binZhou.dxx.service.IMapDatasetService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 地图图层Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/dxx/dataset")
public class MapDatasetController extends BaseController
{
    @Autowired
    private IMapDatasetService mapDatasetService;

    /**
     * 查询地图图层列表
     */
    @PreAuthorize("@ss.hasPermi('dxx:dataset:list')")
    @GetMapping("/list")
    public TableDataInfo list(MapDataset mapDataset)
    {
        startPage();
        List<MapDataset> list = mapDatasetService.selectMapDatasetList(mapDataset);
        return getDataTable(list);
    }

    /**
     * 导出地图图层列表
     */
    @PreAuthorize("@ss.hasPermi('dxx:dataset:export')")
    @Log(title = "地图图层", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MapDataset mapDataset)
    {
        List<MapDataset> list = mapDatasetService.selectMapDatasetList(mapDataset);
        ExcelUtil<MapDataset> util = new ExcelUtil<MapDataset>(MapDataset.class);
        util.exportExcel(response, list, "地图图层数据");
    }

    /**
     * 获取地图图层详细信息
     */
    @PreAuthorize("@ss.hasPermi('dxx:dataset:query')")
    @GetMapping(value = "/{datasetId}")
    public AjaxResult getInfo(@PathVariable("datasetId") Long datasetId)
    {
        return success(mapDatasetService.selectMapDatasetByDatasetId(datasetId));
    }

    /**
     * 新增地图图层
     */
    @PreAuthorize("@ss.hasPermi('dxx:dataset:add')")
    @Log(title = "地图图层", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MapDataset mapDataset)
    {
        return toAjax(mapDatasetService.insertMapDataset(mapDataset));
    }

    /**
     * 修改地图图层
     */
    @PreAuthorize("@ss.hasPermi('dxx:dataset:edit')")
    @Log(title = "地图图层", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MapDataset mapDataset)
    {
        return toAjax(mapDatasetService.updateMapDataset(mapDataset));
    }

    /**
     * 删除地图图层
     */
    @PreAuthorize("@ss.hasPermi('dxx:dataset:remove')")
    @Log(title = "地图图层", businessType = BusinessType.DELETE)
	@DeleteMapping("/{datasetIds}")
    public AjaxResult remove(@PathVariable Long[] datasetIds)
    {
        return toAjax(mapDatasetService.deleteMapDatasetByDatasetIds(datasetIds));
    }
}
