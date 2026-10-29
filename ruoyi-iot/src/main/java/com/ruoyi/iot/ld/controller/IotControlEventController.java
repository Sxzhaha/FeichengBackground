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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.iot.ld.domain.IotControlEvent;
import com.ruoyi.iot.ld.service.IIotControlEventService;

/**
 * 控制事件下发Controller
 */
@RestController
@RequestMapping("/ld/controlEvent")
public class IotControlEventController extends BaseController
{
    @Autowired
    private IIotControlEventService iotControlEventService;

    @PreAuthorize("@ss.hasPermi('ld:controlEvent:list')")
    @GetMapping("/list")
    public TableDataInfo list(IotControlEvent iotControlEvent)
    {
        startPage();
        List<IotControlEvent> list = iotControlEventService.selectIotControlEventList(iotControlEvent);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ld:controlEvent:export')")
    @Log(title = "控制事件下发", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IotControlEvent iotControlEvent)
    {
        List<IotControlEvent> list = iotControlEventService.selectIotControlEventList(iotControlEvent);
        ExcelUtil<IotControlEvent> util = new ExcelUtil<IotControlEvent>(IotControlEvent.class);
        util.exportExcel(response, list, "控制事件下发数据");
    }

    @PreAuthorize("@ss.hasPermi('ld:controlEvent:query')")
    @GetMapping(value = "/{eventId}")
    public AjaxResult getInfo(@PathVariable("eventId") Long eventId)
    {
        return success(iotControlEventService.selectIotControlEventByEventId(eventId));
    }

    @PreAuthorize("@ss.hasPermi('ld:controlEvent:add')")
    @Log(title = "控制事件下发", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IotControlEvent iotControlEvent)
    {
        return toAjax(iotControlEventService.insertIotControlEvent(iotControlEvent));
    }

    @PreAuthorize("@ss.hasPermi('ld:controlEvent:edit')")
    @Log(title = "控制事件下发", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IotControlEvent iotControlEvent)
    {
        return toAjax(iotControlEventService.updateIotControlEvent(iotControlEvent));
    }

    @PreAuthorize("@ss.hasPermi('ld:controlEvent:remove')")
    @Log(title = "控制事件下发", businessType = BusinessType.DELETE)
    @DeleteMapping("/{eventIds}")
    public AjaxResult remove(@PathVariable Long[] eventIds)
    {
        return toAjax(iotControlEventService.deleteIotControlEventByEventIds(eventIds));
    }
}
