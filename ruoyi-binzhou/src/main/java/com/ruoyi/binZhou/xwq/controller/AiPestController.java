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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.binZhou.xwq.domain.AiPest;
import com.ruoyi.binZhou.xwq.service.IAiPestService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 虫情识别结果Controller
 *
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/xwq/pest")
public class AiPestController extends BaseController
{
    @Autowired
    private IAiPestService aiPestService;

    /**
     * 查询虫情识别结果列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:pest:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiPest aiPest)
    {
        startPage();
        List<AiPest> list = aiPestService.selectAiPestList(aiPest);
        return getDataTable(list);
    }

    /**
     * 导出虫情识别结果列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:pest:export')")
    @Log(title = "虫情识别结果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiPest aiPest)
    {
        List<AiPest> list = aiPestService.selectAiPestList(aiPest);
        ExcelUtil<AiPest> util = new ExcelUtil<AiPest>(AiPest.class);
        util.exportExcel(response, list, "虫情识别结果数据");
    }

    /**
     * 获取虫情识别结果详细信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:pest:query')")
    @GetMapping(value = "/{pestId}")
    public AjaxResult getInfo(@PathVariable("pestId") Long pestId)
    {
        return success(aiPestService.selectAiPestByPestId(pestId));
    }

    /**
     * 新增虫情识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:pest:add')")
    @Log(title = "虫情识别结果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiPest aiPest)
    {
        return toAjax(aiPestService.insertAiPest(aiPest));
    }

    /**
     * 修改虫情识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:pest:edit')")
    @Log(title = "虫情识别结果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiPest aiPest)
    {
        return toAjax(aiPestService.updateAiPest(aiPest));
    }

    /**
     * 删除虫情识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:pest:remove')")
    @Log(title = "虫情识别结果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pestIds}")
    public AjaxResult remove(@PathVariable Long[] pestIds)
    {
        return toAjax(aiPestService.deleteAiPestByPestIds(pestIds));
    }

    /**
     * 根据真实虫情记录生成大模型报告文本。
     */
    @PostMapping("/generateReportText")
    public AjaxResult generateReportText(
            @RequestBody Map<String, Object> params)
    {
        String reportText =
                aiPestService.generateReportText(params);

        return AjaxResult.success(
                "操作成功",
                reportText
        );
    }

    /**
     * 虫情趋势预测
     */
    @GetMapping("/predict")
    public AjaxResult predict(@RequestParam(required = false) Long parkId,
                              @RequestParam(required = false) Long areaId,
                              @RequestParam(required = false) String locationId,
                              @RequestParam(required = false) Long deviceId,
                              @RequestParam(required = false) String pestName,
                              @RequestParam(required = false) Integer days)
    {
        return AjaxResult.success(
                aiPestService.predictPestTrend(
                        parkId,
                        areaId,
                        locationId,
                        deviceId,
                        pestName,
                        days
                )
        );
    }

    /**
     * 根据数据库中已经存在的图片ID进行虫情识别。
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
            return AjaxResult.error("modelId不能为空，请先选择虫情识别模型");
        }

        Long imageId = Long.valueOf(params.get("imageId").toString());
        Long modelId = Long.valueOf(params.get("modelId").toString());

        String cropType = params.get("cropType") == null
                ? "冬枣"
                : params.get("cropType").toString();

        String recordMode = params.get("recordMode") == null
                ? "手动上传"
                : params.get("recordMode").toString();

        return AjaxResult.success(
                "识别成功",
                aiPestService.predictByImage(
                        imageId,
                        modelId,
                        cropType,
                        recordMode
                )
        );
    }

    /**
     * 识别临时上传图片。
     *
     * 模型识别成功后，才在同一事务内写入：
     * 1. xwq_ai_image
     * 2. xwq_ai_record
     * 3. xwq_ai_pest
     */
    @PostMapping("/predictUploadedImage")
    public AjaxResult predictUploadedImage(
            @RequestBody Map<String, Object> params)
    {
        if (params.get("imageUrl") == null
                || params.get("imageUrl").toString().trim().isEmpty())
        {
            return AjaxResult.error("imageUrl不能为空");
        }

        if (params.get("modelId") == null)
        {
            return AjaxResult.error(
                    "modelId不能为空，请先选择虫情识别模型"
            );
        }

        return AjaxResult.success(
                "识别成功，数据已入库",
                aiPestService.predictUploadedImage(params)
        );
    }
}
