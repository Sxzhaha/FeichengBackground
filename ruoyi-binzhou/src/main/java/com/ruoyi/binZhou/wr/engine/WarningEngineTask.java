package com.ruoyi.binZhou.wr.engine;

import com.ruoyi.binZhou.wr.domain.WarningRecord;
import com.ruoyi.binZhou.wr.domain.WarningTask;
import com.ruoyi.binZhou.wr.domain.WarningTemplate;
import com.ruoyi.binZhou.wr.domain.WarningTemplateRule;
import com.ruoyi.binZhou.wr.mapper.WarningRecordMapper;
import com.ruoyi.binZhou.wr.service.AiDecisionService;
import com.ruoyi.binZhou.wr.service.IWarningRecordService;
import com.ruoyi.binZhou.wr.service.IWarningTaskService;
import com.ruoyi.binZhou.wr.service.IWarningTemplateRuleService;
import com.ruoyi.binZhou.wr.service.IWarningTemplateService;
import com.ruoyi.binZhou.wr.service.support.WarningIndicatorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 物联网智能预警引擎，统一调度和执行所有运行中的预警任务。
 */
@Component("warningEngine")
public class WarningEngineTask
{
    private static final Logger log = LoggerFactory.getLogger(WarningEngineTask.class);

    @Autowired
    private IWarningTaskService warningTaskService;

    @Autowired
    private IWarningTemplateRuleService warningTemplateRuleService;

    @Autowired
    private IWarningTemplateService warningTemplateService;

    @Autowired
    private IWarningRecordService warningRecordService;
    @Autowired
    private AiDecisionService aiDecisionService;

    @Autowired
    private WarningRecordMapper warningRecordMapper;

    /**
     * 定时扫描并执行所有运行中的预警任务。
     */
    public void executeActiveTasks()
    {
        log.info("[预警雷达] 开始新一轮全盘扫描");

        WarningTask query = new WarningTask();
        query.setStatus("running");
        List<WarningTask> activeTasks = warningTaskService.selectWarningTaskList(query);

        if (activeTasks == null || activeTasks.isEmpty())
        {
            log.info("[预警雷达] 扫描结束，当前暂无需要执行的预警任务");
            return;
        }

        log.info("[预警雷达] 检索到 {} 个运行中的任务", activeTasks.size());
        for (WarningTask task : activeTasks)
        {
            try
            {
                log.info("[引擎启动] 正在执行任务 [{}] (ID:{})", task.getTaskName(), task.getId());
                processSingleTask(task);
            }
            catch (Exception e)
            {
                log.error("[引擎异常] 任务 [" + task.getTaskName() + "] 执行失败", e);
            }
        }

        log.info("[预警雷达] 本轮扫描处理完毕");
    }

    private void processSingleTask(WarningTask task)
    {
        if (task.getTemplateId() == null)
        {
            log.warn("任务 [{}] 未关联预警模板，跳过执行", task.getTaskName());
            return;
        }

        WarningTemplate template = warningTemplateService.selectWarningTemplateById(task.getTemplateId());
        if (template == null || template.getParkId() == null || template.getWarningType() == null)
        {
            log.warn("任务 [{}] 关联的模板缺少园区或预警类型，跳过执行", task.getTaskName());
            return;
        }

        WarningTemplateRule ruleQuery = new WarningTemplateRule();
        ruleQuery.setTemplateId(task.getTemplateId());
        List<WarningTemplateRule> rules = warningTemplateRuleService.selectWarningTemplateRuleList(ruleQuery);
        if (rules == null || rules.isEmpty())
        {
            log.info("任务 [{}] 关联的模板暂无规则，跳过执行", task.getTaskName());
            return;
        }

        Set<String> emittedWarningKeys = new HashSet<>();
        for (WarningTemplateRule rule : rules)
        {
            String canonicalIndicator = WarningIndicatorCode.normalize(rule.getIndicatorCode());
            if (canonicalIndicator == null)
            {
                log.warn("任务 [{}] 的指标编码 [{}] 不受当前气象数据源支持，跳过预警判断",
                        task.getTaskName(), rule.getIndicatorCode());
                continue;
            }

            BigDecimal currentValue = getLatestWeatherLiveValue(task, canonicalIndicator);
            if (currentValue == null)
            {
                log.warn("任务 [{}] 指标 [{}] 暂无实时气象数据，跳过预警判断",
                        task.getTaskName(), rule.getIndicatorCode());
                continue;
            }

            RuleEvaluation evaluation = evaluateRule(rule, currentValue);
            if (!evaluation.isValid())
            {
                log.warn("规则 [{}] 运算符或阈值配置无效，跳过预警判断", rule.getId());
                continue;
            }
            if (!evaluation.isTriggered())
            {
                continue;
            }

            String warningKey = canonicalIndicator + "|" + rule.getLevel() + "|"
                    + currentValue.stripTrailingZeros().toPlainString();
            if (!emittedWarningKeys.add(warningKey))
            {
                log.info("本轮已生成相同预警 [{}]，跳过重复写入", warningKey);
                continue;
            }

            log.warn("触发预警：任务 [{}] 指标 [{}] 当前值 [{}] 规则 [{}]",
                    task.getTaskName(), rule.getIndicatorCode(), currentValue,
                    evaluation.getRuleDescription());

            WarningRecord record = new WarningRecord();
            record.setTaskId(task.getId());
            record.setDeviceId(task.getDataSourceId());
            record.setWarningType(template.getWarningType());
            record.setWarningLevel(rule.getLevel());
            record.setIndicatorCode(rule.getIndicatorCode());
            record.setCurrentValue(currentValue);
            record.setWarningTime(new Date());
            record.setAiSuggestion(aiDecisionService.generateAdvice(
                    task, template, rule, currentValue, evaluation.getRuleDescription()));

            warningRecordService.insertWarningRecord(record);
            log.info("预警记录及处置建议已写入数据库");
        }
    }

