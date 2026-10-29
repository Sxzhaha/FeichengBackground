package com.ruoyi.binZhou.sxz.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.binZhou.sxz.domain.DiseaseKnowledgeAdvice;

public interface DiseaseKnowledgeRecognitionMapper
{
    DiseaseKnowledgeAdvice selectKnowledgeAdvice(@Param("diseaseName") String diseaseName,
            @Param("cropType") String cropType);

    int updateDiseaseKnowledgeAdvice(@Param("diseaseId") Long diseaseId,
            @Param("damageSymptom") String damageSymptom,
            @Param("controlMethod") String controlMethod);
}
