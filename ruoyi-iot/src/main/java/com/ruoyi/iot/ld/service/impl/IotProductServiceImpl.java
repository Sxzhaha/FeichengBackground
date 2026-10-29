package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.IotProductMapper;
import com.ruoyi.iot.ld.domain.IotProduct;
import com.ruoyi.iot.ld.service.IIotProductService;

/**
 * 产品Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
@Service
public class IotProductServiceImpl implements IIotProductService 
{
    @Autowired
    private IotProductMapper iotProductMapper;

    /**
     * 查询产品
     * 
     * @param productId 产品主键
     * @return 产品
     */
    @Override
    public IotProduct selectIotProductByProductId(Long productId)
    {
        return iotProductMapper.selectIotProductByProductId(productId);
    }

    /**
     * 查询产品列表
     * 
     * @param iotProduct 产品
     * @return 产品
     */
    @Override
    public List<IotProduct> selectIotProductList(IotProduct iotProduct)
    {
        return iotProductMapper.selectIotProductList(iotProduct);
    }

    /**
     * 新增产品
     * 
     * @param iotProduct 产品
     * @return 结果
     */
    @Override
    public int insertIotProduct(IotProduct iotProduct)
    {
        return iotProductMapper.insertIotProduct(iotProduct);
    }

    /**
     * 修改产品
     * 
     * @param iotProduct 产品
     * @return 结果
     */
    @Override
    public int updateIotProduct(IotProduct iotProduct)
    {
        return iotProductMapper.updateIotProduct(iotProduct);
    }

    /**
     * 批量删除产品
     * 
     * @param productIds 需要删除的产品主键
     * @return 结果
     */
    @Override
    public int deleteIotProductByProductIds(Long[] productIds)
    {
        return iotProductMapper.deleteIotProductByProductIds(productIds);
    }

    /**
     * 删除产品信息
     * 
     * @param productId 产品主键
     * @return 结果
     */
    @Override
    public int deleteIotProductByProductId(Long productId)
    {
        return iotProductMapper.deleteIotProductByProductId(productId);
    }
}
