package com.ruoyi.binZhou.xwq.controller;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.binZhou.xwq.domain.AiDisease;
import com.ruoyi.binZhou.xwq.service.IAiDiseaseService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 病虫害识别结果Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/xwq/disease")
public class AiDiseaseController extends BaseController
{
    @Autowired
    private IAiDiseaseService aiDiseaseService;

    /**
     * 查询病虫害识别结果列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:disease:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiDisease aiDisease)
    {
        startPage();
        List<AiDisease> list = aiDiseaseService.selectAiDiseaseList(aiDisease);
        return getDataTable(list);
    }

    /**
     * 导出病虫害识别结果列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:disease:export')")
    @Log(title = "病虫害识别结果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiDisease aiDisease)
    {
        List<AiDisease> list = aiDiseaseService.selectAiDiseaseList(aiDisease);
        ExcelUtil<AiDisease> util = new ExcelUtil<AiDisease>(AiDisease.class);
        util.exportExcel(response, list, "病虫害识别结果数据");
    }

    /**
     * 获取病虫害识别结果详细信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:disease:query')")
    @GetMapping(value = "/{diseaseId}")
    public AjaxResult getInfo(@PathVariable("diseaseId") Long diseaseId)
    {
        return success(aiDiseaseService.selectAiDiseaseByDiseaseId(diseaseId));
    }

    /**
     * 新增病虫害识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:disease:add')")
    @Log(title = "病虫害识别结果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiDisease aiDisease)
    {
        return toAjax(aiDiseaseService.insertAiDisease(aiDisease));
    }

    /**
     * 修改病虫害识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:disease:edit')")
    @Log(title = "病虫害识别结果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiDisease aiDisease)
    {
        return toAjax(aiDiseaseService.updateAiDisease(aiDisease));
    }

    /**
     * 删除病虫害识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:disease:remove')")
    @Log(title = "病虫害识别结果", businessType = BusinessType.DELETE)
	@DeleteMapping("/{diseaseIds}")
    public AjaxResult remove(@PathVariable Long[] diseaseIds)
    {
        return toAjax(aiDiseaseService.deleteAiDiseaseByDiseaseIds(diseaseIds));
    }

    /**
     * 根据图片ID识别病虫害
     */
    @PostMapping("/predictByImage")
    public AjaxResult predictByImage(@RequestBody Map<String, Object> params)
    {
        if (params.get("imageId") == null)
        {
            return AjaxResult.error("imageId不能为空");
        }

        if (params.get("modelId") == null)
        {
            return AjaxResult.error("modelId不能为空，请先选择病虫害识别模型");
        }

        Long imageId = Long.valueOf(params.get("imageId").toString());
        Long modelId = Long.valueOf(params.get("modelId").toString());

        String cropType = params.get("cropType") == null ? "小麦" : params.get("cropType").toString();
        String recordMode = params.get("recordMode") == null ? "手动上传" : params.get("recordMode").toString();

        return AjaxResult.success(
                "识别成功",
                aiDiseaseService.predictByImage(imageId, modelId, cropType, recordMode)
        );
    }

}
