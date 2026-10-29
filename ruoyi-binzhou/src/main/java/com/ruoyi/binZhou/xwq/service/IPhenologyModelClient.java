package com.ruoyi.binZhou.xwq.service;

import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.domain.PhenologyModelResult;

/**
 * 物候期模型调用客户端。
 *
 * 当前先使用模拟实现完成前后端和数据库联调。
 * 后续接入 Python 模型服务时，只需新增真实 HTTP 实现，
 * 无需修改识别业务 Service。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
public interface IPhenologyModelClient
{
    /**
     * 调用模型完成物候期识别。
     *
     * @param image 待识别图片
     * @param model 使用的模型
     * @param cropType 作物编码
     * @return 模型推理结果
     */
    public PhenologyModelResult predict(
            AiImage image,
            AiModel model,
            String cropType);
}
