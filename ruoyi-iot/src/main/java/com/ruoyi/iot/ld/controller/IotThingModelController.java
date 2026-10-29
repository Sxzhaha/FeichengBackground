package com.ruoyi.iot.ld.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.exception.ServiceException;
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
import com.ruoyi.iot.ld.domain.IotThingModel;
import com.ruoyi.iot.ld.service.IIotThingModelService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 物模型定义Controller
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
@RestController
@RequestMapping("/binZhou/ld/iot/model")
public class IotThingModelController extends BaseController
{
    @Autowired
    private IIotThingModelService iotThingModelService;

    /**
     * 查询物模型定义列表
     */
    @PreAuthorize("@ss.hasPermi('iot:model:list')")
    @GetMapping("/list")
    public TableDataInfo list(IotThingModel iotThingModel)
    {
        startPage();
        List<IotThingModel> list = iotThingModelService.selectIotThingModelList(iotThingModel);
        return getDataTable(list);
    }

    /**
     * 导出物模型定义列表
     */
    @PreAuthorize("@ss.hasPermi('iot:model:export')")
    @Log(title = "物模型定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IotThingModel iotThingModel)
    {
        List<IotThingModel> list = iotThingModelService.selectIotThingModelList(iotThingModel);
        ExcelUtil<IotThingModel> util = new ExcelUtil<IotThingModel>(IotThingModel.class);
        util.exportExcel(response, list, "物模型定义数据");
    }

    /**
     * 获取物模型定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:model:query')")
    @GetMapping(value = "/{modelId}")
    public AjaxResult getInfo(@PathVariable("modelId") Long modelId)
    {
        return success(iotThingModelService.selectIotThingModelByModelId(modelId));
    }

    /**
     * 新增物模型定义
     */
    @PreAuthorize("@ss.hasPermi('iot:model:add')")
    @Log(title = "物模型定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IotThingModel iotThingModel)
    {
        try {
            return toAjax(iotThingModelService.insertIotThingModel(iotThingModel));
        }
        catch (ServiceException e) {
            // 单独捕获这一处的重复异常
            return AjaxResult.error("操作失败：标识符重复，请检查后重试");
        }
        catch (Exception e) {
            // 其他异常
            return AjaxResult.error("系统异常");
        }
    }

    /**
     * 修改物模型定义
     */
    @PreAuthorize("@ss.hasPermi('iot:model:edit')")
    @Log(title = "物模型定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IotThingModel iotThingModel)
    {
        return toAjax(iotThingModelService.updateIotThingModel(iotThingModel));
    }

    /**
     * 删除物模型定义
     */
    @PreAuthorize("@ss.hasPermi('iot:model:remove')")
    @Log(title = "物模型定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{modelIds}")
    public AjaxResult remove(@PathVariable Long[] modelIds)
    {
        return toAjax(iotThingModelService.deleteIotThingModelByModelIds(modelIds));
    }
}
