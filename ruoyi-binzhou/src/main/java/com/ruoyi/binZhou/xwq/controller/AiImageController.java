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
import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.service.IAiImageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 图片信息Controller
 *
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/xwq/image")
public class AiImageController extends BaseController
{
    @Autowired
    private IAiImageService aiImageService;

    /**
     * 查询图片信息列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:image:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiImage aiImage)
    {
        startPage();
        List<AiImage> list = aiImageService.selectAiImageList(aiImage);
        return getDataTable(list);
    }

    /**
     * 导出图片信息列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:image:export')")
    @Log(title = "图片信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiImage aiImage)
    {
        List<AiImage> list = aiImageService.selectAiImageList(aiImage);
        ExcelUtil<AiImage> util = new ExcelUtil<AiImage>(AiImage.class);
        util.exportExcel(response, list, "图片信息数据");
    }

    /**
     * 导出原始图片 ZIP。
     */
    @PreAuthorize("@ss.hasPermi('xwq:image:export')")
    @Log(title = "图片信息", businessType = BusinessType.EXPORT)
    @GetMapping("/exportOriginalImages")
    public void exportOriginalImages(HttpServletResponse response, AiImage aiImage)
    {
        aiImageService.exportOriginalImages(aiImage, response);
    }

    /**
     * 导出虫情识别结果图 ZIP。
     */
    @PreAuthorize("@ss.hasPermi('xwq:image:export')")
    @Log(title = "虫情识别结果图", businessType = BusinessType.EXPORT)
    @GetMapping("/exportPestResultImages")
    public void exportPestResultImages(HttpServletResponse response, AiImage aiImage)
    {
        aiImageService.exportPestResultImages(aiImage, response);
    }

    /**
     * 获取图片信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:image:query')")
    @GetMapping(value = "/{imageId}")
    public AjaxResult getInfo(@PathVariable("imageId") Long imageId)
    {
        return success(aiImageService.selectAiImageByImageId(imageId));
    }

    /**
     * 新增图片信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:image:add')")
    @Log(title = "图片信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiImage aiImage)
    {
        return toAjax(aiImageService.insertAiImage(aiImage));
    }

    /**
     * 修改图片信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:image:edit')")
    @Log(title = "图片信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiImage aiImage)
    {
        return toAjax(aiImageService.updateAiImage(aiImage));
    }

    /**
     * 删除图片信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:image:remove')")
    @Log(title = "图片信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{imageIds}")
    public AjaxResult remove(@PathVariable Long[] imageIds)
    {
        return toAjax(aiImageService.deleteAiImageByImageIds(imageIds));
    }
}
