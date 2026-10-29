package com.ruoyi.binZhou.ld.mapper;

import java.util.List;
import com.ruoyi.binZhou.ld.domain.KnowledgeNodes;

/**
 * 知识图谱节点Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-01
 */
public interface KnowledgeNodesMapper
{
    /**
     * 查询知识图谱节点
     * 
     * @param id 知识图谱节点主键
     * @return 知识图谱节点
     */
    public KnowledgeNodes selectKnowledgeNodesById(Long id);

    /**
     * 查询知识图谱节点列表
     * 
     * @param knowledgeNodes 知识图谱节点
     * @return 知识图谱节点集合
     */
    public List<KnowledgeNodes> selectKnowledgeNodesList(KnowledgeNodes knowledgeNodes);

    /**
     * 新增知识图谱节点
     * 
     * @param knowledgeNodes 知识图谱节点
     * @return 结果
     */
    public int insertKnowledgeNodes(KnowledgeNodes knowledgeNodes);

    /**
     * 修改知识图谱节点
     * 
     * @param knowledgeNodes 知识图谱节点
     * @return 结果
     */
    public int updateKnowledgeNodes(KnowledgeNodes knowledgeNodes);

    /**
     * 删除知识图谱节点
     * 
     * @param id 知识图谱节点主键
     * @return 结果
     */
    public int deleteKnowledgeNodesById(Long id);

    /**
     * 批量删除知识图谱节点
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKnowledgeNodesByIds(Long[] ids);
}
