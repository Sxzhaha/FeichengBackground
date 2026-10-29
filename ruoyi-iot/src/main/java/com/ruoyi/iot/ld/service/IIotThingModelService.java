package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.IotThingModel;

/**
 * 物模型定义Service接口
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
public interface IIotThingModelService 
{
    /**
     * 查询物模型定义
     * 
     * @param modelId 物模型定义主键
     * @return 物模型定义
     */
    public IotThingModel selectIotThingModelByModelId(Long modelId);

    /**
     * 查询物模型定义列表
     * 
     * @param iotThingModel 物模型定义
     * @return 物模型定义集合
     */
    public List<IotThingModel> selectIotThingModelList(IotThingModel iotThingModel);

    /**
     * 新增物模型定义
     * 
     * @param iotThingModel 物模型定义
     * @return 结果
     */
    public int insertIotThingModel(IotThingModel iotThingModel);

    /**
     * 修改物模型定义
     * 
     * @param iotThingModel 物模型定义
     * @return 结果
     */
    public int updateIotThingModel(IotThingModel iotThingModel);

    /**
     * 批量删除物模型定义
     * 
     * @param modelIds 需要删除的物模型定义主键集合
     * @return 结果
     */
    public int deleteIotThingModelByModelIds(Long[] modelIds);

    /**
     * 删除物模型定义信息
     * 
     * @param modelId 物模型定义主键
     * @return 结果
     */
    public int deleteIotThingModelByModelId(Long modelId);
}
