package com.ruoyi.binZhou.xwq.service;

import java.util.List;

import com.ruoyi.binZhou.xwq.domain.PhenologyRecognitionListVo;
import com.ruoyi.binZhou.xwq.domain.PhenologyRecognitionQuery;

/**
 * 物候期识别工作台历史记录查询Service。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
public interface IAiPhenologyRecognitionQueryService
{
    /**
     * 查询识别工作台历史记录。
     *
     * @param query 查询条件
     * @return 历史记录
     */
    public List<PhenologyRecognitionListVo> selectRecognitionList(
            PhenologyRecognitionQuery query);
}
