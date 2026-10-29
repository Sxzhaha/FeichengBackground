package com.ruoyi.binZhou.xwq.service;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.WorkspaceHiddenItem;

public interface IAiWorkspaceHiddenService
{
    List<WorkspaceHiddenItem> selectHiddenList(Long userId, String moduleType);

    void hide(Long userId, String moduleType, String targetType, Long targetId);

    void hideBatch(Long userId, String moduleType, String targetType, List<Long> targetIds);

    void restore(Long userId, String moduleType, String targetType, Long targetId);
}
