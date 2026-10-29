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
import com.ruoyi.iot.ld.domain.Park;
import com.ruoyi.iot.ld.service.IParkService;

/**
 * 物联网园区管理-园区表
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/binZhou/ld/iot/park")
public class ParkController extends BaseController {
    @Autowired
    private IParkService parkService;

    /**
     * 获取园区列表
     */
    @PreAuthorize("@ss.hasPermi('iot:park:list')")
    @GetMapping("/list")
    public AjaxResult list(Park park) {
        List<Park> list = parkService.selectParkList(park);
        return success(list);
    }

    /**
     * 根据园区编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:park:query')")
    @GetMapping(value = "/{parkId}")
    public AjaxResult getInfo(@PathVariable Long parkId) {
        return success(parkService.selectParkById(parkId));
    }

    /**
     * 根据园区编码获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:park:query')")
    @GetMapping(value = "/code/{parkCode}")
    public AjaxResult getInfoByCode(@PathVariable String parkCode) {
        return success(parkService.selectParkByParkCode(parkCode));
    }

    /**
     * 新增园区
     */
    @PreAuthorize("@ss.hasPermi('iot:park:add')")
    @Log(title = "园区管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Park park) {
        park.setCreateBy(getUsername());
        return toAjax(parkService.insertPark(park));
    }

    /**
     * 修改园区
     */
    @PreAuthorize("@ss.hasPermi('iot:park:edit')")
    @Log(title = "园区管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Park park) {

        park.setUpdateBy(getUsername());
        return toAjax(parkService.updatePark(park));
    }

    /**
     * 删除园区
     */
    @PreAuthorize("@ss.hasPermi('iot:park:remove')")
    @Log(title = "园区管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{parkId}")
    public AjaxResult remove(@PathVariable Long parkId) {
        return toAjax(parkService.deleteParkById(parkId));
    }
}
