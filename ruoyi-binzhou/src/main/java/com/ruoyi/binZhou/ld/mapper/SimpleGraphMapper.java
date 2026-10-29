package com.ruoyi.binZhou.ld.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 知识图谱构建与查询Mapper接口
 * 
 * @author 宋瑞智
 */
public interface SimpleGraphMapper
{
    /** 清空知识图谱关系 */
    void clearKnowledgeRelations();

    /** 清空知识图谱节点 */
    void clearKnowledgeNodes();

    /** 生成作物分类节点 */
    int generateCropNodes();

    /** 基于品种数据生成品种节点 */
    int generateVarietyNodes();

    /** 获取所有品种的特征描述 */
    List<Map<String, Object>> selectCropVarietiesForFeatures();

    /** 获取最大的特征节点ID编号 */
    Integer getMaxFeatureId();

    /** 检查节点是否已存在 */
    int checkNodeExists(String nodeId);

    /** 插入特征节点 */
    int insertFeatureNode(@Param("nodeId") String nodeId, 
                          @Param("nodeName") String nodeName, 
                          @Param("parentId") String parentId, 
                          @Param("description") String description);

    /** 生成作物包含品种的关系 */
    int generateCropVarietyRelations();

    /** 生成品种具有特征的关系 */
    int generateVarietyFeatureRelations();

    /** 获取单个节点数据 */
    Map<String, Object> selectNodeById(String nodeId);

    /** 获取子节点列表 */
    List<Map<String, Object>> selectNodesByParentIdAndType(@Param("parentId") String parentId, 
                                                           @Param("nodeType") String nodeType, 
                                                           @Param("limit") Integer limit);

    /** 批量获取特征节点 */
    List<Map<String, Object>> selectNodesByParentIdsAndType(@Param("parentIds") List<String> parentIds, 
                                                            @Param("nodeType") String nodeType);

    /** 获取同类其他品种 */
    List<Map<String, Object>> selectSiblingVarietyNodes(@Param("parentId") String parentId, 
                                                        @Param("excludeNodeId") String excludeNodeId, 
                                                        @Param("limit") int limit);

    /** 获取品种节点和它的父作物节点 */
    List<Map<String, Object>> selectVarietyAndParentCropNodes(String centerNode);

    /** 获取关联的关系数据 */
    List<Map<String, Object>> selectGraphLinks(String centerNode);

    /** 获取搜索建议 */
    List<Map<String, Object>> selectSuggestions(String keyword);

    /** 根据名称查询一个品种 */
    Map<String, Object> selectOneCropVarietyByName(String keyword);

    /** 获取节点详情 */
    Map<String, Object> selectNodeDetail(String nodeId);
}
