package com.ruoyi.iot.ld.service.impl;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.iot.ld.domain.IotAlertControlLinkage;
import com.ruoyi.iot.ld.domain.IotControlEvent;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.domain.IotProduct;
import com.ruoyi.iot.ld.domain.IotThingModel;
import com.ruoyi.iot.ld.mapper.IotDeviceMapper;
import com.ruoyi.iot.ld.mapper.IotProductMapper;
import com.ruoyi.iot.ld.mapper.IotThingModelMapper;
import com.ruoyi.iot.ld.mqtt.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.iot.ld.mapper.IotDeviceLogMapper;
import com.ruoyi.iot.ld.domain.IotDeviceLog;
import com.ruoyi.iot.ld.domain.AgriProductAlertConfig;
import com.ruoyi.iot.ld.domain.AgriAlertRecord;
import com.ruoyi.iot.ld.service.IIotDeviceLogService;
import com.ruoyi.iot.ld.service.IAgriProductAlertConfigService;
import com.ruoyi.iot.ld.service.IAgriAlertRecordService;
import com.ruoyi.iot.ld.service.IIotAlertControlLinkageService;
import com.ruoyi.iot.ld.service.IIotControlEventService;
import com.ruoyi.iot.ld.service.IIotManualControlEventService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

/**
 * 设备通信日志Service业务层处理
 *
 * @author ruoyi
 * @date 2026-02-02
 */
@Service
public class IotDeviceLogServiceImpl implements IIotDeviceLogService
{
    @Autowired
    private IotDeviceLogMapper iotDeviceLogMapper;

    @Autowired
    private IotDeviceMapper iotDeviceMapper;

    @Autowired
    private IotThingModelMapper iotThingModelMapper;

    @Autowired
    private IotProductMapper iotProductMapper;

    @Autowired
    private IAgriProductAlertConfigService agriProductAlertConfigService;

    @Autowired
    private IAgriAlertRecordService agriAlertRecordService;

    @Autowired
    private IIotAlertControlLinkageService iotAlertControlLinkageService;

    @Autowired
    private IIotControlEventService iotControlEventService;

    @Autowired
    private IIotManualControlEventService iotManualControlEventService;

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private RestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Value("${dashscope.apiKey}")
    private String dashscopeApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<Long, Long> LAST_CONTROL_SEND_TIME = new ConcurrentHashMap<>();

    @org.springframework.beans.factory.annotation.Value("${iot.control-res.alert-prefix:A}")
    private String alertControlResPrefix;

    @org.springframework.beans.factory.annotation.Value("${iot.control-res.manual-prefix:M}")
    private String manualControlResPrefix;

    /**
     * 查询设备通信日志
     */
    @Override
    public IotDeviceLog selectIotDeviceLogByLogId(Long logId)
    {
        return iotDeviceLogMapper.selectIotDeviceLogByLogId(logId);
    }

    /**
     * 查询设备通信日志列表
     */
    @Override
    public List<IotDeviceLog> selectIotDeviceLogList(IotDeviceLog iotDeviceLog)
    {
        return iotDeviceLogMapper.selectIotDeviceLogList(iotDeviceLog);
    }

