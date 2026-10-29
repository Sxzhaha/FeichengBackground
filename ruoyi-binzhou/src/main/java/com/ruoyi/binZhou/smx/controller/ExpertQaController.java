package com.ruoyi.binZhou.smx.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.binZhou.smx.domain.ExpertAnswer;
import com.ruoyi.binZhou.smx.domain.ExpertCrop;
import com.ruoyi.binZhou.smx.domain.ExpertQuestion;
import com.ruoyi.binZhou.smx.service.IExpertQaService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 专家问答Controller（移动端接口，遵循开发说明 /app/方法名 规范）
 *
 * @author smx
 */
@RestController
@RequestMapping("/app")
public class ExpertQaController extends BaseController
{
    private final IExpertQaService service;

    public ExpertQaController(IExpertQaService service)
    {
        this.service = service;
    }

    /**
     * 问题分页列表
     */
    @GetMapping("/expertQuestionList")
    public TableDataInfo expertQuestionList(ExpertQuestion query)
    {
        startPage();
        List<ExpertQuestion> list = service.selectQuestionList(query);
        return getDataTable(list);
    }

    /**
     * 问题详情
     */
    @GetMapping("/expertQuestionDetail")
    public AjaxResult expertQuestionDetail(Long questionId, Boolean countView)
    {
        return AjaxResult.success(service.selectQuestionById(questionId, countView == null || countView));
    }

    /**
     * 提交问题
     */
    @Log(title = "专家问答-提问", businessType = BusinessType.INSERT)
    @PostMapping("/expertQuestionCreate")
    public AjaxResult expertQuestionCreate(@RequestBody ExpertQuestion question)
    {
        return toAjax(service.insertQuestion(question));
    }

    /**
     * 问题下的回答列表
     */
    @GetMapping("/expertAnswerList")
    public AjaxResult expertAnswerList(Long questionId)
    {
        return AjaxResult.success(service.selectAnswersByQuestionId(questionId));
    }

    /**
     * 提交回答
     */
    @Log(title = "专家问答-回答", businessType = BusinessType.INSERT)
    @PostMapping("/expertAnswerCreate")
    public AjaxResult expertAnswerCreate(Long questionId, @RequestBody ExpertAnswer answer)
    {
        return toAjax(service.insertAnswer(questionId, answer));
    }

    /**
     * 提问者本人或管理员删除问题及其关联回答、图片
     */
    @Log(title = "专家问答-删除", businessType = BusinessType.DELETE)
    @PostMapping("/expertQuestionDelete")
    public AjaxResult expertQuestionDelete(Long questionId)
    {
        return toAjax(service.deleteQuestion(questionId));
    }

    /**
     * 回答者本人或管理员删除单条回答
     */
    @Log(title = "专家问答-删除回答", businessType = BusinessType.DELETE)
    @PostMapping("/expertAnswerDelete")
    public AjaxResult expertAnswerDelete(Long answerId)
    {
        return toAjax(service.deleteAnswer(answerId));
    }

    /**
     * 点赞/取消点赞（targetType：1问题 2回答）
     */
    @PostMapping("/expertLikeToggle")
    public AjaxResult expertLikeToggle(String targetType, Long targetId)
    {
        return AjaxResult.success(service.toggleLike(targetType, targetId));
    }

    /**
     * 我的问题分页列表（当前登录用户的提问）
     */
    @GetMapping("/expertMyQuestions")
    public TableDataInfo expertMyQuestions(ExpertQuestion query)
    {
        query.setUserId(SecurityUtils.getUserId());
        startPage();
        List<ExpertQuestion> list = service.selectQuestionList(query);
        return getDataTable(list);
    }

    /**
     * 我的回答分页列表（当前登录用户回答过的问题及回答内容）
     */
    @GetMapping("/expertMyAnswers")
    public TableDataInfo expertMyAnswers()
    {
        startPage();
        List<ExpertQuestion> list = service.selectMyAnsweredQuestions();
        return getDataTable(list);
    }

    /**
     * 作物圈子列表
     */
    @GetMapping("/expertCropList")
    public AjaxResult expertCropList()
    {
        return AjaxResult.success(service.selectCropList());
    }
}
