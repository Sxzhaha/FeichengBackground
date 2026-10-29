package com.ruoyi.binZhou.sxz.service.support;

import com.ruoyi.binZhou.xwq.domain.AiDisease;
import com.ruoyi.binZhou.xwq.domain.AiRecord;
import com.ruoyi.binZhou.xwq.mapper.AiDiseaseMapper;
import com.ruoyi.binZhou.xwq.mapper.AiImageMapper;
import com.ruoyi.binZhou.xwq.mapper.AiRecordMapper;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 病虫害识别状态持久化。
 *
 * 模型网络调用不放在数据库事务中；开始、成功、失败分别使用短事务落库。
 */
@Service
public class DiseaseRecognitionPersistenceService
{
    @Autowired
    private AiDiseaseMapper aiDiseaseMapper;

    @Autowired
    private AiRecordMapper aiRecordMapper;

    @Autowired
    private AiImageMapper aiImageMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createProcessingRecord(AiRecord record, Long imageId)
    {
        record.setRecordStatus(1L);
        if (aiRecordMapper.insertAiRecord(record) <= 0)
        {
            throw new ServiceException("创建病虫害识别记录失败");
        }
        // 自动识别任务可能已提前将图片置为识别中，重复更新不作为失败。
        aiImageMapper.updateImageStatusById(imageId, 1L);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeRecognition(AiDisease disease, AiRecord record, Long imageId)
    {
        if (aiDiseaseMapper.insertAiDisease(disease) <= 0)
        {
            throw new ServiceException("保存病虫害识别结果失败");
        }
        record.setRecordStatus(2L);
        record.setRecordA(null);
        if (aiRecordMapper.updateAiRecord(record) <= 0)
        {
            throw new ServiceException("更新病虫害识别成功状态失败");
        }
        if (aiImageMapper.updateImageStatusById(imageId, 2L) <= 0)
        {
            throw new ServiceException("更新图片识别成功状态失败");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failRecognition(AiRecord record, Long imageId, String errorMessage)
    {
        if (record != null && record.getRecordId() != null)
        {
            record.setRecordStatus(3L);
            record.setRecordA(limit(errorMessage, 500));
            aiRecordMapper.updateAiRecord(record);
        }
        if (imageId != null)
        {
            aiImageMapper.updateImageStatusById(imageId, 3L);
        }
    }

    private String limit(String value, int maxLength)
    {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
