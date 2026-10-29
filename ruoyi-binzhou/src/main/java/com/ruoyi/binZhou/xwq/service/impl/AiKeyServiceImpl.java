package com.ruoyi.binZhou.xwq.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.xwq.mapper.AiKeyMapper;
import com.ruoyi.binZhou.xwq.domain.AiKey;
import com.ruoyi.binZhou.xwq.service.IAiKeyService;

/**
 * 密钥管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class AiKeyServiceImpl implements IAiKeyService 
{
    @Autowired
    private AiKeyMapper aiKeyMapper;

    /**
     * 查询密钥管理
     * 
     * @param keyId 密钥管理主键
     * @return 密钥管理
     */
    @Override
    public AiKey selectAiKeyByKeyId(Long keyId)
    {
        return aiKeyMapper.selectAiKeyByKeyId(keyId);
    }

    /**
     * 查询密钥管理列表
     * 
     * @param aiKey 密钥管理
     * @return 密钥管理
     */
    @Override
    public List<AiKey> selectAiKeyList(AiKey aiKey)
    {
        return aiKeyMapper.selectAiKeyList(aiKey);
    }

    /**
     * 新增密钥管理
     * 
     * @param aiKey 密钥管理
     * @return 结果
     */
    @Override
    public int insertAiKey(AiKey aiKey)
    {
        return aiKeyMapper.insertAiKey(aiKey);
    }

    /**
     * 修改密钥管理
     * 
     * @param aiKey 密钥管理
     * @return 结果
     */
    @Override
    public int updateAiKey(AiKey aiKey)
    {
        return aiKeyMapper.updateAiKey(aiKey);
    }

    /**
     * 批量删除密钥管理
     * 
     * @param keyIds 需要删除的密钥管理主键
     * @return 结果
     */
    @Override
    public int deleteAiKeyByKeyIds(Long[] keyIds)
    {
        return aiKeyMapper.deleteAiKeyByKeyIds(keyIds);
    }

    /**
     * 删除密钥管理信息
     * 
     * @param keyId 密钥管理主键
     * @return 结果
     */
    @Override
    public int deleteAiKeyByKeyId(Long keyId)
    {
        return aiKeyMapper.deleteAiKeyByKeyId(keyId);
    }
}
