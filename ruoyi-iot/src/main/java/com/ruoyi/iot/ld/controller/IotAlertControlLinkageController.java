package com.ruoyi.iot.ld.controller;

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
import com.ruoyi.iot.ld.domain.IotAlertControlLinkage;
import com.ruoyi.iot.ld.service.IIotAlertControlLinkageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * Alert control linkage Controller.
 */
@RestController
@RequestMapping("/ld/controlLinkage")
public class IotAlertControlLinkageController extends BaseController
{
    @Autowired
    private IIotAlertControlLinkageService iotAlertControlLinkageService;

    @PreAuthorize("@ss.hasPermi('ld:controlLinkage:list')")
    @GetMapping("/list")
    public TableDataInfo list(IotAlertControlLinkage iotAlertControlLinkage)
    {
        startPage();
        List<IotAlertControlLinkage> list = iotAlertControlLinkageService.selectIotAlertControlLinkageList(iotAlertControlLinkage);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ld:controlLinkage:export')")
    @Log(title = "预警联动控制", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IotAlertControlLinkage iotAlertControlLinkage)
    {
        List<IotAlertControlLinkage> list = iotAlertControlLinkageService.selectIotAlertControlLinkageList(iotAlertControlLinkage);
        ExcelUtil<IotAlertControlLinkage> util = new ExcelUtil<IotAlertControlLinkage>(IotAlertControlLinkage.class);
        util.exportExcel(response, list, "预警联动控制数据");
    }

    @PreAuthorize("@ss.hasPermi('ld:controlLinkage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(iotAlertControlLinkageService.selectIotAlertControlLinkageById(id));
    }

    @PreAuthorize("@ss.hasPermi('ld:controlLinkage:add')")
    @Log(title = "预警联动控制", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IotAlertControlLinkage iotAlertControlLinkage)
    {
        return toAjax(iotAlertControlLinkageService.insertIotAlertControlLinkage(iotAlertControlLinkage));
    }

    @PreAuthorize("@ss.hasPermi('ld:controlLinkage:edit')")
    @Log(title = "预警联动控制", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IotAlertControlLinkage iotAlertControlLinkage)
    {
        return toAjax(iotAlertControlLinkageService.updateIotAlertControlLinkage(iotAlertControlLinkage));
    }

    @PreAuthorize("@ss.hasPermi('ld:controlLinkage:remove')")
    @Log(title = "预警联动控制", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(iotAlertControlLinkageService.deleteIotAlertControlLinkageByIds(ids));
    }
}
