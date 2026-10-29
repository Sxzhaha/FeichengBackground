package com.ruoyi.binZhou.sxz.service.client;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * 病虫害模型文件客户端。
 *
 * Java 负责定位或获取原始图片，并以 OpenAI image_url data URI 发送给模型；
 * 模型服务只接收图片内容，不依赖业务系统的公网图片 URL。
 */
@Component
public class DiseaseModelClient
{
    private static final Logger log = LoggerFactory.getLogger(DiseaseModelClient.class);
    private static final String PROFILE_MARKER = "/profile/";
    private static final int MAX_RETRY_LIMIT = 10;
    private static final long MAX_RETRY_DELAY_MS = 8000L;
    private static final Set<String> SUPPORTED_SUFFIXES = new HashSet<>(Arrays.asList(
            ".jpg", ".jpeg", ".png", ".webp", ".bmp"));

    @Value("${sxz.disease-model.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Value("${sxz.disease-model.read-timeout-ms:60000}")
    private int readTimeoutMs;

    @Value("${sxz.disease-model.image-download-timeout-ms:20000}")
    private int imageDownloadTimeoutMs;

    @Value("${sxz.disease-model.max-image-bytes:10485760}")
    private int maxImageBytes;

    @Value("${ai-model.api-key:}")
    private String apiKey;

    private RestTemplate modelRestTemplate;

    @PostConstruct
    public void initialize()
    {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Math.max(connectTimeoutMs, 1000));
        requestFactory.setReadTimeout(Math.max(readTimeoutMs, 1000));
        requestFactory.setBufferRequestBody(false);
        modelRestTemplate = new RestTemplate(requestFactory);
    }

    public String predict(AiImage image, AiModel model, String recordNo)
    {
        try
        {
            return predictWithRetry(image, model, recordNo, 0).getResponseBody();
        }
        catch (DiseaseModelCallException e)
        {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 调用模型并仅对临时性网络或服务异常进行重试。
     *
     * @param maxRetryCount 首次失败后允许的额外重试次数
     */
    public DiseaseModelCallResult predictWithRetry(AiImage image, AiModel model, String recordNo,
            Integer maxRetryCount)
    {
        if (image == null || !StringUtils.hasText(image.getImageUrl()))
        {
            throw new ServiceException("图片地址为空，无法读取识别图片");
        }
        if (model == null || !StringUtils.hasText(model.getModelUrl()))
        {
            throw new ServiceException("模型接口地址为空");
        }
        if (!StringUtils.hasText(model.getModelCode()))
        {
            throw new ServiceException("模型编码为空");
        }
        if (!StringUtils.hasText(apiKey))
        {
            throw new ServiceException("AI 模型 API Key 未配置");
        }

        ImagePayload payload = resolveImage(image);
        String imageBase64;
        try (InputStream input = payload.resource.getInputStream())
        {
            imageBase64 = Base64.getEncoder().encodeToString(readBounded(input));
        }
        catch (IOException e)
        {
            throw new ServiceException("读取识别图片失败：" + rootMessage(e));
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model.getModelCode().trim());
        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        JSONArray content = new JSONArray();
        JSONObject imageContent = new JSONObject();
        imageContent.put("type", "image_url");
        JSONObject imageUrl = new JSONObject();
        imageUrl.put("url", "data:" + payload.mediaType.toString() + ";base64," + imageBase64);
        imageContent.put("image_url", imageUrl);
        content.add(imageContent);
        userMessage.put("content", content);
        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("stream", false);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.hasText(apiKey))
        {
            requestHeaders.setBearerAuth(apiKey.trim());
        }

        int retries = maxRetryCount == null ? 0 : Math.max(0, Math.min(maxRetryCount, MAX_RETRY_LIMIT));
        int maxAttempts = retries + 1;
        for (int attempt = 1; attempt <= maxAttempts; attempt++)
        {
            try
            {
                ResponseEntity<String> response = modelRestTemplate.postForEntity(
                        model.getModelUrl().trim(),
                        new HttpEntity<String>(requestBody.toJSONString(), requestHeaders),
                        String.class);
                // OpenAI 兼容响应的业务内容仍按原模型 JSON 交给上层解析。
                return new DiseaseModelCallResult(normalizeOpenAiResponse(response.getBody()), attempt);
            }
            catch (HttpStatusCodeException e)
            {
                boolean retryable = isRetryableStatus(e.getRawStatusCode());
                String responseBody = limitMessage(e.getResponseBodyAsString(), 500);
                String message = "模型服务返回错误（HTTP " + e.getRawStatusCode() + "）：" + responseBody;
                if (!retryable || attempt >= maxAttempts)
                {
                    throw new DiseaseModelCallException(message, attempt, retryable, e);
                }
                waitBeforeRetry(attempt, maxAttempts, message);
            }
            catch (ResourceAccessException e)
            {
                String message = "连接模型服务失败或推理超时：" + rootMessage(e);
                if (attempt >= maxAttempts)
                {
                    throw new DiseaseModelCallException(message, attempt, true, e);
                }
                waitBeforeRetry(attempt, maxAttempts, message);
            }
            catch (DiseaseModelCallException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new DiseaseModelCallException("调用模型服务失败：" + rootMessage(e), attempt, false, e);
            }
        }
        throw new DiseaseModelCallException("调用模型服务失败", maxAttempts, true, null);
    }

    private String normalizeOpenAiResponse(String responseBody)
    {
        if (!StringUtils.hasText(responseBody)) return responseBody;
        try
        {
            JSONObject root = JSONObject.parseObject(responseBody);
            JSONArray choices = root.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) return responseBody;
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            if (message == null || message.get("content") == null) return responseBody;
            Object content = message.get("content");
            return content instanceof String ? (String) content : String.valueOf(content);
        }
        catch (Exception ignored)
        {
            return responseBody;
        }
    }

