package com.ruoyi.binZhou.xwq.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.binZhou.xwq.domain.AiDisease;

/**
 * 病虫害识别结果Service接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IAiDiseaseService 
{
    /**
     * 查询病虫害识别结果
     * 
     * @param diseaseId 病虫害识别结果主键
     * @return 病虫害识别结果
     */
    public AiDisease selectAiDiseaseByDiseaseId(Long diseaseId);

    /**
     * 查询病虫害识别结果列表
     * 
     * @param aiDisease 病虫害识别结果
     * @return 病虫害识别结果集合
     */
    public List<AiDisease> selectAiDiseaseList(AiDisease aiDisease);

    /**
     * 新增病虫害识别结果
     * 
     * @param aiDisease 病虫害识别结果
     * @return 结果
     */
    public int insertAiDisease(AiDisease aiDisease);

    /**
     * 修改病虫害识别结果
     * 
     * @param aiDisease 病虫害识别结果
     * @return 结果
     */
    public int updateAiDisease(AiDisease aiDisease);

    /**
     * 批量删除病虫害识别结果
     * 
     * @param diseaseIds 需要删除的病虫害识别结果主键集合
     * @return 结果
     */
    public int deleteAiDiseaseByDiseaseIds(Long[] diseaseIds);

    /**
     * 删除病虫害识别结果信息
     * 
     * @param diseaseId 病虫害识别结果主键
     * @return 结果
     */
    public int deleteAiDiseaseByDiseaseId(Long diseaseId);

    /**
     * 根据图片ID进行病虫害识别，并写入 AiRecord、AiDisease
     */
    Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode);

    /**
     * 自动识别专用重载：首次失败后最多再重试 maxRetryCount 次。
     * 原方法保持不变，手动识别不会因此自动重试。
     */
    Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode,
            Integer maxRetryCount);
}
