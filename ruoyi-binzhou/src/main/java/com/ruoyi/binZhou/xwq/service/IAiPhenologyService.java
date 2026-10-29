package com.ruoyi.binZhou.xwq.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.binZhou.xwq.domain.AiPhenology;

/**
 * 物候期识别结果Service接口
 *
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IAiPhenologyService
{
    /**
     * 查询物候期识别结果
     *
     * @param phenologyId 物候期识别结果主键
     * @return 物候期识别结果
     */
    public AiPhenology selectAiPhenologyByPhenologyId(
            Long phenologyId);

    /**
     * 查询物候期识别结果列表
     *
     * @param aiPhenology 物候期识别结果
     * @return 物候期识别结果集合
     */
    public List<AiPhenology> selectAiPhenologyList(
            AiPhenology aiPhenology);

    /**
     * 新增物候期识别结果
     *
     * @param aiPhenology 物候期识别结果
     * @return 结果
     */
    public int insertAiPhenology(
            AiPhenology aiPhenology);

    /**
     * 修改物候期识别结果
     *
     * @param aiPhenology 物候期识别结果
     * @return 结果
     */
    public int updateAiPhenology(
            AiPhenology aiPhenology);

    /**
     * 批量删除物候期识别结果
     *
     * @param phenologyIds 需要删除的物候期识别结果主键集合
     * @return 结果
     */
    public int deleteAiPhenologyByPhenologyIds(
            Long[] phenologyIds);

    /**
     * 删除物候期识别结果信息
     *
     * @param phenologyId 物候期识别结果主键
     * @return 结果
     */
    public int deleteAiPhenologyByPhenologyId(
            Long phenologyId);

    /**
     * 识别临时上传的物候图片。
     *
     * 模型成功后，才在同一个事务中写入图片表、
     * 识别记录表和物候结果表。
     *
     * @param params 前端上传信息、模型ID及作物类型
     * @return 识别结果和三个业务主键
     */
    public Map<String, Object> predictUploadedImage(
            Map<String, Object> params);

    /**
     * 根据真实物候期识别记录生成大模型报告文本。
     *
     * @param params 勾选ID或当前查询条件
     * @return 固定章节标记的报告正文
     */
    public String generateReportText(
            Map<String, Object> params);
}
