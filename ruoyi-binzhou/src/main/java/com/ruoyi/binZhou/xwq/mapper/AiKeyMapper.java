package com.ruoyi.binZhou.xwq.mapper;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.AiKey;

/**
 * 密钥管理Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface AiKeyMapper 
{
    /**
     * 查询密钥管理
     * 
     * @param keyId 密钥管理主键
     * @return 密钥管理
     */
    public AiKey selectAiKeyByKeyId(Long keyId);

    /**
     * 查询密钥管理列表
     * 
     * @param aiKey 密钥管理
     * @return 密钥管理集合
     */
    public List<AiKey> selectAiKeyList(AiKey aiKey);

    /**
     * 新增密钥管理
     * 
     * @param aiKey 密钥管理
     * @return 结果
     */
    public int insertAiKey(AiKey aiKey);

    /**
     * 修改密钥管理
     * 
     * @param aiKey 密钥管理
     * @return 结果
     */
    public int updateAiKey(AiKey aiKey);

    /**
     * 删除密钥管理
     * 
     * @param keyId 密钥管理主键
     * @return 结果
     */
    public int deleteAiKeyByKeyId(Long keyId);

    /**
     * 批量删除密钥管理
     * 
     * @param keyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiKeyByKeyIds(Long[] keyIds);
}
