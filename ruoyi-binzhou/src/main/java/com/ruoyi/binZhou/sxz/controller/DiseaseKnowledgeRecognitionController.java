package com.ruoyi.binZhou.sxz.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.binZhou.sxz.domain.DiseaseRecognitionRequest;
import com.ruoyi.binZhou.sxz.service.IDiseaseKnowledgeRecognitionService;
import com.ruoyi.common.core.domain.AjaxResult;

@RestController
@RequestMapping("/sxz/disease-recognition")
public class DiseaseKnowledgeRecognitionController
{
    private final IDiseaseKnowledgeRecognitionService service;

    public DiseaseKnowledgeRecognitionController(IDiseaseKnowledgeRecognitionService service)
    {
        this.service = service;
    }

    @PostMapping("/predictByImage")
    public AjaxResult predictByImage(@RequestBody DiseaseRecognitionRequest request)
    {
        if (request == null || request.getImageId() == null)
        {
            return AjaxResult.error("imageId不能为空");
        }
        if (request.getModelId() == null)
        {
            return AjaxResult.error("modelId不能为空，请先选择病虫害识别模型");
        }

        String cropType = isBlank(request.getCropType()) ? "枣树" : request.getCropType().trim();
        String recordMode = isBlank(request.getRecordMode()) ? "手动上传" : request.getRecordMode().trim();
        return AjaxResult.success(
                "识别成功",
                service.predictByImage(request.getImageId(), request.getModelId(), cropType, recordMode));
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }
}
