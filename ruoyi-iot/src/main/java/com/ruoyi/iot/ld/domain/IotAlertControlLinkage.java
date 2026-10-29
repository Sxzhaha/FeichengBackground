package com.ruoyi.iot.ld.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Alert control linkage rule object iot_alert_control_linkage.
 */
public class IotAlertControlLinkage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "预警规则ID")
    private Long alertConfigId;

    @Excel(name = "控制终端ID")
    private Long targetDeviceId;

    @Excel(name = "控制物模型ID")
    private Long targetModelId;

    @Excel(name = "下发固定值")
    private String commandValue;

    @Excel(name = "动作名称")
    private String actionName;

    @Excel(name = "是否启用", readConverterExp = "0=禁用,1=启用")
    private Integer enabled;

    @Excel(name = "优先级")
    private Integer priority;

    @Excel(name = "冷却时间秒")
    private Integer cooldownSeconds;

    @Excel(name = "延迟执行秒")
    private Integer delaySeconds;

    private String targetDeviceName;

    private String targetDeviceSn;

    private String targetModelName;

    private String targetModelIdentifier;

    private String alertLevel;

    private String ruleDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlertConfigId() {
        return alertConfigId;
    }

    public void setAlertConfigId(Long alertConfigId) {
        this.alertConfigId = alertConfigId;
    }

    public Long getTargetDeviceId() {
        return targetDeviceId;
    }

    public void setTargetDeviceId(Long targetDeviceId) {
        this.targetDeviceId = targetDeviceId;
    }

    public Long getTargetModelId() {
        return targetModelId;
    }

    public void setTargetModelId(Long targetModelId) {
        this.targetModelId = targetModelId;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public void setCommandValue(String commandValue) {
        this.commandValue = commandValue;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(Integer cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public Integer getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(Integer delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public String getTargetDeviceName() {
        return targetDeviceName;
    }

    public void setTargetDeviceName(String targetDeviceName) {
        this.targetDeviceName = targetDeviceName;
    }

    public String getTargetDeviceSn() {
        return targetDeviceSn;
    }

    public void setTargetDeviceSn(String targetDeviceSn) {
        this.targetDeviceSn = targetDeviceSn;
    }

    public String getTargetModelName() {
        return targetModelName;
    }

    public void setTargetModelName(String targetModelName) {
        this.targetModelName = targetModelName;
    }

    public String getTargetModelIdentifier() {
        return targetModelIdentifier;
    }

    public void setTargetModelIdentifier(String targetModelIdentifier) {
        this.targetModelIdentifier = targetModelIdentifier;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("alertConfigId", getAlertConfigId())
            .append("targetDeviceId", getTargetDeviceId())
            .append("targetModelId", getTargetModelId())
            .append("commandValue", getCommandValue())
            .append("actionName", getActionName())
            .append("enabled", getEnabled())
            .append("priority", getPriority())
            .append("cooldownSeconds", getCooldownSeconds())
            .append("delaySeconds", getDelaySeconds())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
