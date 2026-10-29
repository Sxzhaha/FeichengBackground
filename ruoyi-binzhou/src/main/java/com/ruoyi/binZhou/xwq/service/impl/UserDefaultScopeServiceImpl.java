package com.ruoyi.binZhou.xwq.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.xwq.mapper.UserDefaultScopeMapper;
import com.ruoyi.binZhou.xwq.domain.UserDefaultScope;
import com.ruoyi.binZhou.xwq.service.IUserDefaultScopeService;

/**
 * 用户默认监测范围Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-13
 */
@Service
public class UserDefaultScopeServiceImpl implements IUserDefaultScopeService 
{
    @Autowired
    private UserDefaultScopeMapper userDefaultScopeMapper;

    /**
     * 查询用户默认监测范围
     * 
     * @param scopeId 用户默认监测范围主键
     * @return 用户默认监测范围
     */
    @Override
    public UserDefaultScope selectUserDefaultScopeByScopeId(Long scopeId)
    {
        return userDefaultScopeMapper.selectUserDefaultScopeByScopeId(scopeId);
    }

    /**
     * 查询用户默认监测范围列表
     * 
     * @param userDefaultScope 用户默认监测范围
     * @return 用户默认监测范围
     */
    @Override
    public List<UserDefaultScope> selectUserDefaultScopeList(UserDefaultScope userDefaultScope)
    {
        return userDefaultScopeMapper.selectUserDefaultScopeList(userDefaultScope);
    }

    /**
     * 新增用户默认监测范围
     * 
     * @param userDefaultScope 用户默认监测范围
     * @return 结果
     */
    @Override
    public int insertUserDefaultScope(UserDefaultScope userDefaultScope)
    {
        userDefaultScope.setCreateTime(DateUtils.getNowDate());
        return userDefaultScopeMapper.insertUserDefaultScope(userDefaultScope);
    }

    /**
     * 修改用户默认监测范围
     * 
     * @param userDefaultScope 用户默认监测范围
     * @return 结果
     */
    @Override
    public int updateUserDefaultScope(UserDefaultScope userDefaultScope)
    {
        userDefaultScope.setUpdateTime(DateUtils.getNowDate());
        return userDefaultScopeMapper.updateUserDefaultScope(userDefaultScope);
    }

    @Override
    public int updateUserDefaultScopeCurrent(UserDefaultScope userDefaultScope)
    {
        userDefaultScope.setUpdateTime(DateUtils.getNowDate());
        return userDefaultScopeMapper.updateUserDefaultScopeCurrent(userDefaultScope);
    }

    /**
     * 批量删除用户默认监测范围
     * 
     * @param scopeIds 需要删除的用户默认监测范围主键
     * @return 结果
     */
    @Override
    public int deleteUserDefaultScopeByScopeIds(Long[] scopeIds)
    {
        return userDefaultScopeMapper.deleteUserDefaultScopeByScopeIds(scopeIds);
    }

    /**
     * 删除用户默认监测范围信息
     * 
     * @param scopeId 用户默认监测范围主键
     * @return 结果
     */
    @Override
    public int deleteUserDefaultScopeByScopeId(Long scopeId)
    {
        return userDefaultScopeMapper.deleteUserDefaultScopeByScopeId(scopeId);
    }
}
