package com.ruoyi.binZhou.xwq.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.domain.AiPhenology;
import com.ruoyi.binZhou.xwq.domain.AiRecord;
import com.ruoyi.binZhou.xwq.domain.PhenologyModelResult;
import com.ruoyi.binZhou.xwq.domain.PhenologyPredictRequest;
import com.ruoyi.binZhou.xwq.domain.PhenologyPredictResultVo;
import com.ruoyi.binZhou.xwq.service.IAiImageService;
import com.ruoyi.binZhou.xwq.service.IAiPhenologyRecognitionTransactionService;
import com.ruoyi.binZhou.xwq.service.IAiPhenologyService;
import com.ruoyi.binZhou.xwq.service.IAiRecordService;
import com.ruoyi.common.exception.ServiceException;

/**
 * 物候期识别数据库事务服务实现。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
@Service
public class AiPhenologyRecognitionTransactionServiceImpl
        implements IAiPhenologyRecognitionTransactionService
{
    private final IAiRecordService aiRecordService;
    private final IAiPhenologyService aiPhenologyService;
    private final IAiImageService aiImageService;

    public AiPhenologyRecognitionTransactionServiceImpl(
            IAiRecordService aiRecordService,
            IAiPhenologyService aiPhenologyService,
            IAiImageService aiImageService)
    {
        this.aiRecordService = aiRecordService;
        this.aiPhenologyService = aiPhenologyService;
        this.aiImageService = aiImageService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiRecord createProcessingRecord(
            AiImage image,
            AiModel model,
            PhenologyPredictRequest request)
    {
        AiRecord record = new AiRecord();
        record.setRecordNo(buildRecordNo());
        record.setRecordType("phenology");
        record.setRecordMode(defaultRecordMode(request.getRecordMode()));
        record.setModelId(model.getModelId());
        record.setModelName(model.getModelName());
        record.setImageId(image.getImageId());
        record.setCropType(request.getCropType());
        record.setDeviceId(image.getDeviceId());
        record.setParkId(image.getParkId());
        record.setPlotId(image.getPlotId());
        record.setRecordStatus(1L);

        int rows = aiRecordService.insertAiRecord(record);
        if (rows <= 0 || record.getRecordId() == null)
        {
            throw new ServiceException("创建物候期识别记录失败");
        }

        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhenologyPredictResultVo saveSuccess(
            AiRecord record,
            AiImage image,
            AiModel model,
            PhenologyPredictRequest request,
            PhenologyModelResult modelResult)
    {
        AiPhenology phenology = new AiPhenology();
        phenology.setRecordId(record.getRecordId());
        phenology.setImageId(image.getImageId());
        phenology.setPhenologyName(modelResult.getPhenologyName());
        phenology.setPhenologyCode(modelResult.getPhenologyCode());
        phenology.setPhenologyCrop(request.getCropType());
        phenology.setPhenologyConfidence(
                modelResult.getPhenologyConfidence());
        phenology.setPhenologyResult(modelResult.getPhenologyResult());
        phenology.setPhenologyDesc(modelResult.getPhenologyDesc());
        phenology.setPhenologyAdvice(modelResult.getPhenologyAdvice());
        phenology.setPhenologyNextName(
                modelResult.getPhenologyNextName());
        phenology.setPhenologyResultImgUrl(
                modelResult.getPhenologyResultImgUrl());

        int phenologyRows =
                aiPhenologyService.insertAiPhenology(phenology);
        if (phenologyRows <= 0 || phenology.getPhenologyId() == null)
        {
            throw new ServiceException("保存物候期识别结果失败");
        }

        record.setRecordStatus(2L);
        int recordRows = aiRecordService.updateAiRecord(record);
        if (recordRows <= 0)
        {
            throw new ServiceException("更新识别记录状态失败");
        }

        image.setImageStatus(1L);
        if (hasText(modelResult.getPhenologyResultImgUrl()))
        {
            image.setImageResultUrl(
                    modelResult.getPhenologyResultImgUrl());
        }

        int imageRows = aiImageService.updateAiImage(image);
        if (imageRows <= 0)
        {
            throw new ServiceException("更新图片识别状态失败");
        }

        return buildResultVo(
                record,
                image,
                model,
                phenology);
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = Exception.class)
    public void saveFailure(
            AiRecord record,
            AiImage image,
            String errorMessage)
    {
        if (record != null && record.getRecordId() != null)
        {
            record.setRecordStatus(3L);
            record.setRecordA(limitLength(errorMessage, 500));
            aiRecordService.updateAiRecord(record);
        }

        if (image != null && image.getImageId() != null)
        {
            image.setImageStatus(2L);
            aiImageService.updateAiImage(image);
        }
    }

    private PhenologyPredictResultVo buildResultVo(
            AiRecord record,
            AiImage image,
            AiModel model,
            AiPhenology phenology)
    {
        PhenologyPredictResultVo vo =
                new PhenologyPredictResultVo();

        vo.setRecordId(record.getRecordId());
        vo.setPhenologyId(phenology.getPhenologyId());
        vo.setImageId(image.getImageId());
        vo.setImageName(image.getImageName());
        vo.setImageUrl(image.getImageUrl());

        vo.setModelId(model.getModelId());
        vo.setModelName(model.getModelName());

        vo.setPhenologyName(phenology.getPhenologyName());
        vo.setPhenologyCode(phenology.getPhenologyCode());
        vo.setPhenologyCrop(phenology.getPhenologyCrop());
        vo.setPhenologyConfidence(
                phenology.getPhenologyConfidence());
        vo.setPhenologyResult(phenology.getPhenologyResult());
        vo.setPhenologyDesc(phenology.getPhenologyDesc());
        vo.setPhenologyAdvice(phenology.getPhenologyAdvice());
        vo.setPhenologyNextName(
                phenology.getPhenologyNextName());
        vo.setPhenologyResultImgUrl(
                phenology.getPhenologyResultImgUrl());

        vo.setRecordMode(record.getRecordMode());
        vo.setRecordStatus(record.getRecordStatus());
        vo.setCreateTime(new Date());
        return vo;
    }

    private String buildRecordNo()
    {
        String time = new SimpleDateFormat(
                "yyyyMMddHHmmssSSS").format(new Date());
        String random = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        return "PH" + time + random;
    }

    private String defaultRecordMode(String recordMode)
    {
        return hasText(recordMode)
                ? recordMode.trim()
                : "手动上传";
    }

    private boolean hasText(String value)
    {
        return value != null && !value.trim().isEmpty();
    }

    private String limitLength(String value, int maxLength)
    {
        if (value == null)
        {
            return "未知错误";
        }
        return value.length() <= maxLength
                ? value
                : value.substring(0, maxLength);
    }
}
