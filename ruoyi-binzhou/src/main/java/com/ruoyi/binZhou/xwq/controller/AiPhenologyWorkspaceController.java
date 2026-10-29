package com.ruoyi.binZhou.xwq.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.binZhou.xwq.domain.WorkspaceHiddenRequest;
import com.ruoyi.binZhou.xwq.service.IAiWorkspaceHiddenService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

@RestController
@RequestMapping("/xwq/phenology/workspace")
public class AiPhenologyWorkspaceController extends BaseController
{
    private static final String MODULE_TYPE = "phenology";
    private final IAiWorkspaceHiddenService hiddenService;

    public AiPhenologyWorkspaceController(IAiWorkspaceHiddenService hiddenService)
    {
        this.hiddenService = hiddenService;
    }

    @PreAuthorize("@ss.hasPermi('xwq:phenology:list')")
    @GetMapping("/hidden")
    public AjaxResult hiddenList()
    {
        return success(hiddenService.selectHiddenList(getUserId(), MODULE_TYPE));
    }

    @PreAuthorize("@ss.hasPermi('xwq:phenology:list')")
    @PostMapping("/hide")
    public AjaxResult hide(@RequestBody WorkspaceHiddenRequest request)
    {
        if (request.getTargetIds() != null && !request.getTargetIds().isEmpty())
        {
            hiddenService.hideBatch(getUserId(), MODULE_TYPE,
                    request.getTargetType(), request.getTargetIds());
        }
        else
        {
            hiddenService.hide(getUserId(), MODULE_TYPE,
                    request.getTargetType(), request.getTargetId());
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('xwq:phenology:list')")
    @DeleteMapping("/hidden/{targetType}/{targetId}")
    public AjaxResult restore(@PathVariable String targetType, @PathVariable Long targetId)
    {
        hiddenService.restore(getUserId(), MODULE_TYPE, targetType, targetId);
        return success();
    }
}
