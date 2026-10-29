package com.ruoyi.binZhou.xwq.mapper;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import org.springframework.data.repository.query.Param;

/**
 * 模型管理Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface AiModelMapper 
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
     * 删除模型管理
     * 
     * @param modelId 模型管理主键
     * @return 结果
     */
    public int deleteAiModelByModelId(Long modelId);

    /**
     * 批量删除模型管理
     * 
     * @param modelIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiModelByModelIds(Long[] modelIds);

    /**
     * 根据模型类型查询默认启用模型
     */
    public AiModel selectDefaultModelByType(@Param("modelType") String modelType);
}
