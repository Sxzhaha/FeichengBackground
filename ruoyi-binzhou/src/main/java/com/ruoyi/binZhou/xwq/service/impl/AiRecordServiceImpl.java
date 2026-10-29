package com.ruoyi.binZhou.xwq.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.binZhou.xwq.mapper.AiRecordMapper;
import com.ruoyi.binZhou.xwq.domain.AiRecord;
import com.ruoyi.binZhou.xwq.service.IAiRecordService;

/**
 * 识别记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class AiRecordServiceImpl implements IAiRecordService 
{
    @Autowired
    private AiRecordMapper aiRecordMapper;

    /**
     * 查询识别记录
     * 
     * @param recordId 识别记录主键
     * @return 识别记录
     */
    @Override
    public AiRecord selectAiRecordByRecordId(Long recordId)
    {
        return aiRecordMapper.selectAiRecordByRecordId(recordId);
    }

    /**
     * 查询识别记录列表
     * 
     * @param aiRecord 识别记录
     * @return 识别记录
     */
    @Override
    public List<AiRecord> selectAiRecordList(AiRecord aiRecord)
    {
        return aiRecordMapper.selectAiRecordList(aiRecord);
    }

    /**
     * 新增识别记录
     * 
     * @param aiRecord 识别记录
     * @return 结果
     */
    @Override
    public int insertAiRecord(AiRecord aiRecord)
    {
        aiRecord.setCreateBy(getCurrentUsername());
        aiRecord.setCreateTime(DateUtils.getNowDate());
        return aiRecordMapper.insertAiRecord(aiRecord);
    }

    /**
     * 修改识别记录
     * 
     * @param aiRecord 识别记录
     * @return 结果
     */
    @Override
    public int updateAiRecord(AiRecord aiRecord)
    {
        aiRecord.setUpdateBy(getCurrentUsername());
        aiRecord.setUpdateTime(DateUtils.getNowDate());
        return aiRecordMapper.updateAiRecord(aiRecord);
    }

    /**
     * 批量删除识别记录
     * 
     * @param recordIds 需要删除的识别记录主键
     * @return 结果
     */
    @Override
    public int deleteAiRecordByRecordIds(Long[] recordIds)
    {
        return aiRecordMapper.deleteAiRecordByRecordIds(recordIds);
    }

    /**
     * 删除识别记录信息
     * 
     * @param recordId 识别记录主键
     * @return 结果
     */
    @Override
    public int deleteAiRecordByRecordId(Long recordId)
    {
        return aiRecordMapper.deleteAiRecordByRecordId(recordId);
    }

    /**
     * 获取当前操作人。
     * 登录请求使用当前用户名；自动任务或无登录上下文时使用 system。
     *
     * @return 操作人用户名
     */
    private String getCurrentUsername()
    {
        try
        {
            String username = SecurityUtils.getUsername();
            return username == null || username.trim().isEmpty()
                    ? "system"
                    : username;
        }
        catch (Exception e)
        {
            return "system";
        }
    }
}
