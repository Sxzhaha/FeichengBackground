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
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.iot.ld.domain.IotManualControlEvent;
import com.ruoyi.iot.ld.service.IIotManualControlEventService;

/**
 * Manual control event Controller.
 */
@RestController
@RequestMapping("/ld/manualControl")
public class IotManualControlEventController extends BaseController
{
    @Autowired
    private IIotManualControlEventService iotManualControlEventService;

    @PreAuthorize("@ss.hasPermi('ld:manualControl:list')")
    @GetMapping("/list")
    public TableDataInfo list(IotManualControlEvent iotManualControlEvent)
    {
        startPage();
        List<IotManualControlEvent> list = iotManualControlEventService.selectIotManualControlEventList(iotManualControlEvent);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ld:manualControl:export')")
    @Log(title = "手动控制下发", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IotManualControlEvent iotManualControlEvent)
    {
        List<IotManualControlEvent> list = iotManualControlEventService.selectIotManualControlEventList(iotManualControlEvent);
        ExcelUtil<IotManualControlEvent> util = new ExcelUtil<IotManualControlEvent>(IotManualControlEvent.class);
        util.exportExcel(response, list, "手动控制下发数据");
    }

    @PreAuthorize("@ss.hasPermi('ld:manualControl:query')")
    @GetMapping(value = "/{eventId}")
    public AjaxResult getInfo(@PathVariable("eventId") Long eventId)
    {
        return success(iotManualControlEventService.selectIotManualControlEventByEventId(eventId));
    }

    @PreAuthorize("@ss.hasPermi('ld:manualControl:add')")
    @Log(title = "手动控制下发", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IotManualControlEvent iotManualControlEvent)
    {
        return toAjax(iotManualControlEventService.insertIotManualControlEvent(iotManualControlEvent));
    }

    @PreAuthorize("@ss.hasPermi('ld:manualControl:send')")
    @Log(title = "手动控制下发", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public AjaxResult send(@RequestBody IotManualControlEvent iotManualControlEvent)
    {
        return toAjax(iotManualControlEventService.sendIotManualControlEvent(iotManualControlEvent));
    }

    @PreAuthorize("@ss.hasPermi('ld:manualControl:edit')")
    @Log(title = "手动控制下发", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IotManualControlEvent iotManualControlEvent)
    {
        return toAjax(iotManualControlEventService.updateIotManualControlEvent(iotManualControlEvent));
    }

    @PreAuthorize("@ss.hasPermi('ld:manualControl:remove')")
    @Log(title = "手动控制下发", businessType = BusinessType.DELETE)
    @DeleteMapping("/{eventIds}")
    public AjaxResult remove(@PathVariable Long[] eventIds)
    {
        return toAjax(iotManualControlEventService.deleteIotManualControlEventByEventIds(eventIds));
    }
}
