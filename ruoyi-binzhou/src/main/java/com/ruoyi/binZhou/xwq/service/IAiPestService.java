package com.ruoyi.binZhou.xwq.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.binZhou.xwq.domain.AiPest;

/**
 * 虫情识别结果Service接口
 *
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IAiPestService
{
    /**
     * 查询虫情识别结果
     */
    public AiPest selectAiPestByPestId(Long pestId);

    /**
     * 查询虫情识别结果列表
     */
    public List<AiPest> selectAiPestList(AiPest aiPest);

    /**
     * 新增虫情识别结果
     */
    public int insertAiPest(AiPest aiPest);

    /**
     * 修改虫情识别结果
     */
    public int updateAiPest(AiPest aiPest);

    /**
     * 批量删除虫情识别结果
     */
    public int deleteAiPestByPestIds(Long[] pestIds);

    /**
     * 删除虫情识别结果信息
     */
    public int deleteAiPestByPestId(Long pestId);

    /**
     * 根据勾选记录或筛选条件生成虫情大模型报告文本。
     *
     * @param params 勾选ID或当前查询条件
     * @return 固定章节标记的报告正文
     */
    String generateReportText(Map<String, Object> params);

    /**
     * 根据数据库中已有图片ID进行虫情识别。
     */
    Map<String, Object> predictByImage(
            Long imageId,
            Long modelId,
            String cropType,
            String recordMode
    );

    /**
     * 识别临时上传图片。
     *
     * 模型成功后，才在同一事务中写入图片表、记录表和虫情结果表。
     */
    Map<String, Object> predictUploadedImage(
            Map<String, Object> params
    );

    /**
     * 根据历史虫情和未来天气预测虫情趋势。
     */
    Map<String, Object> predictPestTrend(
            Long parkId,
            Long areaId,
            String locationId,
            Long deviceId,
            String pestName,
            Integer days
    );
}
