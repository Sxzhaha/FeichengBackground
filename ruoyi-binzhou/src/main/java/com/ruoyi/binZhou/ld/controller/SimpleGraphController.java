package com.ruoyi.binZhou.ld.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.binZhou.ld.mapper.SimpleGraphMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JDK 8兼容版知识图谱构建控制器
 * @author 宋瑞智
 */
@Anonymous
@RestController
@RequestMapping("/binZhou/ld/knowledge/graph")
public class SimpleGraphController extends BaseController {

    @Autowired
    private SimpleGraphMapper simpleGraphMapper;

    /**
     * 清空知识图谱数据
     */
    @GetMapping("/clear")
    public AjaxResult clearKnowledgeGraph() {
        try {
            simpleGraphMapper.clearKnowledgeRelations();
            simpleGraphMapper.clearKnowledgeNodes();
            return success("知识图谱数据清空成功");
        } catch (Exception e) {
            e.printStackTrace();
            return error("清空失败: " + e.getMessage());
        }
    }

    /**
     * 重新构建知识图谱数据
     */
    @GetMapping("/rebuild")
    public AjaxResult rebuildKnowledgeGraph() {
        try {
            System.out.println("开始重新构建知识图谱...");

            // 1. 清空现有知识图谱数据
            simpleGraphMapper.clearKnowledgeRelations();
            simpleGraphMapper.clearKnowledgeNodes();

            // 2. 重新生成作物节点
            simpleGraphMapper.generateCropNodes();

            // 3. 基于真实品种数据生成品种节点
            simpleGraphMapper.generateVarietyNodes();

            // 4. 从品种特征中提取特征节点
            generateFeatureNodesFromDB();

            // 5. 生成关系数据
            simpleGraphMapper.generateCropVarietyRelations();
            simpleGraphMapper.generateVarietyFeatureRelations();

            System.out.println("知识图谱重建完成！");
            return success("知识图谱重建成功");

        } catch (Exception e) {
            e.printStackTrace();
            return error("知识图谱重建失败: " + e.getMessage());
        }
    }

