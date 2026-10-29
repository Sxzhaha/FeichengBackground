package com.ruoyi.binZhou.ld.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.binZhou.ld.domain.KnowledgeNodes;
import com.ruoyi.binZhou.ld.service.IKnowledgeNodesService;

/**
 * 品种展示控制器（使用若依生成的Service）
 * 用于前端标准象百科页面的数据展示
 *
 * @author ruoyi
 * @date 2025-01-08
 */
@RestController
@RequestMapping("/binZhou/ld/knowledge/display")
public class VarietyDisplayController extends BaseController
{
    @Autowired
    private IKnowledgeNodesService knowledgeNodesService;

    /**
     * 🔥 获取作物分类列表（用于前端展示）
     */
    @GetMapping("/categories")
    public AjaxResult getCategoriesForDisplay()
    {
        try {
            // 查询所有作物节点
            KnowledgeNodes queryNode = new KnowledgeNodes();
            queryNode.setNodeType("crop");
            List<KnowledgeNodes> cropList = knowledgeNodesService.selectKnowledgeNodesList(queryNode);

            // 构建返回数据结构，与象差百科格式一致
            Map<String, Object> result = new HashMap<>();

            for (KnowledgeNodes crop : cropList) {
                // 统计该作物下的品种数量
                KnowledgeNodes varietyQuery = new KnowledgeNodes();
                varietyQuery.setNodeType("variety");
                varietyQuery.setParentId(crop.getNodeId());
                List<KnowledgeNodes> varieties = knowledgeNodesService.selectKnowledgeNodesList(varietyQuery);

                Map<String, Object> cropInfo = new HashMap<>();
                cropInfo.put("name", crop.getNodeName());
                cropInfo.put("count", varieties.size()); // 品种数量

                result.put(crop.getNodeId(), cropInfo);
            }

            return AjaxResult.success(result);
        } catch (Exception e) {
            logger.error("获取作物分类失败", e);
            return AjaxResult.error("获取作物分类失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 根据作物nodeId获取品种列表
     */
    @GetMapping("/varieties")
    public AjaxResult getVarietiesForDisplay(@RequestParam("cropNodeId") String cropNodeId)
    {
        try {
            KnowledgeNodes queryNode = new KnowledgeNodes();
            queryNode.setNodeType("variety");
            queryNode.setParentId(cropNodeId);

            List<KnowledgeNodes> varieties = knowledgeNodesService.selectKnowledgeNodesList(queryNode);

            // 转换为前端需要的格式
            List<Map<String, Object>> varietyList = varieties.stream().map(variety -> {
                Map<String, Object> varietyInfo = new HashMap<>();
                varietyInfo.put("id", variety.getId());
                varietyInfo.put("name", variety.getNodeName());
                varietyInfo.put("nodeId", variety.getNodeId());
                varietyInfo.put("description", variety.getDescription());
                varietyInfo.put("createTime", variety.getCreateTime());
                return varietyInfo;
            }).collect(Collectors.toList());

            return AjaxResult.success(varietyList);
        } catch (Exception e) {
            logger.error("获取品种列表失败", e);
            return AjaxResult.error("获取品种列表失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 搜索品种（支持智能搜索）
     */
    @GetMapping("/search")
    public AjaxResult searchVarietiesForDisplay(@RequestParam("keyword") String keyword)
    {
        try {
            List<KnowledgeNodes> searchResults = new ArrayList<>();

            // 🔥 方法1: 智能作物品种搜索
            if (keyword.contains("品种")) {
                // 识别作物类型
                String cropKeyword = null;
                if (keyword.contains("小麦")) {
                    cropKeyword = "小麦";
                } else if (keyword.contains("玉米")) {
                    cropKeyword = "玉米";
                }

                if (cropKeyword != null) {
                    // 先找到对应的作物节点
                    KnowledgeNodes cropQuery = new KnowledgeNodes();
                    cropQuery.setNodeType("crop");
                    cropQuery.setNodeName(cropKeyword);
                    List<KnowledgeNodes> crops = knowledgeNodesService.selectKnowledgeNodesList(cropQuery);

                    if (!crops.isEmpty()) {
                        // 获取该作物下的所有品种
                        KnowledgeNodes varietyQuery = new KnowledgeNodes();
                        varietyQuery.setNodeType("variety");
                        varietyQuery.setParentId(crops.get(0).getNodeId());
                        searchResults = knowledgeNodesService.selectKnowledgeNodesList(varietyQuery);

                        logger.info("智能搜索: {} -> 找到 {} 个品种", keyword, searchResults.size());
                    }
                }
            }

            // 🔥 方法2: 如果智能搜索无结果，执行传统搜索
            if (searchResults.isEmpty()) {
                // 查询所有品种节点
                KnowledgeNodes queryNode = new KnowledgeNodes();
                queryNode.setNodeType("variety");
                List<KnowledgeNodes> allVarieties = knowledgeNodesService.selectKnowledgeNodesList(queryNode);

                // 进行关键词过滤
                searchResults = allVarieties.stream()
                        .filter(variety ->
                                (variety.getNodeName() != null && variety.getNodeName().toLowerCase().contains(keyword.toLowerCase())) ||
                                        (variety.getDescription() != null && variety.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                        )
                        .collect(Collectors.toList());

                logger.info("传统搜索: {} -> 找到 {} 个品种", keyword, searchResults.size());
            }

            // 转换为前端需要的格式
            List<Map<String, Object>> searchResultMaps = searchResults.stream().map(variety -> {
                Map<String, Object> varietyInfo = new HashMap<>();
                varietyInfo.put("id", variety.getId());
                varietyInfo.put("name", variety.getNodeName());
                varietyInfo.put("nodeId", variety.getNodeId());
                varietyInfo.put("type", "品种");
                varietyInfo.put("description", variety.getDescription());
                varietyInfo.put("parentId", variety.getParentId());
                return varietyInfo;
            }).collect(Collectors.toList());

            return AjaxResult.success(searchResultMaps);
        } catch (Exception e) {
            logger.error("搜索品种失败", e);
            return AjaxResult.error("搜索失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 根据品种名称获取详情
     */
    @GetMapping("/variety")
    public AjaxResult getVarietyDetailByName(@RequestParam("name") String varietyName)
    {
        try {
            KnowledgeNodes queryNode = new KnowledgeNodes();
            queryNode.setNodeType("variety");
            queryNode.setNodeName(varietyName);

            List<KnowledgeNodes> varieties = knowledgeNodesService.selectKnowledgeNodesList(queryNode);

            if (varieties.isEmpty()) {
                return AjaxResult.error("未找到名称为'" + varietyName + "'的品种");
            }

            // 返回第一个匹配的品种
            KnowledgeNodes variety = varieties.get(0);

            Map<String, Object> varietyDetail = new HashMap<>();
            varietyDetail.put("id", variety.getId());
            varietyDetail.put("nodeId", variety.getNodeId());
            varietyDetail.put("name", variety.getNodeName());
            varietyDetail.put("type", "品种");
            varietyDetail.put("parentId", variety.getParentId());
            varietyDetail.put("description", variety.getDescription());
            varietyDetail.put("createTime", variety.getCreateTime());

            return AjaxResult.success(varietyDetail);
        } catch (Exception e) {
            logger.error("根据名称获取品种失败", e);
            return AjaxResult.error("获取品种失败：" + e.getMessage());
        }
    }
}