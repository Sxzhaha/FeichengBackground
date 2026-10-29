package com.ruoyi.binZhou.ld.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.binZhou.ld.mapper.UnifiedGraphMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 优化的统一知识图谱控制器 - 支持第四级展开和布局优化
 *
 * @author 宋瑞智
 */
@Anonymous
@RestController
@RequestMapping("/binZhou/ld/knowledge/unified")
public class UnifiedGraphController extends BaseController {

    @Autowired
    private UnifiedGraphMapper unifiedGraphMapper;

    // 🔥 每种类型显示的L3节点数量
    private static final int NODES_PER_TYPE = 3;

    /**
     * 🔥 获取统一知识图谱数据 - 默认展开到第三级，优化布局
     */
    @GetMapping("/data")
    public AjaxResult getUnifiedGraphData(@RequestParam(defaultValue = "crop_1") String centerNode) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 获取节点数据 - 默认展开到第三级，包含预设位置
            List<Map<String, Object>> nodes = getUnifiedGraphNodesWithOptimizedLayout(centerNode);
            // 获取关系数据
            List<Map<String, Object>> links = getUnifiedGraphLinks(centerNode);

            result.put("nodes", nodes);
            result.put("links", links);
            result.put("centerNode", centerNode);

            return success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取统一知识图谱数据失败: " + e.getMessage());
        }
    }

    /**
     * 🔥 新增：获取包含指定节点的知识图谱数据（用于详情页）
     */
    @GetMapping("/dataWithSpecificNode")
    public AjaxResult getGraphDataWithSpecificNode(
            @RequestParam(defaultValue = "crop_1") String centerNode,
            @RequestParam String specificNodeName,
            @RequestParam(required = false) String nodeType) {

        try {
            Map<String, Object> result = new HashMap<>();

            // 🔥 1. 确定中心节点（根据specificNodeName判断是小麦还是玉米）
            String actualCenterNode = determineCenterNodeByName(specificNodeName);
            if (actualCenterNode == null) {
                actualCenterNode = centerNode; // fallback到默认值
            }

            // 🔥 2. 获取包含指定节点的图谱数据
            List<Map<String, Object>> nodes = getGraphNodesWithSpecificNode(actualCenterNode, specificNodeName, nodeType);
            List<Map<String, Object>> links = getUnifiedGraphLinks(actualCenterNode);

            result.put("nodes", nodes);
            result.put("links", links);
            result.put("centerNode", actualCenterNode);
            result.put("specificNode", specificNodeName);

            System.out.println("生成包含指定节点的图谱: 中心=" + actualCenterNode + ", 指定节点=" + specificNodeName);

            return success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取指定节点图谱数据失败: " + e.getMessage());
        }
    }

    /**
     * 🔥 根据节点名称确定中心节点（小麦还是玉米）
     */
    private String determineCenterNodeByName(String nodeName) {
        // 查找该节点属于哪个作物
        String parentId = unifiedGraphMapper.selectParentIdByNodeName(nodeName);
        if (parentId != null) {
            // 如果parent_id包含crop信息，直接返回
            if (parentId.startsWith("crop_")) {
                return parentId;
            }
            // 否则继续向上查找
            return findRootCropNode(parentId);
        }

        // 根据名称模糊匹配
        if (nodeName.contains("小麦")) {
            return "crop_1"; // 小麦
        } else if (nodeName.contains("玉米")) {
            return "crop_2"; // 玉米
        }

        return "crop_1"; // 默认小麦
    }

    /**
     * 🔥 递归查找根作物节点
     */
    private String findRootCropNode(String nodeId) {
        if (nodeId == null || nodeId.startsWith("crop_")) {
            return nodeId;
        }

        String parentId = unifiedGraphMapper.selectParentIdByNodeId(nodeId);
        if (parentId != null) {
            return findRootCropNode(parentId);
        }

        return "crop_1"; // 默认
    }

    /**
     * 🔥 获取包含指定节点的图谱节点数据
     */
    private List<Map<String, Object>> getGraphNodesWithSpecificNode(String centerNode,
                                                                    String specificNodeName, String nodeType) {

        List<Map<String, Object>> nodes = new ArrayList<>();

        // 🔥 第一步：获取L1作物节点
        Map<String, Object> l1Node = unifiedGraphMapper.selectUnifiedNodeById(centerNode);
        if (l1Node != null) {
            Map<String, Object> node = new HashMap<>(l1Node);
            // 设置中心位置
            node.put("fx", 400.0);
            node.put("fy", 300.0);
            node.put("fixed", true);
            nodes.add(node);
        }

        // 🔥 第二步：获取L2类型节点
        List<Map<String, Object>> l2Nodes = unifiedGraphMapper.selectUnifiedNodesByParentIdAndLevel(centerNode, 2);
        List<String> typeNodeIds = new ArrayList<>();
        for (Map<String, Object> node : l2Nodes) {
            typeNodeIds.add((String) node.get("id"));
        }

        // 设置L2节点环形布局
        setL2NodesLayout(l2Nodes);
        nodes.addAll(l2Nodes);

        // 🔥 第三步：为每个L2类型节点获取L3具体项，确保包含指定节点
        for (int typeIndex = 0; typeIndex < typeNodeIds.size(); typeIndex++) {
            String typeNodeId = typeNodeIds.get(typeIndex);

            List<Map<String, Object>> l3NodesForType = getL3NodesWithSpecific(
                    typeNodeId, specificNodeName, nodeType, typeIndex, l2Nodes.get(typeIndex));

            nodes.addAll(l3NodesForType);
        }

        return nodes;
    }

    /**
     * 🔥 获取L3节点，确保包含指定节点
     */
    private List<Map<String, Object>> getL3NodesWithSpecific(String typeNodeId,
                                                             String specificNodeName, String nodeType, int typeIndex, Map<String, Object> l2Node) {

        List<Map<String, Object>> l3Nodes = new ArrayList<>();

        // 🔥 首先尝试查找指定节点
        Map<String, Object> specificNode = unifiedGraphMapper.selectSpecificL3Node(typeNodeId, specificNodeName);
        boolean foundSpecificNode = false;
        if (specificNode != null) {
            Map<String, Object> node = new HashMap<>(specificNode);
            node.put("isSpecific", true); // 标记为指定节点
            l3Nodes.add(node);
            foundSpecificNode = true;
        }

        // 🔥 然后获取其他随机节点（总共3个，已有1个指定节点，还需要2个）
        int remainingCount = foundSpecificNode ? 2 : 3;
        List<Map<String, Object>> otherNodes = unifiedGraphMapper.selectOtherL3Nodes(typeNodeId, specificNodeName, remainingCount);

        for (Map<String, Object> rawNode : otherNodes) {
            Map<String, Object> node = new HashMap<>(rawNode);
            node.put("hasChildren", unifiedGraphMapper.countChildNodes((String) rawNode.get("id")) > 0);
            node.put("expanded", false);
            l3Nodes.add(node);
        }

        // 🔥 设置L3节点的扇形布局
        setL3NodesLayout(l3Nodes, l2Node, typeIndex);

        return l3Nodes;
    }

    /**
     * 🔥 设置L2节点环形布局
     */
    private void setL2NodesLayout(List<Map<String, Object>> l2Nodes) {
        double centerX = 400.0;
        double centerY = 300.0;
        double radius = 150.0;
        int l2Count = l2Nodes.size();

        for (int i = 0; i < l2Count; i++) {
            Map<String, Object> node = l2Nodes.get(i);
            double angle = (2 * Math.PI * i) / l2Count - Math.PI / 2;

            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            node.put("fx", x);
            node.put("fy", y);
            node.put("fixed", true);
        }
    }

    /**
     * 🔥 设置L3节点扇形布局
     */
    private void setL3NodesLayout(List<Map<String, Object>> l3Nodes, Map<String, Object> l2Node, int typeIndex) {
        if (l3Nodes.isEmpty()) return;

        double centerX = 400.0;
        double centerY = 300.0;
        double l2X = (Double) l2Node.get("fx");
        double l2Y = (Double) l2Node.get("fy");
        double l3Radius = 100.0;
        int l3Count = l3Nodes.size();

        for (int i = 0; i < l3Count; i++) {
            Map<String, Object> l3Node = l3Nodes.get(i);

            // 计算相对于L2节点的扇形角度
            double baseAngle = Math.atan2(l2Y - centerY, l2X - centerX);
            double spreadAngle = Math.PI / 3; // 60度扇形
            double startAngle = baseAngle - spreadAngle / 2;
            double angle = startAngle + (spreadAngle * i) / Math.max(1, l3Count - 1);

            double x = l2X + l3Radius * Math.cos(angle);
            double y = l2Y + l3Radius * Math.sin(angle);

            l3Node.put("fx", x);
            l3Node.put("fy", y);
            l3Node.put("fixed", true);

            // 🔥 如果是指定节点，添加特殊标记
            if (Boolean.TRUE.equals(l3Node.get("isSpecific"))) {
                l3Node.put("highlight", true);
            }
        }
    }

    /**
     * 🔥 新增：获取第四级子节点（品种特征）
     */
    @GetMapping("/children/{parentNodeId}")
    public AjaxResult getNodeChildren(@PathVariable String parentNodeId) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 获取子节点数据
            List<Map<String, Object>> children = unifiedGraphMapper.selectUnifiedChildren(parentNodeId);
            // 获取关系数据
            List<Map<String, Object>> links = unifiedGraphMapper.selectUnifiedChildrenLinks(parentNodeId);

            result.put("nodes", children);
            result.put("links", links);
            result.put("parentNodeId", parentNodeId);

            System.out.println("获取子节点，父节点: " + parentNodeId + ", 子节点数量: " + children.size());

            return success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取子节点失败: " + e.getMessage());
        }
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public AjaxResult getUnifiedSearchSuggestions(@RequestParam String keyword) {
        try {
            List<Map<String, Object>> suggestions = new ArrayList<>();
            List<Map<String, Object>> dbSuggestions = unifiedGraphMapper.selectUnifiedSuggestions(keyword);

            for (Map<String, Object> row : dbSuggestions) {
                String nodeType = (String) row.get("nodeType");
                String description = getNodeTypeDescription(nodeType, (String) row.get("category"));

                suggestions.add(createSuggestion(
                        (String) row.get("nodeId"),
                        (String) row.get("nodeName"),
                        nodeType,
                        description
                ));
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
    public AjaxResult searchUnifiedNodes(@RequestParam String keyword, @RequestParam(required = false) String nodeId) {
        try {
            String centerNode = null;
            String searchType = "crop";

            if (nodeId != null && !nodeId.trim().isEmpty()) {
                centerNode = nodeId;
                searchType = determineNodeType(nodeId);
            } else {
                Map<String, Object> match = unifiedGraphMapper.searchUnifiedNodes(keyword);
                if (match != null) {
                    centerNode = (String) match.get("centerNode");
                    searchType = (String) match.get("searchType");
                }
            }

            if (centerNode != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("centerNode", centerNode);
                result.put("keyword", keyword);
                result.put("searchType", searchType);
                return success(result);
            } else {
                return error("未找到相关节点，请搜索作物名称、品种名称或病虫害名称");
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
            Map<String, Object> result = unifiedGraphMapper.selectUnifiedNodeDetail(nodeId);
            return success(result);
        } catch (Exception e) {
            return error("获取节点详情失败: " + e.getMessage());
        }
    }

    // =================================
    // 私有方法实现
    // =================================

    /**
     * 🔥 优化版：获取具有预设布局位置的图谱节点
     */
    private List<Map<String, Object>> getUnifiedGraphNodesWithOptimizedLayout(String centerNode) {
        List<Map<String, Object>> nodes = new ArrayList<>();

        // 🔥 第一步：获取L1作物节点 - 设置中心位置
        Map<String, Object> l1Node = unifiedGraphMapper.selectUnifiedNodeById(centerNode);
        if (l1Node != null) {
            Map<String, Object> node = new HashMap<>(l1Node);
            node.put("fx", 400.0); // 固定X坐标
            node.put("fy", 300.0); // 固定Y坐标
            node.put("fixed", true); // 标记为固定位置
            nodes.add(node);
        }

        // 🔥 第二步：获取L2类型节点 - 设置环形布局
        List<Map<String, Object>> l2Nodes = unifiedGraphMapper.selectUnifiedNodesByParentIdAndLevel(centerNode, 2);
        List<String> typeNodeIds = new ArrayList<>();
        for (Map<String, Object> node : l2Nodes) {
            typeNodeIds.add((String) node.get("id"));
        }

        // 🔥 为L2节点设置环形布局
        double centerX = 400.0;
        double centerY = 300.0;
        double radius = 150.0;
        int l2Count = l2Nodes.size();

        for (int i = 0; i < l2Count; i++) {
            Map<String, Object> node = l2Nodes.get(i);
            double angle = (2 * Math.PI * i) / l2Count - Math.PI / 2; // 从上方开始

            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            node.put("fx", x);
            node.put("fy", y);
            node.put("fixed", true);
        }

        nodes.addAll(l2Nodes);

        // 🔥 第三步：为每个L2类型节点获取L3具体项 - 设置扇形布局
        for (int typeIndex = 0; typeIndex < typeNodeIds.size(); typeIndex++) {
            String typeNodeId = typeNodeIds.get(typeIndex);

            List<Map<String, Object>> dbL3Nodes = unifiedGraphMapper.selectDefaultL3Nodes(typeNodeId, NODES_PER_TYPE);
            List<Map<String, Object>> l3NodesForType = new ArrayList<>();

            for (Map<String, Object> rawNode : dbL3Nodes) {
                Map<String, Object> node = new HashMap<>(rawNode);
                node.put("hasChildren", unifiedGraphMapper.countChildNodes((String) rawNode.get("id")) > 0);
                node.put("expanded", false); // 初始为未展开状态
                l3NodesForType.add(node);
            }

            // 🔥 为当前类型的L3节点设置扇形布局
            Map<String, Object> l2Node = l2Nodes.get(typeIndex);
            double l2X = (Double) l2Node.get("fx");
            double l2Y = (Double) l2Node.get("fy");

            double l3Radius = 100.0;
            int l3Count = l3NodesForType.size();

            for (int i = 0; i < l3Count; i++) {
                Map<String, Object> l3Node = l3NodesForType.get(i);

                // 计算相对于L2节点的扇形角度
                double baseAngle = Math.atan2(l2Y - centerY, l2X - centerX);
                double spreadAngle = Math.PI / 3; // 60度扇形
                double startAngle = baseAngle - spreadAngle / 2;
                double angle = startAngle + (spreadAngle * i) / Math.max(1, l3Count - 1);

                double x = l2X + l3Radius * Math.cos(angle);
                double y = l2Y + l3Radius * Math.sin(angle);

                l3Node.put("fx", x);
                l3Node.put("fy", y);
                l3Node.put("fixed", true);
            }

            nodes.addAll(l3NodesForType);
        }

        System.out.println("优化布局完成，节点数量: " + nodes.size() + ", 中心节点: " + centerNode);
        return nodes;
    }

    /**
     * 获取统一图谱关系数据
     */
    private List<Map<String, Object>> getUnifiedGraphLinks(String centerNode) {
        List<Map<String, Object>> links = new ArrayList<>();

        // 第一步：L1作物 -> L2类型的关系
        List<Map<String, Object>> l1ToL2 = unifiedGraphMapper.selectL1ToL2Links(centerNode);
        links.addAll(l1ToL2);

        // 第二步：L2类型 -> L3具体项的关系
        List<Map<String, Object>> l2ToL3 = unifiedGraphMapper.selectL2ToL3Links(centerNode);
        links.addAll(l2ToL3);

        return links;
    }

    private String determineNodeType(String nodeId) {
        if (nodeId == null) return "crop";
        if (nodeId.startsWith("crop_")) return "crop";
        if (nodeId.startsWith("type_")) return "type";
        if (nodeId.startsWith("variety_")) return "variety";
        if (nodeId.startsWith("disease_")) return "disease";
        if (nodeId.startsWith("pest_")) return "pest";
        if (nodeId.startsWith("stress_")) return "stress";
        return "crop";
    }

    private String getNodeTypeDescription(String nodeType, String category) {
        if ("crop".equals(nodeType)) return "作物分类";
        if ("type".equals(nodeType)) return "知识分类";

        if ("variety".equals(nodeType)) return "作物品种";

        if (category != null) {
            return category; // 直接使用类别名字，如 "病害"、"虫害"、"非生物胁迫"
        }

        if ("disease".equals(nodeType)) return "病害";
        if ("pest".equals(nodeType)) return "虫害";
        if ("stress".equals(nodeType)) return "非生物胁迫";

        return "相关节点";
    }

    private Map<String, Object> createSuggestion(String id, String name, String type, String description) {
        Map<String, Object> suggestion = new HashMap<>();
        suggestion.put("id", id);
        suggestion.put("name", name);
        suggestion.put("type", type);
        suggestion.put("description", description);
        return suggestion;
    }
}