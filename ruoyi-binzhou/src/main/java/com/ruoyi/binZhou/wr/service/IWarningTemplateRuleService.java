package com.ruoyi.binZhou.wr.service;

import java.util.List;
import com.ruoyi.binZhou.wr.domain.WarningTemplateRule;

/**
 * 预警模板规则Service接口
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
public interface IWarningTemplateRuleService 
{
    /**
     * 查询预警模板规则
     * 
     * @param id 预警模板规则主键
     * @return 预警模板规则
     */
    public WarningTemplateRule selectWarningTemplateRuleById(Long id);

    /**
     * 查询预警模板规则列表
     * 
     * @param warningTemplateRule 预警模板规则
     * @return 预警模板规则集合
     */
    public List<WarningTemplateRule> selectWarningTemplateRuleList(WarningTemplateRule warningTemplateRule);

    /**
     * 新增预警模板规则
     * 
     * @param warningTemplateRule 预警模板规则
     * @return 结果
     */
    public int insertWarningTemplateRule(WarningTemplateRule warningTemplateRule);

    /**
     * 修改预警模板规则
     * 
     * @param warningTemplateRule 预警模板规则
     * @return 结果
     */
    public int updateWarningTemplateRule(WarningTemplateRule warningTemplateRule);

    /**
     * 批量删除预警模板规则
     * 
     * @param ids 需要删除的预警模板规则主键集合
     * @return 结果
     */
    public int deleteWarningTemplateRuleByIds(Long[] ids);

    /**
     * 删除预警模板规则信息
     * 
     * @param id 预警模板规则主键
     * @return 结果
     */
    public int deleteWarningTemplateRuleById(Long id);
}
