package com.ruoyi.binZhou.wr.service.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import org.springframework.web.client.RestClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.TreeMap;

/** Bounded SSE transport for report advice only. */
public final class AgriAdviceStream {
    private static final Logger log = LoggerFactory.getLogger(AgriAdviceStream.class);
    private AgriAdviceStream() {}

    public static String request(String url, String key, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "wr-advice-deadline");
            thread.setDaemon(true);
            return thread;
        });
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(35);
        ScheduledFuture<?> timeout = timer.schedule(connection::disconnect, 35, TimeUnit.SECONDS);
        try {
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "text/event-stream");
            connection.setRequestProperty("Authorization", "Bearer " + key);
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(bytes.length);
            try (OutputStream out = connection.getOutputStream()) { out.write(bytes); }
            int status = connection.getResponseCode();
            String contentType = connection.getContentType();
            // Log only the media type, not arbitrary response header values.
            String mediaType = contentType == null ? "missing" : contentType.split(";", 2)[0].trim();
            if (!mediaType.matches("[A-Za-z0-9.+-]+/[A-Za-z0-9.+-]+")) mediaType = "invalid";
            log.info("AgriAdvice HTTP response, httpStatus={}, contentType={}", status, mediaType);
            if (status != 200) throw new RestClientResponseException("AI HTTP error", status, "", null, new byte[0], StandardCharsets.UTF_8);
            connection.setReadTimeout(10000);
            try (InputStream in = connection.getInputStream()) { return parse(in, deadline); }
        } catch (IOException e) {
            if (System.nanoTime() >= deadline) {
                SocketTimeoutException expired = new SocketTimeoutException("AI total generation deadline exceeded");
                expired.initCause(e);
                throw expired;
            }
            throw e;
        } finally {
            timeout.cancel(false);
            connection.disconnect();
            timer.shutdownNow();
        }
    }

    public static String parse(InputStream input, long deadline) throws IOException {
        return parse(input, deadline, new Diagnostics());
    }

    static String parse(InputStream input, long deadline, Diagnostics stats) throws IOException {
        long started = System.nanoTime();
        boolean success = false;
        try {
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT));
        StringBuilder line = new StringBuilder(), event = new StringBuilder(), content = new StringBuilder();
        boolean stopped = false;
        while (true) {
            if (System.nanoTime() >= deadline) throw new SocketTimeoutException("AI total generation deadline exceeded");
            int ch = reader.read();
            if (ch == -1) throw new IOException("Incomplete AI stream");
            if (++stats.totalChars > 131072) throw new StreamSizeLimitException();
            if (ch != '\n') {
                if (line.length() >= 32768) throw new IOException("AI event exceeds limit");
                line.append((char) ch);
                continue;
            }
            String text = line.toString();
            line.setLength(0);
            if (text.endsWith("\r")) text = text.substring(0, text.length() - 1);
            if (!text.isEmpty()) {
                if (text.startsWith("data:")) {
                    String data = text.substring(5);
                    if (data.startsWith(" ")) data = data.substring(1);
                    if (event.length() > 0) event.append('\n');
                    event.append(data);
                    if (event.length() > 32768) throw new IOException("AI event exceeds limit");
                }
                continue;
            }
            if (event.length() == 0) continue;
            String data = event.toString();
            event.setLength(0);
            stats.events++;
            if ("[DONE]".equals(data)) {
                stats.done = true;
                if (!stopped || content.length() == 0) throw new IOException("Incomplete AI stream");
                success = true;
                return content.toString();
            }
            JSONObject json;
            try { json = JSON.parseObject(data); }
            catch (RuntimeException e) { throw new IOException("Invalid AI stream JSON"); }
            if (json == null || json.containsKey("error")) throw new IOException("AI stream error");
            JSONArray choices = json.getJSONArray("choices");
            if (choices == null) throw new IOException("Missing AI stream choices");
            if (choices.isEmpty()) continue;
            JSONObject choice = choices.getJSONObject(0);
            JSONObject delta = choice.getJSONObject("delta");
            if (delta != null) stats.recordDelta(delta);
            Object fragment = delta == null ? null : delta.get("content");
            if (fragment != null) {
                if (!(fragment instanceof String) || stopped) throw new IOException("Invalid AI stream content");
                content.append((String) fragment);
                if (content.length() > 6000) throw new IOException("AI content exceeds limit");
            }
            String finish = choice.getString("finish_reason");
            if (finish != null) {
                stats.finishSeen = true;
                // Standard protocol values only; unknown strings may contain sensitive content.
                stats.finish = java.util.Arrays.asList("stop", "length", "tool_calls", "content_filter", "function_call")
                        .contains(finish) ? finish : "unknown";
                if (!"stop".equals(finish)) throw new IOException("Incomplete AI stream finish");
                stopped = true;
            }
        }
        } finally {
            log.info("AgriAdvice SSE summary, events={}, totalChars={}, contentChars={}, reasoningPresent={}, reasoningChars={}, otherDeltaLengths={}, finishSeen={}, finishReason={}, done={}, elapsedMs={}, success={}",
                    stats.events, stats.totalChars, stats.contentChars, stats.reasoningPresent, stats.reasoningChars,
                    stats.otherLengths, stats.finishSeen, stats.finish, stats.done,
                    (System.nanoTime() - started) / 1000000, success);
        }
    }

    static final class Diagnostics {
        int events, totalChars, contentChars, reasoningChars;
        boolean reasoningPresent, finishSeen, done;
        String finish = "missing";
        final Map<String, Integer> otherLengths = new TreeMap<>();

        void recordDelta(JSONObject delta) {
            for (Map.Entry<String, Object> field : delta.entrySet()) {
                Object value = field.getValue();
                int length = value == null ? 0 : value instanceof String
                        ? ((String) value).length() : JSON.toJSONString(value).length();
                if ("content".equals(field.getKey())) {
                    if (value instanceof String) contentChars += length;
                } else if ("reasoning_content".equals(field.getKey())) {
                    reasoningPresent = true;
                    if (value instanceof String) reasoningChars += length;
                } else {
                    String name = field.getKey();
                    // Bound and sanitize field names; never include their values.
                    if (!name.matches("[A-Za-z_][A-Za-z0-9_]{0,47}")) name = "invalid_field";
                    if (!otherLengths.containsKey(name) && otherLengths.size() >= 16) name = "other_fields";
                    otherLengths.merge(name, length, Integer::sum);
                }
            }
        }
    }

    public static final class StreamSizeLimitException extends IOException {
        private static final long serialVersionUID = 1L;
        private StreamSizeLimitException() { super("AI stream exceeds limit"); }
    }
}
