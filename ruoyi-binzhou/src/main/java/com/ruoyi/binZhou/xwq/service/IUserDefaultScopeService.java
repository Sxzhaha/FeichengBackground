package com.ruoyi.binZhou.xwq.service;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.UserDefaultScope;

/**
 * 用户默认监测范围Service接口
 * 
 * @author ruoyi
 * @date 2026-10-13
 */
public interface IUserDefaultScopeService 
{
    /**
     * 查询用户默认监测范围
     * 
     * @param scopeId 用户默认监测范围主键
     * @return 用户默认监测范围
     */
    public UserDefaultScope selectUserDefaultScopeByScopeId(Long scopeId);

    /**
     * 查询用户默认监测范围列表
     * 
     * @param userDefaultScope 用户默认监测范围
     * @return 用户默认监测范围集合
     */
    public List<UserDefaultScope> selectUserDefaultScopeList(UserDefaultScope userDefaultScope);

    /**
     * 新增用户默认监测范围
     * 
     * @param userDefaultScope 用户默认监测范围
     * @return 结果
     */
    public int insertUserDefaultScope(UserDefaultScope userDefaultScope);

    /**
     * 修改用户默认监测范围
     * 
     * @param userDefaultScope 用户默认监测范围
     * @return 结果
     */
    public int updateUserDefaultScope(UserDefaultScope userDefaultScope);

    /**
     * 更新当前用户默认范围，允许清空地块或设备。
     */
    public int updateUserDefaultScopeCurrent(UserDefaultScope userDefaultScope);

    /**
     * 批量删除用户默认监测范围
     * 
     * @param scopeIds 需要删除的用户默认监测范围主键集合
     * @return 结果
     */
    public int deleteUserDefaultScopeByScopeIds(Long[] scopeIds);

    /**
     * 删除用户默认监测范围信息
     * 
     * @param scopeId 用户默认监测范围主键
     * @return 结果
     */
    public int deleteUserDefaultScopeByScopeId(Long scopeId);
}
