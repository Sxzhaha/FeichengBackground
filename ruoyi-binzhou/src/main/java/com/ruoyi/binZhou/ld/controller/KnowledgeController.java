package com.ruoyi.binZhou.ld.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.R;
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
import com.ruoyi.binZhou.ld.domain.Knowledge;
import com.ruoyi.binZhou.ld.service.IKnowledgeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 病虫害百科Controller
 * 
 * @author ruoyi
 * @date 2025-05-12
 */
@RestController
@RequestMapping("/binZhou/ld/knowledge")
public class KnowledgeController extends BaseController
{
    @Autowired
    private IKnowledgeService knowledgeService;

    /**
     * 查询病虫害百科列表
     */
    @PreAuthorize("@ss.hasPermi('ld:knowledge:list')")
    @GetMapping("/list")
    public TableDataInfo list(Knowledge knowledge)
    {
        startPage();
        List<Knowledge> list = knowledgeService.selectKnowledgeList(knowledge);
        return getDataTable(list);
    }
    /**
     * 查询病虫害百科列表
     */
    @PreAuthorize("@ss.hasPermi('ld:knowledge:list')")
    @GetMapping("/list/app")
    public R listApp(Knowledge knowledge)
    {
        return R.ok(knowledgeService.selectKnowledgeList(knowledge));
    }
    @PreAuthorize("@ss.hasPermi('ld:knowledge:list')")
    @GetMapping("/menu")
    public R getKnowledgeMenuTree() {
        return R.ok(knowledgeService.getKnowledgeMenuTree());
    }
    //    按名称模糊获取菜单
    @PreAuthorize("@ss.hasPermi('ld:articles:list')")
    @GetMapping("/menu/{name}")
    public R menuName(@PathVariable("name") String name)
    {

return R.ok(knowledgeService.getKnowledgeMenuByLikeName(name));
    }
    /**
     * 导出病虫害百科列表
     */
    @PreAuthorize("@ss.hasPermi('ld:knowledge:export')")
    @Log(title = "病虫害百科", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Knowledge knowledge)
    {
        List<Knowledge> list = knowledgeService.selectKnowledgeList(knowledge);
        ExcelUtil<Knowledge> util = new ExcelUtil<Knowledge>(Knowledge.class);
        util.exportExcel(response, list, "病虫害百科数据");
    }

    /**
     * 获取病虫害百科详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:knowledge:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(knowledgeService.selectKnowledgeById(id));
    }

    /**
     * 新增病虫害百科
     */
    @PreAuthorize("@ss.hasPermi('ld:knowledge:add')")
    @Log(title = "病虫害百科", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Knowledge knowledge)
    {
        return toAjax(knowledgeService.insertKnowledge(knowledge));
    }

    /**
     * 修改病虫害百科
     */
    @PreAuthorize("@ss.hasPermi('ld:knowledge:edit')")
    @Log(title = "病虫害百科", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Knowledge knowledge)
    {
        return toAjax(knowledgeService.updateKnowledge(knowledge));
    }

    /**
     * 删除病虫害百科
     */
    @PreAuthorize("@ss.hasPermi('ld:knowledge:remove')")
    @Log(title = "病虫害百科", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(knowledgeService.deleteKnowledgeByIds(ids));
    }
}
