package com.ruoyi.binZhou.xwq.mapper;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.AiDisease;

/**
 * 病虫害识别结果Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface AiDiseaseMapper 
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
     * 删除病虫害识别结果
     * 
     * @param diseaseId 病虫害识别结果主键
     * @return 结果
     */
    public int deleteAiDiseaseByDiseaseId(Long diseaseId);

    /**
     * 批量删除病虫害识别结果
     * 
     * @param diseaseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiDiseaseByDiseaseIds(Long[] diseaseIds);
}
