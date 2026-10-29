package com.ruoyi.binZhou.smx.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 专家问答-点赞记录Mapper接口
 *
 * @author smx
 */
@Mapper
public interface ExpertLikeMapper
{
    /**
     * 判断当前用户是否已点赞目标（1已赞 0未赞）
     */
    int existsLike(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

    /**
     * 新增点赞记录（目标已存在时忽略）
     */
    int insertLike(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

    /**
     * 删除点赞记录（取消点赞）
     */
    int deleteLike(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

    /**
     * 删除问题及其所有回答的点赞记录（删除问题时级联清理）
     */
    int deleteLikesByQuestion(@Param("questionId") Long questionId);

    /**
     * 删除单条回答的点赞记录（删除回答时级联清理）
     */
    int deleteLikesByAnswer(@Param("answerId") Long answerId);
}
