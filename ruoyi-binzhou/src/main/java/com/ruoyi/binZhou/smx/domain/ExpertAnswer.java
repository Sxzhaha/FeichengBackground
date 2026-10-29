package com.ruoyi.binZhou.smx.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专家问答-回答对象 smx_expert_answer
 *
 * @author smx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpertAnswer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 回答ID */
    private Long answerId;

    /** 问题ID */
    private Long questionId;

    /** 回答用户ID */
    private Long answerUserId;

    /** 专家姓名 */
    private String expertName;

    /** 回答者职称（如高级农艺师） */
    private String expertTitle;

    /** 专家所属机构 */
    private String expertOrg;

    /** 专家头像 */
    private String expertAvatar;

    /** 回答内容 */
    private String content;

    /** 回答图片URL，JSON数组 */
    private String images;

    /** 回答图片（移动端入参，非数据库列） */
    private java.util.List<String> photos;

    /** 是否被采纳：0否 1是 */
    private String isAdoption;

    /** 点赞数 */
    private Long likeCount;

    /** 删除标志：0存在 2删除 */
    private String delFlag;

    /* ===== 以下为移动端展示字段 ===== */

    /** 回答时间 yyyy-MM-dd HH:mm */
    private String date;

    /** 回答者（姓名 职称） */
    private String expert;

    /** 当前登录用户是否已点赞该回答 */
    private Boolean liked;

    /** 当前登录用户是否可删除该回答（回答者本人或管理员） */
    private Boolean canDelete;
}
