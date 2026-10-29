package com.ruoyi.binZhou.wr.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.wr.mapper.WarningTemplateMapper;
import com.ruoyi.binZhou.wr.domain.WarningTemplate;
import com.ruoyi.binZhou.wr.service.IWarningTemplateService;

/**
 * 预警模板Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
@Service
public class WarningTemplateServiceImpl implements IWarningTemplateService 
{
    @Autowired
    private WarningTemplateMapper warningTemplateMapper;

    /**
     * 查询预警模板
     * 
     * @param id 预警模板主键
     * @return 预警模板
     */
    @Override
    public WarningTemplate selectWarningTemplateById(Long id)
    {
        return warningTemplateMapper.selectWarningTemplateById(id);
    }

    /**
     * 查询预警模板列表
     * 
     * @param warningTemplate 预警模板
     * @return 预警模板
     */
    @Override
    public List<WarningTemplate> selectWarningTemplateList(WarningTemplate warningTemplate)
    {
        return warningTemplateMapper.selectWarningTemplateList(warningTemplate);
    }

    /**
     * 新增预警模板
     * 
     * @param warningTemplate 预警模板
     * @return 结果
     */
    @Override
    public int insertWarningTemplate(WarningTemplate warningTemplate)
    {
        warningTemplate.setCreateTime(DateUtils.getNowDate());
        return warningTemplateMapper.insertWarningTemplate(warningTemplate);
    }

    /**
     * 修改预警模板
     * 
     * @param warningTemplate 预警模板
     * @return 结果
     */
    @Override
    public int updateWarningTemplate(WarningTemplate warningTemplate)
    {
        warningTemplate.setUpdateTime(DateUtils.getNowDate());
        return warningTemplateMapper.updateWarningTemplate(warningTemplate);
    }

    /**
     * 批量删除预警模板
     * 
     * @param ids 需要删除的预警模板主键
     * @return 结果
     */
    @Override
    public int deleteWarningTemplateByIds(Long[] ids)
    {
        return warningTemplateMapper.deleteWarningTemplateByIds(ids);
    }

    /**
     * 删除预警模板信息
     * 
     * @param id 预警模板主键
     * @return 结果
     */
    @Override
    public int deleteWarningTemplateById(Long id)
    {
        return warningTemplateMapper.deleteWarningTemplateById(id);
    }
}