    /**
     * 新增设备通信日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertIotDeviceLog(IotDeviceLog iotDeviceLog)
    {
        String payload = iotDeviceLog.getPayload();
        String deviceSn = iotDeviceLog.getDeviceSn();

        // Step 1: 基础判空
        if (StringUtils.isEmpty(deviceSn)) {
            throw new ServiceException("设备SN不能为空");
        }
        if (StringUtils.isEmpty(payload)) {
            throw new ServiceException("上报报文不能为空");
        }

        // Step 2: 校验并解析 JSON 格式
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(payload);
            if (!jsonNode.isObject()) {
                throw new ServiceException("Payload必须是JSON对象格式");
            }
        } catch (Exception e) {
            throw new ServiceException("Payload JSON格式解析失败");
        }

        // ================== 核心校验逻辑开始 ==================

        handleControlResponse(deviceSn, payload, jsonNode);

        // Step 3: 查询设备及产品ID
        IotDevice deviceParam = new IotDevice();
        deviceParam.setDeviceSn(deviceSn);
        List<IotDevice> devices = iotDeviceMapper.selectIotDeviceList(deviceParam);

        if (devices.isEmpty()) {
            throw new ServiceException("未找到SN为 " + deviceSn + " 的设备");
        }
        IotDevice device = devices.get(0);
        Long productId = device.getProductId();

        // 查询产品名称
        IotProduct product = iotProductMapper.selectIotProductByProductId(productId);
        String productName = (product != null) ? product.getProductName() : "未知产品";

        // Step 4: 查询物模型定义，并转为 Map<标识符, 模型对象>
        IotThingModel modelParam = new IotThingModel();
        modelParam.setProductId(productId);
        List<IotThingModel> models = iotThingModelMapper.selectIotThingModelList(modelParam);

        Map<String, IotThingModel> modelMap = models.stream()
                .collect(Collectors.toMap(IotThingModel::getIdentifier, m -> m));

        List<AgriAlertRecord> pendingAlertRecords = new ArrayList<>();
        List<AlertTask> alertTasks = new ArrayList<>();

        // Step 5: 遍历 Payload 校验字段存在性、数据类型及预警规则
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            JsonNode valueNode = jsonNode.get(key);

            if ("res".equalsIgnoreCase(key)) {
                continue;
            }

            // 5.1 校验字段是否存在
            if (!modelMap.containsKey(key)) {
                throw new ServiceException("非法字段: [" + key + "] 不属于该产品的物模型定义");
            }

            // 5.2 获取定义的类型及物模型对象
            IotThingModel model = modelMap.get(key);
            String definedType = model.getType();

            // 5.3 校验数据类型
            checkDataType(key, valueNode, definedType);

            // 5.4 获取并校验预警规则
            checkAlertConfig(device, model, valueNode, pendingAlertRecords, alertTasks);
        }

        // ================== 核心校验逻辑结束 ==================

        // Step 6: 插入设备日志，获取自增的主键 logId
        int rows = iotDeviceLogMapper.insertIotDeviceLog(iotDeviceLog);

        // Step 7: 插入预警记录信息
        if (rows > 0 && iotDeviceLog.getLogId() != null && !pendingAlertRecords.isEmpty()) {
            for (AgriAlertRecord record : pendingAlertRecords) {
                record.setDeviceLogId(iotDeviceLog.getLogId());
                agriAlertRecordService.insertAgriAlertRecord(record);
            }

            // 预警触发后，按联动规则异步下发控制终端命令。
            triggerAsyncControlLinkageDispatch(alertTasks);

            // 异步在大模型中更新警报防灾处置建议，避免同步外部HTTP网络调用引起的接口请求超时
            triggerAsyncAlertAdviceUpdate(productName, alertTasks);
        }

        return rows;
    }

    @Override
    public boolean checkLogExists(String deviceSn, String payload, Date createTime) {
        int count = iotDeviceLogMapper.checkLogExists(deviceSn, payload, createTime);
        return count > 0; // 如果数量大于0，说明已存在，返回 true
    }

    /**
     * 辅助方法：校验预警规则配置并生成预警记录
     * @param deviceSn 设备编号
     * @param model 物模型定义
     * @param valueNode JSON节点值
     * @param pendingAlertRecords 暂存的预警列表
     * @param alertTasks 暂存的异步更新任务列表
     */
    private void checkAlertConfig(IotDevice device, IotThingModel model, JsonNode valueNode, List<AgriAlertRecord> pendingAlertRecords, List<AlertTask> alertTasks) {
        String deviceSn = device.getDeviceSn();
        Long deviceId = device.getDeviceId();
        String type = model.getType().toLowerCase();

        // 预警规则通常针对数值型数据。如果不是数值类型也不是可解析为数值的字符串，直接返回。
        if (!type.equals("int") && !type.equals("integer") &&
                !type.equals("float") && !type.equals("double") && !type.equals("decimal")) {
            return;
        }

        if (valueNode == null || valueNode.isNull()) {
            return;
        }

        java.math.BigDecimal actualValue;
        try {
            actualValue = new java.math.BigDecimal(valueNode.asText());
        } catch (NumberFormatException e) {
            return; // 无法解析为数值则不进行预警判断
        }

        // 查询对应的预警规则配置，只查询启用的规则
        AgriProductAlertConfig queryConfig = new AgriProductAlertConfig();
        queryConfig.setModelId(model.getModelId());
        queryConfig.setIsEnabled(1L); // 1表示启用
        List<AgriProductAlertConfig> configs = agriProductAlertConfigService.selectAgriProductAlertConfigList(queryConfig);

        if (configs == null || configs.isEmpty()) {
            return; // 没有对应物模型的预警规则，则不需要预警
        }

        for (AgriProductAlertConfig config : configs) {
            Long configDeviceId = config.getDeviceId();
            if (configDeviceId != null && !configDeviceId.equals(deviceId)) {
                continue;
            }

            java.math.BigDecimal thresholdMin = config.getThresholdMin();
            java.math.BigDecimal thresholdMax = config.getThresholdMax();
            boolean isTriggered = true;

            if (thresholdMin == null && thresholdMax == null) {
                isTriggered = false;
            }
            if (thresholdMin != null && actualValue.compareTo(thresholdMin) < 0) {
                isTriggered = false;
            }
            if (thresholdMax != null && actualValue.compareTo(thresholdMax) > 0) {
                isTriggered = false;
            }

            if (isTriggered) {
                AgriAlertRecord record = new AgriAlertRecord();
                record.setLevelConfigId(config.getId());
                record.setActualValue(actualValue);
                record.setDeviceSn(deviceSn);
                // 设置默认防灾建议为预警配置中的描述规则
                record.setAlertAdvice(config.getRuleDescription());
                record.setHandleStatus(0L); // 0: 未处理
                record.setCreateTime(DateUtils.getNowDate());
                pendingAlertRecords.add(record);

                alertTasks.add(new AlertTask(record, model, config));
            }
        }
    }

