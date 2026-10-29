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
import com.ruoyi.iot.ld.domain.AgriWeatherForecast;
import com.ruoyi.iot.ld.service.IAgriWeatherForecastService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 园区未来天气预报趋势Controller
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
@RestController
@RequestMapping("/ld/weatherForecast")
public class AgriWeatherForecastController extends BaseController
{
    @Autowired
    private IAgriWeatherForecastService agriWeatherForecastService;

    /**
     * 查询园区未来天气预报趋势列表
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherForecast:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgriWeatherForecast agriWeatherForecast)
    {
        startPage();
        List<AgriWeatherForecast> list = agriWeatherForecastService.selectAgriWeatherForecastList(agriWeatherForecast);
        return getDataTable(list);
    }

    /**
     * 导出园区未来天气预报趋势列表
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherForecast:export')")
    @Log(title = "园区未来天气预报趋势", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgriWeatherForecast agriWeatherForecast)
    {
        List<AgriWeatherForecast> list = agriWeatherForecastService.selectAgriWeatherForecastList(agriWeatherForecast);
        ExcelUtil<AgriWeatherForecast> util = new ExcelUtil<AgriWeatherForecast>(AgriWeatherForecast.class);
        util.exportExcel(response, list, "园区未来天气预报趋势数据");
    }

    /**
     * 获取园区未来天气预报趋势详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherForecast:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(agriWeatherForecastService.selectAgriWeatherForecastById(id));
    }

    /**
     * 新增园区未来天气预报趋势
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherForecast:add')")
    @Log(title = "园区未来天气预报趋势", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AgriWeatherForecast agriWeatherForecast)
    {
        return toAjax(agriWeatherForecastService.insertAgriWeatherForecast(agriWeatherForecast));
    }

    /**
     * 修改园区未来天气预报趋势
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherForecast:edit')")
    @Log(title = "园区未来天气预报趋势", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AgriWeatherForecast agriWeatherForecast)
    {
        return toAjax(agriWeatherForecastService.updateAgriWeatherForecast(agriWeatherForecast));
    }

    /**
     * 删除园区未来天气预报趋势
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherForecast:remove')")
    @Log(title = "园区未来天气预报趋势", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(agriWeatherForecastService.deleteAgriWeatherForecastByIds(ids));
    }
}
