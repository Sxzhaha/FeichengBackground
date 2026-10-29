package com.ruoyi.binZhou.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.ld.mapper.KnowledgeNodesMapper;
import com.ruoyi.binZhou.ld.domain.KnowledgeNodes;
import com.ruoyi.binZhou.ld.service.IKnowledgeNodesService;

import javax.annotation.Resource;

/**
 * 知识图谱节点Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-01
 */
@Service
public class KnowledgeNodesServiceImpl implements IKnowledgeNodesService
{
    @Resource
    private KnowledgeNodesMapper knowledgeNodesMapper;

    /**
     * 查询知识图谱节点
     * 
     * @param id 知识图谱节点主键
     * @return 知识图谱节点
     */
    @Override
    public KnowledgeNodes selectKnowledgeNodesById(Long id)
    {
        return knowledgeNodesMapper.selectKnowledgeNodesById(id);
    }

    /**
     * 查询知识图谱节点列表
     * 
     * @param knowledgeNodes 知识图谱节点
     * @return 知识图谱节点
     */
    @Override
    public List<KnowledgeNodes> selectKnowledgeNodesList(KnowledgeNodes knowledgeNodes)
    {
        return knowledgeNodesMapper.selectKnowledgeNodesList(knowledgeNodes);
    }

    /**
     * 新增知识图谱节点
     * 
     * @param knowledgeNodes 知识图谱节点
     * @return 结果
     */
    @Override
    public int insertKnowledgeNodes(KnowledgeNodes knowledgeNodes)
    {
        knowledgeNodes.setCreateTime(DateUtils.getNowDate());
        return knowledgeNodesMapper.insertKnowledgeNodes(knowledgeNodes);
    }

    /**
     * 修改知识图谱节点
     * 
     * @param knowledgeNodes 知识图谱节点
     * @return 结果
     */
    @Override
    public int updateKnowledgeNodes(KnowledgeNodes knowledgeNodes)
    {
        return knowledgeNodesMapper.updateKnowledgeNodes(knowledgeNodes);
    }

    /**
     * 批量删除知识图谱节点
     * 
     * @param ids 需要删除的知识图谱节点主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeNodesByIds(Long[] ids)
    {
        return knowledgeNodesMapper.deleteKnowledgeNodesByIds(ids);
    }

    /**
     * 删除知识图谱节点信息
     * 
     * @param id 知识图谱节点主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeNodesById(Long id)
    {
        return knowledgeNodesMapper.deleteKnowledgeNodesById(id);
    }
}
