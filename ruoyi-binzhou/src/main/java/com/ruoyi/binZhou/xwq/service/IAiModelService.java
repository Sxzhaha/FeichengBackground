package com.ruoyi.binZhou.xwq.service;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.AiModel;

/**
 * 模型管理Service接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IAiModelService 
{
    /**
     * 查询模型管理
     * 
     * @param modelId 模型管理主键
     * @return 模型管理
     */
    public AiModel selectAiModelByModelId(Long modelId);

    /**
     * 查询模型管理列表
     * 
     * @param aiModel 模型管理
     * @return 模型管理集合
     */
    public List<AiModel> selectAiModelList(AiModel aiModel);

    /**
     * 新增模型管理
     * 
     * @param aiModel 模型管理
     * @return 结果
     */
    public int insertAiModel(AiModel aiModel);

    /**
     * 修改模型管理
     * 
     * @param aiModel 模型管理
     * @return 结果
     */
    public int updateAiModel(AiModel aiModel);

    /**
     * 批量删除模型管理
     * 
     * @param modelIds 需要删除的模型管理主键集合
     * @return 结果
     */
    public int deleteAiModelByModelIds(Long[] modelIds);

    /**
     * 删除模型管理信息
     * 
     * @param modelId 模型管理主键
     * @return 结果
     */
    public int deleteAiModelByModelId(Long modelId);
}
