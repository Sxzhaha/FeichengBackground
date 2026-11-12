package com.ruoyi.binZhou.common;

import com.ruoyi.common.utils.SecurityUtils;

/**
 * 肥城业务数据范围工具。
 *
 * <p>管理员返回 {@code null}，表示不限制园区；普通用户返回当前用户 ID，
 * Mapper 使用 {@code park.user_id} 过滤其可访问的桃园。</p>
 */
public final class FeichengDataScopeUtils
{
    private FeichengDataScopeUtils()
    {
    }

    public static Long currentParkOwnerUserId()
    {
        Long userId = SecurityUtils.getUserId();
        return SecurityUtils.isAdmin(userId) ? null : userId;
    }
}
