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
import com.ruoyi.binZhou.xwq.domain.AiRecord;
import com.ruoyi.binZhou.xwq.service.IAiRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 识别记录Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/xwq/record")
public class AiRecordController extends BaseController
{
    @Autowired
    private IAiRecordService aiRecordService;

    /**
     * 查询识别记录列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiRecord aiRecord)
    {
        startPage();
        List<AiRecord> list = aiRecordService.selectAiRecordList(aiRecord);
        return getDataTable(list);
    }

    /**
     * 导出识别记录列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:record:export')")
    @Log(title = "识别记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiRecord aiRecord)
    {
        List<AiRecord> list = aiRecordService.selectAiRecordList(aiRecord);
        ExcelUtil<AiRecord> util = new ExcelUtil<AiRecord>(AiRecord.class);
        util.exportExcel(response, list, "识别记录数据");
    }

    /**
     * 获取识别记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:record:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(aiRecordService.selectAiRecordByRecordId(recordId));
    }

    /**
     * 新增识别记录
     */
    @PreAuthorize("@ss.hasPermi('xwq:record:add')")
    @Log(title = "识别记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiRecord aiRecord)
    {
        return toAjax(aiRecordService.insertAiRecord(aiRecord));
    }

    /**
     * 修改识别记录
     */
    @PreAuthorize("@ss.hasPermi('xwq:record:edit')")
    @Log(title = "识别记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiRecord aiRecord)
    {
        return toAjax(aiRecordService.updateAiRecord(aiRecord));
    }

    /**
     * 删除识别记录
     */
    @PreAuthorize("@ss.hasPermi('xwq:record:remove')")
    @Log(title = "识别记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(aiRecordService.deleteAiRecordByRecordIds(recordIds));
    }
}
