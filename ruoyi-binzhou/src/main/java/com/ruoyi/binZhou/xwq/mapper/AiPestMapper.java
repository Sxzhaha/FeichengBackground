package com.ruoyi.binZhou.xwq.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.binZhou.xwq.domain.AiPest;

/**
 * 虫情识别结果Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface AiPestMapper 
{
    /**
     * 查询虫情识别结果
     * 
     * @param pestId 虫情识别结果主键
     * @return 虫情识别结果
     */
    public AiPest selectAiPestByPestId(Long pestId);

    /**
     * 查询虫情识别结果列表
     * 
     * @param aiPest 虫情识别结果
     * @return 虫情识别结果集合
     */
    public List<AiPest> selectAiPestList(AiPest aiPest);

    /**
     * 新增虫情识别结果
     * 
     * @param aiPest 虫情识别结果
     * @return 结果
     */
    public int insertAiPest(AiPest aiPest);

    /**
     * 修改虫情识别结果
     * 
     * @param aiPest 虫情识别结果
     * @return 结果
     */
    public int updateAiPest(AiPest aiPest);

    /**
     * 删除虫情识别结果
     * 
     * @param pestId 虫情识别结果主键
     * @return 结果
     */
    public int deleteAiPestByPestId(Long pestId);

    /**
     * 批量删除虫情识别结果
     * 
     * @param pestIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiPestByPestIds(Long[] pestIds);

    /**
     * 查询最近7天虫情按天统计趋势
     *
     * @param params 查询参数
     * @return 日期与数量集合
     */
    public List<Map<String, Object>> selectPestHistoryTrend(Map<String, Object> params);
}
