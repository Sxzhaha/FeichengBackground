package com.ruoyi.binZhou.ld.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.binZhou.ld.domain.KnowledgeNodes;
import com.ruoyi.binZhou.ld.service.IKnowledgeNodesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 知识图谱节点Controller
 * 
 * @author ruoyi
 * @date 2025-08-01
 */
@RestController
@RequestMapping("/binZhou/ld/nodes")
public class KnowledgeNodesController extends BaseController
{
    @Autowired
    private IKnowledgeNodesService knowledgeNodesService;

    /**
     * 查询知识图谱节点列表
     */
    @PreAuthorize("@ss.hasPermi('ld:nodes:list')")
    @GetMapping("/list")
    public TableDataInfo list(KnowledgeNodes knowledgeNodes)
    {
        startPage();
        List<KnowledgeNodes> list = knowledgeNodesService.selectKnowledgeNodesList(knowledgeNodes);
        return getDataTable(list);
    }

    /**
     * 导出知识图谱节点列表
     */
    @PreAuthorize("@ss.hasPermi('ld:nodes:export')")
    @Log(title = "知识图谱节点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KnowledgeNodes knowledgeNodes)
    {
        List<KnowledgeNodes> list = knowledgeNodesService.selectKnowledgeNodesList(knowledgeNodes);
        ExcelUtil<KnowledgeNodes> util = new ExcelUtil<KnowledgeNodes>(KnowledgeNodes.class);
        util.exportExcel(response, list, "知识图谱节点数据");
    }

    /**
     * 获取知识图谱节点详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:nodes:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(knowledgeNodesService.selectKnowledgeNodesById(id));
    }

    /**
     * 新增知识图谱节点
     */
    @PreAuthorize("@ss.hasPermi('ld:nodes:add')")
    @Log(title = "知识图谱节点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KnowledgeNodes knowledgeNodes)
    {
        return toAjax(knowledgeNodesService.insertKnowledgeNodes(knowledgeNodes));
    }

    /**
     * 修改知识图谱节点
     */
    @PreAuthorize("@ss.hasPermi('ld:nodes:edit')")
    @Log(title = "知识图谱节点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KnowledgeNodes knowledgeNodes)
    {
        return toAjax(knowledgeNodesService.updateKnowledgeNodes(knowledgeNodes));
    }

    /**
     * 删除知识图谱节点
     */
    @PreAuthorize("@ss.hasPermi('ld:nodes:remove')")
    @Log(title = "知识图谱节点", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(knowledgeNodesService.deleteKnowledgeNodesByIds(ids));
    }
}
