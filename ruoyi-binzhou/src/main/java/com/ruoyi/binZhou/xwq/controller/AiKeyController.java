package com.ruoyi.binZhou.xwq.controller;

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
import com.ruoyi.binZhou.xwq.domain.AiKey;
import com.ruoyi.binZhou.xwq.service.IAiKeyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 密钥管理Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/xwq/key")
public class AiKeyController extends BaseController
{
    @Autowired
    private IAiKeyService aiKeyService;

    /**
     * 查询密钥管理列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:key:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiKey aiKey)
    {
        startPage();
        List<AiKey> list = aiKeyService.selectAiKeyList(aiKey);
        return getDataTable(list);
    }

    /**
     * 导出密钥管理列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:key:export')")
    @Log(title = "密钥管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiKey aiKey)
    {
        List<AiKey> list = aiKeyService.selectAiKeyList(aiKey);
        ExcelUtil<AiKey> util = new ExcelUtil<AiKey>(AiKey.class);
        util.exportExcel(response, list, "密钥管理数据");
    }

    /**
     * 获取密钥管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:key:query')")
    @GetMapping(value = "/{keyId}")
    public AjaxResult getInfo(@PathVariable("keyId") Long keyId)
    {
        return success(aiKeyService.selectAiKeyByKeyId(keyId));
    }

    /**
     * 新增密钥管理
     */
    @PreAuthorize("@ss.hasPermi('xwq:key:add')")
    @Log(title = "密钥管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiKey aiKey)
    {
        return toAjax(aiKeyService.insertAiKey(aiKey));
    }

    /**
     * 修改密钥管理
     */
    @PreAuthorize("@ss.hasPermi('xwq:key:edit')")
    @Log(title = "密钥管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiKey aiKey)
    {
        return toAjax(aiKeyService.updateAiKey(aiKey));
    }

    /**
     * 删除密钥管理
     */
    @PreAuthorize("@ss.hasPermi('xwq:key:remove')")
    @Log(title = "密钥管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{keyIds}")
    public AjaxResult remove(@PathVariable Long[] keyIds)
    {
        return toAjax(aiKeyService.deleteAiKeyByKeyIds(keyIds));
    }
}
