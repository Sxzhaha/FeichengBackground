package com.ruoyi.binZhou.xwq.mapper;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.AiRecord;

/**
 * 识别记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface AiRecordMapper 
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
     * 删除识别记录
     * 
     * @param recordId 识别记录主键
     * @return 结果
     */
    public int deleteAiRecordByRecordId(Long recordId);

    /**
     * 批量删除识别记录
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiRecordByRecordIds(Long[] recordIds);
}
