package com.ruoyi.binZhou.xwq.domain;

import java.util.List;

/** 前台工作区隐藏请求。 */
public class WorkspaceHiddenRequest
{
    private String targetType;
    private Long targetId;
    private List<Long> targetIds;

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public List<Long> getTargetIds() { return targetIds; }
    public void setTargetIds(List<Long> targetIds) { this.targetIds = targetIds; }
}
