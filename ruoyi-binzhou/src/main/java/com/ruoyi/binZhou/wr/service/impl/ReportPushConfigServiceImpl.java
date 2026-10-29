package com.ruoyi.binZhou.wr.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.wr.mapper.ReportPushConfigMapper;
import com.ruoyi.binZhou.wr.domain.ReportPushConfig;
import com.ruoyi.binZhou.wr.service.IReportPushConfigService;

/**
 * 农情报推送配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-02
 */
@Service
public class ReportPushConfigServiceImpl implements IReportPushConfigService 
{
    @Autowired
    private ReportPushConfigMapper reportPushConfigMapper;

    /**
     * 查询农情报推送配置
     * 
     * @param configId 农情报推送配置主键
     * @return 农情报推送配置
     */
    @Override
    public ReportPushConfig selectReportPushConfigByConfigId(Long configId)
    {
        return reportPushConfigMapper.selectReportPushConfigByConfigId(configId);
    }

    /**
     * 查询农情报推送配置列表
     * 
     * @param reportPushConfig 农情报推送配置
     * @return 农情报推送配置
     */
    @Override
    public List<ReportPushConfig> selectReportPushConfigList(ReportPushConfig reportPushConfig)
    {
        return reportPushConfigMapper.selectReportPushConfigList(reportPushConfig);
    }

    /**
     * 新增农情报推送配置
     * 
     * @param reportPushConfig 农情报推送配置
     * @return 结果
     */
    @Override
    public int insertReportPushConfig(ReportPushConfig reportPushConfig)
    {
        reportPushConfig.setCreateTime(DateUtils.getNowDate());
        return reportPushConfigMapper.insertReportPushConfig(reportPushConfig);
    }

    /**
     * 修改农情报推送配置
     * 
     * @param reportPushConfig 农情报推送配置
     * @return 结果
     */
    @Override
    public int updateReportPushConfig(ReportPushConfig reportPushConfig)
    {
        reportPushConfig.setUpdateTime(DateUtils.getNowDate());
        return reportPushConfigMapper.updateReportPushConfig(reportPushConfig);
    }

    /**
     * 批量删除农情报推送配置
     * 
     * @param configIds 需要删除的农情报推送配置主键
     * @return 结果
     */
    @Override
    public int deleteReportPushConfigByConfigIds(Long[] configIds)
    {
        return reportPushConfigMapper.deleteReportPushConfigByConfigIds(configIds);
    }

    /**
     * 删除农情报推送配置信息
     * 
     * @param configId 农情报推送配置主键
     * @return 结果
     */
    @Override
    public int deleteReportPushConfigByConfigId(Long configId)
    {
        return reportPushConfigMapper.deleteReportPushConfigByConfigId(configId);
    }
}
