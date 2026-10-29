package com.ruoyi.binZhou.wr.controller;

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
import com.ruoyi.binZhou.wr.domain.WarningRecord;
import com.ruoyi.binZhou.wr.service.IWarningRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 预警记录Controller
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
@RestController
@RequestMapping("/wr/warningRecord")
public class WarningRecordController extends BaseController
{
    @Autowired
    private IWarningRecordService warningRecordService;

    /**
     * 查询预警记录列表
     */
    @PreAuthorize("@ss.hasPermi('wr:warningRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(WarningRecord warningRecord)
    {
        startPage();
        List<WarningRecord> list = warningRecordService.selectWarningRecordList(warningRecord);
        return getDataTable(list);
    }

    /**
     * 导出预警记录列表
     */
    @PreAuthorize("@ss.hasPermi('wr:warningRecord:export')")
    @Log(title = "预警记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WarningRecord warningRecord)
    {
        List<WarningRecord> list = warningRecordService.selectWarningRecordList(warningRecord);
        ExcelUtil<WarningRecord> util = new ExcelUtil<WarningRecord>(WarningRecord.class);
        util.exportExcel(response, list, "预警记录数据");
    }

    /**
     * 获取预警记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('wr:warningRecord:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(warningRecordService.selectWarningRecordById(id));
    }

    /**
     * 新增预警记录
     */
    @PreAuthorize("@ss.hasPermi('wr:warningRecord:add')")
    @Log(title = "预警记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WarningRecord warningRecord)
    {
        return toAjax(warningRecordService.insertWarningRecord(warningRecord));
    }

    /**
     * 修改预警记录
     */
    @PreAuthorize("@ss.hasPermi('wr:warningRecord:edit')")
    @Log(title = "预警记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WarningRecord warningRecord)
    {
        return toAjax(warningRecordService.updateWarningRecord(warningRecord));
    }

    /**
     * 删除预警记录
     */
    @PreAuthorize("@ss.hasPermi('wr:warningRecord:remove')")
    @Log(title = "预警记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(warningRecordService.deleteWarningRecordByIds(ids));
    }
}
