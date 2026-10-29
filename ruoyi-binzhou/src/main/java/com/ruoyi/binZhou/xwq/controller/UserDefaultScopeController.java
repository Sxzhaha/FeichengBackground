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
import com.ruoyi.binZhou.xwq.domain.UserDefaultScope;
import com.ruoyi.binZhou.xwq.service.IUserDefaultScopeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户默认监测范围Controller
 * 
 * @author ruoyi
 * @date 2026-10-13
 */
@RestController
@RequestMapping("/app/xwq/scope")
public class UserDefaultScopeController extends BaseController
{
    @Autowired
    private IUserDefaultScopeService userDefaultScopeService;

    /**
     * 查询用户默认监测范围列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:scope:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserDefaultScope userDefaultScope)
    {
        startPage();
        List<UserDefaultScope> list = userDefaultScopeService.selectUserDefaultScopeList(userDefaultScope);
        return getDataTable(list);
    }

    /**
     * 导出用户默认监测范围列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:scope:export')")
    @Log(title = "用户默认监测范围", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserDefaultScope userDefaultScope)
    {
        List<UserDefaultScope> list = userDefaultScopeService.selectUserDefaultScopeList(userDefaultScope);
        ExcelUtil<UserDefaultScope> util = new ExcelUtil<UserDefaultScope>(UserDefaultScope.class);
        util.exportExcel(response, list, "用户默认监测范围数据");
    }

    /**
     * 获取用户默认监测范围详细信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:scope:query')")
    @GetMapping(value = "/{scopeId}")
    public AjaxResult getInfo(@PathVariable("scopeId") Long scopeId)
    {
        return success(userDefaultScopeService.selectUserDefaultScopeByScopeId(scopeId));
    }

    /**
     * 新增用户默认监测范围
     */
    @PreAuthorize("@ss.hasPermi('xwq:scope:add')")
    @Log(title = "用户默认监测范围", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserDefaultScope userDefaultScope)
    {
        return toAjax(userDefaultScopeService.insertUserDefaultScope(userDefaultScope));
    }

    /**
     * 修改用户默认监测范围
     */
    @PreAuthorize("@ss.hasPermi('xwq:scope:edit')")
    @Log(title = "用户默认监测范围", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserDefaultScope userDefaultScope)
    {
        return toAjax(userDefaultScopeService.updateUserDefaultScope(userDefaultScope));
    }

    /**
     * 删除用户默认监测范围
     */
    @PreAuthorize("@ss.hasPermi('xwq:scope:remove')")
    @Log(title = "用户默认监测范围", businessType = BusinessType.DELETE)
	@DeleteMapping("/{scopeIds}")
    public AjaxResult remove(@PathVariable Long[] scopeIds)
    {
        return toAjax(userDefaultScopeService.deleteUserDefaultScopeByScopeIds(scopeIds));
    }
}
