package com.ruoyi.binZhou.smx.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专家问答-作物圈子对象 smx_expert_crop
 *
 * @author smx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpertCrop extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 作物ID */
    private Long cropId;

    /** 分类：粮食作物/蔬菜/水果/其他 */
    private String category;

    /** 作物名称 */
    private String crop;

    /** 作物下所有未删除问答的浏览量总和（前端显示为关注人数） */
    private Long followers;

    /** 问答总数（从问题表聚合） */
    private Long total;
}
