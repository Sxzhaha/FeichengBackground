package com.ruoyi.binZhou.wr.controller;

import com.ruoyi.binZhou.wr.domain.vo.WeatherTrendVo;
import com.ruoyi.binZhou.wr.service.IWeatherTrendService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天气趋势图控制器
 */
@RestController
@RequestMapping("/binZhou/wr/weatherTrend")
public class WeatherTrendController extends BaseController
{
    @Autowired
    private IWeatherTrendService weatherTrendService;

    /**
     * 查询天气趋势图数据
     *
     * 示例：
     * /binZhou/wr/weatherTrend?parkId=1&historyDays=5&forecastDays=7
     */
    @GetMapping
    public AjaxResult getWeatherTrend(
            @RequestParam(required = false) Long parkId,
            @RequestParam(required = false) Integer historyDays,
            @RequestParam(required = false) Integer forecastDays)
    {
        WeatherTrendVo data =
                weatherTrendService.selectWeatherTrend(
                        parkId,
                        historyDays,
                        forecastDays
                );

        return success(data);
    }
}
