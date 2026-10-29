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
import com.ruoyi.iot.ld.domain.ImageRecord;
import com.ruoyi.iot.ld.service.IImageRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 抓拍图片记录Controller
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/binZhou/ld/iot/record")
public class ImageRecordController extends BaseController
{
    @Autowired
    private IImageRecordService imageRecordService;

    /**
     * 查询抓拍图片记录列表
     */
    @PreAuthorize("@ss.hasPermi('iot:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(ImageRecord imageRecord)
    {
        startPage();
        List<ImageRecord> list = imageRecordService.selectImageRecordList(imageRecord);
        return getDataTable(list);
    }

    /**
     * 导出抓拍图片记录列表
     */
    @PreAuthorize("@ss.hasPermi('iot:record:export')")
    @Log(title = "抓拍图片记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ImageRecord imageRecord)
    {
        List<ImageRecord> list = imageRecordService.selectImageRecordList(imageRecord);
        ExcelUtil<ImageRecord> util = new ExcelUtil<ImageRecord>(ImageRecord.class);
        util.exportExcel(response, list, "抓拍图片记录数据");
    }

    /**
     * 获取抓拍图片记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(imageRecordService.selectImageRecordById(id));
    }

    /**
     * 新增抓拍图片记录
     */
    @PreAuthorize("@ss.hasPermi('iot:record:add')")
    @Log(title = "抓拍图片记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ImageRecord imageRecord)
    {
        return toAjax(imageRecordService.insertImageRecord(imageRecord));
    }

    /**
     * 修改抓拍图片记录
     */
    @PreAuthorize("@ss.hasPermi('iot:record:edit')")
    @Log(title = "抓拍图片记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ImageRecord imageRecord)
    {
        return toAjax(imageRecordService.updateImageRecord(imageRecord));
    }

    /**
     * 删除抓拍图片记录
     */
    @PreAuthorize("@ss.hasPermi('iot:record:remove')")
    @Log(title = "抓拍图片记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(imageRecordService.deleteImageRecordByIds(ids));
    }
}
