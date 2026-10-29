package com.ruoyi.binZhou.wr.engine;

import com.ruoyi.binZhou.wr.service.IFarmerReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 农情报表定时推送任务雷达
 */
@Component("reportPushEngine") // 给若依后台调用的 Spring Bean 别名
public class ReportPushEngineTask {

    private static final Logger log = LoggerFactory.getLogger(ReportPushEngineTask.class);

    @Autowired
    private IFarmerReportService reportService;

    /**
     * 每日推送入口 (供若依后台配置)
     * 调用目标字符串：reportPushEngine.executeDailyPush()
     */
    public void executeDailyPush() {
        log.info("⏰ [定时任务启动] 开始聚合执行：每日农事报表推送...");
        try {
            reportService.executeScheduledPush("daily");
            log.info("✨ 每日农事报表推送完成。");
        } catch (Exception e) {
            log.error("❌ 每日农事报表推送发生异常: ", e);
        }
    }

    /**
     * 每周推送入口 (供若依后台配置)
     * 调用目标字符串：reportPushEngine.executeWeeklyPush()
     */
    public void executeWeeklyPush() {
        log.info("⏰ [定时任务启动] 开始聚合执行：每周农事报表推送...");
        try {
            reportService.executeScheduledPush("weekly");
            log.info("✨ 每周农事报表推送完成。");
        } catch (Exception e) {
            log.error("❌ 每周农事报表推送发生异常: ", e);
        }
    }
}