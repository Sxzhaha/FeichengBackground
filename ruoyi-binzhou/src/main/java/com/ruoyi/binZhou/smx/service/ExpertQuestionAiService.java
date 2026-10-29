package com.ruoyi.binZhou.smx.service;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.binZhou.smx.mapper.ExpertQuestionMapper;
import com.ruoyi.common.config.RuoYiConfig;

/**
 * 提问图片AI病害识别服务。
 *
 * 用户发布带图提问后，一次将全部图片发送给阿里百炼视觉大模型（qwen-vl-plus）
 * 逐张进行病害识别，识别结果以带图片编号的JSON数组落库，详情页按当前图片展示结果。
 * 识别过程异步执行，不阻塞提问接口。
 *
 * @author smx
 */
@Service
public class ExpertQuestionAiService
{
    private static final Logger log = LoggerFactory.getLogger(ExpertQuestionAiService.class);

    /** DashScope OpenAI兼容接口地址 */
    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    private final ExpertQuestionMapper questionMapper;
    private final RuoYiConfig ruoYiConfig;
    /** 正在识别中的问题ID，防止重复触发 */
    private final Set<Long> inFlight = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor = new ThreadPoolExecutor(
            1, 2, 60L, java.util.concurrent.TimeUnit.SECONDS,
            new java.util.concurrent.ArrayBlockingQueue<>(20),
            runnable -> {
                Thread thread = new Thread(runnable, "expert-qa-ai-worker");
                thread.setDaemon(true);
                return thread;
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    @Value("${dashscope.apiKey:}")
    private String apiKey;

    @Value("${dashscope.vision-model:qwen3-vl-plus}")
    private String visionModel;

    private final RestTemplate restTemplate;

    public ExpertQuestionAiService(ExpertQuestionMapper questionMapper, RuoYiConfig ruoYiConfig)
    {
        this.questionMapper = questionMapper;
        this.ruoYiConfig = ruoYiConfig;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(60000);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
    * 异步识别提问图片。photos为上传接口返回的 /profile/upload/... 相对路径数组，最多3张。
     */
    public void recognizeAsync(Long questionId, String[] photos, String crop, String title)
    {
        if (photos == null || photos.length == 0)
        {
            questionMapper.updateAiResult(questionId, null, "2");
            return;
        }
        if (!inFlight.add(questionId))
        {
            return;
        }
        executor.execute(() -> {
            try
            {
                doRecognize(questionId, photos, crop, title);
            }
            catch (Exception e)
            {
                log.warn("提问图片AI识别失败, questionId={}", questionId, e);
                questionMapper.updateAiResult(questionId, null, "2");
            }
            finally
            {
                inFlight.remove(questionId);
            }
        });
    }

    /**
     * 存量数据自愈：详情页查询时发现仍处于"识别中"且带图的问题（如服务重启前的旧数据），
     * 自动补跑一次识别。inFlight 去重保证同一问题不会并发重复调用大模型。
     */
    public void retryIfPending(Long questionId, String[] photos, String crop, String title)
    {
        if (photos == null || photos.length == 0)
        {
            return;
        }
        if (!inFlight.add(questionId))
        {
            return;
        }
        executor.execute(() -> {
            try
            {
                doRecognize(questionId, photos, crop, title);
            }
            catch (Exception e)
            {
                log.warn("提问图片AI识别失败, questionId={}", questionId, e);
                questionMapper.updateAiResult(questionId, null, "2");
            }
            finally
            {
                inFlight.remove(questionId);
            }
        });
    }

    private void doRecognize(Long questionId, String[] photos, String crop, String title)
    {
        if (apiKey == null || apiKey.trim().isEmpty())
        {
            log.warn("dashscope.apiKey 未配置，跳过提问图片AI识别");
            questionMapper.updateAiResult(questionId, null, "2");
            return;
        }
        List<Map<String, Object>> imageParts = new ArrayList<>();
        List<Integer> imageIndexes = new ArrayList<>();
        for (int index = 0; index < photos.length; index++)
        {
            String base64 = readImageBase64(photos[index]);
            if (base64 == null)
            {
                continue;
            }
            Map<String, Object> imageUrl = new HashMap<>();
            imageUrl.put("url", "data:image/jpeg;base64," + base64);
            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("type", "image_url");
            imagePart.put("image_url", imageUrl);
            imageParts.add(imagePart);
            imageIndexes.add(index + 1);
        }
        if (imageParts.isEmpty())
        {
            questionMapper.updateAiResult(questionId, null, "2");
            return;
        }

        // 组装视觉消息：文本提示词 + base64图片（DashScope兼容模式支持data URL）
        String cropText = (crop == null || crop.trim().isEmpty()) ? "未知作物" : crop.trim();
        String prompt = "用户选择作物：" + cropText + "。提问标题：" + title + "。"
            + "下面会按顺序提供多张图片，图片编号依次为：" + imageIndexes + "。"
            + "请逐张判断图片中的作物是否为用户选择的作物，再判断病害或虫害。"
            + "必须为每张图片返回一个结果，不能合并或遗漏，按图片编号升序输出。"
            + "只输出一个JSON对象，不要任何其他文字，格式为："
            + "{\"results\":[{\"imageIndex\":1,\"match\":true,\"detectedCrop\":\"作物\",\"diseaseName\":\"病害名称\",\"confidence\":0到100的整数,"
            + "\"description\":\"50字以内\",\"advice\":\"60字以内\",\"message\":\"提示\"}]}。"
            + "作物不一致时match=false，diseaseName为空，message说明不一致；无法确认时confidence填0。";

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("type", "text");
        textPart.put("text", prompt);
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        List<Map<String, Object>> contentParts = new ArrayList<>();
        contentParts.add(textPart);
        contentParts.addAll(imageParts);
        message.put("content", contentParts);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", visionModel);
        requestBody.put("messages", Collections.singletonList(message));
        requestBody.put("temperature", 0.3);
        requestAndSave(questionId, requestBody);
    }

    @SuppressWarnings("unchecked")
    private void requestAndSave(Long questionId, Map<String, Object> requestBody)
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            HttpEntity<Map<String, Object>> entity = new HttpEntity(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            JSONObject body = JSON.parseObject(response.getBody());
            String content = body.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content");
            if (content == null)
            {
                log.warn("AI识别响应缺少content, questionId={}, body={}", questionId, response.getBody());
                questionMapper.updateAiResult(questionId, null, "2");
                return;
            }
            // 模型可能用```json代码块包裹，截取首个{到最后一个}
            int start = content.indexOf('{');
            int end = content.lastIndexOf('}');
            if (start < 0 || end <= start)
            {
                log.warn("AI识别响应无法解析出JSON, questionId={}, content={}", questionId, content);
                questionMapper.updateAiResult(questionId, null, "2");
                return;
            }
            JSONObject result = JSON.parseObject(content.substring(start, end + 1));
            JSONArray rawResults = result.getJSONArray("results");
            if (rawResults == null || rawResults.isEmpty())
            {
                log.warn("AI识别响应缺少results, questionId={}, content={}", questionId, content);
                questionMapper.updateAiResult(questionId, null, "2");
                return;
            }
            JSONArray normalizedResults = new JSONArray();
            for (int index = 0; index < rawResults.size(); index++)
            {
                JSONObject item = rawResults.getJSONObject(index);
                JSONObject normalizedItem = new JSONObject();
                normalizedItem.put("imageIndex", item.getIntValue("imageIndex"));
                normalizedItem.put("match", !item.containsKey("match") || item.getBooleanValue("match"));
                normalizedItem.put("detectedCrop", item.getString("detectedCrop"));
                normalizedItem.put("diseaseName", item.getString("diseaseName"));
                normalizedItem.put("confidence", item.getIntValue("confidence"));
                normalizedItem.put("description", item.getString("description"));
                normalizedItem.put("advice", item.getString("advice"));
                normalizedItem.put("message", item.getString("message"));
                normalizedResults.add(normalizedItem);
            }
            JSONObject normalized = new JSONObject();
            normalized.put("results", normalizedResults);
            questionMapper.updateAiResult(questionId, normalized.toJSONString(), "1");
            log.info("提问图片AI识别完成, questionId={}, result={}", questionId, normalized);
        }
        catch (Exception e)
        {
            log.warn("调用视觉大模型识别失败, questionId={}", questionId, e);
            questionMapper.updateAiResult(questionId, null, "2");
        }
    }

    /**
     * 读取本地上传图片并转base64。photo形如 /profile/upload/2026/09/26/xxx.jpg
     */
    private String readImageBase64(String photo)
    {
        try
        {
            String marker = "/profile/";
            int idx = photo.indexOf(marker);
            if (idx < 0)
            {
                return null;
            }
            String relative = photo.substring(idx + marker.length());
            java.nio.file.Path profileRoot = Paths.get(ruoYiConfig.getProfile()).toAbsolutePath().normalize();
            java.nio.file.Path filePath = profileRoot.resolve(relative).normalize();
            if (!filePath.startsWith(profileRoot))
            {
                log.warn("识别图片路径非法: {}", photo);
                return null;
            }
            File file = filePath.toFile();
            if (!file.exists())
            {
                log.warn("识别图片不存在: {}", file.getAbsolutePath());
                return null;
            }
            BufferedImage source = ImageIO.read(file);
            if (source == null)
            {
                return null;
            }
            int maxDimension = 1280;
            double scale = Math.min(1.0, (double) maxDimension / Math.max(source.getWidth(), source.getHeight()));
            int width = Math.max(1, (int) Math.round(source.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(source.getHeight() * scale));
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resized.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(source, 0, 0, width, height, null);
            graphics.dispose();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(resized, "jpg", output);
            byte[] bytes = output.toByteArray();
            if (bytes.length == 0 || bytes.length > 2 * 1024 * 1024)
            {
                return null;
            }
            return Base64.getEncoder().encodeToString(bytes);
        }
        catch (Exception e)
        {
            log.warn("读取识别图片失败: {}", photo, e);
            return null;
        }
    }
}
