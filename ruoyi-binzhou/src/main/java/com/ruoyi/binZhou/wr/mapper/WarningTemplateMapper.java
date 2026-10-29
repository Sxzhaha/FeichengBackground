package com.ruoyi.binZhou.wr.mapper;

import java.util.List;
import com.ruoyi.binZhou.wr.domain.WarningTemplate;

/**
 * 预警模板Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
public interface WarningTemplateMapper 
{
    /**
     * 查询预警模板
     * 
     * @param id 预警模板主键
     * @return 预警模板
     */
    public WarningTemplate selectWarningTemplateById(Long id);

    /**
     * 查询预警模板列表
     * 
     * @param warningTemplate 预警模板
     * @return 预警模板集合
     */
    public List<WarningTemplate> selectWarningTemplateList(WarningTemplate warningTemplate);

    /**
     * 新增预警模板
     * 
     * @param warningTemplate 预警模板
     * @return 结果
     */
    public int insertWarningTemplate(WarningTemplate warningTemplate);

    /**
     * 修改预警模板
     * 
     * @param warningTemplate 预警模板
     * @return 结果
     */
    public int updateWarningTemplate(WarningTemplate warningTemplate);

    /**
     * 删除预警模板
     * 
     * @param id 预警模板主键
     * @return 结果
     */
    public int deleteWarningTemplateById(Long id);

    /**
     * 批量删除预警模板
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWarningTemplateByIds(Long[] ids);
}
