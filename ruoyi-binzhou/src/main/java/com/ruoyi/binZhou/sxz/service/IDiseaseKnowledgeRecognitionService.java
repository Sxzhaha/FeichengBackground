package com.ruoyi.binZhou.sxz.service;

import java.util.Map;

public interface IDiseaseKnowledgeRecognitionService
{
    Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode);

    Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode,
            Integer maxRetryCount);
}
