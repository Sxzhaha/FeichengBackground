package com.ruoyi.binZhou.xwq.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.binZhou.xwq.domain.AiPhenology;
import com.ruoyi.binZhou.xwq.service.IAiPhenologyService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 物候期识别结果Controller
 *
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/xwq/phenology")
public class AiPhenologyController extends BaseController
{
    @Autowired
    private IAiPhenologyService aiPhenologyService;

    /**
     * 查询物候期识别结果列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:phenology:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiPhenology aiPhenology)
    {
        startPage();
        List<AiPhenology> list =
                aiPhenologyService.selectAiPhenologyList(aiPhenology);
        return getDataTable(list);
    }

    /**
     * 导出物候期识别结果列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:phenology:export')")
    @Log(title = "物候期识别结果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(
            HttpServletResponse response,
            AiPhenology aiPhenology)
    {
        List<AiPhenology> list =
                aiPhenologyService.selectAiPhenologyList(aiPhenology);
        ExcelUtil<AiPhenology> util =
                new ExcelUtil<AiPhenology>(AiPhenology.class);
        util.exportExcel(
                response,
                list,
                "物候期识别结果数据"
        );
    }

    /**
     * 获取物候期识别结果详细信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:phenology:query')")
    @GetMapping(value = "/{phenologyId}")
    public AjaxResult getInfo(
            @PathVariable("phenologyId") Long phenologyId)
    {
        return success(
                aiPhenologyService
                        .selectAiPhenologyByPhenologyId(phenologyId)
        );
    }

    /**
     * 新增物候期识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:phenology:add')")
    @Log(title = "物候期识别结果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(
            @RequestBody AiPhenology aiPhenology)
    {
        return toAjax(
                aiPhenologyService.insertAiPhenology(aiPhenology)
        );
    }

    /**
     * 修改物候期识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:phenology:edit')")
    @Log(title = "物候期识别结果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(
            @RequestBody AiPhenology aiPhenology)
    {
        return toAjax(
                aiPhenologyService.updateAiPhenology(aiPhenology)
        );
    }

    /**
     * 删除物候期识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:phenology:remove')")
    @Log(title = "物候期识别结果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{phenologyIds}")
    public AjaxResult remove(
            @PathVariable Long[] phenologyIds)
    {
        return toAjax(
                aiPhenologyService
                        .deleteAiPhenologyByPhenologyIds(phenologyIds)
        );
    }

    /**
     * 生成物候期报告大模型文本。
     */
    @PostMapping("/generateReportText")
    public AjaxResult generateReportText(
            @RequestBody Map<String, Object> params)
    {
        String reportText =
                aiPhenologyService.generateReportText(params);

        return AjaxResult.success(
                "操作成功",
                reportText
        );
    }

    /**
     * 临时上传图片进行物候识别。
     *
     * 模型识别成功后，才在同一个事务中写入：
     * 1. xwq_ai_image
     * 2. xwq_ai_record
     * 3. xwq_ai_phenology
     *
     * 前端地址：
     * POST /xwq/phenology/predictUploadedImage
     */
    @Log(title = "物候期智能识别", businessType = BusinessType.INSERT)
    @PostMapping("/predictUploadedImage")
    public AjaxResult predictUploadedImage(
            @RequestBody Map<String, Object> params)
    {
        if (params == null)
        {
            return AjaxResult.error("请求参数不能为空");
        }

        Object imageUrl = params.get("imageUrl");
        if (imageUrl == null
                || imageUrl.toString().trim().isEmpty())
        {
            return AjaxResult.error("imageUrl不能为空");
        }

        Object modelId = params.get("modelId");
        if (modelId == null
                || modelId.toString().trim().isEmpty())
        {
            return AjaxResult.error(
                    "modelId不能为空，请先选择物候期识别模型"
            );
        }

        return AjaxResult.success(
                "识别成功，数据已入库",
                aiPhenologyService.predictUploadedImage(params)
        );
    }
}
