package com.ruoyi.binZhou.ld.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.binZhou.ld.domain.KnowledgeCategory;
import com.ruoyi.binZhou.ld.domain.MenuItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.ld.mapper.KnowledgeMapper;
import com.ruoyi.binZhou.ld.domain.Knowledge;
import com.ruoyi.binZhou.ld.service.IKnowledgeService;

/**
 * 病虫害百科Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-12
 */
@Service
public class KnowledgeServiceImpl implements IKnowledgeService 
{
    @Autowired
    private KnowledgeMapper knowledgeMapper;

    /**
     * 查询病虫害百科
     * 
     * @param id 病虫害百科主键
     * @return 病虫害百科
     */
    @Override
    public Knowledge selectKnowledgeById(Long id)
    {
        return knowledgeMapper.selectKnowledgeById(id);
    }

    /**
     * 查询病虫害百科列表
     * 
     * @param knowledge 病虫害百科
     * @return 病虫害百科
     */
    @Override
    public List<Knowledge> selectKnowledgeList(Knowledge knowledge)
    {
        return knowledgeMapper.selectKnowledgeList(knowledge);
    }

    /**
     * 新增病虫害百科
     * 
     * @param knowledge 病虫害百科
     * @return 结果
     */
    @Override
    public int insertKnowledge(Knowledge knowledge)
    {
        return knowledgeMapper.insertKnowledge(knowledge);
    }

    /**
     * 修改病虫害百科
     * 
     * @param knowledge 病虫害百科
     * @return 结果
     */
    @Override
    public int updateKnowledge(Knowledge knowledge)
    {
        return knowledgeMapper.updateKnowledge(knowledge);
    }

    /**
     * 批量删除病虫害百科
     * 
     * @param ids 需要删除的病虫害百科主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeByIds(Long[] ids)
    {
        return knowledgeMapper.deleteKnowledgeByIds(ids);
    }

    /**
     * 删除病虫害百科信息
     * 
     * @param id 病虫害百科主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeById(Long id)
    {
        return knowledgeMapper.deleteKnowledgeById(id);
    }

    @Override
    public List<MenuItemDTO> getKnowledgeMenuTree() {
        // 从数据库获取所有分类数据
        List<KnowledgeCategory> categories = knowledgeMapper.findAllCategories();

        // 构建三级菜单结构
        Map<String, Map<String, List<String>>> categoryMap = new LinkedHashMap<>();

        for (KnowledgeCategory category : categories) {
            String firstLevel = category.getFirstLevel();
            String secondLevel = category.getSecondLevel();
            String thirdLevel = category.getThirdLevel();

            categoryMap.computeIfAbsent(firstLevel, k -> new LinkedHashMap<>())
                    .computeIfAbsent(secondLevel, k -> new ArrayList<>())
                    .add(thirdLevel);
        }

        // 转换为前端需要的菜单结构
        List<MenuItemDTO> menuList = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<String>>> firstEntry : categoryMap.entrySet()) {
            MenuItemDTO firstLevelItem = new MenuItemDTO(firstEntry.getKey());
            List<MenuItemDTO> secondLevelItems = new ArrayList<>();

            for (Map.Entry<String, List<String>> secondEntry : firstEntry.getValue().entrySet()) {
                MenuItemDTO secondLevelItem = new MenuItemDTO(secondEntry.getKey());
                List<MenuItemDTO> thirdLevelItems = secondEntry.getValue().stream()
                        .map(MenuItemDTO::new)
                        .collect(Collectors.toList());

                secondLevelItem.setChildren(thirdLevelItems);
                secondLevelItems.add(secondLevelItem);
            }

            firstLevelItem.setChildren(secondLevelItems);
            menuList.add(firstLevelItem);
        }

        return menuList;
    }

    @Override
    public String[] getKnowledgeMenuByLikeName(String name) {
        return knowledgeMapper.getKnowledgeMenuByLikeName(name);
    }
}
