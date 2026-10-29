package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.IotProduct;

/**
 * 产品Service接口
 * 
 * @author ruoyi
 * @date 2026-02-02
 */
public interface IIotProductService 
{
    /**
     * 查询产品
     * 
     * @param productId 产品主键
     * @return 产品
     */
    public IotProduct selectIotProductByProductId(Long productId);

    /**
     * 查询产品列表
     * 
     * @param iotProduct 产品
     * @return 产品集合
     */
    public List<IotProduct> selectIotProductList(IotProduct iotProduct);

    /**
     * 新增产品
     * 
     * @param iotProduct 产品
     * @return 结果
     */
    public int insertIotProduct(IotProduct iotProduct);

    /**
     * 修改产品
     * 
     * @param iotProduct 产品
     * @return 结果
     */
    public int updateIotProduct(IotProduct iotProduct);

    /**
     * 批量删除产品
     * 
     * @param productIds 需要删除的产品主键集合
     * @return 结果
     */
    public int deleteIotProductByProductIds(Long[] productIds);

    /**
     * 删除产品信息
     * 
     * @param productId 产品主键
     * @return 结果
     */
    public int deleteIotProductByProductId(Long productId);
}
