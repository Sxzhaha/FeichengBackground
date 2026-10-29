package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.AgriProductAlertConfigMapper;
import com.ruoyi.iot.ld.domain.AgriProductAlertConfig;
import com.ruoyi.iot.ld.service.IAgriProductAlertConfigService;

/**
 * 物模型产品预警规则配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class AgriProductAlertConfigServiceImpl implements IAgriProductAlertConfigService 
{
    @Autowired
    private AgriProductAlertConfigMapper agriProductAlertConfigMapper;

    /**
     * 查询物模型产品预警规则配置
     * 
     * @param id 物模型产品预警规则配置主键
     * @return 物模型产品预警规则配置
     */
    @Override
    public AgriProductAlertConfig selectAgriProductAlertConfigById(Long id)
    {
        return agriProductAlertConfigMapper.selectAgriProductAlertConfigById(id);
    }

    /**
     * 查询物模型产品预警规则配置列表
     * 
     * @param agriProductAlertConfig 物模型产品预警规则配置
     * @return 物模型产品预警规则配置
     */
    @Override
    public List<AgriProductAlertConfig> selectAgriProductAlertConfigList(AgriProductAlertConfig agriProductAlertConfig)
    {
        return agriProductAlertConfigMapper.selectAgriProductAlertConfigList(agriProductAlertConfig);
    }

    /**
     * 新增物模型产品预警规则配置
     * 
     * @param agriProductAlertConfig 物模型产品预警规则配置
     * @return 结果
     */
    @Override
    public int insertAgriProductAlertConfig(AgriProductAlertConfig agriProductAlertConfig)
    {
        try {
            agriProductAlertConfig.setCreator(SecurityUtils.getUserId());
        } catch (Exception e) {
            // 登录态异常时安全容错，防止系统级调用报错
        }
        agriProductAlertConfig.setCreateTime(DateUtils.getNowDate());
        return agriProductAlertConfigMapper.insertAgriProductAlertConfig(agriProductAlertConfig);
    }

    /**
     * 修改物模型产品预警规则配置
     * 
     * @param agriProductAlertConfig 物模型产品预警规则配置
     * @return 结果
     */
    @Override
    public int updateAgriProductAlertConfig(AgriProductAlertConfig agriProductAlertConfig)
    {
        return agriProductAlertConfigMapper.updateAgriProductAlertConfig(agriProductAlertConfig);
    }

    /**
     * 批量删除物模型产品预警规则配置
     * 
     * @param ids 需要删除的物模型产品预警规则配置主键
     * @return 结果
     */
    @Override
    public int deleteAgriProductAlertConfigByIds(Long[] ids)
    {
        return agriProductAlertConfigMapper.deleteAgriProductAlertConfigByIds(ids);
    }

    /**
     * 删除物模型产品预警规则配置信息
     * 
     * @param id 物模型产品预警规则配置主键
     * @return 结果
     */
    @Override
    public int deleteAgriProductAlertConfigById(Long id)
    {
        return agriProductAlertConfigMapper.deleteAgriProductAlertConfigById(id);
    }
}
