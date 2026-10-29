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
import com.ruoyi.iot.ld.domain.AgriProductAlertConfig;
import com.ruoyi.iot.ld.service.IAgriProductAlertConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 物模型产品预警规则配置Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/ld/warnConfig")
public class AgriProductAlertConfigController extends BaseController
{
    @Autowired
    private IAgriProductAlertConfigService agriProductAlertConfigService;

    /**
     * 查询物模型产品预警规则配置列表
     */
    @PreAuthorize("@ss.hasPermi('ld:warnConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgriProductAlertConfig agriProductAlertConfig)
    {
        startPage();
        List<AgriProductAlertConfig> list = agriProductAlertConfigService.selectAgriProductAlertConfigList(agriProductAlertConfig);
        return getDataTable(list);
    }

    /**
     * 导出物模型产品预警规则配置列表
     */
    @PreAuthorize("@ss.hasPermi('ld:warnConfig:export')")
    @Log(title = "物模型产品预警规则配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgriProductAlertConfig agriProductAlertConfig)
    {
        List<AgriProductAlertConfig> list = agriProductAlertConfigService.selectAgriProductAlertConfigList(agriProductAlertConfig);
        ExcelUtil<AgriProductAlertConfig> util = new ExcelUtil<AgriProductAlertConfig>(AgriProductAlertConfig.class);
        util.exportExcel(response, list, "物模型产品预警规则配置数据");
    }

    /**
     * 获取物模型产品预警规则配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:warnConfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(agriProductAlertConfigService.selectAgriProductAlertConfigById(id));
    }

    /**
     * 新增物模型产品预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('ld:warnConfig:add')")
    @Log(title = "物模型产品预警规则配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AgriProductAlertConfig agriProductAlertConfig)
    {
        return toAjax(agriProductAlertConfigService.insertAgriProductAlertConfig(agriProductAlertConfig));
    }

    /**
     * 修改物模型产品预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('ld:warnConfig:edit')")
    @Log(title = "物模型产品预警规则配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AgriProductAlertConfig agriProductAlertConfig)
    {
        return toAjax(agriProductAlertConfigService.updateAgriProductAlertConfig(agriProductAlertConfig));
    }

    /**
     * 删除物模型产品预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('ld:warnConfig:remove')")
    @Log(title = "物模型产品预警规则配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(agriProductAlertConfigService.deleteAgriProductAlertConfigByIds(ids));
    }
}
