package com.ruoyi.binZhou.xwq.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.xwq.mapper.UserDefaultModelMapper;
import com.ruoyi.binZhou.xwq.domain.UserDefaultModel;
import com.ruoyi.binZhou.xwq.service.IUserDefaultModelService;

/**
 * 用户默认公共模型Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-13
 */
@Service
public class UserDefaultModelServiceImpl implements IUserDefaultModelService 
{
    @Autowired
    private UserDefaultModelMapper userDefaultModelMapper;

    /**
     * 查询用户默认公共模型
     * 
     * @param preferenceId 用户默认公共模型主键
     * @return 用户默认公共模型
     */
    @Override
    public UserDefaultModel selectUserDefaultModelByPreferenceId(Long preferenceId)
    {
        return userDefaultModelMapper.selectUserDefaultModelByPreferenceId(preferenceId);
    }

    /**
     * 查询用户默认公共模型列表
     * 
     * @param userDefaultModel 用户默认公共模型
     * @return 用户默认公共模型
     */
    @Override
    public List<UserDefaultModel> selectUserDefaultModelList(UserDefaultModel userDefaultModel)
    {
        return userDefaultModelMapper.selectUserDefaultModelList(userDefaultModel);
    }

    /**
     * 新增用户默认公共模型
     * 
     * @param userDefaultModel 用户默认公共模型
     * @return 结果
     */
    @Override
    public int insertUserDefaultModel(UserDefaultModel userDefaultModel)
    {
        userDefaultModel.setCreateTime(DateUtils.getNowDate());
        return userDefaultModelMapper.insertUserDefaultModel(userDefaultModel);
    }

    /**
     * 修改用户默认公共模型
     * 
     * @param userDefaultModel 用户默认公共模型
     * @return 结果
     */
    @Override
    public int updateUserDefaultModel(UserDefaultModel userDefaultModel)
    {
        userDefaultModel.setUpdateTime(DateUtils.getNowDate());
        return userDefaultModelMapper.updateUserDefaultModel(userDefaultModel);
    }

    /**
     * 批量删除用户默认公共模型
     * 
     * @param preferenceIds 需要删除的用户默认公共模型主键
     * @return 结果
     */
    @Override
    public int deleteUserDefaultModelByPreferenceIds(Long[] preferenceIds)
    {
        return userDefaultModelMapper.deleteUserDefaultModelByPreferenceIds(preferenceIds);
    }

    /**
     * 删除用户默认公共模型信息
     * 
     * @param preferenceId 用户默认公共模型主键
     * @return 结果
     */
    @Override
    public int deleteUserDefaultModelByPreferenceId(Long preferenceId)
    {
        return userDefaultModelMapper.deleteUserDefaultModelByPreferenceId(preferenceId);
    }
}
