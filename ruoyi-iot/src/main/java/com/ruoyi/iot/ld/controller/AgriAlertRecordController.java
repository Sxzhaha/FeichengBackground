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
import com.ruoyi.iot.ld.domain.AgriAlertRecord;
import com.ruoyi.iot.ld.service.IAgriAlertRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 产品预警触发与历史处理记录Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/ld/warnRecord")
public class AgriAlertRecordController extends BaseController
{
    @Autowired
    private IAgriAlertRecordService agriAlertRecordService;

    /**
     * 查询产品预警触发与历史处理记录列表
     */
    @PreAuthorize("@ss.hasPermi('ld:warnRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgriAlertRecord agriAlertRecord)
    {
        startPage();
        List<AgriAlertRecord> list = agriAlertRecordService.selectAgriAlertRecordList(agriAlertRecord);
        return getDataTable(list);
    }

    /**
     * 导出产品预警触发与历史处理记录列表
     */
    @PreAuthorize("@ss.hasPermi('ld:warnRecord:export')")
    @Log(title = "产品预警触发与历史处理记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgriAlertRecord agriAlertRecord)
    {
        List<AgriAlertRecord> list = agriAlertRecordService.selectAgriAlertRecordList(agriAlertRecord);
        ExcelUtil<AgriAlertRecord> util = new ExcelUtil<AgriAlertRecord>(AgriAlertRecord.class);
        util.exportExcel(response, list, "产品预警触发与历史处理记录数据");
    }

    /**
     * 获取产品预警触发与历史处理记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:warnRecord:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(agriAlertRecordService.selectAgriAlertRecordById(id));
    }

    /**
     * 新增产品预警触发与历史处理记录
     */
    @PreAuthorize("@ss.hasPermi('ld:warnRecord:add')")
    @Log(title = "产品预警触发与历史处理记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AgriAlertRecord agriAlertRecord)
    {
        return toAjax(agriAlertRecordService.insertAgriAlertRecord(agriAlertRecord));
    }

    /**
     * 修改产品预警触发与历史处理记录
     */
    @PreAuthorize("@ss.hasPermi('ld:warnRecord:edit')")
    @Log(title = "产品预警触发与历史处理记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AgriAlertRecord agriAlertRecord)
    {
        return toAjax(agriAlertRecordService.updateAgriAlertRecord(agriAlertRecord));
    }

    /**
     * 删除产品预警触发与历史处理记录
     */
    @PreAuthorize("@ss.hasPermi('ld:warnRecord:remove')")
    @Log(title = "产品预警触发与历史处理记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(agriAlertRecordService.deleteAgriAlertRecordByIds(ids));
    }
}
