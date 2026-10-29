package com.ruoyi.binZhou.xwq.service;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.UserDefaultModel;

/**
 * 用户默认公共模型Service接口
 * 
 * @author ruoyi
 * @date 2026-10-13
 */
public interface IUserDefaultModelService 
{
    /**
     * 查询用户默认公共模型
     * 
     * @param preferenceId 用户默认公共模型主键
     * @return 用户默认公共模型
     */
    public UserDefaultModel selectUserDefaultModelByPreferenceId(Long preferenceId);

    /**
     * 查询用户默认公共模型列表
     * 
     * @param userDefaultModel 用户默认公共模型
     * @return 用户默认公共模型集合
     */
    public List<UserDefaultModel> selectUserDefaultModelList(UserDefaultModel userDefaultModel);

    /**
     * 新增用户默认公共模型
     * 
     * @param userDefaultModel 用户默认公共模型
     * @return 结果
     */
    public int insertUserDefaultModel(UserDefaultModel userDefaultModel);

    /**
     * 修改用户默认公共模型
     * 
     * @param userDefaultModel 用户默认公共模型
     * @return 结果
     */
    public int updateUserDefaultModel(UserDefaultModel userDefaultModel);

    /**
     * 批量删除用户默认公共模型
     * 
     * @param preferenceIds 需要删除的用户默认公共模型主键集合
     * @return 结果
     */
    public int deleteUserDefaultModelByPreferenceIds(Long[] preferenceIds);

    /**
     * 删除用户默认公共模型信息
     * 
     * @param preferenceId 用户默认公共模型主键
     * @return 结果
     */
    public int deleteUserDefaultModelByPreferenceId(Long preferenceId);
}
