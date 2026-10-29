package com.ruoyi.binZhou.smx.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.binZhou.smx.domain.ExpertAnswer;
import com.ruoyi.binZhou.smx.domain.ExpertCrop;
import com.ruoyi.binZhou.smx.domain.ExpertQuestion;

/**
 * 专家问答Service接口
 *
 * @author smx
 */
public interface IExpertQaService
{
    /**
     * 查询问题分页列表（聚合首答信息）
     */
    List<ExpertQuestion> selectQuestionList(ExpertQuestion query);

    /**
     * 查询问题详情（浏览量+1）
     */
    ExpertQuestion selectQuestionById(Long questionId);

    /**
     * 查询问题详情，可控制是否增加浏览量
     */
    ExpertQuestion selectQuestionById(Long questionId, boolean countView);

    /**
     * 新增问题（自动填充当前登录用户信息）
     */
    int insertQuestion(ExpertQuestion question);

    /**
     * 查询问题下的回答列表
     */
    List<ExpertAnswer> selectAnswersByQuestionId(Long questionId);

    /**
     * 新增回答（自动维护问题回复数与状态）
     */
    int insertAnswer(Long questionId, ExpertAnswer answer);

    /**
     * 删除问题及其全部回答、关联图片（仅提问者本人或管理员）
     */
    int deleteQuestion(Long questionId);

    /**
     * 删除单条回答（仅回答者本人或管理员，同步维护回复数与问题状态）
     */
    int deleteAnswer(Long answerId);

    /**
     * 点赞/取消点赞（targetType：1问题 2回答），返回 liked 与最新 likeCount
     */
    Map<String, Object> toggleLike(String targetType, Long targetId);

    /**
     * 作物圈子列表（含问答总数，冬枣置顶）
     */
    List<ExpertCrop> selectCropList();

    /**
     * 我的回答列表：当前用户回答过的问题及对应回答内容
     */
    List<ExpertQuestion> selectMyAnsweredQuestions();
}
