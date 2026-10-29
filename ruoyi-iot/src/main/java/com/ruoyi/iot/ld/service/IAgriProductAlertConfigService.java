package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.AgriProductAlertConfig;

/**
 * 物模型产品预警规则配置Service接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IAgriProductAlertConfigService 
{
    /**
     * 查询物模型产品预警规则配置
     * 
     * @param id 物模型产品预警规则配置主键
     * @return 物模型产品预警规则配置
     */
    public AgriProductAlertConfig selectAgriProductAlertConfigById(Long id);

    /**
     * 查询物模型产品预警规则配置列表
     * 
     * @param agriProductAlertConfig 物模型产品预警规则配置
     * @return 物模型产品预警规则配置集合
     */
    public List<AgriProductAlertConfig> selectAgriProductAlertConfigList(AgriProductAlertConfig agriProductAlertConfig);

    /**
     * 新增物模型产品预警规则配置
     * 
     * @param agriProductAlertConfig 物模型产品预警规则配置
     * @return 结果
     */
    public int insertAgriProductAlertConfig(AgriProductAlertConfig agriProductAlertConfig);

    /**
     * 修改物模型产品预警规则配置
     * 
     * @param agriProductAlertConfig 物模型产品预警规则配置
     * @return 结果
     */
    public int updateAgriProductAlertConfig(AgriProductAlertConfig agriProductAlertConfig);

    /**
     * 批量删除物模型产品预警规则配置
     * 
     * @param ids 需要删除的物模型产品预警规则配置主键集合
     * @return 结果
     */
    public int deleteAgriProductAlertConfigByIds(Long[] ids);

    /**
     * 删除物模型产品预警规则配置信息
     * 
     * @param id 物模型产品预警规则配置主键
     * @return 结果
     */
    public int deleteAgriProductAlertConfigById(Long id);
}
