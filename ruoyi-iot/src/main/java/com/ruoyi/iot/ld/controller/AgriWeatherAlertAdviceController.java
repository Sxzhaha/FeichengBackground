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
import com.ruoyi.iot.ld.domain.AgriWeatherAlertAdvice;
import com.ruoyi.iot.ld.service.IAgriWeatherAlertAdviceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 极端天气突发预警与防灾建议Controller
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
@RestController
@RequestMapping("/ld/weatherWarn")
public class AgriWeatherAlertAdviceController extends BaseController
{
    @Autowired
    private IAgriWeatherAlertAdviceService agriWeatherAlertAdviceService;

    /**
     * 查询极端天气突发预警与防灾建议列表
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherWarn:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgriWeatherAlertAdvice agriWeatherAlertAdvice)
    {
        startPage();
        List<AgriWeatherAlertAdvice> list = agriWeatherAlertAdviceService.selectAgriWeatherAlertAdviceList(agriWeatherAlertAdvice);
        return getDataTable(list);
    }

    /**
     * 导出极端天气突发预警与防灾建议列表
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherWarn:export')")
    @Log(title = "极端天气突发预警与防灾建议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgriWeatherAlertAdvice agriWeatherAlertAdvice)
    {
        List<AgriWeatherAlertAdvice> list = agriWeatherAlertAdviceService.selectAgriWeatherAlertAdviceList(agriWeatherAlertAdvice);
        ExcelUtil<AgriWeatherAlertAdvice> util = new ExcelUtil<AgriWeatherAlertAdvice>(AgriWeatherAlertAdvice.class);
        util.exportExcel(response, list, "极端天气突发预警与防灾建议数据");
    }

    /**
     * 获取极端天气突发预警与防灾建议详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherWarn:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(agriWeatherAlertAdviceService.selectAgriWeatherAlertAdviceById(id));
    }

    /**
     * 新增极端天气突发预警与防灾建议
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherWarn:add')")
    @Log(title = "极端天气突发预警与防灾建议", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AgriWeatherAlertAdvice agriWeatherAlertAdvice)
    {
        return toAjax(agriWeatherAlertAdviceService.insertAgriWeatherAlertAdvice(agriWeatherAlertAdvice));
    }

    /**
     * 修改极端天气突发预警与防灾建议
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherWarn:edit')")
    @Log(title = "极端天气突发预警与防灾建议", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AgriWeatherAlertAdvice agriWeatherAlertAdvice)
    {
        return toAjax(agriWeatherAlertAdviceService.updateAgriWeatherAlertAdvice(agriWeatherAlertAdvice));
    }

    /**
     * 删除极端天气突发预警与防灾建议
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherWarn:remove')")
    @Log(title = "极端天气突发预警与防灾建议", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(agriWeatherAlertAdviceService.deleteAgriWeatherAlertAdviceByIds(ids));
    }
}
