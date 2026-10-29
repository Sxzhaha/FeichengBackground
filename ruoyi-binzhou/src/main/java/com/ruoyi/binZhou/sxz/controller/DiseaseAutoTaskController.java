package com.ruoyi.binZhou.sxz.controller;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoResultQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTask;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskItem;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskResult;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskRun;
import com.ruoyi.binZhou.sxz.service.IDiseaseAutoTaskService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/sxz/disease-auto-task")
public class DiseaseAutoTaskController extends BaseController
{
    private final IDiseaseAutoTaskService service;

    public DiseaseAutoTaskController(IDiseaseAutoTaskService service)
    {
        this.service = service;
    }

    @GetMapping("/list")
    public TableDataInfo list(DiseaseAutoTaskQuery query)
    {
        startPage();
        List<DiseaseAutoTask> list = service.selectTaskList(query);
        return getDataTable(list);
    }

    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId)
    {
        return AjaxResult.success(service.selectTaskById(taskId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody DiseaseAutoTask task)
    {
        return toAjax(service.insertTask(task));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody DiseaseAutoTask task)
    {
        return toAjax(service.updateTask(task));
    }

    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(service.deleteTaskByIds(taskIds));
    }

    @PutMapping("/{taskId}/status")
    public AjaxResult changeStatus(@PathVariable Long taskId, @RequestBody Map<String, Object> body)
    {
        Object status = body == null ? null : body.get("taskStatus");
        return toAjax(service.updateTaskStatus(taskId, status == null ? "1" : status.toString()));
    }

    @PostMapping("/{taskId}/run")
    public AjaxResult run(@PathVariable Long taskId)
    {
        return AjaxResult.success(service.executeImmediately(taskId));
    }

    @GetMapping("/{taskId}/runs")
    public TableDataInfo runs(@PathVariable Long taskId)
    {
        startPage();
        List<DiseaseAutoTaskRun> list = service.selectRunList(taskId);
        return getDataTable(list);
    }

    @GetMapping("/runs/{runId}/items")
    public TableDataInfo runItems(@PathVariable Long runId)
    {
        startPage();
        List<DiseaseAutoTaskItem> list = service.selectRunItems(runId);
        return getDataTable(list);
    }

    @GetMapping("/summary")
    public AjaxResult summary()
    {
        return AjaxResult.success(service.selectSummary());
    }

    @GetMapping("/results")
    public TableDataInfo results(DiseaseAutoResultQuery query)
    {
        startPage();
        List<DiseaseAutoTaskResult> list = service.selectResultList(query);
        return getDataTable(list);
    }

    @GetMapping("/results/{itemId}")
    public AjaxResult resultDetail(@PathVariable Long itemId)
    {
        return AjaxResult.success(service.selectResultByItemId(itemId));
    }

    @GetMapping("/options")
    public AjaxResult options(Long parkId)
    {
        return AjaxResult.success(service.selectOptions(parkId));
    }
}
