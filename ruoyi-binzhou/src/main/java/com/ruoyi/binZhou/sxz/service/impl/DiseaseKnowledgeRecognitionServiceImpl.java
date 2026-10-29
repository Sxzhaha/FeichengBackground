package com.ruoyi.binZhou.sxz.service.impl;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.binZhou.sxz.domain.DiseaseKnowledgeAdvice;
import com.ruoyi.binZhou.sxz.mapper.DiseaseKnowledgeRecognitionMapper;
import com.ruoyi.binZhou.sxz.service.IDiseaseKnowledgeRecognitionService;
import com.ruoyi.binZhou.xwq.service.IAiDiseaseService;

@Service
public class DiseaseKnowledgeRecognitionServiceImpl implements IDiseaseKnowledgeRecognitionService
{
    private static final Logger log = LoggerFactory.getLogger(DiseaseKnowledgeRecognitionServiceImpl.class);

    private final IAiDiseaseService aiDiseaseService;
    private final DiseaseKnowledgeRecognitionMapper mapper;

    public DiseaseKnowledgeRecognitionServiceImpl(IAiDiseaseService aiDiseaseService,
            DiseaseKnowledgeRecognitionMapper mapper)
    {
        this.aiDiseaseService = aiDiseaseService;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode)
    {
        Map<String, Object> result = aiDiseaseService.predictByImage(imageId, modelId, cropType, recordMode);
        enrichWithKnowledge(result, cropType);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode,
            Integer maxRetryCount)
    {
        Map<String, Object> result = aiDiseaseService.predictByImage(
                imageId, modelId, cropType, recordMode, maxRetryCount);
        enrichWithKnowledge(result, cropType);
        return result;
    }

    private void enrichWithKnowledge(Map<String, Object> result, String cropType)
    {
        if (result == null)
        {
            return;
        }

        String diseaseName = asText(result.get("diseaseName"));
        Long diseaseId = asLong(result.get("diseaseId"));
        if (diseaseName == null || diseaseId == null || isHealthyResult(diseaseName))
        {
            result.put("knowledgeMatched", false);
            return;
        }

        try
        {
            DiseaseKnowledgeAdvice knowledge = mapper.selectKnowledgeAdvice(diseaseName, cropType);
            if (knowledge == null || isBlank(knowledge.getControlMethod()))
            {
                result.put("knowledgeMatched", false);
                return;
            }

            mapper.updateDiseaseKnowledgeAdvice(
                    diseaseId,
                    knowledge.getDamageSymptom(),
                    knowledge.getControlMethod());

            result.put("diseaseAdvice", knowledge.getControlMethod());
            if (isBlank(asText(result.get("diseaseDesc"))) || "暂无症状描述".equals(asText(result.get("diseaseDesc"))))
            {
                result.put("diseaseDesc", knowledge.getDamageSymptom());
            }
            result.put("knowledgeMatched", true);
            result.put("knowledgeId", knowledge.getKnowledgeId());
            result.put("knowledgeName", knowledge.getKnowledgeName());
            result.put("knowledgeIntroduction", knowledge.getIntroduction());
            result.put("knowledgeDamageSymptom", knowledge.getDamageSymptom());
            result.put("knowledgeOccurrenceRule", knowledge.getOccurrenceRule());
        }
        catch (Exception e)
        {
            // 知识增强是识别后的附加步骤，异常时保留模型原始结果，避免阻断主识别流程。
            log.warn("病虫害知识匹配失败，diseaseId={}, diseaseName={}", diseaseId, diseaseName, e);
            result.put("knowledgeMatched", false);
        }
    }

    private boolean isHealthyResult(String diseaseName)
    {
        String normalized = diseaseName.replace(" ", "");
        return normalized.contains("健康") || normalized.contains("无病害");
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }

    private String asText(Object value)
    {
        if (value == null)
        {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }

    private Long asLong(Object value)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        try
        {
            return Long.valueOf(value.toString());
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
}
