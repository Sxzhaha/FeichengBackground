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
import com.ruoyi.iot.ld.domain.CameraTask;
import com.ruoyi.iot.ld.service.ICameraTaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 摄像头定时抓拍任务Controller
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/binZhou/ld/iot/cameraTask")
public class CameraTaskController extends BaseController
{
    @Autowired
    private ICameraTaskService cameraTaskService;

    /**
     * 查询摄像头定时抓拍任务列表
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(CameraTask cameraTask)
    {
        startPage();
        List<CameraTask> list = cameraTaskService.selectCameraTaskList(cameraTask);
        return getDataTable(list);
    }

    /**
     * 导出摄像头定时抓拍任务列表
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraTask:export')")
    @Log(title = "摄像头定时抓拍任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CameraTask cameraTask)
    {
        List<CameraTask> list = cameraTaskService.selectCameraTaskList(cameraTask);
        ExcelUtil<CameraTask> util = new ExcelUtil<CameraTask>(CameraTask.class);
        util.exportExcel(response, list, "摄像头定时抓拍任务数据");
    }

    /**
     * 获取摄像头定时抓拍任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraTask:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        return success(cameraTaskService.selectCameraTaskByTaskId(taskId));
    }

    /**
     * 新增摄像头定时抓拍任务
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraTask:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CameraTask cameraTask) {
        // 1. 基础校验
        if (cameraTask.getPresetId() == null) {
            return error("必须关联设备");
        }

        // 2. 根据任务类型进行特定校验
        if ("ONCE".equals(cameraTask.getJobType())) {
            if (cameraTask.getExecuteTime() == null) {
                return error("单次任务必须选择执行时间");
            }
        } else if ("CRON".equals(cameraTask.getJobType())) {
            if (cameraTask.getCronExpression() == null || "".equals(cameraTask.getCronExpression())) {
                return error("循环任务必须配置执行周期");
            }
        }

        // 3. 保存到数据库
        return toAjax(cameraTaskService.insertCameraTask(cameraTask));
    }

    /**
     * 修改摄像头定时抓拍任务
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraTask:edit')")
    @Log(title = "摄像头定时抓拍任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CameraTask cameraTask)
    {
        return toAjax(cameraTaskService.updateCameraTask(cameraTask));
    }

    /**
     * 删除摄像头定时抓拍任务
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraTask:remove')")
    @Log(title = "摄像头定时抓拍任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(cameraTaskService.deleteCameraTaskByTaskIds(taskIds));
    }
}
