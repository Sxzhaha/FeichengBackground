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
import com.ruoyi.binZhou.xwq.domain.UserDefaultModel;
import com.ruoyi.binZhou.xwq.service.IUserDefaultModelService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户默认公共模型Controller
 * 
 * @author ruoyi
 * @date 2026-10-13
 */
@RestController
@RequestMapping("/app/xwq/model")
public class UserDefaultModelController extends BaseController
{
    @Autowired
    private IUserDefaultModelService userDefaultModelService;

    /**
     * 查询用户默认公共模型列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserDefaultModel userDefaultModel)
    {
        startPage();
        List<UserDefaultModel> list = userDefaultModelService.selectUserDefaultModelList(userDefaultModel);
        return getDataTable(list);
    }

    /**
     * 导出用户默认公共模型列表
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:export')")
    @Log(title = "用户默认公共模型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserDefaultModel userDefaultModel)
    {
        List<UserDefaultModel> list = userDefaultModelService.selectUserDefaultModelList(userDefaultModel);
        ExcelUtil<UserDefaultModel> util = new ExcelUtil<UserDefaultModel>(UserDefaultModel.class);
        util.exportExcel(response, list, "用户默认公共模型数据");
    }

    /**
     * 获取用户默认公共模型详细信息
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:query')")
    @GetMapping(value = "/{preferenceId}")
    public AjaxResult getInfo(@PathVariable("preferenceId") Long preferenceId)
    {
        return success(userDefaultModelService.selectUserDefaultModelByPreferenceId(preferenceId));
    }

    /**
     * 新增用户默认公共模型
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:add')")
    @Log(title = "用户默认公共模型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserDefaultModel userDefaultModel)
    {
        return toAjax(userDefaultModelService.insertUserDefaultModel(userDefaultModel));
    }

    /**
     * 修改用户默认公共模型
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:edit')")
    @Log(title = "用户默认公共模型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserDefaultModel userDefaultModel)
    {
        return toAjax(userDefaultModelService.updateUserDefaultModel(userDefaultModel));
    }

    /**
     * 删除用户默认公共模型
     */
    @PreAuthorize("@ss.hasPermi('xwq:model:remove')")
    @Log(title = "用户默认公共模型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{preferenceIds}")
    public AjaxResult remove(@PathVariable Long[] preferenceIds)
    {
        return toAjax(userDefaultModelService.deleteUserDefaultModelByPreferenceIds(preferenceIds));
    }
}
