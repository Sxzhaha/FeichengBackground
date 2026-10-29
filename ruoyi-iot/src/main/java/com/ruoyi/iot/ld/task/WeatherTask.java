package com.ruoyi.iot.ld.task;

import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.iot.ld.domain.AgriWeatherForecast;
import com.ruoyi.iot.ld.domain.AgriWeatherLive;
import com.ruoyi.iot.ld.domain.Park;
import org.springframework.http.MediaType;
import com.ruoyi.iot.ld.domain.AgriWeatherAlertAdvice;
import com.ruoyi.iot.ld.service.IAgriWeatherAlertAdviceService;
import com.ruoyi.iot.ld.service.IAgriWeatherForecastService;
import com.ruoyi.iot.ld.service.IAgriWeatherLiveService;
import com.ruoyi.iot.ld.service.IParkService;

/**
 * 和风天气定时拉取抓取任务
 * 每天早上8点和下午3点执行
 *
 * @author ruoyi
 */
@Component
public class WeatherTask {

    private static final Logger log = LoggerFactory.getLogger(WeatherTask.class);

    @Value("${qweather.apiHost}")
    private String apiHost;

    @Value("${qweather.apiKey}")
    private String apiKey;

    @Value("${qweather.sync-on-startup:true}")
    private boolean syncOnStartup;

    @Value("${qweather.live-cron:0 0 0/2 * * ?}")
    private String liveCron;

    @Value("${qweather.forecast-cron:0 5 0/6 * * ?}")
    private String forecastCron;

    @Value("${qweather.time-zone:Asia/Shanghai}")
    private String timeZone;

    @Value("${qweather.connect-timeout-ms:10000}")
    private int connectTimeoutMs;

    @Value("${qweather.read-timeout-ms:30000}")
    private int readTimeoutMs;

    private RestTemplate weatherRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IParkService parkService;

    @Autowired
    private IAgriWeatherLiveService weatherLiveService;

    @Autowired
    private IAgriWeatherForecastService weatherForecastService;

    @Value("${dashscope.apiKey}")
    private String dashscopeApiKey;

    @Value("${dashscope.model:qwen3.7-plus}")
    private String dashscopeModel;

    @Autowired
    private IAgriWeatherAlertAdviceService weatherAlertAdviceService;

