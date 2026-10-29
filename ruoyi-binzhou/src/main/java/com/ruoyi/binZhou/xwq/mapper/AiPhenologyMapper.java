package com.ruoyi.binZhou.xwq.mapper;

import java.util.List;
import com.ruoyi.binZhou.xwq.domain.AiPhenology;

/**
 * 物候期识别结果Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface AiPhenologyMapper 
{
    /**
     * 查询物候期识别结果
     * 
     * @param phenologyId 物候期识别结果主键
     * @return 物候期识别结果
     */
    public AiPhenology selectAiPhenologyByPhenologyId(Long phenologyId);

    /**
     * 查询物候期识别结果列表
     * 
     * @param aiPhenology 物候期识别结果
     * @return 物候期识别结果集合
     */
    public List<AiPhenology> selectAiPhenologyList(AiPhenology aiPhenology);

    /**
     * 新增物候期识别结果
     * 
     * @param aiPhenology 物候期识别结果
     * @return 结果
     */
    public int insertAiPhenology(AiPhenology aiPhenology);

    /**
     * 修改物候期识别结果
     * 
     * @param aiPhenology 物候期识别结果
     * @return 结果
     */
    public int updateAiPhenology(AiPhenology aiPhenology);

    /**
     * 删除物候期识别结果
     * 
     * @param phenologyId 物候期识别结果主键
     * @return 结果
     */
    public int deleteAiPhenologyByPhenologyId(Long phenologyId);

    /**
     * 批量删除物候期识别结果
     * 
     * @param phenologyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiPhenologyByPhenologyIds(Long[] phenologyIds);
}
