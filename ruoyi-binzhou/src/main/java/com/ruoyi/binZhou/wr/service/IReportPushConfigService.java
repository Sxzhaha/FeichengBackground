package com.ruoyi.binZhou.wr.service;

import java.util.List;
import com.ruoyi.binZhou.wr.domain.ReportPushConfig;

/**
 * 农情报推送配置Service接口
 * 
 * @author ruoyi
 * @date 2026-07-02
 */
public interface IReportPushConfigService 
{
    /**
     * 查询农情报推送配置
     * 
     * @param configId 农情报推送配置主键
     * @return 农情报推送配置
     */
    public ReportPushConfig selectReportPushConfigByConfigId(Long configId);

    /**
     * 查询农情报推送配置列表
     * 
     * @param reportPushConfig 农情报推送配置
     * @return 农情报推送配置集合
     */
    public List<ReportPushConfig> selectReportPushConfigList(ReportPushConfig reportPushConfig);

    /**
     * 新增农情报推送配置
     * 
     * @param reportPushConfig 农情报推送配置
     * @return 结果
     */
    public int insertReportPushConfig(ReportPushConfig reportPushConfig);

    /**
     * 修改农情报推送配置
     * 
     * @param reportPushConfig 农情报推送配置
     * @return 结果
     */
    public int updateReportPushConfig(ReportPushConfig reportPushConfig);

    /**
     * 批量删除农情报推送配置
     * 
     * @param configIds 需要删除的农情报推送配置主键集合
     * @return 结果
     */
    public int deleteReportPushConfigByConfigIds(Long[] configIds);

    /**
     * 删除农情报推送配置信息
     * 
     * @param configId 农情报推送配置主键
     * @return 结果
     */
    public int deleteReportPushConfigByConfigId(Long configId);
}