    @PostConstruct
    public void initWeatherClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Math.max(connectTimeoutMs, 1000));
        requestFactory.setReadTimeout(Math.max(readTimeoutMs, 1000));
        weatherRestTemplate = new RestTemplate(requestFactory);
        log.info("【天气定时任务】配置已加载：实时天气 cron=[{}]，七天天气 cron=[{}]，时区=[{}]，连接超时={}ms，读取超时={}ms",
                liveCron, forecastCron, timeZone, connectTimeoutMs, readTimeoutMs);
    }

    /**
     * 手动触发完整天气数据拉取同步
     */
    public void syncWeather() {
        log.info("【和风天气手动同步任务】开始执行...");
        syncLiveWeather();
        syncForecastWeather();
        log.info("【和风天气手动同步任务】全部园区天气同步完成。");
    }

    /** 服务启动后先同步一次，避免页面在首次定时触发前没有天气数据。 */
    @EventListener(ApplicationReadyEvent.class)
    public void syncWeatherOnStartup() {
        if (!syncOnStartup) {
            return;
        }
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                log.info("【和风天气启动同步任务】开始执行...");
                syncWeather();
            } catch (Exception e) {
                log.error("【和风天气启动同步任务】执行失败", e);
            }
        });
    }

    /** 定时同步实时天气，执行周期由 application.yml 配置。 */
    @Scheduled(
            cron = "${qweather.live-cron:0 0 0/2 * * ?}",
            zone = "${qweather.time-zone:Asia/Shanghai}")
    public void scheduledSyncLiveWeather() {
        long startTime = System.currentTimeMillis();
        log.info("【定时任务】开始自动抓取实时天气，当前 cron=[{}]...", liveCron);
        try {
            syncLiveWeather();
            log.info("【定时任务】实时天气自动抓取完成，耗时={}ms。", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("【定时任务】实时天气自动抓取失败，耗时=" + (System.currentTimeMillis() - startTime) + "ms。", e);
        }
    }

    /** 定时同步未来七天天气，执行周期由 application.yml 配置。 */
    @Scheduled(
            cron = "${qweather.forecast-cron:0 5 0/6 * * ?}",
            zone = "${qweather.time-zone:Asia/Shanghai}")
    public void scheduledSyncForecastWeather() {
        long startTime = System.currentTimeMillis();
        log.info("【定时任务】开始自动抓取七天天气预报，当前 cron=[{}]...", forecastCron);
        try {
            syncForecastWeather();
            log.info("【定时任务】七天天气预报自动抓取完成，耗时={}ms。", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("【定时任务】七天天气预报自动抓取失败，耗时=" + (System.currentTimeMillis() - startTime) + "ms。", e);
        }
    }


    /**
     * 同步所有园区实时天气数据
     */
    public void syncLiveWeather() {
        log.info("【和风实时天气抓取任务】开始执行...");
        List<Park> parks = parkService.selectParkList(new Park());
        if (parks == null || parks.isEmpty()) {
            log.warn("【和风实时天气抓取任务】未找到任何园区，跳过。");
            return;
        }

        for (Park park : parks) {
            try {
                String location = getQWeatherLocation(park);
                if (StringUtils.isEmpty(location)) {
                    log.warn("【和风实时天气抓取任务】无法解析园区 [{}] 的经纬度或地区代码，跳过天气抓取。", park.getParkName());
                    continue;
                }

                log.info("【和风实时天气抓取任务】正在为园区 [{}] (Location: {}) 抓取实时天气...", park.getParkName(), location);
                fetchAndSaveLiveWeather(park, location);
            } catch (Exception e) {
                log.error("【和风实时天气抓取任务】同步园区 [" + park.getParkName() + "] 实时天气失败: ", e);
            }
        }
        log.info("【和风实时天气抓取任务】执行完成。");
    }

    /**
     * 同步所有园区未来7天天气预报数据
     */
    public void syncForecastWeather() {
        log.info("【和风7天预报抓取任务】开始执行...");
        List<Park> parks = parkService.selectParkList(new Park());
        if (parks == null || parks.isEmpty()) {
            log.warn("【和风7天预报抓取任务】未找到任何园区，跳过。");
            return;
        }

        for (Park park : parks) {
            try {
                String location = getQWeatherLocation(park);
                if (StringUtils.isEmpty(location)) {
                    log.warn("【和风7天预报抓取任务】无法解析园区 [{}] 的经纬度或地区代码，跳过天气预报抓取。", park.getParkName());
                    continue;
                }

                log.info("【和风7天预报抓取任务】正在为园区 [{}] (Location: {}) 抓取7天天气预报...", park.getParkName(), location);
                fetchAndSaveForecastWeather(park, location);
            } catch (Exception e) {
                log.error("【和风7天预报抓取任务】同步园区 [" + park.getParkName() + "] 7天天气预报失败: ", e);
            }
        }
        log.info("【和风7天预报抓取任务】执行完成。");
    }


    /**
     * 获取和风天气的 Location 参数 (支持 lon,lat 坐标或 Location ID)
     */
    private String getQWeatherLocation(Park park) {
        // 优先使用 regionCode (即已存储的 location ID, 如 101120801)
        if (StringUtils.isNotEmpty(park.getRegionCode())) {
            return park.getRegionCode();
        }

        // 其次使用 coordinate 经纬度信息
        String coords = parseCoordinates(park.getCoordinate());
        if (StringUtils.isNotEmpty(coords)) {
            return coords;
        }

        // 回退使用 region (城市名字) 从 GeoAPI 查询 Location ID
        String cityQuery = park.getRegion();
        if (StringUtils.isEmpty(cityQuery)) {
            return null;
        }

        try {
            String geoUrl = String.format("https://%s/v2/city/lookup?location=%s", apiHost, cityQuery);
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-QW-Api-Key", apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = weatherRestTemplate.exchange(geoUrl, HttpMethod.GET, entity, byte[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String bodyStr = decompressResponse(response.getBody());
                JSONObject json = JSON.parseObject(bodyStr);
                if ("200".equals(json.getString("code"))) {
                    JSONArray locArray = json.getJSONArray("location");
                    if (locArray != null && !locArray.isEmpty()) {
                        return locArray.getJSONObject(0).getString("id");
                    }
                }
            }
        } catch (Exception e) {
            log.error("【和风天气同步任务】调用城市查询接口失败，参数: " + cityQuery, e);
        }
        return null;
    }


    /**
     * 抓取实时天气并持久化
     */
    private void fetchAndSaveLiveWeather(Park park, String location) {
        String url = String.format("https://%s/v7/weather/now?location=%s", apiHost, location);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-QW-Api-Key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = weatherRestTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String bodyStr = decompressResponse(response.getBody());
            JSONObject json = JSON.parseObject(bodyStr);
            if ("200".equals(json.getString("code"))) {
                JSONObject nowObj = json.getJSONObject("now");
                if (nowObj != null) {
                    AgriWeatherLive live = new AgriWeatherLive();
                    live.setParkId(String.valueOf(park.getParkId()));
                    live.setFetchTime(new Date());

                    String obsTime = nowObj.getString("obsTime");
                    if (StringUtils.isNotEmpty(obsTime)) {
                        try {
                            live.setReportTime(Date.from(java.time.ZonedDateTime.parse(obsTime).toInstant()));
                        } catch (Exception e) {
                            live.setReportTime(new Date());
                        }
                    } else {
                        live.setReportTime(new Date());
                    }

                    live.setTemperature(nowObj.getDouble("temp"));
                    live.setHumidity(nowObj.getLong("humidity"));
                    live.setWindDirection(nowObj.getString("windDir"));
                    live.setWindSpeed(nowObj.getDouble("windSpeed"));
                    live.setWindScale(nowObj.getLong("windScale"));
                    live.setPrecipitation(nowObj.getDouble("precip"));
                    live.setAirPressure(nowObj.getDouble("pressure"));
                    live.setWeatherCode(nowObj.getString("icon"));
                    live.setWeatherText(nowObj.getString("text"));
                    live.setRemark1(1L);
                    live.setRemark2(bodyStr); // 快照存盘

                    weatherLiveService.insertAgriWeatherLive(live);
                    log.info("【和风天气同步任务】园区 [{}] 实时天气记录写入成功 (温度: {}℃, 天气: {})",
                            park.getParkName(), live.getTemperature(), live.getWeatherText());
                }
            } else {
                log.warn("【和风天气同步任务】获取实时天气返回非200状态码: {}", json.getString("code"));
            }
        }
    }

        /**
     * 抓取7天天气预报并持久化
     */
    private void fetchAndSaveForecastWeather(Park park, String location) {
        String url = String.format("https://%s/v7/weather/7d?location=%s", apiHost, location);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-QW-Api-Key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = weatherRestTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String bodyStr = decompressResponse(response.getBody());
            JSONObject json = JSON.parseObject(bodyStr);
            if ("200".equals(json.getString("code"))) {
                JSONArray dailyArray = json.getJSONArray("daily");
                if (dailyArray != null && !dailyArray.isEmpty()) {
                    int insertCount = 0;
                    java.util.List<AgriWeatherForecast> forecastList = new java.util.ArrayList<>();
                    for (int i = 0; i < dailyArray.size(); i++) {
                        JSONObject dayObj = dailyArray.getJSONObject(i);
                        AgriWeatherForecast forecast = new AgriWeatherForecast();
                        forecast.setParkId(String.valueOf(park.getParkId()));
                        forecast.setFetchTime(new Date());

                        String fxDate = dayObj.getString("fxDate");
                        if (StringUtils.isNotEmpty(fxDate)) {
                            try {
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                forecast.setForecastDate(sdf.parse(fxDate));
                            } catch (Exception e) {
                                forecast.setForecastDate(new Date());
                            }
                        } else {
                            forecast.setForecastDate(new Date());
                        }

                        forecast.setTempMax(dayObj.getDouble("tempMax"));
                        forecast.setTempMin(dayObj.getDouble("tempMin"));
                        forecast.setHumidityAvg(dayObj.getDouble("humidity"));
                        forecast.setWindDirDay(dayObj.getString("windDirDay"));
                        forecast.setWindScaleDay(dayObj.getString("windScaleDay"));
                        forecast.setPrecipTotal(dayObj.getDouble("precip"));
                        forecast.setPrecipProb(dayObj.getDouble("precipProb"));
                        forecast.setWeatherTextDay(dayObj.getString("textDay"));
                        forecast.setWeatherTextNight(dayObj.getString("textNight"));
                        forecast.setDayCode(dayObj.getString("iconDay"));
                        forecast.setMoonCode(dayObj.getString("iconNight"));
                        forecast.setForecastType(7); // 7天预报
                        forecast.setRemark1(1L);
                        forecast.setRemark2(bodyStr); // 快照存盘

                        weatherForecastService.insertAgriWeatherForecast(forecast);
                        forecastList.add(forecast);
                        insertCount++;
                    }
                    log.info("【和风天气同步任务】园区 [{}] 7天天气预报成功写入 {} 条记录", park.getParkName(), insertCount);
                    if (!forecastList.isEmpty()) {
                        // 大模型风险研判与预警触发 (异步后台运行，防止阻塞主线程导致请求超时)
                        java.util.concurrent.CompletableFuture.runAsync(() -> checkAndGenerateForecastAlertBatch(park, forecastList));
                    }
                }
            } else {
                log.warn("【和风天气同步任务】获取7天天气预报返回非200状态码: {}", json.getString("code"));
            }
        }
    }

    /**
     * 解压/转换响应 Body，支持 GZIP 解压
     */
    private String decompressResponse(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        try {
            // 判断是否是 GZIP 压缩格式 (GZIP Magic Number: 0x1f8b)
            if (bytes.length >= 2 && ((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8)) == 0x8b1f) {
                try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(bytes);
                     java.util.zip.GZIPInputStream gzis = new java.util.zip.GZIPInputStream(bais);
                     java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = gzis.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    return baos.toString("UTF-8");
                }
            }
        } catch (Exception e) {
            log.error("【和风天气同步】GZIP 解压失败，尝试使用默认 UTF-8 转换", e);
        }
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }


    /**
     * 经纬度安全解析辅助方法，智能识别经纬度排列并返回 lon,lat 格式
     */
    private String parseCoordinates(String coordinateStr) {
        if (StringUtils.isEmpty(coordinateStr)) {
            return null;
        }
        try {
            JSONArray array = JSON.parseArray(coordinateStr);
            if (array != null && !array.isEmpty()) {
                Object first = array.get(0);
                if (first instanceof JSONArray) {
                    JSONArray point = (JSONArray) first;
                    if (point.size() >= 2) {
                        Double val1 = point.getDouble(0);
                        Double val2 = point.getDouble(1);
                        return formatLonLat(val1, val2);
                    }
                } else if (array.size() >= 2) {
                    Double val1 = array.getDouble(0);
                    Double val2 = array.getDouble(1);
                    return formatLonLat(val1, val2);
                }
            }
        } catch (Exception e) {
            log.error("【和风天气同步任务】解析园区经纬度字段失败: " + coordinateStr, e);
        }
        return null;
    }

    /**
     * 智能对齐经度和纬度 (中国境内经度约 73.5 ~ 135.0, 纬度约 18.0 ~ 53.5)
     */
    private String formatLonLat(Double val1, Double val2) {
        if (val1 == null || val2 == null) {
            return null;
        }
        // 识别经度与纬度分布
        if (val1 >= 70.0 && val1 <= 140.0 && val2 >= 15.0 && val2 <= 55.0) {
            return String.format("%.4f,%.4f", val1, val2); // val1是经度，val2是纬度
        } else if (val2 >= 70.0 && val2 <= 140.0 && val1 >= 15.0 && val1 <= 55.0) {
            return String.format("%.4f,%.4f", val2, val1); // val2是经度，val1是纬度
        }
        // 默认按 standard GeoJSON 格式 lon,lat 返回
        return String.format("%.4f,%.4f", val1, val2);
    }

    /**
     * 调用大模型研判实时天气是否需要预警，如需要则写入预警表
     */
    private void checkAndGenerateLiveAlert(Park park, AgriWeatherLive live) {
        log.info("【和风天气】开始对园区 [{}] 进行实时天气大模型研判...", park.getParkName());
        if (StringUtils.isEmpty(dashscopeApiKey)) {
            log.info("【和风天气】大模型 API Key 为空，跳过实时天气预警研判。");
            return;
        }
        try {
            String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json"));
            headers.set("Authorization", "Bearer " + dashscopeApiKey);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", dashscopeModel);

            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一位精通物联网数据监控与枣树农业防灾减灾的AI专家。根据提供的实时天气数据，只通过 JSON 格式输出分析研判结果。");
            messages.add(systemMessage);

            String userContent = String.format(
                "请根据以下物联网园区实时气象环境数据，研判当前气象是否存在灾害风险（例如：极端高温、寒潮大降温、暴雨、大风沙尘暴等会对大枣/枣树造成负面影响的异常天气）。\n" +
                "如果判定存在灾害风险，需要生成农业预警，并提供具体的农事防灾应急建议。\n\n" +
                "【当前园区实时气象数据】\n" +
                "- 园区名称: %s\n" +
                "- 天气现象: %s\n" +
                "- 当前温度: %s ℃\n" +
                "- 相对湿度: %s %%\n" +
                "- 风向风力: %s %s级 (风速 %s m/s)\n" +
                "- 当前降水量: %s mm\n" +
                "- 大气压强: %s hPa\n\n" +
                "【要求】请分析并以标准的 JSON 格式返回（不需要 markdown 块标记，仅返回纯 JSON，不要有任何前导或后随文字）。\n" +
                "格式如下：\n" +
                "{\n" +
                "  \"needAlert\": true 或 false (当数据指标明显偏离正常值，存在寒潮、冻害、大风、暴雨、高温等灾害风险时为true，否则为false),\n" +
                "  \"alertLevel\": \"蓝色\" / \"黄色\" / \"橙色\" / \"红色\" / \"无\" (如果needAlert为false，此处为\"无\"),\n" +
                "  \"anomalyInfo\": \"触发异常的实况描述（如：当前气温高达39.5℃，相对湿度极低，且风力达5级）\",\n" +
                "  \"aqiEvaluation\": \"对当前空气质量与气候环境的综合评价\",\n" +
                "  \"alertAdvice\": \"对当前异常天气的防灾减灾农事建议（如有，请给出，没有则为无，限80字左右）\"\n" +
                "}",
                park.getParkName(),
                live.getWeatherText() != null ? live.getWeatherText() : "未知",
                live.getTemperature() != null ? live.getTemperature().toString() : "无数据",
                live.getHumidity() != null ? live.getHumidity().toString() : "无数据",
                live.getWindDirection() != null ? live.getWindDirection() : "未知",
                live.getWindScale() != null ? live.getWindScale().toString() : "0",
                live.getWindSpeed() != null ? live.getWindSpeed().toString() : "0",
                live.getPrecipitation() != null ? live.getPrecipitation().toString() : "0",
                live.getAirPressure() != null ? live.getAirPressure().toString() : "无数据"
            );

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userContent);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject responseJson = JSON.parseObject(response.getBody());
                JSONArray choices = responseJson.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                    if (message != null) {
                        String content = message.getString("content");
                        if (StringUtils.isNotEmpty(content)) {
                            // 鲁棒性提取 JSON 字符串
                            int startIdx = content.indexOf("{");
                            int endIdx = content.lastIndexOf("}");
                            if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
                                content = content.substring(startIdx, endIdx + 1);
                            }
                            JSONObject resultJson = JSON.parseObject(content);
                            Boolean needAlert = resultJson.getBoolean("needAlert");
                            log.info("【和风天气】实时数据大模型研判结果 - 园区: {}, 是否预警: {}, 预警级别: {}, 异常描述: {}", 
                                    park.getParkName(), needAlert, resultJson.getString("alertLevel"), resultJson.getString("anomalyInfo"));
                            if (needAlert != null && needAlert) {
                                AgriWeatherAlertAdvice advice = new AgriWeatherAlertAdvice();
                                advice.setWeatherId("[" + live.getId() + "]");
                                advice.setAnomalyInfo(resultJson.getString("anomalyInfo"));
                                advice.setAlertLevel(resultJson.getString("alertLevel"));
                                advice.setAqiEvaluation(resultJson.getString("aqiEvaluation"));
                                advice.setAlertAdvice(resultJson.getString("alertAdvice"));
                                advice.setAuditStatus(0L); // 未处理
                                advice.setRemark1(1L);
                                advice.setType(0L); // 0代表实时
                                advice.setCreateTime(new Date());

                                weatherAlertAdviceService.insertAgriWeatherAlertAdvice(advice);
                                log.info("【和风天气】实时数据触发大模型预警并成功插入，预警等级: {}", advice.getAlertLevel());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("【和风天气】实时气象大模型研判异常: ", e);
        }
    }

    /**
     * 调用大模型批量研判7天天气预报是否需要预警，如需要则写入预警表
     */
    private void checkAndGenerateForecastAlertBatch(Park park, java.util.List<AgriWeatherForecast> forecasts) {
        log.info("【和风天气】开始对园区 [{}] 进行7天天气预报大模型研判...", park.getParkName());
        if (StringUtils.isEmpty(dashscopeApiKey)) {
            log.info("【和风天气】大模型 API Key 为空，跳过7天天气预报预警研判。");
            return;
        }
        try {
            String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json"));
            headers.set("Authorization", "Bearer " + dashscopeApiKey);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", dashscopeModel);

            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一位精通物联网数据监控与枣树农业防灾减灾的AI专家。根据提供的天气预报数据，只通过 JSON 格式输出分析研判结果。");
            messages.add(systemMessage);

            java.text.SimpleDateFormat methodSdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            StringBuilder forecastsDetails = new StringBuilder();
            for (int i = 0; i < forecasts.size(); i++) {
                AgriWeatherForecast f = forecasts.get(i);
                String fDate = f.getForecastDate() != null ? methodSdf.format(f.getForecastDate()) : "未知";
                forecastsDetails.append(String.format(
                    "%d. 预报日期: %s\n" +
                    "   - 天气现象: 白天 [%s], 夜间 [%s]\n" +
                    "   - 温度区间: 最低 %s ℃ ~ 最高 %s ℃\n" +
                    "   - 相对湿度: %s %%\n" +
                    "   - 风向风力: %s %s级\n" +
                    "   - 预报降水量: %s mm, 降水概率: %s %%\n\n",
                    i + 1,
                    fDate,
                    f.getWeatherTextDay() != null ? f.getWeatherTextDay() : "未知",
                    f.getWeatherTextNight() != null ? f.getWeatherTextNight() : "未知",
                    f.getTempMin() != null ? f.getTempMin().toString() : "无数据",
                    f.getTempMax() != null ? f.getTempMax().toString() : "无数据",
                    f.getHumidityAvg() != null ? f.getHumidityAvg().toString() : "无数据",
                    f.getWindDirDay() != null ? f.getWindDirDay() : "未知",
                    f.getWindScaleDay() != null ? f.getWindScaleDay() : "0",
                    f.getPrecipTotal() != null ? f.getPrecipTotal().toString() : "0",
                    f.getPrecipProb() != null ? f.getPrecipProb().toString() : "0"
                ));
            }

            String userContent = String.format(
                "请根据以下物联网园区未来7天的天气预报数据，研判未来是否将面临气象灾害风险（例如：连续阴雨、倒春寒、冰雹、强降雨、寒潮大降温、大风、台风等可能对大枣/枣树造成严重负面影响的极端或持续性天气状况）。\n" +
                "如果判定存在灾害风险，需要生成农业预警，并提供具体的防灾备耕或防御应急农事建议。\n\n" +
                "【未来7天预报气象数据】\n" +
                "- 园区名称: %s\n\n" +
                "【各天天气预报详情】\n" +
                "%s" +
                "【要求】请整体评估未来7天的天气趋势，分析是否存在需防范的灾害风险，并以标准的 JSON 格式返回（不需要 markdown 块标记，仅返回纯 JSON，不要有任何前导或后随文字）。\n" +
                "格式如下：\n" +
                "{\n" +
                "  \"needAlert\": true 或 false (如果未来7天中存在灾害风险则为true，否则为false),\n" +
                "  \"alertLevel\": \"蓝色\" / \"黄色\" / \"橙色\" / \"红色\" / \"无\" (如果needAlert为false，此处为\"无\"),\n" +
                "  \"anomalyInfo\": \"触发异常的预报描述（如：预计在X月X日到X月X日将有持续大暴雨，日降雨量达XXmm，并伴有大风）\",\n" +
                "  \"aqiEvaluation\": \"对未来一周整体气候环境与空气质量的综合评估\",\n" +
                "  \"alertAdvice\": \"针对未来一周气象灾害的防灾备耕或应急农事建议（如有，请给出，没有则为无，限120字左右）\"\n" +
                "}",
                park.getParkName(),
                forecastsDetails.toString()
            );

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userContent);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject responseJson = JSON.parseObject(response.getBody());
                JSONArray choices = responseJson.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                    if (message != null) {
                        String content = message.getString("content");
                        if (StringUtils.isNotEmpty(content)) {
                            // 鲁棒性提取 JSON 字符串
                            int startIdx = content.indexOf("{");
                            int endIdx = content.lastIndexOf("}");
                            if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
                                content = content.substring(startIdx, endIdx + 1);
                            }
                            JSONObject resultJson = JSON.parseObject(content);
                            Boolean needAlert = resultJson.getBoolean("needAlert");

                            // 拼接这7天预报的ID列表，形如 [5,6,8,9,12,16,18]
                            String weatherIds = forecasts.stream()
                                    .map(f -> String.valueOf(f.getId()))
                                    .collect(java.util.stream.Collectors.joining(",", "[", "]"));

                            log.info("【和风天气】7天预报数据大模型研判结果 - 园区: {}, 是否预警: {}, 预警级别: {}, 异常描述: {}, 关联ID: {}", 
                                    park.getParkName(), needAlert, resultJson.getString("alertLevel"), resultJson.getString("anomalyInfo"), weatherIds);

                            if (needAlert != null && needAlert) {
                                AgriWeatherAlertAdvice advice = new AgriWeatherAlertAdvice();
                                advice.setWeatherId(weatherIds);
                                advice.setAnomalyInfo(resultJson.getString("anomalyInfo"));
                                advice.setAlertLevel(resultJson.getString("alertLevel"));
                                advice.setAqiEvaluation(resultJson.getString("aqiEvaluation"));
                                advice.setAlertAdvice(resultJson.getString("alertAdvice"));
                                advice.setAuditStatus(0L); // 未处理
                                advice.setRemark1(1L);
                                advice.setType(1L); // 1代表预报天气
                                advice.setCreateTime(new Date());

                                weatherAlertAdviceService.insertAgriWeatherAlertAdvice(advice);
                                log.info("【和风天气】7天预报数据触发大模型预警并成功插入，预警等级: {}", advice.getAlertLevel());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("【和风天气】7天预报天气大模型研判异常: ", e);
        }
    }
}
