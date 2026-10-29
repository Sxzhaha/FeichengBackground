package com.ruoyi.binZhou.wr.controller;

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
import com.ruoyi.binZhou.wr.domain.WarningTemplateRule;
import com.ruoyi.binZhou.wr.service.IWarningTemplateRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 预警模板规则Controller
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
@RestController
@RequestMapping("/wr/rule")
public class WarningTemplateRuleController extends BaseController
{
    @Autowired
    private IWarningTemplateRuleService warningTemplateRuleService;

    /**
     * 查询预警模板规则列表
     */
    @PreAuthorize("@ss.hasPermi('wr:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(WarningTemplateRule warningTemplateRule)
    {
        startPage();
        List<WarningTemplateRule> list = warningTemplateRuleService.selectWarningTemplateRuleList(warningTemplateRule);
        return getDataTable(list);
    }

    /**
     * 导出预警模板规则列表
     */
    @PreAuthorize("@ss.hasPermi('wr:rule:export')")
    @Log(title = "预警模板规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WarningTemplateRule warningTemplateRule)
    {
        List<WarningTemplateRule> list = warningTemplateRuleService.selectWarningTemplateRuleList(warningTemplateRule);
        ExcelUtil<WarningTemplateRule> util = new ExcelUtil<WarningTemplateRule>(WarningTemplateRule.class);
        util.exportExcel(response, list, "预警模板规则数据");
    }

    /**
     * 获取预警模板规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('wr:rule:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(warningTemplateRuleService.selectWarningTemplateRuleById(id));
    }

    /**
     * 新增预警模板规则
     */
    @PreAuthorize("@ss.hasPermi('wr:rule:add')")
    @Log(title = "预警模板规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WarningTemplateRule warningTemplateRule)
    {
        return toAjax(warningTemplateRuleService.insertWarningTemplateRule(warningTemplateRule));
    }

    /**
     * 修改预警模板规则
     */
    @PreAuthorize("@ss.hasPermi('wr:rule:edit')")
    @Log(title = "预警模板规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WarningTemplateRule warningTemplateRule)
    {
        return toAjax(warningTemplateRuleService.updateWarningTemplateRule(warningTemplateRule));
    }

    /**
     * 删除预警模板规则
     */
    @PreAuthorize("@ss.hasPermi('wr:rule:remove')")
    @Log(title = "预警模板规则", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(warningTemplateRuleService.deleteWarningTemplateRuleByIds(ids));
    }
}
