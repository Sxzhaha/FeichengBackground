package com.ruoyi.binZhou.wr.controller;

import java.util.List;
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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.binZhou.wr.domain.ReportPushConfig;
import com.ruoyi.binZhou.wr.service.IReportPushConfigService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 农情报表推送配置Controller
 */
@RestController
@RequestMapping("/wr/pushConfig")
public class ReportPushConfigController extends BaseController
{
    @Autowired
    private IReportPushConfigService reportPushConfigService;

    /**
     * 【核心封装】利用若依自带的 List 查询，来模拟根据 UserId 查询一条数据
     */
    private ReportPushConfig getConfigByUserId(Long userId) {
        ReportPushConfig query = new ReportPushConfig();
        query.setUserId(userId);
        // 调用若依生成的通用列表查询
        List<ReportPushConfig> list = reportPushConfigService.selectReportPushConfigList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0); // 取第一条匹配的数据
        }
        return null;
    }

    /**
     * 获取当前登录农户的专属推送配置 (供前台订阅卡片渲染使用)
     */
    @GetMapping("/getLatestConfig")
    public AjaxResult getLatestConfig()
    {
        Long userId = SecurityUtils.getUserId();

        // 使用我们上面刚刚封装的方法
        ReportPushConfig config = getConfigByUserId(userId);

        if (config == null) {
            config = new ReportPushConfig();
            config.setUserId(userId);
            config.setPushFrequency("weekly");
            config.setPushChannel("system");
        }
        return AjaxResult.success(config);
    }

    /**
     * 农户在前台勾选推送规则后点击保存 (智能判断新增或更新)
     */
    @PostMapping("/saveConfig")
    public AjaxResult saveConfig(@RequestBody ReportPushConfig reportPushConfig)
    {
        Long userId = SecurityUtils.getUserId();
        String username = SecurityUtils.getUsername();

        reportPushConfig.setUserId(userId);
        reportPushConfig.setUpdateBy(username);
        reportPushConfig.setUserRole("farmer");
        reportPushConfig.setStatus("0");

        // 同样使用上面封装的方法
        ReportPushConfig exist = getConfigByUserId(userId);
        if (exist == null) {
            reportPushConfig.setCreateBy(username);
            return toAjax(reportPushConfigService.insertReportPushConfig(reportPushConfig));
        } else {
            // 如果已存在，必须把数据库里的主键ID塞进来，MyBatis 才知道要更新哪一行
            reportPushConfig.setConfigId(exist.getConfigId());
            return toAjax(reportPushConfigService.updateReportPushConfig(reportPushConfig));
        }
    }

    /**
     * 查询农情报表推送配置列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ReportPushConfig reportPushConfig)
    {
        startPage();
        List<ReportPushConfig> list = reportPushConfigService.selectReportPushConfigList(reportPushConfig);
        return getDataTable(list);
    }

    /**
     * 获取农情报表推送配置详细信息
     */
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") Long configId)
    {
        return success(reportPushConfigService.selectReportPushConfigByConfigId(configId));
    }

    /**
     * 新增农情报表推送配置
     */
    @Log(title = "农情报表推送配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ReportPushConfig reportPushConfig)
    {
        return toAjax(reportPushConfigService.insertReportPushConfig(reportPushConfig));
    }

    /**
     * 修改农情报表推送配置
     */
    @Log(title = "农情报表推送配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ReportPushConfig reportPushConfig)
    {
        return toAjax(reportPushConfigService.updateReportPushConfig(reportPushConfig));
    }

    /**
     * 删除农情报表推送配置
     */
    @Log(title = "农情报表推送配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        return toAjax(reportPushConfigService.deleteReportPushConfigByConfigIds(configIds));
    }
}