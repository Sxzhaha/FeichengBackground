package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.IotThingModel;

/**
 * 物模型定义Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
public interface IotThingModelMapper 
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
     * 删除物模型定义
     * 
     * @param modelId 物模型定义主键
     * @return 结果
     */
    public int deleteIotThingModelByModelId(Long modelId);

    /**
     * 批量删除物模型定义
     * 
     * @param modelIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIotThingModelByModelIds(Long[] modelIds);
}