    private void handleControlResponse(String deviceSn, String payload, JsonNode jsonNode) {
        if (jsonNode == null || !jsonNode.has("res")) {
            return;
        }

        JsonNode resNode = jsonNode.get("res");
        if (resNode == null || resNode.isNull()) {
            return;
        }

        String res = resNode.asText();
        if (StringUtils.isEmpty(res)) {
            return;
        }

        if (isResForPrefix(res, alertControlResPrefix)) {
            int rows = iotControlEventService.updateIotControlEventResponseByRes(
                    deviceSn,
                    res,
                    payload,
                    "设备返回相同res，预警控制下发成功");
            if (rows > 0) {
                System.out.println(">>> [CONTROL-RESP] SN: " + deviceSn + " res: " + res
                        + " alertEvents: " + rows + " manualEvents: 0");
            }
            return;
        }

        if (isResForPrefix(res, manualControlResPrefix)) {
            int manualRows = iotManualControlEventService.updateIotManualControlEventResponseByRes(
                    deviceSn,
                    res,
                    payload,
                    "设备返回相同res，手动控制下发成功");
            if (manualRows > 0) {
                System.out.println(">>> [CONTROL-RESP] SN: " + deviceSn + " res: " + res
                        + " alertEvents: 0 manualEvents: " + manualRows);
            }
            return;
        }

        int rows = iotControlEventService.updateIotControlEventResponseByRes(
                deviceSn,
                res,
                payload,
                "设备返回相同res，控制下发成功");
        int manualRows = iotManualControlEventService.updateIotManualControlEventResponseByRes(
                deviceSn,
                res,
                payload,
                "设备返回相同res，手动控制下发成功");
        if (rows > 0 || manualRows > 0) {
            System.out.println(">>> [CONTROL-RESP] SN: " + deviceSn + " res: " + res
                    + " alertEvents: " + rows + " manualEvents: " + manualRows);
        }
    }

