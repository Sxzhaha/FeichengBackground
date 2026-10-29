package com.ruoyi.binZhou.xwq.mapper;

import java.util.List;

import com.ruoyi.binZhou.xwq.domain.PhenologyRecognitionListVo;
import com.ruoyi.binZhou.xwq.domain.PhenologyRecognitionQuery;

/**
 * 物候期识别工作台关联查询Mapper。
 *
 * 只提供只读查询，不修改原 AiPhenologyMapper CRUD 行为。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
public interface AiPhenologyRecognitionMapper
{
    /**
     * 查询物候期识别工作台历史记录。
     *
     * @param query 查询条件
     * @return 关联历史记录
     */
    public List<PhenologyRecognitionListVo> selectRecognitionList(
            PhenologyRecognitionQuery query);
}
