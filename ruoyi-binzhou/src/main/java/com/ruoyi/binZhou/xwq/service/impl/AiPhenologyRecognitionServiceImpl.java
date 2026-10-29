package com.ruoyi.binZhou.xwq.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.URL;
import java.net.URLConnection;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.domain.AiRecord;
import com.ruoyi.binZhou.xwq.domain.PhenologyModelResult;
import com.ruoyi.binZhou.xwq.domain.PhenologyPredictRequest;
import com.ruoyi.binZhou.xwq.domain.PhenologyPredictResultVo;
import com.ruoyi.binZhou.xwq.service.IAiImageService;
import com.ruoyi.binZhou.xwq.service.IAiModelService;
import com.ruoyi.binZhou.xwq.service.IAiPhenologyRecognitionService;
import com.ruoyi.binZhou.xwq.service.IAiPhenologyRecognitionTransactionService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;

/**
 * 物候期识别业务Service实现。
 *
 * 负责参数校验、图片与模型校验、调用模型客户端，
 * 并协调事务服务完成状态和结果落库。
 *
 * @author ruoyi
 * @date 2026-06-28
 */
@Service
public class AiPhenologyRecognitionServiceImpl
        implements IAiPhenologyRecognitionService
{
    private static final Logger log =
            LoggerFactory.getLogger(
                    AiPhenologyRecognitionServiceImpl.class);

    private final IAiImageService aiImageService;
    private final IAiModelService aiModelService;
    private final IAiPhenologyRecognitionTransactionService
            transactionService;
    private final RestTemplate restTemplate;

    private static final String DASHSCOPE_URL =
            "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String[] PHENOLOGY_STAGES = {
            "休眠期", "萌芽期", "展叶期", "开花期", "坐果期",
            "果实膨大期", "果实着色期", "成熟期", "采收期", "落叶期"
    };

    @Value("${dashscope.apiKey:}")
    private String dashscopeApiKey;

    @Value("${dashscope.vision-model:qwen3-vl-plus}")
    private String dashscopeVisionModel;

    @Value("${ruoyi.domain:}")
    private String ruoyiDomain;

    public AiPhenologyRecognitionServiceImpl(
            IAiImageService aiImageService,
            IAiModelService aiModelService,
            IAiPhenologyRecognitionTransactionService transactionService,
            RestTemplate restTemplate)
    {
        this.aiImageService = aiImageService;
        this.aiModelService = aiModelService;
        this.transactionService = transactionService;
        this.restTemplate = restTemplate;
    }

    @Override
    public PhenologyPredictResultVo predict(
            PhenologyPredictRequest request)
    {
        validateRequest(request);

        AiImage image = aiImageService.selectAiImageByImageId(
                request.getImageId());
        validateImage(image);

        AiModel model = aiModelService.selectAiModelByModelId(
                request.getModelId());
        validateModel(model, request.getCropType());

        AiRecord record =
                transactionService.createProcessingRecord(
                        image,
                        model,
                        request);

        try
        {
            // 原物候模型接口暂时停用，统一改为后端直接调用视觉大模型。
            // PhenologyModelResult modelResult = phenologyModelClient.predict(...);
            PhenologyModelResult modelResult =
                    predictByDashScope(image, request.getCropType());

            validateModelResult(modelResult);

            return transactionService.saveSuccess(
                    record,
                    image,
                    model,
                    request,
                    modelResult);
        }
        catch (Exception e)
        {
            String errorMessage = getErrorMessage(e);
            log.error(
                    "物候期识别失败，recordId={}, imageId={}, modelId={}",
                    record.getRecordId(),
                    image.getImageId(),
                    model.getModelId(),
                    e);

            try
            {
                transactionService.saveFailure(
                        record,
                        image,
                        errorMessage);
            }
            catch (Exception statusException)
            {
                log.error(
                        "保存物候期识别失败状态异常，recordId={}",
                        record.getRecordId(),
                        statusException);
            }

            if (e instanceof ServiceException)
            {
                throw (ServiceException) e;
            }
            throw new ServiceException(
                    "物候期识别失败：" + errorMessage);
        }
    }

    /**
     * 使用视觉大模型判断是否为冬枣，并从固定物候期集合中选择阶段。
     * 阶段说明、下一阶段和农事建议由后端固定生成，避免模型自由发挥导致
     * 同一阶段的数据库内容不一致。
     */
    private PhenologyModelResult predictByDashScope(
            AiImage image,
            String cropType)
    {
        if (!hasText(dashscopeApiKey))
        {
            throw new ServiceException("DashScope API Key为空");
        }

        String imageSource = buildVisionImageSource(image.getImageUrl());
        String prompt = "你是冬枣物候期识别专家。请只分析图片本身，不要根据文件名猜测。"
                + "先判断图片主体是否为冬枣树、冬枣果实或冬枣枝叶。"
                + "如果不是冬枣，isJujube 必须为 false，phase 必须为空。"
                + "如果是冬枣，只能从以下固定物候期中选择一个 phase："
                + String.join("、", PHENOLOGY_STAGES) + "。"
                + "只返回 JSON，不要 Markdown，不要额外文字，格式为："
                + "{\"isJujube\":true,\"phase\":\"坐果期\",\"confidence\":0.94}。"
                + "当前作物类型仅用于约束识别：" + cropType;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + dashscopeApiKey.trim());

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", dashscopeVisionModel);
        requestBody.put("stream", false);
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你必须严格返回合法 JSON。");
        messages.add(systemMessage);

        JSONArray content = new JSONArray();
        JSONObject imagePart = new JSONObject();
        imagePart.put("type", "image_url");
        JSONObject imageUrl = new JSONObject();
        imageUrl.put("url", imageSource);
        imagePart.put("image_url", imageUrl);
        content.add(imagePart);
        JSONObject textPart = new JSONObject();
        textPart.put("type", "text");
        textPart.put("text", prompt);
        content.add(textPart);
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", content);
        messages.add(userMessage);
        requestBody.put("messages", messages);

        try
        {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    DASHSCOPE_URL,
                    new HttpEntity<String>(requestBody.toJSONString(), headers),
                    String.class);
            JSONObject root = JSONObject.parseObject(response.getBody());
            String contentText = root.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            JSONObject result = parseJsonContent(contentText);
            if (Boolean.FALSE.equals(result.getBoolean("isJujube")))
            {
                throw new ServiceException("当前图片不是冬枣图片，无法进行冬枣物候期识别");
            }

            String stage = normalizeStage(result.getString("phase"));
            if (!hasText(stage))
            {
                throw new ServiceException("视觉大模型未识别出有效的冬枣物候期");
            }
            return buildFixedPhenologyResult(stage, result.getBigDecimal("confidence"));
        }
        catch (HttpStatusCodeException e)
        {
            String body = e.getResponseBodyAsString();
            if (e.getRawStatusCode() == 404 && body != null
                    && body.contains("Model not exist"))
            {
                throw new ServiceException(
                        "物候识别模型不可用，请联系管理员检查视觉模型名称及账号权限");
            }
            if (body != null && body.length() > 500)
            {
                body = body.substring(0, 500);
            }
            throw new ServiceException("视觉大模型调用失败，HTTP "
                    + e.getRawStatusCode() + "：" + body);
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("视觉大模型返回结果解析失败：" + e.getMessage());
        }
    }

    private PhenologyModelResult buildFixedPhenologyResult(
            String stage,
            BigDecimal confidence)
    {
        Map<String, String> profile = fixedStageProfiles().get(stage);
        int stageIndex = indexOfStage(stage);
        String nextStage = stageIndex >= 0 && stageIndex < PHENOLOGY_STAGES.length - 1
                ? PHENOLOGY_STAGES[stageIndex + 1] : "翌年休眠期";

        PhenologyModelResult result = new PhenologyModelResult();
        result.setPhenologyName(stage);
        result.setPhenologyCode("DZ-" + String.format("%02d", stageIndex + 1));
        result.setPhenologyConfidence(normalizeConfidence(confidence));
        result.setPhenologyResult("识别为冬枣" + stage + "，图像中的枝叶、花果或果实发育特征与该阶段相符。");
        result.setPhenologyDesc(profile.get("desc"));
        result.setPhenologyAdvice(profile.get("advice"));
        result.setPhenologyNextName(nextStage);
        return result;
    }

    private Map<String, Map<String, String>> fixedStageProfiles()
    {
        Map<String, Map<String, String>> profiles = new LinkedHashMap<>();
        profiles.put("休眠期", profile("树体基本停止生长，芽体处于休眠状态。", "做好清园、修剪和越冬防护，检查枝干病虫源并合理安排春季施肥。"));
        profiles.put("萌芽期", profile("芽体开始膨大并陆续萌动，树体由休眠转入生长期。", "关注低温和晚霜风险，适量补水，完成萌芽前清园和病虫害预防。"));
        profiles.put("展叶期", profile("叶片陆续展开，枝叶生长加快，树体进入营养生长阶段。", "保持土壤墒情稳定，合理补充氮肥，及时抹除无效芽并加强病虫监测。"));
        profiles.put("开花期", profile("花蕾开放或进入盛花阶段，是授粉和坐果的关键时期。", "注意温湿度和风雨变化，保护授粉条件，避免花期大水大肥及不当用药。"));
        profiles.put("坐果期", profile("幼果已经形成并开始稳定发育，落果风险仍然存在。", "保持水分均衡，及时疏果和补充硼钙等营养，重点观察落果及病虫害情况。"));
        profiles.put("果实膨大期", profile("果实体积持续增加，树体水肥需求和负载管理要求提高。", "根据墒情稳步灌溉和追肥，合理控产，保持通风透光并加强果实病害巡查。"));
        profiles.put("果实着色期", profile("果皮颜色开始转变，果实品质和糖分积累进入关键阶段。", "控制氮肥和灌水，改善通风透光，防范裂果、日灼和病虫害，减少不必要喷药。"));
        profiles.put("成熟期", profile("果实颜色和质地接近采收标准，品质指标逐步稳定。", "结合果色、硬度和糖度确定采收窗口，控制灌水并做好采前病虫害和安全间隔检查。"));
        profiles.put("采收期", profile("果实达到适宜采收程度，进入分批采摘和品质分级阶段。", "按成熟度分批采摘，轻拿轻放并及时预冷分级，做好采后树体营养恢复准备。"));
        profiles.put("落叶期", profile("叶片逐步衰老脱落，树体进入越冬前的营养回流阶段。", "及时清除落叶和病果，做好采后施肥、修剪及越冬防寒，降低越冬病虫基数。"));
        return profiles;
    }

    private Map<String, String> profile(String desc, String advice)
    {
        Map<String, String> profile = new LinkedHashMap<>();
        profile.put("desc", desc);
        profile.put("advice", advice);
        return profile;
    }

    private JSONObject parseJsonContent(String content)
    {
        if (!hasText(content)) throw new ServiceException("视觉大模型返回为空");
        String json = content.trim().replaceAll("^```(?:json)?", "")
                .replaceAll("```$", "").trim();
        int start = json.indexOf('{');
        int end = json.lastIndexOf('}');
        if (start < 0 || end <= start) throw new ServiceException("视觉大模型未返回有效 JSON");
        return JSONObject.parseObject(json.substring(start, end + 1));
    }

    private String normalizeStage(String stage)
    {
        if (!hasText(stage)) return null;
        for (String item : PHENOLOGY_STAGES)
        {
            if (item.equals(stage.trim())) return item;
        }
        return null;
    }

    private int indexOfStage(String stage)
    {
        for (int i = 0; i < PHENOLOGY_STAGES.length; i++)
        {
            if (PHENOLOGY_STAGES[i].equals(stage)) return i;
        }
        return 0;
    }

    private BigDecimal normalizeConfidence(BigDecimal confidence)
    {
        BigDecimal value = confidence == null ? BigDecimal.ZERO : confidence;
        if (value.compareTo(BigDecimal.ONE) > 0)
        {
            value = value.divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP);
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) value = BigDecimal.ZERO;
        if (value.compareTo(BigDecimal.ONE) > 0) value = BigDecimal.ONE;
        return value.setScale(5, RoundingMode.HALF_UP);
    }

    private String buildVisionImageSource(String imageUrl)
    {
        if (imageUrl.startsWith("data:image/")) return imageUrl;

        try
        {
            String profileRelativePath = resolveProfileRelativePath(imageUrl);
            if (profileRelativePath != null)
            {
                Path file = Paths.get(RuoYiConfig.getProfile(), profileRelativePath);
                return toImageDataUrl(Files.readAllBytes(file), null, imageUrl);
            }

            // 千问服务器无法访问 localhost、内网地址或本系统代理地址，
            // 统一由后端读取后转为 data URL 再提交。
            if (imageUrl.startsWith("http://")
                    || imageUrl.startsWith("https://"))
            {
                URLConnection connection = new URL(imageUrl).openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(60000);
                String contentType = connection.getContentType();
                byte[] bytes = readAllBytes(connection.getInputStream());
                return toImageDataUrl(bytes, contentType, imageUrl);
            }

            String relative = imageUrl.replaceFirst("^/+(?:dev-api/|bz-api/)?", "");
            relative = relative.replaceFirst("^profile/", "");
            Path file = Paths.get(RuoYiConfig.getProfile(), relative);
            return toImageDataUrl(Files.readAllBytes(file), null, imageUrl);
        }
        catch (Exception e)
        {
            throw new ServiceException("无法读取待识别图片：" + imageUrl);
        }
    }

    /**
     * 将完整访问地址映射为 profile 下的相对路径。
     * 例如：${ruoyi.domain}/profile/upload/a.png -> upload/a.png。
     */
    private String resolveProfileRelativePath(String imageUrl)
    {
        String value = imageUrl.trim();
        String domain = ruoyiDomain == null ? "" : ruoyiDomain.trim();
        if (domain.endsWith("/")) domain = domain.substring(0, domain.length() - 1);

        String path = null;
        if (!domain.isEmpty() && value.startsWith(domain))
        {
            path = value.substring(domain.length());
        }
        else if (value.startsWith("http://") || value.startsWith("https://"))
        {
            try
            {
                path = new URL(value).getPath();
            }
            catch (Exception ignored)
            {
                return null;
            }
        }
        else
        {
            path = value;
        }

        path = path.replaceFirst("^/+(?:dev-api/|bz-api/)?", "");
        if (!path.startsWith("profile/")) return null;
        return path.substring("profile/".length());
    }

    private String toImageDataUrl(byte[] bytes, String contentType, String imageUrl)
    {
        if (bytes == null || bytes.length == 0)
        {
            throw new ServiceException("待识别图片内容为空");
        }
        String mimeType = contentType;
        if (mimeType == null || !mimeType.toLowerCase().startsWith("image/"))
        {
            String lowerUrl = imageUrl.toLowerCase();
            mimeType = lowerUrl.contains(".png") ? "image/png"
                    : lowerUrl.contains(".webp") ? "image/webp"
                    : lowerUrl.contains(".gif") ? "image/gif"
                    : "image/jpeg";
        }
        int semicolon = mimeType.indexOf(';');
        if (semicolon > 0) mimeType = mimeType.substring(0, semicolon);
        return "data:" + mimeType + ";base64,"
                + Base64.getEncoder().encodeToString(bytes);
    }

    /*
     * Java 8 没有 InputStream.readAllBytes()，保留一个兼容实现。
     */
    private byte[] readAllBytes(java.io.InputStream inputStream)
            throws java.io.IOException
    {
        try (java.io.InputStream input = inputStream;
             java.io.ByteArrayOutputStream output =
                     new java.io.ByteArrayOutputStream())
        {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = input.read(buffer)) != -1)
            {
                output.write(buffer, 0, length);
            }
            return output.toByteArray();
        }
    }

    private void validateRequest(
            PhenologyPredictRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("识别请求不能为空");
        }
        if (request.getImageId() == null)
        {
            throw new ServiceException("图片ID不能为空");
        }
        if (request.getModelId() == null)
        {
            throw new ServiceException("模型ID不能为空");
        }
        if (!hasText(request.getCropType()))
        {
            throw new ServiceException("作物类型不能为空");
        }
    }

    private void validateImage(AiImage image)
    {
        if (image == null)
        {
            throw new ServiceException("待识别图片不存在");
        }
        if (!hasText(image.getImageUrl()))
        {
            throw new ServiceException("图片地址不能为空");
        }

        String imageAiType = image.getImageAiType();
        if (!"phenology".equalsIgnoreCase(imageAiType))
        {
            throw new ServiceException(
                    "当前图片不是物候期识别类型");
        }
    }

    private void validateModel(
            AiModel model,
            String cropType)
    {
        if (model == null)
        {
            throw new ServiceException("识别模型不存在");
        }
        if (model.getModelStatus() == null
                || model.getModelStatus().longValue() != 1L)
        {
            throw new ServiceException("识别模型未启用");
        }

        String modelType = model.getModelType();
        boolean phenologyType =
                "phenology".equalsIgnoreCase(modelType)
                || "物候期".equals(modelType);
        if (!phenologyType)
        {
            throw new ServiceException(
                    "所选模型不是物候期识别模型");
        }

        if (!isModelCropCompatible(model.getModelCrop(), cropType))
        {
            throw new ServiceException(
                    "所选模型与当前作物类型不匹配：模型适用作物="
                            + (hasText(model.getModelCrop())
                            ? model.getModelCrop().trim() : "未配置")
                            + "，当前作物=" + cropType.trim());
        }
    }

    /**
     * 模型管理中既有中文作物名，也有前端字典编码；两者表示同一作物。
     * 视觉模型还可能配置为通用模型，允许它服务多个作物。
     */
    private boolean isModelCropCompatible(
            String modelCrop,
            String cropType)
    {
        if (!hasText(modelCrop) || !hasText(cropType))
        {
            return false;
        }

        String expected = normalizeCropType(cropType);
        String[] supportedCrops = modelCrop.trim()
                .split("[,，、/]+");
        for (String supportedCrop : supportedCrops)
        {
            String normalized = normalizeCropType(supportedCrop);
            if ("*".equals(normalized)
                    || "all".equals(normalized)
                    || "通用".equals(normalized)
                    || "不限".equals(normalized))
            {
                return true;
            }
            if (normalized.equals(expected))
            {
                return true;
            }
        }
        return false;
    }

    private String normalizeCropType(String value)
    {
        String text = value == null ? "" : value.trim();
        if ("小麦".equalsIgnoreCase(text)
                || "wheat".equalsIgnoreCase(text))
        {
            return "wheat";
        }
        if ("玉米".equalsIgnoreCase(text)
                || "corn".equalsIgnoreCase(text)
                || "maize".equalsIgnoreCase(text))
        {
            return "corn";
        }
        if ("水稻".equalsIgnoreCase(text)
                || "rice".equalsIgnoreCase(text))
        {
            return "rice";
        }
        if ("冬枣".equalsIgnoreCase(text)
                || "枣树".equalsIgnoreCase(text)
                || "枣".equalsIgnoreCase(text)
                || "jujube".equalsIgnoreCase(text))
        {
            return "jujube";
        }
        return text.toLowerCase();
    }

    private void validateModelResult(
            PhenologyModelResult modelResult)
    {
        if (modelResult == null)
        {
            throw new ServiceException("模型未返回识别结果");
        }
        if (!hasText(modelResult.getPhenologyName()))
        {
            throw new ServiceException(
                    "模型返回的物候期名称为空");
        }
        if (modelResult.getPhenologyConfidence() == null)
        {
            throw new ServiceException(
                    "模型返回的置信度为空");
        }
    }

    private boolean hasText(String value)
    {
        return value != null && !value.trim().isEmpty();
    }

    private String getErrorMessage(Exception exception)
    {
        if (exception.getMessage() == null
                || exception.getMessage().trim().isEmpty())
        {
            return exception.getClass().getSimpleName();
        }
        return exception.getMessage();
    }
}
