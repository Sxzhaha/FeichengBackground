package com.ruoyi.binZhou.sxz.service;

import java.util.Map;

public interface IDiseaseKnowledgeRecognitionService
{
    void checkImageAccess(Long imageId);

    Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode);

    Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode,
            Integer maxRetryCount);
}
