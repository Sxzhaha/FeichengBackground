package com.ruoyi.binZhou.smx.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.binZhou.smx.domain.ExpertAnswer;

/**
 * 专家问答-回答Mapper接口
 *
 * @author smx
 */
@Mapper
public interface ExpertAnswerMapper
{
    /**
     * 查询问题下的回答列表（userId 用于标记当前用户是否已点赞）
     */
    List<ExpertAnswer> selectAnswersByQuestionId(@Param("questionId") Long questionId, @Param("userId") Long userId);

    /**
     * 新增回答
     */
    int insertAnswer(ExpertAnswer answer);

    /**
     * 点赞数增减（delta 为 +1 或 -1）
     */
    int updateLikeCount(@Param("answerId") Long answerId, @Param("delta") int delta);

    /**
     * 查询回答最新点赞数
     */
    Long selectAnswerLikeCountById(Long answerId);

    /**
     * 查询单条回答（删除前校验存在性与归属）
     */
    ExpertAnswer selectAnswerMetaById(Long answerId);

    /**
     * 物理删除单条回答
     */
    int deleteAnswerById(Long answerId);

    /**
     * 删除问题下的全部回答
     */
    int deleteAnswersByQuestionId(Long questionId);

    /**
     * 查询用户主岗位名（作为回答展示的职称，无岗位返回 null）
     */
    String selectPostNameByUserId(Long userId);
}