    private BigDecimal getLatestWeatherLiveValue(WarningTask task, String canonicalIndicator)
    {
        if (task.getTemplateId() == null || canonicalIndicator == null)
        {
            return null;
        }
        return warningRecordMapper.selectLatestWeatherLiveValue(
                task.getTemplateId(), task.getDataSourceId(), canonicalIndicator);
    }

    private RuleEvaluation evaluateRule(WarningTemplateRule rule, BigDecimal currentValue)
    {
        if (rule == null || currentValue == null || rule.getOperator() == null)
        {
            return RuleEvaluation.invalid();
        }

        String operator = rule.getOperator().trim().toLowerCase();
        if ("between".equals(operator))
        {
            if (rule.getThresholdMin() == null || rule.getThresholdMax() == null)
            {
                return RuleEvaluation.invalid();
            }
            boolean triggered = currentValue.compareTo(rule.getThresholdMin()) >= 0
                    && currentValue.compareTo(rule.getThresholdMax()) <= 0;
            return RuleEvaluation.valid(triggered,
                    "between " + rule.getThresholdMin().toPlainString()
                            + " and " + rule.getThresholdMax().toPlainString());
        }

        BigDecimal threshold = resolveThreshold(rule, operator);
        if (threshold == null)
        {
            return RuleEvaluation.invalid();
        }

        int comparison = currentValue.compareTo(threshold);
        boolean triggered;
        switch (operator)
        {
            case ">":
                triggered = comparison > 0;
                break;
            case ">=":
                triggered = comparison >= 0;
                break;
            case "<":
                triggered = comparison < 0;
                break;
            case "<=":
                triggered = comparison <= 0;
                break;
            case "=":
            case "==":
                triggered = comparison == 0;
                break;
            default:
                return RuleEvaluation.invalid();
        }
        return RuleEvaluation.valid(triggered, operator + " " + threshold.toPlainString());
    }

    private BigDecimal resolveThreshold(WarningTemplateRule rule, String operator)
    {
        if (">".equals(operator) || ">=".equals(operator))
        {
            return rule.getThresholdMax() != null ? rule.getThresholdMax() : rule.getThresholdMin();
        }
        if ("<".equals(operator) || "<=".equals(operator))
        {
            return rule.getThresholdMin() != null ? rule.getThresholdMin() : rule.getThresholdMax();
        }
        return rule.getThresholdMin() != null ? rule.getThresholdMin() : rule.getThresholdMax();
    }

    private static class RuleEvaluation
    {
        private final boolean valid;
        private final boolean triggered;
        private final String ruleDescription;

        private RuleEvaluation(boolean valid, boolean triggered, String ruleDescription)
        {
            this.valid = valid;
            this.triggered = triggered;
            this.ruleDescription = ruleDescription;
        }

        private static RuleEvaluation invalid()
        {
            return new RuleEvaluation(false, false, null);
        }

        private static RuleEvaluation valid(boolean triggered, String ruleDescription)
        {
            return new RuleEvaluation(true, triggered, ruleDescription);
        }

        private boolean isValid()
        {
            return valid;
        }

        private boolean isTriggered()
        {
            return triggered;
        }

        private String getRuleDescription()
        {
            return ruleDescription;
        }
    }
}
