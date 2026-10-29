package com.ruoyi.binZhou.ld.mapper;

import java.util.List;
import com.ruoyi.binZhou.ld.domain.Knowledge;
import com.ruoyi.binZhou.ld.domain.KnowledgeCategory;

/**
 * 病虫害百科Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-12
 */
public interface KnowledgeMapper 
{
    /**
     * 查询病虫害百科
     * 
     * @param id 病虫害百科主键
     * @return 病虫害百科
     */
    public Knowledge selectKnowledgeById(Long id);

    /**
     * 查询病虫害百科列表
     * 
     * @param knowledge 病虫害百科
     * @return 病虫害百科集合
     */
    public List<Knowledge> selectKnowledgeList(Knowledge knowledge);

    /**
     * 新增病虫害百科
     * 
     * @param knowledge 病虫害百科
     * @return 结果
     */
    public int insertKnowledge(Knowledge knowledge);

    /**
     * 修改病虫害百科
     * 
     * @param knowledge 病虫害百科
     * @return 结果
     */
    public int updateKnowledge(Knowledge knowledge);

    /**
     * 删除病虫害百科
     * 
     * @param id 病虫害百科主键
     * @return 结果
     */
    public int deleteKnowledgeById(Long id);

    /**
     * 批量删除病虫害百科
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKnowledgeByIds(Long[] ids);
    public List<KnowledgeCategory> findAllCategories();
    public String[] getKnowledgeMenuByLikeName(String name);
}
