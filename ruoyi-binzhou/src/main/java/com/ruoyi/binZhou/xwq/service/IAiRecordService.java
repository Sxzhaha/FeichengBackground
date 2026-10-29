package com.ruoyi.binZhou.xwq.service;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.AiRecord;

/**
 * 识别记录Service接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IAiRecordService 
{
    /**
     * 查询识别记录
     * 
     * @param recordId 识别记录主键
     * @return 识别记录
     */
    public AiRecord selectAiRecordByRecordId(Long recordId);

    /**
     * 查询识别记录列表
     * 
     * @param aiRecord 识别记录
     * @return 识别记录集合
     */
    public List<AiRecord> selectAiRecordList(AiRecord aiRecord);

    /**
     * 新增识别记录
     * 
     * @param aiRecord 识别记录
     * @return 结果
     */
    public int insertAiRecord(AiRecord aiRecord);

    /**
     * 修改识别记录
     * 
     * @param aiRecord 识别记录
     * @return 结果
     */
    public int updateAiRecord(AiRecord aiRecord);

    /**
     * 批量删除识别记录
     * 
     * @param recordIds 需要删除的识别记录主键集合
     * @return 结果
     */
    public int deleteAiRecordByRecordIds(Long[] recordIds);

    /**
     * 删除识别记录信息
     * 
     * @param recordId 识别记录主键
     * @return 结果
     */
    public int deleteAiRecordByRecordId(Long recordId);
}
