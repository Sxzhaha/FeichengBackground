package com.ruoyi.binZhou.wr.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.wr.mapper.WarningTemplateRuleMapper;
import com.ruoyi.binZhou.wr.domain.WarningTemplateRule;
import com.ruoyi.binZhou.wr.service.IWarningTemplateRuleService;

/**
 * 预警模板规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
@Service
public class WarningTemplateRuleServiceImpl implements IWarningTemplateRuleService 
{
    @Autowired
    private WarningTemplateRuleMapper warningTemplateRuleMapper;

    /**
     * 查询预警模板规则
     * 
     * @param id 预警模板规则主键
     * @return 预警模板规则
     */
    @Override
    public WarningTemplateRule selectWarningTemplateRuleById(Long id)
    {
        return warningTemplateRuleMapper.selectWarningTemplateRuleById(id);
    }

    /**
     * 查询预警模板规则列表
     * 
     * @param warningTemplateRule 预警模板规则
     * @return 预警模板规则
     */
    @Override
    public List<WarningTemplateRule> selectWarningTemplateRuleList(WarningTemplateRule warningTemplateRule)
    {
        return warningTemplateRuleMapper.selectWarningTemplateRuleList(warningTemplateRule);
    }

    /**
     * 新增预警模板规则
     * 
     * @param warningTemplateRule 预警模板规则
     * @return 结果
     */
    @Override
    public int insertWarningTemplateRule(WarningTemplateRule warningTemplateRule)
    {
        return warningTemplateRuleMapper.insertWarningTemplateRule(warningTemplateRule);
    }

    /**
     * 修改预警模板规则
     * 
     * @param warningTemplateRule 预警模板规则
     * @return 结果
     */
    @Override
    public int updateWarningTemplateRule(WarningTemplateRule warningTemplateRule)
    {
        return warningTemplateRuleMapper.updateWarningTemplateRule(warningTemplateRule);
    }

    /**
     * 批量删除预警模板规则
     * 
     * @param ids 需要删除的预警模板规则主键
     * @return 结果
     */
    @Override
    public int deleteWarningTemplateRuleByIds(Long[] ids)
    {
        return warningTemplateRuleMapper.deleteWarningTemplateRuleByIds(ids);
    }

    /**
     * 删除预警模板规则信息
     * 
     * @param id 预警模板规则主键
     * @return 结果
     */
    @Override
    public int deleteWarningTemplateRuleById(Long id)
    {
        return warningTemplateRuleMapper.deleteWarningTemplateRuleById(id);
    }
}
