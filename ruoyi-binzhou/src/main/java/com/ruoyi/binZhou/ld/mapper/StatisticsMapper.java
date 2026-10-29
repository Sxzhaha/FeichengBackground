package com.ruoyi.binZhou.ld.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 知识库统计Mapper接口
 * 
 * @author 宋瑞智
 */
public interface StatisticsMapper
{
    /** 获取某种节点类型的节点数 */
    int countNodesByType(String nodeType);

    /** 获取统一图谱某种类别的节点数 */
    int countUnifiedNodesByCategory(String category);

    /** 获取备用病虫害表的病害数量 */
    int countDiseaseAltNodes();

    /** 获取备用病虫害表的虫害数量 */
    int countPestAltNodes();

    /** 按作物分类统计品种数量 */
    List<Map<String, Object>> selectCropVarietyStatistics();

    /** 按类别统计病虫害数量 */
    List<Map<String, Object>> selectUnifiedCategoryStatistics();

    /** 获取更详尽的病虫害类别统计数据 */
    List<Map<String, Object>> selectUnifiedPestDiseaseStatistics();

    /** 获取备用表的病虫害类别统计数据 */
    List<Map<String, Object>> selectAltPestDiseaseStatistics();
}
