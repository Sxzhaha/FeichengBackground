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
import com.ruoyi.binZhou.wr.domain.WarningTask;
import com.ruoyi.binZhou.wr.service.IWarningTaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 预警任务Controller
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
@RestController
@RequestMapping("/wr/task")
public class WarningTaskController extends BaseController
{
    @Autowired
    private IWarningTaskService warningTaskService;

    /**
     * 查询预警任务列表
     */
    @PreAuthorize("@ss.hasPermi('wr:task:list')")
    @GetMapping("/list")
    public TableDataInfo list(WarningTask warningTask)
    {
        startPage();
        List<WarningTask> list = warningTaskService.selectWarningTaskList(warningTask);
        return getDataTable(list);
    }

    /**
     * 导出预警任务列表
     */
    @PreAuthorize("@ss.hasPermi('wr:task:export')")
    @Log(title = "预警任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WarningTask warningTask)
    {
        List<WarningTask> list = warningTaskService.selectWarningTaskList(warningTask);
        ExcelUtil<WarningTask> util = new ExcelUtil<WarningTask>(WarningTask.class);
        util.exportExcel(response, list, "预警任务数据");
    }

    /**
     * 获取预警任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('wr:task:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(warningTaskService.selectWarningTaskById(id));
    }

    /**
     * 新增预警任务
     */
    @PreAuthorize("@ss.hasPermi('wr:task:add')")
    @Log(title = "预警任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WarningTask warningTask)
    {
        // 【关键新增】获取当前登录用户名并赋值
        warningTask.setCreateBy(getUsername());
        return toAjax(warningTaskService.insertWarningTask(warningTask));
    }

    /**
     * 修改预警任务
     */
    @PreAuthorize("@ss.hasPermi('wr:task:edit')")
    @Log(title = "预警任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WarningTask warningTask)
    {
        // 【关键新增】获取当前登录用户名并赋值
        warningTask.setUpdateBy(getUsername());
        return toAjax(warningTaskService.updateWarningTask(warningTask));
    }

    /**
     * 删除预警任务
     */
    @PreAuthorize("@ss.hasPermi('wr:task:remove')")
    @Log(title = "预警任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(warningTaskService.deleteWarningTaskByIds(ids));
    }
}