    private boolean isRetryableStatus(int status)
    {
        return status == 408 || status == 429 || status == 500 || status == 502
                || status == 503 || status == 504;
    }

    private void waitBeforeRetry(int attempt, int maxAttempts, String reason)
    {
        long delayMs = Math.min(MAX_RETRY_DELAY_MS, 1000L << Math.min(Math.max(attempt - 1, 0), 3));
        log.warn("病虫害模型调用失败，准备重试，attempt={}/{}, delayMs={}, reason={}",
                attempt, maxAttempts, delayMs, limitMessage(reason, 300));
        try
        {
            Thread.sleep(delayMs);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new DiseaseModelCallException("模型重试等待被中断", attempt, true, e);
        }
    }

    private ImagePayload resolveImage(AiImage image)
    {
        String source = image.getImageUrl().trim();
        if (source.startsWith("data:image/"))
        {
            int separator = source.indexOf(";base64,");
            if (separator < 0) throw new ServiceException("识别图片 Base64 格式无效");
            try
            {
                byte[] bytes = Base64.getDecoder().decode(source.substring(separator + 8));
                validateSize(bytes.length);
                return new ImagePayload(new ByteArrayResource(bytes), "recognition-image",
                        MediaType.parseMediaType(source.substring(5, separator)));
            }
            catch (IllegalArgumentException e)
            {
                throw new ServiceException("识别图片 Base64 格式无效");
            }
        }
        Path localPath = resolveProfilePath(source);
        if (localPath != null && Files.isRegularFile(localPath))
        {
            return localPayload(localPath);
        }

        if (isHttpUrl(source))
        {
            return remotePayload(source, image.getImageName());
        }

        Path directPath = resolveDirectPath(source);
        if (directPath != null && Files.isRegularFile(directPath))
        {
            return localPayload(directPath);
        }

        if (localPath != null)
        {
            throw new ServiceException("识别图片文件不存在：" + localPath);
        }
        throw new ServiceException("无法将图片地址映射为可读取文件：" + source);
    }

    private Path resolveProfilePath(String source)
    {
        String requestPath = extractRequestPath(source).replace('\\', '/');
        int markerIndex = requestPath.indexOf(PROFILE_MARKER);
        String relativePath = null;

        if (markerIndex >= 0)
        {
            relativePath = requestPath.substring(markerIndex + PROFILE_MARKER.length());
        }
        else if (requestPath.startsWith("profile/"))
        {
            relativePath = requestPath.substring("profile/".length());
        }

        if (relativePath == null)
        {
            return null;
        }

        try
        {
            relativePath = URLDecoder.decode(relativePath.replace("+", "%2B"), "UTF-8");
        }
        catch (Exception e)
        {
            throw new ServiceException("图片地址编码不合法：" + e.getMessage());
        }

        Path profileRoot = Paths.get(RuoYiConfig.getProfile()).toAbsolutePath().normalize();
        Path candidate = profileRoot.resolve(relativePath.replace('/', File.separatorChar)).normalize();
        if (!candidate.startsWith(profileRoot))
        {
            throw new ServiceException("图片路径超出若依上传目录");
        }
        return candidate;
    }

    private String extractRequestPath(String source)
    {
        String cleanSource = source.split("#", 2)[0].split("\\?", 2)[0];
        if (!isHttpUrl(source))
        {
            return cleanSource;
        }
        try
        {
            // 数据库中的历史图片 URL 可能含有未编码的中文和空格，URL 对此兼容性更好。
            String path = new URL(source).getPath();
            return path == null ? "" : path;
        }
        catch (Exception e)
        {
            throw new ServiceException("图片 URL 格式不合法：" + source);
        }
    }

