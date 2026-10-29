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
import com.ruoyi.binZhou.wr.domain.WarningTemplate;
import com.ruoyi.binZhou.wr.service.IWarningTemplateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 预警模板Controller
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
@RestController
@RequestMapping("/wr/template")
public class WarningTemplateController extends BaseController
{
    @Autowired
    private IWarningTemplateService warningTemplateService;

    /**
     * 查询预警模板列表
     */
    @PreAuthorize("@ss.hasPermi('wr:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(WarningTemplate warningTemplate)
    {
        startPage();
        List<WarningTemplate> list = warningTemplateService.selectWarningTemplateList(warningTemplate);
        return getDataTable(list);
    }

    /**
     * 导出预警模板列表
     */
    @PreAuthorize("@ss.hasPermi('wr:template:export')")
    @Log(title = "预警模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WarningTemplate warningTemplate)
    {
        List<WarningTemplate> list = warningTemplateService.selectWarningTemplateList(warningTemplate);
        ExcelUtil<WarningTemplate> util = new ExcelUtil<WarningTemplate>(WarningTemplate.class);
        util.exportExcel(response, list, "预警模板数据");
    }

    /**
     * 获取预警模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('wr:template:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(warningTemplateService.selectWarningTemplateById(id));
    }

    /**
     * 新增预警模板
     */
    @PreAuthorize("@ss.hasPermi('wr:template:add')")
    @Log(title = "预警模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WarningTemplate warningTemplate)
    {
        // 获取当前登录用户名并赋值给 create_by 字段
        warningTemplate.setCreateBy(getUsername());

        // 如果 create_time 也报错没有默认值，请把下面这行也加上：
        // warningTemplate.setCreateTime(com.ruoyi.common.utils.DateUtils.getNowDate());

        return toAjax(warningTemplateService.insertWarningTemplate(warningTemplate));
    }

    /**
     * 修改预警模板
     */
    @PreAuthorize("@ss.hasPermi('wr:template:edit')")
    @Log(title = "预警模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WarningTemplate warningTemplate)
    {
        // 获取当前登录用户名并赋值给 update_by 字段
        warningTemplate.setUpdateBy(getUsername());

        // 如果 update_time 需要手动更新，请把下面这行加上：
        // warningTemplate.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());

        return toAjax(warningTemplateService.updateWarningTemplate(warningTemplate));
    }

    /**
     * 删除预警模板
     */
    @PreAuthorize("@ss.hasPermi('wr:template:remove')")
    @Log(title = "预警模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(warningTemplateService.deleteWarningTemplateByIds(ids));
    }
}
