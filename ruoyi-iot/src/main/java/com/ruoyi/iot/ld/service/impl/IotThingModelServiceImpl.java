package com.ruoyi.iot.ld.service.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.IotThingModelMapper;
import com.ruoyi.iot.ld.domain.IotThingModel;
import com.ruoyi.iot.ld.service.IIotThingModelService;

/**
 * 物模型定义Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
@Service
public class IotThingModelServiceImpl implements IIotThingModelService 
{
    @Autowired
    private IotThingModelMapper iotThingModelMapper;

    /**
     * 查询物模型定义
     * 
     * @param modelId 物模型定义主键
     * @return 物模型定义
     */
    @Override
    public IotThingModel selectIotThingModelByModelId(Long modelId)
    {
        return iotThingModelMapper.selectIotThingModelByModelId(modelId);
    }

    /**
     * 查询物模型定义列表
     * 
     * @param iotThingModel 物模型定义
     * @return 物模型定义
     */
    @Override
    public List<IotThingModel> selectIotThingModelList(IotThingModel iotThingModel)
    {
        return iotThingModelMapper.selectIotThingModelList(iotThingModel);
    }

    /**
     * 新增物模型定义
     * 
     * @param iotThingModel 物模型定义
     * @return 结果
     */
    @Override
    public int insertIotThingModel(IotThingModel iotThingModel)
    {
        // 1. 构建查询条件
        IotThingModel search = new IotThingModel();
        search.setProductId(iotThingModel.getProductId());
        search.setIdentifier(iotThingModel.getIdentifier());

        // 2. 查询数据库是否存在
        List<IotThingModel> list = iotThingModelMapper.selectIotThingModelList(search);

        // 3. 如果存在，手动抛出业务异常
        if (list.size() > 0) {
            throw new ServiceException("该产品下已存在相同的标识符：" + iotThingModel.getIdentifier());
        }
        // 4. 不存在则插入
        return iotThingModelMapper.insertIotThingModel(iotThingModel);
    }

    /**
     * 修改物模型定义
     * 
     * @param iotThingModel 物模型定义
     * @return 结果
     */
    @Override
    public int updateIotThingModel(IotThingModel iotThingModel)
    {
        return iotThingModelMapper.updateIotThingModel(iotThingModel);
    }

    /**
     * 批量删除物模型定义
     * 
     * @param modelIds 需要删除的物模型定义主键
     * @return 结果
     */
    @Override
    public int deleteIotThingModelByModelIds(Long[] modelIds)
    {
        return iotThingModelMapper.deleteIotThingModelByModelIds(modelIds);
    }

    /**
     * 删除物模型定义信息
     * 
     * @param modelId 物模型定义主键
     * @return 结果
     */
    @Override
    public int deleteIotThingModelByModelId(Long modelId)
    {
        return iotThingModelMapper.deleteIotThingModelByModelId(modelId);
    }
}
