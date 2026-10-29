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
import com.ruoyi.iot.ld.domain.Location;
import com.ruoyi.iot.ld.service.ILocationService;

/**
 * 物联网园区管理-位置表
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/binZhou/ld/iot/location")
public class LocationController extends BaseController {
    @Autowired
    private ILocationService locationService;

    /**
     * 获取位置列表
     */
    @PreAuthorize("@ss.hasPermi('iot:location:list')")
    @GetMapping("/list")
    public AjaxResult list(Location location) {
        List<Location> list = locationService.selectLocationList(location);
        return success(list);
    }

    /**
     * 根据区域ID查询位置列表
     */
    @PreAuthorize("@ss.hasPermi('iot:location:list')")
    @GetMapping("/area/{areaId}")
    public AjaxResult listByAreaId(@PathVariable Long areaId) {
        List<Location> list = locationService.selectLocationListByAreaId(areaId);
        return success(list);
    }

    /**
     * 根据位置编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:location:query')")
    @GetMapping(value = "/{locationId}")
    public AjaxResult getInfo(@PathVariable Long locationId) {
        return success(locationService.selectLocationById(locationId));
    }

    /**
     * 根据位置编码获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:location:query')")
    @GetMapping(value = "/code/{locationCode}")
    public AjaxResult getInfoByCode(@PathVariable String locationCode) {
        return success(locationService.selectLocationByLocationCode(locationCode));
    }

    /**
     * 新增位置
     */
    @PreAuthorize("@ss.hasPermi('iot:location:add')")
    @Log(title = "位置管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Location location) {
        location.setCreateBy(getUsername());
        return toAjax(locationService.insertLocation(location));
    }

    /**
     * 修改位置
     */
    @PreAuthorize("@ss.hasPermi('iot:location:edit')")
    @Log(title = "位置管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Location location) {
        location.setUpdateBy(getUsername());
        return toAjax(locationService.updateLocation(location));
    }

    /**
     * 删除位置
     */
    @PreAuthorize("@ss.hasPermi('iot:location:remove')")
    @Log(title = "位置管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{locationId}")
    public AjaxResult remove(@PathVariable Long locationId) {
        return toAjax(locationService.deleteLocationById(locationId));
    }
}
