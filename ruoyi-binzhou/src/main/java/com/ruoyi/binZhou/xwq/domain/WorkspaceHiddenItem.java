package com.ruoyi.binZhou.xwq.domain;

/** 前台工作区隐藏项。 */
public class WorkspaceHiddenItem
{
    private String targetType;
    private Long targetId;

    public WorkspaceHiddenItem()
    {
    }

    public WorkspaceHiddenItem(String targetType, Long targetId)
    {
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
    }

    public Long getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Long targetId)
    {
        this.targetId = targetId;
    }
}
