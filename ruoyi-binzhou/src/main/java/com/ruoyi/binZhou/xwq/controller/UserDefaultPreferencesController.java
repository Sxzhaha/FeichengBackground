package com.ruoyi.binZhou.xwq.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.binZhou.xwq.domain.UserDefaultModel;
import com.ruoyi.binZhou.xwq.domain.UserDefaultPreferencesRequest;
import com.ruoyi.binZhou.xwq.domain.UserDefaultScope;
import com.ruoyi.binZhou.xwq.service.IUserDefaultModelService;
import com.ruoyi.binZhou.xwq.service.IUserDefaultScopeService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;

/**
 * 当前登录用户默认配置接口。
 * 与代码生成的后台 CRUD 分开，避免客户端提交 userId 查询或修改其他用户配置。
 */
@RestController
@RequestMapping("/xwq/default")
public class UserDefaultPreferencesController extends BaseController
{
    private static final String[] SCENES = {"disease", "pest", "phenology"};

    @Autowired
    private IUserDefaultScopeService scopeService;

    @Autowired
    private IUserDefaultModelService modelService;

    @GetMapping("/current")
    public AjaxResult current()
    {
        Long userId = getUserId();
        UserDefaultScope scopeQuery = new UserDefaultScope();
        scopeQuery.setUserId(userId);
        List<UserDefaultScope> scopes = scopeService.selectUserDefaultScopeList(scopeQuery);

        Map<String, Object> scope = new LinkedHashMap<>();
        if (!scopes.isEmpty())
        {
            UserDefaultScope saved = scopes.get(0);
            scope.put("parkId", saved.getParkId());
            scope.put("areaId", saved.getAreaId());
            scope.put("deviceId", saved.getDeviceId());
        }
        else
        {
            scope.put("parkId", null);
            scope.put("areaId", null);
            scope.put("deviceId", null);
        }

        UserDefaultModel modelQuery = new UserDefaultModel();
        modelQuery.setUserId(userId);
        List<UserDefaultModel> savedModels = modelService.selectUserDefaultModelList(modelQuery);
        Map<String, Long> models = new LinkedHashMap<>();
        for (String scene : SCENES) models.put(scene, null);
        for (UserDefaultModel model : savedModels)
        {
            if (models.containsKey(model.getSceneCode())) models.put(model.getSceneCode(), model.getModelId());
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("scope", scope);
        data.put("models", models);
        return success(data);
    }

    @PutMapping("/current")
    @Log(title = "用户默认配置", businessType = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult save(@RequestBody UserDefaultPreferencesRequest request)
    {
        if (request == null || request.getParkId() == null)
        {
            throw new ServiceException("默认园区不能为空");
        }

        Long userId = getUserId();
        UserDefaultScope query = new UserDefaultScope();
        query.setUserId(userId);
        List<UserDefaultScope> scopes = scopeService.selectUserDefaultScopeList(query);
        UserDefaultScope scope = scopes.isEmpty() ? new UserDefaultScope() : scopes.get(0);
        scope.setUserId(userId);
        scope.setParkId(request.getParkId());
        scope.setAreaId(request.getAreaId());
        scope.setDeviceId(request.getDeviceId());
        scope.setStatus("0");
        if (scope.getScopeId() == null)
        {
            scope.setCreateBy(getUsername());
            scopeService.insertUserDefaultScope(scope);
        }
        else
        {
            scope.setUpdateBy(getUsername());
            scopeService.updateUserDefaultScopeCurrent(scope);
        }

        Map<String, Long> requestedModels = request.getModels() == null
                ? new LinkedHashMap<>() : request.getModels();
        UserDefaultModel modelQuery = new UserDefaultModel();
        modelQuery.setUserId(userId);
        List<UserDefaultModel> existingModels = modelService.selectUserDefaultModelList(modelQuery);
        Map<String, UserDefaultModel> existingByScene = new LinkedHashMap<>();
        for (UserDefaultModel model : existingModels) existingByScene.put(model.getSceneCode(), model);

        for (String scene : SCENES)
        {
            UserDefaultModel existing = existingByScene.get(scene);
            Long modelId = requestedModels.get(scene);
            if (modelId == null)
            {
                if (existing != null) modelService.deleteUserDefaultModelByPreferenceId(existing.getPreferenceId());
                continue;
            }
            if (existing == null)
            {
                UserDefaultModel model = new UserDefaultModel();
                model.setUserId(userId);
                model.setSceneCode(scene);
                model.setModelId(modelId);
                model.setStatus("0");
                model.setCreateBy(getUsername());
                modelService.insertUserDefaultModel(model);
            }
            else
            {
                existing.setModelId(modelId);
                existing.setStatus("0");
                existing.setUpdateBy(getUsername());
                modelService.updateUserDefaultModel(existing);
            }
        }
        return success();
    }
}
