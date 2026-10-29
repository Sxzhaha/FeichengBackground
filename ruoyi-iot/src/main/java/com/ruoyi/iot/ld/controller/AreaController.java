package com.ruoyi.iot.ld.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.iot.ld.domain.Area;
import com.ruoyi.iot.ld.service.IAreaService;

/**
 * 物联网园区管理-区域表
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/binZhou/ld/iot/area")
public class AreaController extends BaseController
{
    @Autowired
    private IAreaService areaService;

    /**
     * 获取区域列表
     */
    @PreAuthorize("@ss.hasPermi('iot:area:list')")
    @GetMapping("/list")
    public AjaxResult list(Area area)
    {
        List<Area> list = areaService.selectAreaList(area);
        return success(list);
    }

    /**
     * 根据园区ID查询区域列表
     */
    @PreAuthorize("@ss.hasPermi('iot:area:list')")
    @GetMapping("/park/{parkId}")
    public AjaxResult listByParkId(@PathVariable Long parkId)
    {
        List<Area> list = areaService.selectAreaListByParkId(parkId);
        return success(list);
    }

    /**
     * 根据区域编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:area:query')")
    @GetMapping(value = "/{areaId}")
    public AjaxResult getInfo(@PathVariable Long areaId)
    {
        return success(areaService.selectAreaById(areaId));
    }

    /**
     * 根据区域编码获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:area:query')")
    @GetMapping(value = "/code/{areaCode}")
    public AjaxResult getInfoByCode(@PathVariable String areaCode)
    {
        return success(areaService.selectAreaByAreaCode(areaCode));
    }

    /**
     * 新增区域
     */
    @PreAuthorize("@ss.hasPermi('iot:area:add')")
    @Log(title = "区域管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Area area)
    {
        area.setCreateBy(getUsername());
        return toAjax(areaService.insertArea(area));
    }

    /**
     * 修改区域
     */
    @PreAuthorize("@ss.hasPermi('iot:area:edit')")
    @Log(title = "区域管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Area area)
    {
        area.setUpdateBy(getUsername());
        return toAjax(areaService.updateArea(area));
    }

    /**
     * 删除区域
     */
    @PreAuthorize("@ss.hasPermi('iot:area:remove')")
    @Log(title = "区域管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{areaId}")
    public AjaxResult remove(@PathVariable Long areaId)
    {
        return toAjax(areaService.deleteAreaById(areaId));
    }
}
