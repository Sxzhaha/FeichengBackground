package com.ruoyi.binZhou.xwq.controller;

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
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.service.IAiModelService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 模型管理Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/xwq/model")
public class AiModelController extends BaseController
{
    @Autowired
    private IAiModelService aiModelService;

    /**
     * 查询模型管理列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiModel aiModel)
    {
        startPage();
        List<AiModel> list = aiModelService.selectAiModelList(aiModel);
        return getDataTable(list);
    }

    /**
     * 导出模型管理列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:export')")
    @Log(title = "模型管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiModel aiModel)
    {
        List<AiModel> list = aiModelService.selectAiModelList(aiModel);
        ExcelUtil<AiModel> util = new ExcelUtil<AiModel>(AiModel.class);
        util.exportExcel(response, list, "模型管理数据");
    }

    /**
     * 获取模型管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:query')")
    @GetMapping(value = "/{modelId}")
    public AjaxResult getInfo(@PathVariable("modelId") Long modelId)
    {
        return success(aiModelService.selectAiModelByModelId(modelId));
    }

    /**
     * 新增模型管理
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:add')")
    @Log(title = "模型管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiModel aiModel)
    {
        return toAjax(aiModelService.insertAiModel(aiModel));
    }

    /**
     * 修改模型管理
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:edit')")
    @Log(title = "模型管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiModel aiModel)
    {
        return toAjax(aiModelService.updateAiModel(aiModel));
    }

    /**
     * 删除模型管理
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:remove')")
    @Log(title = "模型管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{modelIds}")
    public AjaxResult remove(@PathVariable Long[] modelIds)
    {
        return toAjax(aiModelService.deleteAiModelByModelIds(modelIds));
    }
}
