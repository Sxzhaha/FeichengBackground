package com.ruoyi.binZhou.xwq.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.binZhou.xwq.mapper.AiModelMapper;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.service.IAiModelService;

/**
 * 模型管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class AiModelServiceImpl implements IAiModelService 
{
    @Autowired
    private AiModelMapper aiModelMapper;

    /**
     * 查询模型管理
     * 
     * @param modelId 模型管理主键
     * @return 模型管理
     */
    @Override
    public AiModel selectAiModelByModelId(Long modelId)
    {
        return aiModelMapper.selectAiModelByModelId(modelId);
    }

    /**
     * 查询模型管理列表
     * 
     * @param aiModel 模型管理
     * @return 模型管理
     */
    @Override
    public List<AiModel> selectAiModelList(AiModel aiModel)
    {
        return aiModelMapper.selectAiModelList(aiModel);
    }

    /**
     * 新增模型管理
     * 
     * @param aiModel 模型管理
     * @return 结果
     */
    @Override
    public int insertAiModel(AiModel aiModel)
    {
        aiModel.setCreateBy(getCurrentUsername());
        aiModel.setCreateTime(DateUtils.getNowDate());
        return aiModelMapper.insertAiModel(aiModel);
    }

    /**
     * 修改模型管理
     * 
     * @param aiModel 模型管理
     * @return 结果
     */
    @Override
    public int updateAiModel(AiModel aiModel)
    {
        aiModel.setUpdateBy(getCurrentUsername());
        aiModel.setUpdateTime(DateUtils.getNowDate());
        return aiModelMapper.updateAiModel(aiModel);
    }

    /**
     * 批量删除模型管理
     * 
     * @param modelIds 需要删除的模型管理主键
     * @return 结果
     */
    @Override
    public int deleteAiModelByModelIds(Long[] modelIds)
    {
        return aiModelMapper.deleteAiModelByModelIds(modelIds);
    }

    /**
     * 删除模型管理信息
     * 
     * @param modelId 模型管理主键
     * @return 结果
     */
    @Override
    public int deleteAiModelByModelId(Long modelId)
    {
        return aiModelMapper.deleteAiModelByModelId(modelId);
    }

    /**
     * 获取当前操作人。
     * 登录请求使用当前用户名；自动任务或无登录上下文时使用 system。
     *
     * @return 操作人用户名
     */
    private String getCurrentUsername()
    {
        try
        {
            String username = SecurityUtils.getUsername();
            return username == null || username.trim().isEmpty()
                    ? "system"
                    : username;
        }
        catch (Exception e)
        {
            return "system";
        }
    }
}
