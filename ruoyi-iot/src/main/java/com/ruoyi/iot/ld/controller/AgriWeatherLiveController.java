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
import com.ruoyi.iot.ld.domain.AgriWeatherLive;
import com.ruoyi.iot.ld.service.IAgriWeatherLiveService;
import com.ruoyi.iot.ld.task.WeatherTask;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 园区实时与当天天气实况记录Controller
 * 
 * @author ruoyi
 * @date 2026-06-25
 */
@RestController
@RequestMapping("/ld/weatherLive")
public class AgriWeatherLiveController extends BaseController
{
    @Autowired
    private IAgriWeatherLiveService agriWeatherLiveService;

    @Autowired
    private WeatherTask weatherTask;

    /**
     * 查询园区实时与当天天气实况记录列表
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherLive:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgriWeatherLive agriWeatherLive)
    {
        startPage();
        List<AgriWeatherLive> list = agriWeatherLiveService.selectAgriWeatherLiveList(agriWeatherLive);
        return getDataTable(list);
    }

    /**
     * 手动触发实时天气抓取与同步
     */
    @GetMapping("/syncLive")
    public AjaxResult syncLiveWeather()
    {
        weatherTask.syncLiveWeather();
        return success("所有园区实时天气数据抓取同步已成功触发");
    }

    /**
     * 手动触发未来七天天气预报抓取与同步
     */
    @GetMapping("/syncForecast")
    public AjaxResult syncForecastWeather()
    {
        weatherTask.syncForecastWeather();
        return success("所有园区未来七天天气预报数据抓取同步已成功触发");
    }

    /**
     * 手动触发完整天气数据拉取同步（包括实时与七天预报）
     */
    @GetMapping("/sync")
    public AjaxResult syncWeather()
    {
        weatherTask.syncWeather();
        return success("所有园区完整天气数据拉取同步已成功触发");
    }


    /**
     * 导出园区实时与当天天气实况记录列表
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherLive:export')")
    @Log(title = "园区实时与当天天气实况记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgriWeatherLive agriWeatherLive)
    {
        List<AgriWeatherLive> list = agriWeatherLiveService.selectAgriWeatherLiveList(agriWeatherLive);
        ExcelUtil<AgriWeatherLive> util = new ExcelUtil<AgriWeatherLive>(AgriWeatherLive.class);
        util.exportExcel(response, list, "园区实时与当天天气实况记录数据");
    }

    /**
     * 获取园区实时与当天天气实况记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherLive:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(agriWeatherLiveService.selectAgriWeatherLiveById(id));
    }

    /**
     * 新增园区实时与当天天气实况记录
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherLive:add')")
    @Log(title = "园区实时与当天天气实况记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AgriWeatherLive agriWeatherLive)
    {
        return toAjax(agriWeatherLiveService.insertAgriWeatherLive(agriWeatherLive));
    }

    /**
     * 修改园区实时与当天天气实况记录
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherLive:edit')")
    @Log(title = "园区实时与当天天气实况记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AgriWeatherLive agriWeatherLive)
    {
        return toAjax(agriWeatherLiveService.updateAgriWeatherLive(agriWeatherLive));
    }

    /**
     * 删除园区实时与当天天气实况记录
     */
    @PreAuthorize("@ss.hasPermi('ld:weatherLive:remove')")
    @Log(title = "园区实时与当天天气实况记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(agriWeatherLiveService.deleteAgriWeatherLiveByIds(ids));
    }
}
