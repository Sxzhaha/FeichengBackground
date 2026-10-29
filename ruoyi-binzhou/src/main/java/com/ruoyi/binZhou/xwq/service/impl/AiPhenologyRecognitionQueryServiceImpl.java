package com.ruoyi.binZhou.xwq.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ruoyi.binZhou.xwq.domain.PhenologyRecognitionListVo;
import com.ruoyi.binZhou.xwq.domain.PhenologyRecognitionQuery;
import com.ruoyi.binZhou.xwq.mapper.AiPhenologyRecognitionMapper;
import com.ruoyi.binZhou.xwq.service.IAiPhenologyRecognitionQueryService;

/**
 * 物候期识别工作台历史记录查询Service实现。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
@Service
public class AiPhenologyRecognitionQueryServiceImpl
        implements IAiPhenologyRecognitionQueryService
{
    private final AiPhenologyRecognitionMapper recognitionMapper;

    public AiPhenologyRecognitionQueryServiceImpl(
            AiPhenologyRecognitionMapper recognitionMapper)
    {
        this.recognitionMapper = recognitionMapper;
    }

    @Override
    public List<PhenologyRecognitionListVo> selectRecognitionList(
            PhenologyRecognitionQuery query)
    {
        if (query == null)
        {
            query = new PhenologyRecognitionQuery();
        }
        return recognitionMapper.selectRecognitionList(query);
    }
}
