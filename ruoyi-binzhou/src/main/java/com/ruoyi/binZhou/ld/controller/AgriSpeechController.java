package com.ruoyi.binZhou.ld.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.alibaba.fastjson2.JSONWriter;

import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

/** 语音输入代理：App 不直接暴露百炼 API Key。 */
@RestController
@RequestMapping("/binZhou/ld/speech")
public class AgriSpeechController {
    @Value("${dashscope.apiKey:}")
    private String apiKey;

    @Value("${dashscope.realtimeWsUrl:wss://ws-fqxac1dhsduuhlwh.cn-beijing.maas.aliyuncs.com/api-ws/v1/realtime?model=qwen3.8-livetranslate-flash-realtime}")
    private String realtimeWsUrl;

    @Value("${dashscope.workspaceId:}")
    private String workspaceId;

    @Value("${dashscope.translationLanguage:zh}")
    private String translationLanguage;

    @Value("${dashscope.sourceLanguage:zh}")
    private String sourceLanguage;

    @PostMapping("/transcribe")
    public AjaxResult transcribe(@RequestParam("audio") MultipartFile audio) {
        if (audio == null || audio.isEmpty()) return AjaxResult.error("录音文件为空");
        if (apiKey == null || apiKey.trim().isEmpty()) return AjaxResult.error("DashScope API Key 未配置");
        try {
            byte[] pcm = wavPcm(audio.getBytes());
            String text = transcribePcm(pcm);
            return AjaxResult.success().put("text", text);
        } catch (Exception e) {
            return AjaxResult.error("语音识别失败：" + (e.getMessage() == null ? "实时模型请求异常" : e.getMessage()));
        }
    }

    private byte[] wavPcm(byte[] bytes) {
        if (bytes.length < 44 || bytes[0] != 'R' || bytes[1] != 'I' || bytes[2] != 'F' || bytes[3] != 'F') return bytes;
        int offset = 12;
        while (offset + 8 <= bytes.length) {
            int size = littleEndianInt(bytes, offset + 4);
            if (bytes[offset] == 'd' && bytes[offset + 1] == 'a' && bytes[offset + 2] == 't' && bytes[offset + 3] == 'a') {
                int start = offset + 8;
                int end = Math.min(bytes.length, start + size);
                byte[] pcm = new byte[Math.max(0, end - start)];
                System.arraycopy(bytes, start, pcm, 0, pcm.length);
                return pcm;
            }
            offset += 8 + Math.max(0, size);
        }
        return bytes;
    }

    private int littleEndianInt(byte[] bytes, int offset) {
        return (bytes[offset] & 255) | ((bytes[offset + 1] & 255) << 8) | ((bytes[offset + 2] & 255) << 16) | ((bytes[offset + 3] & 255) << 24);
    }

    private String transcribePcm(byte[] pcm) throws Exception {
        CountDownLatch finished = new CountDownLatch(1);
        AtomicReference<String> recognized = new AtomicReference<>("");
        AtomicReference<String> translated = new AtomicReference<>("");
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicBoolean responseDone = new AtomicBoolean(false);
        AtomicBoolean transcriptionDone = new AtomicBoolean(false);
        AtomicBoolean audioSent = new AtomicBoolean(false);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey.trim());
        if (workspaceId != null && !workspaceId.trim().isEmpty()) headers.set("X-DashScope-WorkSpace", workspaceId.trim());
        StandardWebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(new TextWebSocketHandler() {
            @Override public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                JSONObject update = new JSONObject();
                update.put("type", "session.update");
                // qwen3.8-livetranslate uses session.finish to end a complete recording.
                JSONObject data = new JSONObject();
                data.put("output_modalities", new String[]{"text"});
                JSONObject transcription = new JSONObject();
                transcription.put("model", "qwen3-asr-flash-realtime");
                transcription.put("language", sourceLanguage == null || sourceLanguage.trim().isEmpty() ? "zh" : sourceLanguage.trim());
                data.put("input_audio_transcription", transcription);
                JSONObject translation = new JSONObject();
                translation.put("language", translationLanguage == null || translationLanguage.trim().isEmpty() ? "zh" : translationLanguage.trim());
                data.put("translation", translation);
                update.put("session", data);
                session.sendMessage(new TextMessage(update.toJSONString(JSONWriter.Feature.WriteMapNullValue)));
            }

            @Override protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                try {
                    JSONObject event = JSON.parseObject(message.getPayload());
                    String type = event.getString("type");
                    if ("session.updated".equals(type) && audioSent.compareAndSet(false, true)) {
                        JSONObject append = new JSONObject();
                        append.put("type", "input_audio_buffer.append");
                        append.put("audio", Base64.getEncoder().encodeToString(pcm));
                        session.sendMessage(new TextMessage(append.toJSONString()));
                        session.sendMessage(new TextMessage("{\"type\":\"session.finish\"}"));
                    }
                    if ("conversation.item.input_audio_transcription.delta".equals(type)) {
                        String delta = event.getString("delta");
                        if (delta != null) recognized.set(recognized.get() + delta);
                    } else if ("conversation.item.input_audio_transcription.text".equals(type)) {
                        String text = event.getString("text");
                        if (text != null) recognized.set(recognized.get() + text);
                    } else if ("conversation.item.input_audio_transcription.completed".equals(type)) {
                        String text = event.getString("transcript");
                        if (text == null) text = event.getString("text");
                        if (text != null && !text.trim().isEmpty()) recognized.set(text);
                        transcriptionDone.set(true);
                        finished.countDown();
                    } else if ("conversation.item.input_audio_transcription.failed".equals(type)) {
                        JSONObject error = event.getJSONObject("error");
                        failure.set(new IllegalStateException(error == null ? "语音识别失败" : error.getString("message")));
                        finished.countDown();
                    }
                    else if ("response.text.delta".equals(type) || "response.audio_transcript.delta".equals(type)) {
                        String delta = event.getString("delta");
                        if (delta != null) translated.set(translated.get() + delta);
                    }
                    if ("response.done".equals(type)) {
                        responseDone.set(true);
                        if (transcriptionDone.get() || !recognized.get().trim().isEmpty()) finished.countDown();
                    }
                    if ("session.finished".equals(type)) finished.countDown();
                    if ("error".equals(type)) {
                        JSONObject error = event.getJSONObject("error");
                        failure.set(new IllegalStateException(error == null ? "实时模型请求失败" : error.getString("message")));
                        finished.countDown();
                    }
                } catch (Exception e) { failure.set(e); finished.countDown(); }
            }

            @Override public void handleTransportError(WebSocketSession session, Throwable exception) { failure.set(exception); finished.countDown(); }
        }, headers, java.net.URI.create(realtimeWsUrl)).get(20, TimeUnit.SECONDS);
        if (!finished.await(90, TimeUnit.SECONDS)) throw new IllegalStateException("实时语音识别超时");
        if (failure.get() != null) throw new IllegalStateException(failure.get().getMessage(), failure.get());
        String result = recognized.get().trim();
        return result.isEmpty() ? translated.get().trim() : result;
    }

}
