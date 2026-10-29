package com.ruoyi.binZhou.ld.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.binZhou.ld.domain.PeachbaikeSimpleVO;
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
import com.ruoyi.binZhou.ld.domain.Peachbaike;
import com.ruoyi.binZhou.ld.service.IPeachbaikeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2026-04-18
 */
@RestController
@RequestMapping("/binZhou/ld/peachbaike")
public class PeachbaikeController extends BaseController
{
    @Autowired
    private IPeachbaikeService peachbaikeService;
    /**
     * 获取桃树简易列表（卡片展示用）
     */
    @PreAuthorize("@ss.hasPermi('ld:peachbaike:list')")
    @GetMapping("/simpleList")
    public TableDataInfo simpleList() {
        //startPage();
        List<PeachbaikeSimpleVO> list = peachbaikeService.selectSimpleList();
        return getDataTable(list);
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('ld:peachbaike:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(peachbaikeService.selectPeachbaikeById(id));
    }

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('ld:peachbaike:list')")
    @GetMapping("/list")
    public TableDataInfo list(Peachbaike peachbaike)
    {
        startPage();
        List<Peachbaike> list = peachbaikeService.selectPeachbaikeList(peachbaike);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('ld:peachbaike:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Peachbaike peachbaike)
    {
        List<Peachbaike> list = peachbaikeService.selectPeachbaikeList(peachbaike);
        ExcelUtil<Peachbaike> util = new ExcelUtil<Peachbaike>(Peachbaike.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }



    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('ld:peachbaike:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Peachbaike peachbaike)
    {
        return toAjax(peachbaikeService.insertPeachbaike(peachbaike));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('ld:peachbaike:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Peachbaike peachbaike)
    {
        return toAjax(peachbaikeService.updatePeachbaike(peachbaike));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('ld:peachbaike:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(peachbaikeService.deletePeachbaikeByIds(ids));
    }
}
