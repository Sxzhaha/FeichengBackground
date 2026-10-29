package com.ruoyi.binZhou.smx.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.binZhou.smx.domain.ExpertQuestion;

/**
 * 专家问答-问题Mapper接口
 *
 * @author smx
 */
@Mapper
public interface ExpertQuestionMapper
{
    /**
     * 查询问题列表（聚合首答专家信息）
     */
    List<ExpertQuestion> selectQuestionList(ExpertQuestion query);

    /**
     * 查询问题详情
     */
    ExpertQuestion selectQuestionById(Long questionId);

    /**
     * 删除前读取问题图片字段，不增加浏览量
     */
    String selectQuestionImagesById(Long questionId);

    /**
     * 判断问题是否存在
     */
    int existsQuestionById(Long questionId);

    /**
     * 查询问题归属用户ID（删除权限校验用）
     */
    Long selectQuestionUserIdById(Long questionId);

    /**
     * 新增问题
     */
    int insertQuestion(ExpertQuestion question);

    /**
     * 浏览量+1
     */
    int increaseViewCount(Long questionId);

    /**
     * 点赞数增减（delta 为 +1 或 -1）
     */
    int updateLikeCount(@Param("questionId") Long questionId, @Param("delta") int delta);

    /**
     * 查询问题最新点赞数
     */
    Long selectQuestionLikeCountById(Long questionId);

    /**
     * 回复数+1并置为已回答
     */
    int increaseReplyCount(Long questionId);

    /**
     * 回复数-1（删除回答时）
     */
    int decreaseReplyCount(Long questionId);

    /**
     * 按是否仍有回答刷新问题状态（有=已回答1，无=待回答0）
     */
    int refreshQuestionAnswerStatus(Long questionId);

    /**
     * 物理删除问题
     */
    int deleteQuestionById(Long questionId);

    /**
     * 更新AI识别结果
     */
    int updateAiResult(@Param("questionId") Long questionId, @Param("aiResult") String aiResult, @Param("aiStatus") String aiStatus);

    /**
     * 我的回答列表：当前用户回答过的问题及对应回答内容
     */
    List<ExpertQuestion> selectMyAnsweredQuestions(Long userId);
}
