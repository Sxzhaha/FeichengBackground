package com.ruoyi.binZhou.ld.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 统一知识图谱查询Mapper接口
 * 
 * @author 宋瑞智
 */
public interface UnifiedGraphMapper
{
    /** 根据节点名称查询父节点ID */
    String selectParentIdByNodeName(String nodeName);

    /** 根据节点ID查询父节点ID */
    String selectParentIdByNodeId(String nodeId);

    /** 获取统一图谱节点数据 */
    Map<String, Object> selectUnifiedNodeById(String nodeId);

    /** 获取层级节点列表 */
    List<Map<String, Object>> selectUnifiedNodesByParentIdAndLevel(@Param("parentId") String parentId, 
                                                                   @Param("level") int level);

    /** 查找指定具体L3节点 */
    Map<String, Object> selectSpecificL3Node(@Param("parentId") String parentId, 
                                             @Param("nodeName") String nodeName);

    /** 获取其他L3随机节点 */
    List<Map<String, Object>> selectOtherL3Nodes(@Param("parentId") String parentId, 
                                                 @Param("excludeNodeName") String excludeNodeName, 
                                                 @Param("limit") int limit);

    /** 获取默认L3随机节点 */
    List<Map<String, Object>> selectDefaultL3Nodes(@Param("parentId") String parentId, 
                                                   @Param("limit") int limit);

    /** 检查节点是否有子节点 */
    int countChildNodes(String parentId);

    /** 获取第四级子节点（具体特征/详情项） */
    List<Map<String, Object>> selectUnifiedChildren(String parentId);

    /** 获取统一图谱子节点的关系 */
    List<Map<String, Object>> selectUnifiedChildrenLinks(String parentId);

    /** 获取搜索建议 */
    List<Map<String, Object>> selectUnifiedSuggestions(String keyword);

    /** 搜索定位节点 */
    Map<String, Object> searchUnifiedNodes(String keyword);

    /** 获取节点详情 */
    Map<String, Object> selectUnifiedNodeDetail(String nodeId);

    /** 获取L1到L2的关系列表 */
    List<Map<String, Object>> selectL1ToL2Links(@Param("centerNode") String centerNode);

    /** 获取L2到L3的关系列表 */
    List<Map<String, Object>> selectL2ToL3Links(@Param("centerNode") String centerNode);
}
