package com.ruoyi.binZhou.sxz.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoItemQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageItemRow;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageRunRow;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageTaskRow;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoRunQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTask;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskQuery;
import com.ruoyi.binZhou.sxz.service.IDiseaseAutoManageService;
import com.ruoyi.binZhou.sxz.service.IDiseaseAutoTaskService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

@RestController
@RequestMapping("/sxz/disease-auto-manage")
public class DiseaseAutoManageController extends BaseController
{
    private final IDiseaseAutoManageService manageService;
    private final IDiseaseAutoTaskService taskService;

    public DiseaseAutoManageController(IDiseaseAutoManageService manageService,
            IDiseaseAutoTaskService taskService)
    {
        this.manageService = manageService;
        this.taskService = taskService;
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:list')")
    @GetMapping("/summary")
    public AjaxResult summary()
    {
        return AjaxResult.success(manageService.selectSummary());
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:list')")
    @GetMapping("/tasks")
    public TableDataInfo tasks(DiseaseAutoTaskQuery query)
    {
        startPage();
        return getDataTable(taskService.selectTaskList(query));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:query')")
    @GetMapping("/tasks/{taskId}")
    public AjaxResult task(@PathVariable Long taskId)
    {
        return AjaxResult.success(taskService.selectTaskById(taskId));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:add')")
    @Log(title = "自动识别任务", businessType = BusinessType.INSERT)
    @PostMapping("/tasks")
    public AjaxResult addTask(@RequestBody DiseaseAutoTask task)
    {
        return toAjax(taskService.insertTask(task));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:edit')")
    @Log(title = "自动识别任务", businessType = BusinessType.UPDATE)
    @PutMapping("/tasks")
    public AjaxResult editTask(@RequestBody DiseaseAutoTask task)
    {
        return toAjax(taskService.updateTask(task));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:remove')")
    @Log(title = "自动识别任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/tasks/{taskIds}")
    public AjaxResult removeTask(@PathVariable Long[] taskIds)
    {
        return toAjax(taskService.deleteTaskByIds(taskIds));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:edit')")
    @Log(title = "自动识别任务状态", businessType = BusinessType.UPDATE)
    @PutMapping("/tasks/{taskId}/status")
    public AjaxResult status(@PathVariable Long taskId, @RequestBody Map<String, Object> body)
    {
        Object value = body == null ? null : body.get("taskStatus");
        return toAjax(taskService.updateTaskStatus(taskId, value == null ? "1" : value.toString()));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:execute')")
    @Log(title = "立即执行自动识别任务", businessType = BusinessType.OTHER)
    @PostMapping("/tasks/{taskId}/run")
    public AjaxResult run(@PathVariable Long taskId)
    {
        return AjaxResult.success(taskService.executeImmediately(taskId));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:list')")
    @GetMapping("/runs")
    public TableDataInfo runs(DiseaseAutoRunQuery query)
    {
        startPage();
        return getDataTable(manageService.selectRunRows(query));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:list')")
    @GetMapping("/items")
    public TableDataInfo items(DiseaseAutoItemQuery query)
    {
        startPage();
        return getDataTable(manageService.selectItemRows(query));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:query')")
    @GetMapping("/options")
    public AjaxResult options(Long parkId)
    {
        return AjaxResult.success(taskService.selectOptions(parkId));
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:export')")
    @Log(title = "自动识别任务", businessType = BusinessType.EXPORT)
    @PostMapping("/tasks/export")
    public void exportTasks(HttpServletResponse response, DiseaseAutoTaskQuery query)
    {
        List<DiseaseAutoManageTaskRow> list = manageService.selectTaskRows(query);
        new ExcelUtil<DiseaseAutoManageTaskRow>(DiseaseAutoManageTaskRow.class)
                .exportExcel(response, list, "自动识别任务");
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:export')")
    @Log(title = "自动识别执行批次", businessType = BusinessType.EXPORT)
    @PostMapping("/runs/export")
    public void exportRuns(HttpServletResponse response, DiseaseAutoRunQuery query)
    {
        List<DiseaseAutoManageRunRow> list = manageService.selectRunRows(query);
        new ExcelUtil<DiseaseAutoManageRunRow>(DiseaseAutoManageRunRow.class)
                .exportExcel(response, list, "自动识别执行批次");
    }

    @PreAuthorize("@ss.hasPermi('sxz:autoManage:export')")
    @Log(title = "自动识别图片明细", businessType = BusinessType.EXPORT)
    @PostMapping("/items/export")
    public void exportItems(HttpServletResponse response, DiseaseAutoItemQuery query)
    {
        List<DiseaseAutoManageItemRow> list = manageService.selectItemRows(query);
        new ExcelUtil<DiseaseAutoManageItemRow>(DiseaseAutoManageItemRow.class)
                .exportExcel(response, list, "自动识别图片明细");
    }
}