    /**
     * 获取知识图谱数据
     */
    @GetMapping("/data")
    public AjaxResult getGraphData(@RequestParam(defaultValue = "crop_1") String centerNode) {
        try {
            Map<String, Object> result = new HashMap<String, Object>();

            // 获取节点数据
            List<Map<String, Object>> nodes = getGraphNodes(centerNode);

            // 获取关系数据
            List<Map<String, Object>> links = simpleGraphMapper.selectGraphLinks(centerNode);

            result.put("nodes", nodes);
            result.put("links", links);
            result.put("centerNode", centerNode);

            return success(result);

        } catch (Exception e) {
            e.printStackTrace();
            return error("获取知识图谱数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public AjaxResult getSearchSuggestions(@RequestParam String keyword) {
        try {
            List<Map<String, Object>> suggestions = new ArrayList<>();

            // 搜索作物
            if ("小麦".contains(keyword) || "xiaomai".contains(keyword.toLowerCase())) {
                Map<String, Object> crop = new HashMap<>();
                crop.put("id", "crop_1");
                crop.put("name", "小麦");
                crop.put("type", "crop");
                crop.put("description", "作物分类");
                suggestions.add(crop);
            }

            if ("玉米".contains(keyword) || "yumi".contains(keyword.toLowerCase())) {
                Map<String, Object> crop = new HashMap<>();
                crop.put("id", "crop_2");
                crop.put("name", "玉米");
                crop.put("type", "crop");
                crop.put("description", "作物分类");
                suggestions.add(crop);
            }

            // 搜索品种
            List<Map<String, Object>> dbSuggestions = simpleGraphMapper.selectSuggestions(keyword);
            for (Map<String, Object> row : dbSuggestions) {
                Map<String, Object> variety = new HashMap<>();
                variety.put("id", "variety_" + row.get("id"));
                variety.put("name", row.get("variety_name"));
                variety.put("type", "variety");
                variety.put("description", row.get("crop_name") + "品种");
                variety.put("categoryId", row.get("category_id"));
                suggestions.add(variety);
            }

            return success(suggestions);

        } catch (Exception e) {
            e.printStackTrace();
            return error("获取搜索建议失败: " + e.getMessage());
        }
    }

    /**
     * 搜索节点
     */
    @GetMapping("/search")
    public AjaxResult searchNodes(@RequestParam String keyword, @RequestParam(required = false) String nodeId) {
        try {
            String centerNode = null;
            String searchType = "crop";
            String actualNodeId = null;

            // 如果提供了具体的nodeId，直接使用
            if (nodeId != null && !nodeId.trim().isEmpty()) {
                centerNode = nodeId;
                actualNodeId = nodeId;

                if (nodeId.startsWith("crop_")) {
                    searchType = "crop";
                } else if (nodeId.startsWith("variety_")) {
                    searchType = "variety";
                }
            } else {
                // 原有的搜索逻辑
                if (keyword.contains("小麦")) {
                    centerNode = "crop_1";
                } else if (keyword.contains("玉米")) {
                    centerNode = "crop_2";
                } else {
                    // 搜索品种名称
                    Map<String, Object> variety = simpleGraphMapper.selectOneCropVarietyByName(keyword);
                    if (variety != null) {
                        Long varietyId = ((Number) variety.get("id")).longValue();
                        String varietyName = (String) variety.get("variety_name");

                        centerNode = "variety_" + varietyId;  // 以品种为中心
                        actualNodeId = centerNode;
                        searchType = "variety";

                        System.out.println("找到品种: " + varietyName + ", 节点ID: " + centerNode);
                    }
                }
            }

            if (centerNode != null) {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("centerNode", centerNode);
                result.put("keyword", keyword);
                result.put("searchType", searchType);
                result.put("actualNodeId", actualNodeId);
                return success(result);
            } else {
                return error("未找到相关节点，请搜索作物名称（如：小麦、玉米）或品种名称");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return error("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 获取节点详情
     */
    @GetMapping("/node/{nodeId}")
    public AjaxResult getNodeDetail(@PathVariable String nodeId) {
        try {
            Map<String, Object> result = simpleGraphMapper.selectNodeDetail(nodeId);
            return success(result);
        } catch (Exception e) {
            return error("获取节点详情失败: " + e.getMessage());
        }
    }

    // =================================
    // 私有方法实现
    // =================================

    private void generateFeatureNodesFromDB() {
        // 获取所有品种的特征描述
        List<Map<String, Object>> varieties = simpleGraphMapper.selectCropVarietiesForFeatures();

        // 先查询当前最大的特征ID，避免重复
        Integer maxId = simpleGraphMapper.getMaxFeatureId();
        int featureId = (maxId != null ? maxId : 0) + 1;
        int totalFeatures = 0;

        for (Map<String, Object> variety : varieties) {
            Long varietyId = ((Number) variety.get("id")).longValue();
            String varietyName = (String) variety.get("variety_name");
            String characteristics = (String) variety.get("characteristics");

            if (characteristics != null && !characteristics.trim().isEmpty()) {
                // 提取特征
                List<String> features = extractFeaturesFromText(characteristics);

                for (String feature : features) {
                    String nodeId = "feature_" + featureId++;
                    // 检查节点ID是否已存在，如果存在则跳过
                    if (simpleGraphMapper.checkNodeExists(nodeId) == 0) {
                        simpleGraphMapper.insertFeatureNode(nodeId, feature, "variety_" + varietyId, varietyName + "的特征");
                        totalFeatures++;
                    }
                }
            }
        }

        System.out.println("✓ 特征节点提取完成，共提取 " + totalFeatures + " 个特征");
    }

    private List<String> extractFeaturesFromText(String text) {
        List<String> features = new ArrayList<String>();

        // 定义特征提取模式
        Pattern[] patterns = {
                Pattern.compile("(春性|冬性|半冬性)[^，。；]*品种"),
                Pattern.compile("(早熟|中熟|晚熟)[^，。；]*"),
                Pattern.compile("(高抗|中抗|抗|感|高感)([^，。；]*)(病|虫害)"),
                Pattern.compile("抗寒性(较好|好|强|较强|一般)"),
                Pattern.compile("抗倒性(较好|好|强|较强|一般)"),
                Pattern.compile("株高[\\d\\.]+.*?厘米"),
                Pattern.compile("(纺锤型|长方形|椭圆形)穗"),
                Pattern.compile("(长芒|短芒|无芒)"),
                Pattern.compile("(白壳|红壳)"),
                Pattern.compile("(白粒|红粒)"),
                Pattern.compile("籽粒(硬质|半硬质|软质)"),
                Pattern.compile("粗蛋白.*?[\\d\\.]+%"),
                Pattern.compile("千粒重[\\d\\.]+.*?克")
        };

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String feature = matcher.group().trim();
                if (feature.length() > 2 && feature.length() < 30) {
                    features.add(feature);
                }
            }
        }

        // 如果没有提取到特征，添加一个默认特征
        if (features.isEmpty()) {
            features.add("待分析特征");
        }

        return features;
    }

    private List<Map<String, Object>> getGraphNodes(String centerNode) {
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();

        if (centerNode.startsWith("crop_")) {
            // 第一步：获取中心作物节点
            Map<String, Object> centerMap = simpleGraphMapper.selectNodeById(centerNode);
            if (centerMap != null) {
                nodes.add(centerMap);
            }

            // 第二步：获取限量品种节点（最多8个）
            List<Map<String, Object>> varietyNodes = simpleGraphMapper.selectNodesByParentIdAndType(centerNode, "variety", 8);
            nodes.addAll(varietyNodes);

            List<String> varietyIds = new ArrayList<>();
            for (Map<String, Object> node : varietyNodes) {
                varietyIds.add((String) node.get("id"));
            }

            // 第三步：获取这些品种的特征节点
            if (!varietyIds.isEmpty()) {
                List<Map<String, Object>> featureNodes = simpleGraphMapper.selectNodesByParentIdsAndType(varietyIds, "feature");
                nodes.addAll(featureNodes);
            }

        } else if (centerNode.startsWith("variety_")) {
            // 第一步：获取中心品种节点和其父作物
            List<Map<String, Object>> hierarchyNodes = simpleGraphMapper.selectVarietyAndParentCropNodes(centerNode);
            nodes.addAll(hierarchyNodes);

            String parentCropId = null;
            for (Map<String, Object> node : hierarchyNodes) {
                if ("variety".equals(node.get("type"))) {
                    parentCropId = (String) node.get("parent_id");
                }
            }

            // 第二步：获取同类其他品种（最多6个，排除中心品种）
            if (parentCropId != null) {
                List<Map<String, Object>> siblingNodes = simpleGraphMapper.selectSiblingVarietyNodes(parentCropId, centerNode, 6);
                nodes.addAll(siblingNodes);
            }

            // 第三步：获取中心品种的特征节点
            List<Map<String, Object>> featureNodes = simpleGraphMapper.selectNodesByParentIdAndType(centerNode, "feature", null);
            nodes.addAll(featureNodes);

        } else {
            // 默认：返回crop_1
            return getGraphNodes("crop_1");
        }

        System.out.println("查询到节点数量: " + nodes.size() + ", 中心节点: " + centerNode);
        return nodes;
    }
}