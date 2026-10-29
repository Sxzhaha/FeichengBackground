package com.ruoyi.binZhou.xwq.service.impl;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.domain.PhenologyModelResult;
import com.ruoyi.binZhou.xwq.service.IPhenologyModelClient;

/**
 * 物候期模型模拟客户端。
 *
 * 仅用于模型接口尚未接入时的前后端与数据库联调。
 * 必须显式配置 xwq.ai.phenology.mock-enabled=true 才会启用。
 * 接入真实模型后应关闭该配置，并替换为真实 HTTP 客户端。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
@Component
@ConditionalOnProperty(
        prefix = "xwq.ai.phenology",
        name = "mock-enabled",
        havingValue = "true")
public class MockPhenologyModelClient implements IPhenologyModelClient
{
    @Override
    public PhenologyModelResult predict(
            AiImage image,
            AiModel model,
            String cropType)
    {
        PhenologyModelResult result = new PhenologyModelResult();

        if ("wheat".equalsIgnoreCase(cropType))
        {
            result.setPhenologyName("拔节期");
            result.setPhenologyCode("WHEAT_JOINTING");
            result.setPhenologyNextName("抽穗期");
            result.setPhenologyDesc("植株茎秆开始明显伸长，节间逐步形成。");
            result.setPhenologyAdvice("加强水肥管理，结合田间长势合理追肥并关注倒伏风险。");
        }
        else if ("corn".equalsIgnoreCase(cropType)
                || "maize".equalsIgnoreCase(cropType))
        {
            result.setPhenologyName("拔节期");
            result.setPhenologyCode("CORN_JOINTING");
            result.setPhenologyNextName("大喇叭口期");
            result.setPhenologyDesc("玉米植株进入快速营养生长阶段，茎节开始伸长。");
            result.setPhenologyAdvice("及时中耕除草并根据苗情补充水肥。");
        }
        else if ("rice".equalsIgnoreCase(cropType))
        {
            result.setPhenologyName("分蘖期");
            result.setPhenologyCode("RICE_TILLERING");
            result.setPhenologyNextName("拔节孕穗期");
            result.setPhenologyDesc("水稻开始形成新的分蘖，群体数量逐步增加。");
            result.setPhenologyAdvice("控制合理水层，结合群体长势进行分蘖肥管理。");
        }
        else
        {
            result.setPhenologyName("生长期");
            result.setPhenologyCode("CROP_GROWTH");
            result.setPhenologyNextName("下一生育阶段");
            result.setPhenologyDesc("当前作物处于持续生长阶段。");
            result.setPhenologyAdvice("持续监测田间长势，并根据实际情况调整水肥管理。");
        }

        result.setPhenologyConfidence(new BigDecimal("0.93"));
        result.setPhenologyResult(
                "模拟识别结果：当前作物处于" + result.getPhenologyName() + "。");

        // 模拟阶段暂时使用原图作为结果图，避免前端出现空地址。
        result.setPhenologyResultImgUrl(image.getImageUrl());
        return result;
    }
}
