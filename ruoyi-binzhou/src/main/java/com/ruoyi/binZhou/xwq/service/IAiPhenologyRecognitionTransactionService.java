package com.ruoyi.binZhou.xwq.service;

import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.domain.AiRecord;
import com.ruoyi.binZhou.xwq.domain.PhenologyModelResult;
import com.ruoyi.binZhou.xwq.domain.PhenologyPredictRequest;
import com.ruoyi.binZhou.xwq.domain.PhenologyPredictResultVo;

/**
 * 物候期识别数据库事务服务。
 *
 * 将数据库写入与外部模型调用分开，避免在等待模型推理时
 * 长时间占用数据库事务。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
public interface IAiPhenologyRecognitionTransactionService
{
    /**
     * 新建一条“识别中”记录。
     */
    public AiRecord createProcessingRecord(
            AiImage image,
            AiModel model,
            PhenologyPredictRequest request);

    /**
     * 在同一事务中保存识别结果并更新相关状态。
     */
    public PhenologyPredictResultVo saveSuccess(
            AiRecord record,
            AiImage image,
            AiModel model,
            PhenologyPredictRequest request,
            PhenologyModelResult modelResult);

    /**
     * 使用独立事务记录识别失败状态。
     */
    public void saveFailure(
            AiRecord record,
            AiImage image,
            String errorMessage);
}