    private boolean isResForPrefix(String res, String prefix) {
        return StringUtils.isNotEmpty(res) && StringUtils.isNotEmpty(prefix) && res.startsWith(prefix);
    }

    /**
     * 预警记录提交后，异步执行联动控制下发，避免阻塞设备日志上报。
     */
    private void triggerAsyncControlLinkageDispatch(List<AlertTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        List<AlertTask> taskSnapshot = new ArrayList<>(tasks);
        if (org.springframework.transaction.support.TransactionSynchronizationManager.isSynchronizationActive()) {
            org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                new org.springframework.transaction.support.TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        runAsyncControlLinkageDispatch(taskSnapshot);
                    }
                }
            );
        } else {
            runAsyncControlLinkageDispatch(taskSnapshot);
        }
    }

    private void runAsyncControlLinkageDispatch(List<AlertTask> tasks) {
        CompletableFuture.runAsync(() -> {
            for (AlertTask task : tasks) {
                try {
                    dispatchControlLinkages(task);
                } catch (Exception e) {
                    System.err.println("预警联动控制下发失败，预警记录ID: "
                            + (task.record != null ? task.record.getId() : null)
                            + ", 错误: " + e.getMessage());
                }
            }
        });
    }

    private void dispatchControlLinkages(AlertTask task) {
        if (task == null || task.config == null || task.config.getId() == null) {
            return;
        }

        IotAlertControlLinkage query = new IotAlertControlLinkage();
        query.setAlertConfigId(task.config.getId());
        query.setEnabled(1);
        List<IotAlertControlLinkage> linkages = iotAlertControlLinkageService.selectIotAlertControlLinkageList(query);
        if (linkages == null || linkages.isEmpty()) {
            return;
        }

        linkages.sort(Comparator
                .comparingInt(this::priorityOrder)
                .thenComparingInt(this::delayOrder)
                .thenComparing(l -> l.getTargetDeviceId() == null ? Long.MAX_VALUE : l.getTargetDeviceId())
                .thenComparing(l -> l.getId() == null ? Long.MAX_VALUE : l.getId()));

        Map<ControlDispatchGroupKey, List<IotAlertControlLinkage>> groupedLinkages = new LinkedHashMap<>();
        for (IotAlertControlLinkage linkage : linkages) {
            ControlDispatchGroupKey key = new ControlDispatchGroupKey(
                    linkage.getTargetDeviceId(),
                    priorityOrder(linkage),
                    delayOrder(linkage));
            groupedLinkages.computeIfAbsent(key, k -> new ArrayList<>()).add(linkage);
        }

        for (List<IotAlertControlLinkage> group : groupedLinkages.values()) {
            dispatchControlLinkageGroup(task, group);
        }
    }

    private void dispatchControlLinkageGroup(AlertTask task, List<IotAlertControlLinkage> group) {
        if (group == null || group.isEmpty()) {
            return;
        }

        List<ControlDispatchItem> items = new ArrayList<>();
        Map<String, Object> payloadMap = new LinkedHashMap<>();
        String res = buildControlRes(alertControlResPrefix, "A");

        for (IotAlertControlLinkage linkage : group) {
            ControlDispatchItem item = buildControlDispatchItem(task, linkage);
            if (item == null) {
                continue;
            }

            item.event.setPayload(buildControlPayload(payloadMap, item.targetIdentifier, linkage.getCommandValue(), res));
            if (isInCooldown(linkage)) {
                item.event.setStatus(3);
                item.event.setResponseDesc("联动规则处于冷却时间内，跳过下发");
                iotControlEventService.insertIotControlEvent(item.event);
                continue;
            }

            payloadMap.put(item.targetIdentifier, parseCommandValue(linkage.getCommandValue()));
            items.add(item);
        }

        if (items.isEmpty()) {
            return;
        }

        payloadMap.put("res", res);

        IotAlertControlLinkage firstLinkage = items.get(0).linkage;
        String topic = items.get(0).event.getTopic();
        String payload;
        try {
            payload = objectMapper.writeValueAsString(payloadMap);
            for (ControlDispatchItem item : items) {
                item.event.setPayload(payload);
                iotControlEventService.insertIotControlEvent(item.event);
            }

            if (firstLinkage.getDelaySeconds() != null && firstLinkage.getDelaySeconds() > 0) {
                Thread.sleep(firstLinkage.getDelaySeconds() * 1000L);
            }

            mqttGateway.sendToMqtt(topic, payload);
            Date sendTime = DateUtils.getNowDate();
            saveDownlinkDeviceLog(items.get(0).event.getDeviceSn(), 3, payload, sendTime);
            for (ControlDispatchItem item : items) {
                LAST_CONTROL_SEND_TIME.put(item.linkage.getId(), System.currentTimeMillis());
                item.event.setStatus(1);
                item.event.setSendTime(sendTime);
                item.event.setResponseDesc("已下发，等待设备响应");
                iotControlEventService.updateIotControlEvent(item.event);
            }
            System.out.println(">>> [ALERT-CONTROL] Topic: " + topic + " Payload: " + payload);
        } catch (Exception e) {
            for (ControlDispatchItem item : items) {
                item.event.setStatus(3);
                item.event.setResponseDesc("下发失败: " + e.getMessage());
                if (item.event.getEventId() == null) {
                    iotControlEventService.insertIotControlEvent(item.event);
                } else {
                    iotControlEventService.updateIotControlEvent(item.event);
                }
            }
        }
    }

    private void saveDownlinkDeviceLog(String deviceSn, Integer logType, String payload, Date createTime) {
        IotDeviceLog log = new IotDeviceLog();
        log.setDeviceSn(deviceSn);
        log.setLogType(logType);
        log.setPayload(payload);
        log.setCreateTime(createTime);
        iotDeviceLogMapper.insertIotDeviceLog(log);
    }

    private ControlDispatchItem buildControlDispatchItem(AlertTask task, IotAlertControlLinkage linkage) {
        IotControlEvent event = createControlEvent(task, linkage);

        if (linkage.getTargetDeviceId() == null) {
            saveFailedControlEvent(event, "联动规则未配置控制终端设备");
            return null;
        }

        IotDevice targetDevice = iotDeviceMapper.selectIotDeviceByDeviceId(linkage.getTargetDeviceId());
        if (targetDevice == null || StringUtils.isEmpty(targetDevice.getDeviceSn())) {
            saveFailedControlEvent(event, "控制终端设备不存在或SN为空");
            return null;
        }

        event.setDeviceSn(targetDevice.getDeviceSn());
        if (targetDevice.getProductType() != null && targetDevice.getProductType() != 1) {
            saveFailedControlEvent(event, "目标设备不是控制终端");
            return null;
        }

        String targetIdentifier = linkage.getTargetModelIdentifier();
        if (StringUtils.isEmpty(targetIdentifier) && linkage.getTargetModelId() != null) {
            IotThingModel targetModel = iotThingModelMapper.selectIotThingModelByModelId(linkage.getTargetModelId());
            targetIdentifier = targetModel != null ? targetModel.getIdentifier() : null;
        }
        if (StringUtils.isEmpty(targetIdentifier)) {
            saveFailedControlEvent(event, "联动规则未配置控制物模型字段");
            return null;
        }

        event.setTargetIdentifier(targetIdentifier);
        event.setTopic("/set/" + targetDevice.getDeviceSn());
        return new ControlDispatchItem(linkage, event, targetIdentifier);
    }

    private IotControlEvent createControlEvent(AlertTask task, IotAlertControlLinkage linkage) {
        IotControlEvent event = new IotControlEvent();
        event.setControlId(linkage.getId());
        event.setAlertRecordId(task.record.getId());
        event.setDeviceId(linkage.getTargetDeviceId());
        event.setTargetModelId(linkage.getTargetModelId());
        event.setTargetIdentifier(linkage.getTargetModelIdentifier());
        event.setStatus(0);
        event.setRemark(buildControlEventRemark(task, linkage));
        return event;
    }

    private void saveFailedControlEvent(IotControlEvent event, String responseDesc) {
        event.setPayload("{}");
        event.setStatus(3);
        event.setResponseDesc(responseDesc);
        iotControlEventService.insertIotControlEvent(event);
    }

    private boolean isInCooldown(IotAlertControlLinkage linkage) {
        if (linkage == null || linkage.getId() == null || linkage.getCooldownSeconds() == null || linkage.getCooldownSeconds() <= 0) {
            return false;
        }
        Long lastSendTime = LAST_CONTROL_SEND_TIME.get(linkage.getId());
        if (lastSendTime == null) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - lastSendTime;
        return elapsed < linkage.getCooldownSeconds() * 1000L;
    }

    private String buildControlPayload(String targetIdentifier, String commandValue) throws Exception {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(targetIdentifier, parseCommandValue(commandValue));
        payload.put("res", buildControlRes(alertControlResPrefix, "A"));
        return objectMapper.writeValueAsString(payload);
    }

    private String buildControlPayload(Map<String, Object> basePayload, String targetIdentifier, String commandValue, String res) {
        Map<String, Object> payload = new LinkedHashMap<>(basePayload);
        payload.put(targetIdentifier, parseCommandValue(commandValue));
        payload.put("res", res);
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String buildControlRes(String configuredPrefix, String defaultPrefix) {
        String prefix = StringUtils.isNotEmpty(configuredPrefix) ? configuredPrefix.trim() : defaultPrefix;
        if (prefix.length() > 5) {
            prefix = prefix.substring(0, 5);
        }
        int suffixLength = Math.max(1, 15 - prefix.length());
        String suffix = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        if (suffix.length() > suffixLength) {
            suffix = suffix.substring(suffix.length() - suffixLength);
        }
        return prefix + suffix;
    }

    private int priorityOrder(IotAlertControlLinkage linkage) {
        return linkage.getPriority() == null ? Integer.MAX_VALUE : linkage.getPriority();
    }

    private int delayOrder(IotAlertControlLinkage linkage) {
        return linkage.getDelaySeconds() == null ? 0 : linkage.getDelaySeconds();
    }

    private Object parseCommandValue(String commandValue) {
        if (commandValue == null) {
            return null;
        }
        String value = commandValue.trim();
        if (value.isEmpty()) {
            return "";
        }
        try {
            return objectMapper.readValue(value, Object.class);
        } catch (Exception e) {
            return commandValue;
        }
    }

    private String buildControlEventRemark(AlertTask task, IotAlertControlLinkage linkage) {
        StringBuilder remark = new StringBuilder();
        remark.append("预警规则ID: ").append(task.config.getId());
        if (task.model != null) {
            remark.append(", 触发字段: ").append(task.model.getIdentifier());
        }
        if (task.record != null && task.record.getActualValue() != null) {
            remark.append(", 实际值: ").append(task.record.getActualValue());
        }
        if (StringUtils.isNotEmpty(linkage.getActionName())) {
            remark.append(", 动作: ").append(linkage.getActionName());
        }
        return remark.toString();
    }

    /**
     * 触发异步更新，并安全地在事务提交后或当前直接执行
     */
    private void triggerAsyncAlertAdviceUpdate(String productName, List<AlertTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        // 使用 Spring 事务同步机制，在事务成功提交后才触发异步大模型调用，确保数据已落地
        if (org.springframework.transaction.support.TransactionSynchronizationManager.isSynchronizationActive()) {
            org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                new org.springframework.transaction.support.TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        runAsyncAdviceTasks(productName, tasks);
                    }
                }
            );
        } else {
            // 如果当前无事务，则直接以异步方式运行
            runAsyncAdviceTasks(productName, tasks);
        }
    }

    /**
     * 后台异步线程执行大模型防灾建议查询并更新数据库记录
     */
    private void runAsyncAdviceTasks(String productName, List<AlertTask> tasks) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            for (AlertTask task : tasks) {
                try {
                    Long recordId = task.record.getId();
                    if (recordId == null) {
                        continue;
                    }
                    // 调用大模型获取防灾建议
                    String advice = getModelAlertAdvice(productName, task.model, task.record.getActualValue(), task.config);
                    
                    // 如果获取到了非空的新建议，且与默认配置描述不同，则更新数据库
                    if (StringUtils.isNotEmpty(advice) && !advice.equals(task.config.getRuleDescription())) {
                        AgriAlertRecord updateRecord = new AgriAlertRecord();
                        updateRecord.setId(recordId);
                        updateRecord.setAlertAdvice(advice);
                        agriAlertRecordService.updateAgriAlertRecord(updateRecord);
                    }
                } catch (Exception e) {
                    System.err.println("异步更新预警建议失败，记录ID: " + task.record.getId() + ", 错误: " + e.getMessage());
                }
            }
        });
    }

    /**
     * 调用大模型接口获取详细的针对该条预警的防灾应急建议
     */
    private String getModelAlertAdvice(String productName, IotThingModel model, java.math.BigDecimal actualValue, AgriProductAlertConfig config) {
        if (StringUtils.isEmpty(dashscopeApiKey)) {
            return config.getRuleDescription();
        }

        try {
            String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + dashscopeApiKey);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "qwen3.6-plus");

            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一位精通物联网数据监控与枣树农业防灾减灾的AI专家。根据物联网设备上传的数据指标异常情况及预警规则，生成一份详细的、可操作的防治/应急处置建议。");
            messages.add(systemMessage);

            String userContent = String.format(
                "【监控上下文】\n" +
                "- 产品名称: %s\n" +
                "- 触发指标: %s (标识符: %s, 数据定义与单位: %s)\n" +
                "- 当前实际数值: %s\n\n" +
                "【触发的预警规则】\n" +
                "- 规则等级: %s\n" +
                "- 下限阈值: %s\n" +
                "- 上限阈值: %s\n" +
                "- 规则描述: %s\n\n" +
                "请针对这次异常读数，给出一份具体的、面向农户或技术管理人员的防灾应急及实操建议。字数在100字左右，排版简洁明了。不要使用Markdown特殊字符（如星号、井号等），使用纯文本。",
                productName, model.getModelName(), model.getIdentifier(),
                model.getSpecs() != null ? model.getSpecs() : "无说明",
                actualValue.toString(),
                config.getAlertLevel() != null ? config.getAlertLevel() : "未指定",
                config.getThresholdMin() != null ? config.getThresholdMin().toString() : "无",
                config.getThresholdMax() != null ? config.getThresholdMax().toString() : "无",
                config.getRuleDescription() != null ? config.getRuleDescription() : "无"
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
                            content = content.replaceAll("\\*", "").replaceAll("#", "").trim();
                            return content;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("调用大模型获取建议失败: " + e.getMessage());
        }

        return config.getRuleDescription();
    }
    /**
     * 辅助方法：校验单个字段的数据类型
     * （保持原有逻辑不变，直接复用你写好的代码）
     */
    private void checkDataType(String key, JsonNode node, String type) {
        if (node.isNull()) {
            throw new ServiceException("字段 [" + key + "] 的值不能为 null");
        }

        String nodeValueStr = node.asText();

        switch (type.toLowerCase()) {
            case "int":
            case "integer":
                if (node.isIntegralNumber()) return;
                if (node.isTextual()) {
                    try {
                        Long.parseLong(nodeValueStr);
                        return;
                    } catch (NumberFormatException e) { }
                }
                throw new ServiceException("字段 [" + key + "] 格式错误，定义为 int，实际值: " + node.toString());

            case "float":
            case "double":
            case "decimal":
                if (node.isNumber()) return;
                if (node.isTextual()) {
                    try {
                        Double.parseDouble(nodeValueStr);
                        return;
                    } catch (NumberFormatException e) { }
                }
                throw new ServiceException("字段 [" + key + "] 格式错误，定义为 float，实际值: " + node.toString());

            case "bool":
            case "boolean":
                if (node.isBoolean()) return;
                if (node.isTextual()) {
                    String val = nodeValueStr.toLowerCase();
                    if ("true".equals(val) || "false".equals(val)) return;
                }
                throw new ServiceException("字段 [" + key + "] 格式错误，定义为 bool，实际值: " + node.toString());

            case "string":
            case "text":
                if (node.isTextual()) return;
                if (node.isObject() || node.isArray()) {
                    throw new ServiceException("字段 [" + key + "] 格式错误，定义为 string，实际是 对象/数组");
                }
                break;
            default:
                break;
        }
    }

    // ========== 下方省略了 update、delete 等其他原始方法，原样保留即可 ==========
    /**
     * 修改设备通信日志
     *
     * @param iotDeviceLog 设备通信日志
     * @return 结果
     */
    @Override
    public int updateIotDeviceLog(IotDeviceLog iotDeviceLog)
    {
        return iotDeviceLogMapper.updateIotDeviceLog(iotDeviceLog);
    }

    /**
     * 批量删除设备通信日志
     *
     * @param logIds 需要删除的设备通信日志主键
     * @return 结果
     */
    @Override
    public int deleteIotDeviceLogByLogIds(Long[] logIds)
    {
        return iotDeviceLogMapper.deleteIotDeviceLogByLogIds(logIds);
    }

    /**
     * 删除设备通信日志信息
     *
     * @param logId 设备通信日志主键
     * @return 结果
     */
    @Override
    public int deleteIotDeviceLogByLogId(Long logId)
    {
        return iotDeviceLogMapper.deleteIotDeviceLogByLogId(logId);
    }

    private static class AlertTask {
        final AgriAlertRecord record;
        final IotThingModel model;
        final AgriProductAlertConfig config;

        AlertTask(AgriAlertRecord record, IotThingModel model, AgriProductAlertConfig config) {
            this.record = record;
            this.model = model;
            this.config = config;
        }
    }

    private static class ControlDispatchGroupKey {
        final Long targetDeviceId;
        final Integer priority;
        final Integer delaySeconds;

        ControlDispatchGroupKey(Long targetDeviceId, Integer priority, Integer delaySeconds) {
            this.targetDeviceId = targetDeviceId;
            this.priority = priority;
            this.delaySeconds = delaySeconds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ControlDispatchGroupKey)) {
                return false;
            }
            ControlDispatchGroupKey that = (ControlDispatchGroupKey) o;
            return Objects.equals(targetDeviceId, that.targetDeviceId)
                    && Objects.equals(priority, that.priority)
                    && Objects.equals(delaySeconds, that.delaySeconds);
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetDeviceId, priority, delaySeconds);
        }
    }

    private static class ControlDispatchItem {
        final IotAlertControlLinkage linkage;
        final IotControlEvent event;
        final String targetIdentifier;

        ControlDispatchItem(IotAlertControlLinkage linkage, IotControlEvent event, String targetIdentifier) {
            this.linkage = linkage;
            this.event = event;
            this.targetIdentifier = targetIdentifier;
        }
    }
}
