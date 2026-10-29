package com.ruoyi.binZhou.xwq.service;

import com.ruoyi.binZhou.xwq.domain.PhenologyPredictRequest;
import com.ruoyi.binZhou.xwq.domain.PhenologyPredictResultVo;

/**
 * 物候期识别业务Service接口。
 *
 * 与后台管理使用的 IAiPhenologyService 分开：
 * IAiPhenologyService 负责物候期结果表的标准 CRUD；
 * 本接口负责图片、模型、识别记录和物候期结果之间的完整识别流程。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
public interface IAiPhenologyRecognitionService
{
    /**
     * 根据图片和模型执行一次物候期识别。
     *
     * @param request 识别请求
     * @return 完整识别结果
     */
    public PhenologyPredictResultVo predict(PhenologyPredictRequest request);
}