    private Path resolveDirectPath(String source)
    {
        try
        {
            Path candidate = Paths.get(source).toAbsolutePath().normalize();
            return Files.isRegularFile(candidate) ? candidate : null;
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private ImagePayload localPayload(Path path)
    {
        try
        {
            long size = Files.size(path);
            validateSize(size);
            String fileName = path.getFileName().toString();
            validateSuffix(fileName);
            return new ImagePayload(new FileSystemResource(path.toFile()), fileName, detectMediaType(fileName));
        }
        catch (IOException e)
        {
            throw new ServiceException("读取识别图片失败：" + rootMessage(e));
        }
    }

    private ImagePayload remotePayload(String source, String preferredName)
    {
        HttpURLConnection connection = null;
        try
        {
            connection = (HttpURLConnection) new URL(source).openConnection();
            connection.setConnectTimeout(Math.max(connectTimeoutMs, 1000));
            connection.setReadTimeout(Math.max(imageDownloadTimeoutMs, 1000));
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "Binzhou-Disease-Recognition/1.0");

            int status = connection.getResponseCode();
            if (status < 200 || status >= 300)
            {
                throw new ServiceException("远程图片下载失败（HTTP " + status + "）");
            }
            long contentLength = connection.getContentLengthLong();
            if (contentLength > 0)
            {
                validateSize(contentLength);
            }

            byte[] bytes;
            try (InputStream input = connection.getInputStream())
            {
                bytes = readBounded(input);
            }
            String fileName = chooseFileName(preferredName, source);
            validateSuffix(fileName);
            MediaType mediaType = parseMediaType(connection.getContentType(), fileName);
            ByteArrayResource resource = new ByteArrayResource(bytes)
            {
                @Override
                public String getFilename()
                {
                    return fileName;
                }
            };
            return new ImagePayload(resource, fileName, mediaType);
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("获取远程识别图片失败：" + rootMessage(e));
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }

    private byte[] readBounded(InputStream input) throws IOException
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int total = 0;
        int count;
        while ((count = input.read(buffer)) != -1)
        {
            total += count;
            if (total > maxImageBytes)
            {
                throw new ServiceException("识别图片超过大小限制：" + maxImageBytes + " 字节");
            }
            output.write(buffer, 0, count);
        }
        if (total == 0)
        {
            throw new ServiceException("识别图片内容为空");
        }
        return output.toByteArray();
    }

    private void validateSize(long size)
    {
        if (size <= 0 || size > maxImageBytes)
        {
            throw new ServiceException("识别图片大小不合法：" + size + " 字节");
        }
    }

    private void validateSuffix(String fileName)
    {
        String suffix = fileSuffix(fileName);
        if (!SUPPORTED_SUFFIXES.contains(suffix))
        {
            throw new ServiceException("不支持的识别图片格式：" + suffix);
        }
    }

    private String chooseFileName(String preferredName, String source)
    {
        if (StringUtils.hasText(preferredName) && SUPPORTED_SUFFIXES.contains(fileSuffix(preferredName)))
        {
            return Paths.get(preferredName).getFileName().toString();
        }
        String path = extractRequestPath(source);
        String fileName = Paths.get(path).getFileName().toString();
        return StringUtils.hasText(fileName) && SUPPORTED_SUFFIXES.contains(fileSuffix(fileName))
                ? fileName : "recognition-image.jpg";
    }

    private MediaType parseMediaType(String contentType, String fileName)
    {
        if (StringUtils.hasText(contentType))
        {
            try
            {
                MediaType mediaType = MediaType.parseMediaType(contentType.split(";", 2)[0]);
                if ("image".equalsIgnoreCase(mediaType.getType()))
                {
                    return mediaType;
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return detectMediaType(fileName);
    }

    private MediaType detectMediaType(String fileName)
    {
        String suffix = fileSuffix(fileName);
        if (".png".equals(suffix)) return MediaType.IMAGE_PNG;
        if (".jpg".equals(suffix) || ".jpeg".equals(suffix)) return MediaType.IMAGE_JPEG;
        if (".bmp".equals(suffix)) return MediaType.parseMediaType("image/bmp");
        if (".webp".equals(suffix)) return MediaType.parseMediaType("image/webp");
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private String fileSuffix(String fileName)
    {
        if (!StringUtils.hasText(fileName)) return "";
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex < 0 ? "" : fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private boolean isHttpUrl(String source)
    {
        String lower = source == null ? "" : source.toLowerCase(Locale.ROOT);
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    private String rootMessage(Throwable throwable)
    {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current)
        {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.getClass().getSimpleName() : current.getMessage();
    }

    private String limitMessage(String message, int maxLength)
    {
        if (!StringUtils.hasText(message)) return "模型服务未返回错误详情";
        return message.length() <= maxLength ? message : message.substring(0, maxLength);
    }

    private static class ImagePayload
    {
        private final Resource resource;
        private final String fileName;
        private final MediaType mediaType;

        private ImagePayload(Resource resource, String fileName, MediaType mediaType)
        {
            this.resource = resource;
            this.fileName = fileName;
            this.mediaType = mediaType;
        }
    }
}
