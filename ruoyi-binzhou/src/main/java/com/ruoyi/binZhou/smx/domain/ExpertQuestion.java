package com.ruoyi.binZhou.smx.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专家问答-问题对象 smx_expert_question
 *
 * @author smx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpertQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 问题ID */
    private Long questionId;

    /** 提问用户ID */
    private Long userId;

    /** 提问用户昵称 */
    private String userName;

    /** 提问用户头像 */
    private String userAvatar;

    /** 作物名称 */
    @Excel(name = "作物")
    private String crop;

    /** 作物大类：粮食作物/蔬菜/水果/其他 */
    private String category;

    /** 问题标题 */
    @Excel(name = "问题标题")
    private String title;

    /** 问题描述 */
    private String content;

    /** 问题图片URL，JSON数组字符串 */
    private String images;

    /** 浏览量 */
    private Long viewCount;

    /** 点赞数 */
    private Long likeCount;

    /** 回复数 */
    private Long replyCount;

    /** 状态：0待回答 1已回答 */
    private String status;

    /** 删除标志：0存在 2删除 */
    private String delFlag;

    /** 检索关键词：模糊匹配标题、描述与作物（非数据库字段） */
    private String keyword;

    /* ===== 以下为移动端列表/详情展示字段 ===== */

    /** 前端列表ID */
    private Long id;

    /** 提问时间 yyyy-MM-dd HH:mm */
    private String date;

    /** 图片URL数组 */
    private String[] photos;

    /** 首答专家头像 */
    private String avatar;

    /** 首答回答者（姓名 职称） */
    private String expert;

    /** 首答内容摘要 */
    private String answer;

    /** 浏览量（前端别名） */
    private Long views;

    /** 点赞数（前端别名） */
    private Long likes;

    /** 回复数（前端别名） */
    private Long replies;

    /** AI识别结果JSON字符串 */
    private String aiResult;

    /** AI识别状态：0识别中 1完成 2失败/无图 */
    private String aiStatus;

    /** 当前登录用户是否可删除该问答（提问者本人或管理员） */
    private Boolean canDelete;

    /** 当前登录用户是否已点赞该问题 */
    private Boolean liked;

    /** 提问者地区（取自 sys_user.region） */
    private String region;

    /** 我的回答ID（我的回答列表用，非数据库列） */
    private Long answerId;

    /** 我的回答内容（我的回答列表用，非数据库列） */
    private String myAnswer;

    /** 我的回答图片JSON（我的回答列表用，非数据库列） */
    private String myAnswerImages;

    /** 我的回答时间（我的回答列表用，非数据库列） */
    private String myAnswerDate;
}
