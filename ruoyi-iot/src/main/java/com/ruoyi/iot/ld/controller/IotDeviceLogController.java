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
import com.ruoyi.iot.ld.domain.IotDeviceLog;
import com.ruoyi.iot.ld.service.IIotDeviceLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备通信日志Controller
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
@RestController
@RequestMapping("/binZhou/ld/iot/log")
public class IotDeviceLogController extends BaseController
{
    @Autowired
    private IIotDeviceLogService iotDeviceLogService;

    /**
     * 查询设备通信日志列表
     */
    @PreAuthorize("@ss.hasPermi('iot:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(IotDeviceLog iotDeviceLog)
    {
        startPage();
        List<IotDeviceLog> list = iotDeviceLogService.selectIotDeviceLogList(iotDeviceLog);
        return getDataTable(list);
    }

    /**
     * 导出设备通信日志列表
     */
    @PreAuthorize("@ss.hasPermi('iot:log:export')")
    @Log(title = "设备通信日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IotDeviceLog iotDeviceLog)
    {
        List<IotDeviceLog> list = iotDeviceLogService.selectIotDeviceLogList(iotDeviceLog);
        ExcelUtil<IotDeviceLog> util = new ExcelUtil<IotDeviceLog>(IotDeviceLog.class);
        util.exportExcel(response, list, "设备通信日志数据");
    }

    /**
     * 获取设备通信日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:log:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return success(iotDeviceLogService.selectIotDeviceLogByLogId(logId));
    }

    /**
     * 新增设备通信日志
     */
    @PreAuthorize("@ss.hasPermi('iot:log:add')")
    @Log(title = "设备通信日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IotDeviceLog iotDeviceLog)
    {
        try{
            return toAjax(iotDeviceLogService.insertIotDeviceLog(iotDeviceLog));
        }catch (Exception e){
            return error(e.getMessage());
        }

    }

    /**
     * 修改设备通信日志
     */
    @PreAuthorize("@ss.hasPermi('iot:log:edit')")
    @Log(title = "设备通信日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IotDeviceLog iotDeviceLog)
    {
        return toAjax(iotDeviceLogService.updateIotDeviceLog(iotDeviceLog));
    }

    /**
     * 删除设备通信日志
     */
    @PreAuthorize("@ss.hasPermi('iot:log:remove')")
    @Log(title = "设备通信日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(iotDeviceLogService.deleteIotDeviceLogByLogIds(logIds));
    }
}
