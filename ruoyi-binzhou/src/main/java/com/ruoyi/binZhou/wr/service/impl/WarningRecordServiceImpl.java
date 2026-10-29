package com.ruoyi.binZhou.wr.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.ruoyi.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.wr.mapper.WarningRecordMapper;
import com.ruoyi.binZhou.wr.domain.WarningRecord;
import com.ruoyi.binZhou.wr.service.IWarningRecordService;
import com.ruoyi.binZhou.wr.service.AiDecisionService;
import org.apache.commons.lang3.StringUtils;

/**
 * 预警记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
@Service
public class WarningRecordServiceImpl implements IWarningRecordService 
{
    private static final Logger log = LoggerFactory.getLogger(WarningRecordServiceImpl.class);

    @Autowired
    private WarningRecordMapper warningRecordMapper;

    @Autowired
    private AiDecisionService aiDecisionService;

    /**
     * 查询预警记录
     * 
     * @param id 预警记录主键
     * @return 预警记录
     */
    @Override
    public WarningRecord selectWarningRecordById(Long id)
    {
        return warningRecordMapper.selectWarningRecordById(id);
    }

    /**
     * 查询预警记录列表
     * 
     * @param warningRecord 预警记录
     * @return 预警记录
     */
    @Override
    public List<WarningRecord> selectWarningRecordList(WarningRecord warningRecord)
    {
        return warningRecordMapper.selectWarningRecordList(warningRecord);
    }

    /**
     * 新增预警记录
     * 
     * @param warningRecord 预警记录
     * @return 结果
     */
        @Override
    public int insertWarningRecord(WarningRecord warningRecord)
    {
        boolean needGenerateAiSuggestion = shouldGenerateAiSuggestion(warningRecord);
        if (needGenerateAiSuggestion) {
            warningRecord.setAiSuggestion("AI建议生成中，请稍后刷新查看");
        }

        

        warningRecord.setCreateTime(DateUtils.getNowDate());
        int rows = warningRecordMapper.insertWarningRecord(warningRecord);
        if (rows > 0 && needGenerateAiSuggestion) {
            generateAiSuggestionAsync(warningRecord);
        }
        return rows;
    }

    private boolean shouldGenerateAiSuggestion(WarningRecord warningRecord)
    {
        return warningRecord != null
                && StringUtils.isBlank(warningRecord.getAiSuggestion())
                && StringUtils.isNotBlank(warningRecord.getIndicatorCode())
                && warningRecord.getCurrentValue() != null;
    }

    private void generateAiSuggestionAsync(WarningRecord warningRecord)
    {
        Long recordId = warningRecord.getId();
        CompletableFuture.runAsync(() -> {
            try {
                String aiAdvice = aiDecisionService.generateAdvice(warningRecord);
                WarningRecord updateRecord = new WarningRecord();
                updateRecord.setId(recordId);
                updateRecord.setAiSuggestion(aiAdvice);
                updateRecord.setUpdateTime(DateUtils.getNowDate());
                warningRecordMapper.updateWarningRecord(updateRecord);
                log.info("预警记录 [{}] AI 建议已异步生成", recordId);
            } catch (Exception e) {
                log.error("预警记录 [" + recordId + "] AI 建议异步生成失败", e);
            }
        });
    }

    /**
     * 修改预警记录
     * 
     * @param warningRecord 预警记录
     * @return 结果
     */
    @Override
    public int updateWarningRecord(WarningRecord warningRecord)
    {
        warningRecord.setUpdateTime(DateUtils.getNowDate());
        return warningRecordMapper.updateWarningRecord(warningRecord);
    }

    /**
     * 批量删除预警记录
     * 
     * @param ids 需要删除的预警记录主键
     * @return 结果
     */
    @Override
    public int deleteWarningRecordByIds(Long[] ids)
    {
        return warningRecordMapper.deleteWarningRecordByIds(ids);
    }

    /**
     * 删除预警记录信息
     * 
     * @param id 预警记录主键
     * @return 结果
     */
    @Override
    public int deleteWarningRecordById(Long id)
    {
        return warningRecordMapper.deleteWarningRecordById(id);
    }
}

