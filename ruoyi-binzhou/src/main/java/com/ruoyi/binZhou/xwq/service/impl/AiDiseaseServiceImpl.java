package com.ruoyi.binZhou.xwq.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.xwq.mapper.AiDiseaseMapper;
import com.ruoyi.binZhou.xwq.domain.AiDisease;
import com.ruoyi.binZhou.xwq.service.IAiDiseaseService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiModel;
import com.ruoyi.binZhou.xwq.domain.AiRecord;
import com.ruoyi.binZhou.xwq.mapper.AiImageMapper;
import com.ruoyi.binZhou.xwq.mapper.AiModelMapper;
import com.ruoyi.binZhou.sxz.service.client.DiseaseModelCallException;
import com.ruoyi.binZhou.sxz.service.client.DiseaseModelCallResult;
import com.ruoyi.binZhou.sxz.service.client.DiseaseModelClient;
import com.ruoyi.binZhou.sxz.service.support.DiseaseRecognitionPersistenceService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.iot.ld.domain.Park;
import com.ruoyi.iot.ld.mapper.ParkMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
/**
 * 病虫害识别结果Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class AiDiseaseServiceImpl implements IAiDiseaseService
{
    private static final Logger log = LoggerFactory.getLogger(AiDiseaseServiceImpl.class);

    @Autowired
    private AiDiseaseMapper aiDiseaseMapper;

    @Autowired
    private IAiDiseaseService aiDiseaseService;

    @Autowired
    private AiImageMapper aiImageMapper;

    @Autowired
    private AiModelMapper aiModelMapper;

    @Autowired
    private DiseaseModelClient diseaseModelClient;

    @Autowired
    private DiseaseRecognitionPersistenceService recognitionPersistenceService;

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询病虫害识别结果
     * 
     * @param diseaseId 病虫害识别结果主键
     * @return 病虫害识别结果
     */
    @Override
    public AiDisease selectAiDiseaseByDiseaseId(Long diseaseId)
    {
        return aiDiseaseMapper.selectAiDiseaseByDiseaseId(diseaseId);
    }

    /**
     * 查询病虫害识别结果列表
     * 
     * @param aiDisease 病虫害识别结果
     * @return 病虫害识别结果
     */
    @Override
    public List<AiDisease> selectAiDiseaseList(AiDisease aiDisease)
    {
        return aiDiseaseMapper.selectAiDiseaseList(aiDisease);
    }

    /**
     * 新增病虫害识别结果
     * 
     * @param aiDisease 病虫害识别结果
     * @return 结果
     */
    @Override
    public int insertAiDisease(AiDisease aiDisease)
    {
        return aiDiseaseMapper.insertAiDisease(aiDisease);
    }

    /**
     * 修改病虫害识别结果
     * 
     * @param aiDisease 病虫害识别结果
     * @return 结果
     */
    @Override
    public int updateAiDisease(AiDisease aiDisease)
    {
        return aiDiseaseMapper.updateAiDisease(aiDisease);
    }

    /**
     * 批量删除病虫害识别结果
     * 
     * @param diseaseIds 需要删除的病虫害识别结果主键
     * @return 结果
     */
    @Override
    public int deleteAiDiseaseByDiseaseIds(Long[] diseaseIds)
    {
        return aiDiseaseMapper.deleteAiDiseaseByDiseaseIds(diseaseIds);
    }

    /**
     * 删除病虫害识别结果信息
     * 
     * @param diseaseId 病虫害识别结果主键
     * @return 结果
     */
    @Override
    public int deleteAiDiseaseByDiseaseId(Long diseaseId)
    {
        return aiDiseaseMapper.deleteAiDiseaseByDiseaseId(diseaseId);
    }

    /**
     * 根据图片ID进行病虫害识别
     *
     * 说明：
     * 1. AiImage 只读取图片，不保存识别结果
     * 2. AiRecord 保存识别记录
     * 3. AiDisease 保存病虫害识别结果
     * 4. Java 读取图片文件并通过 OpenAI 格式调用病虫害模型
     */
    @Override
    public Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode)
    {
        return predictByImageInternal(imageId, modelId, cropType, recordMode, 0, false);
    }

    @Override
    public Map<String, Object> predictByImage(Long imageId, Long modelId, String cropType, String recordMode,
            Integer maxRetryCount)
    {
        return predictByImageInternal(imageId, modelId, cropType, recordMode, maxRetryCount, true);
    }

    private Map<String, Object> predictByImageInternal(Long imageId, Long modelId, String cropType,
            String recordMode, Integer maxRetryCount, boolean preserveModelCallException)
    {
        // 1. 读取图片信息
        AiImage image = aiImageMapper.selectAiImageByImageId(imageId);
        if (image == null)
        {
            throw new ServiceException("图片不存在，imageId=" + imageId);
        }

        if (image.getImageUrl() == null || image.getImageUrl().trim().isEmpty())
        {
            throw new ServiceException("图片地址为空，无法识别");
        }

        // 2. 读取模型信息
        AiModel model = aiModelMapper.selectAiModelByModelId(modelId);
        if (model == null)
        {
            throw new ServiceException("模型不存在，modelId=" + modelId);
        }

        if (model.getModelStatus() == null || model.getModelStatus() == 0)
        {
            throw new ServiceException("当前模型已停用，不能用于识别");
        }

        if (model.getModelUrl() == null || model.getModelUrl().trim().isEmpty())
        {
            throw new ServiceException("模型接口地址为空，请先在模型管理中配置 model_url");
        }
        if (model.getModelCode() == null || model.getModelCode().trim().isEmpty())
        {
            throw new ServiceException("模型编码为空，请先在模型管理中配置 model_code");
        }

        // 3. 创建识别记录 AiRecord
        Date recognitionTime = new Date();
        AiRecord record = new AiRecord();
        record.setRecordNo("DIS" + System.currentTimeMillis());
        record.setRecordType("病虫害");
        record.setRecordMode(recordMode);
        record.setModelId(model.getModelId());
        record.setModelName(model.getModelName());
        record.setImageId(image.getImageId());
        record.setCropType(cropType);
        record.setDeviceId(image.getDeviceId());
        record.setParkId(image.getParkId());
        record.setPlotId(image.getPlotId());
        // 调用模型前保存操作人，低置信度等失败记录也必须保留用户归属。
        record.setCreateBy(resolveRecordUsername(image, recordMode));
        record.setCreateTime(recognitionTime);
        recognitionPersistenceService.createProcessingRecord(record, image.getImageId());

        int modelAttemptCount = 0;
        try
        {
            // 4. Java 读取原始图片，并以 OpenAI JSON 和 Base64 图片发送给模型服务。
            DiseaseModelCallResult modelCallResult = diseaseModelClient.predictWithRetry(
                    image, model, record.getRecordNo(), maxRetryCount);
            modelAttemptCount = modelCallResult.getAttemptCount();
            String responseBody = modelCallResult.getResponseBody();

            if (responseBody == null || responseBody.trim().isEmpty())
            {
                throw new ServiceException("模型服务返回为空");
            }

            // 5. 解析模型返回
            JSONObject modelResult = parseModelResult(responseBody);

            String diseaseName = modelResult.getString("diseaseName");
            if (diseaseName == null || diseaseName.trim().isEmpty())
            {
                diseaseName = modelResult.getString("name");
            }
            if (diseaseName == null || diseaseName.trim().isEmpty())
            {
                diseaseName = "未知病虫害";
            }

            BigDecimal confidence = getConfidence(modelResult);

            String diseaseResult = modelResult.getString("diseaseResult");
            if (diseaseResult == null || diseaseResult.trim().isEmpty())
            {
                diseaseResult = "识别到病虫害：" + diseaseName;
            }

            String diseaseDesc = modelResult.getString("diseaseDesc");
            if (diseaseDesc == null || diseaseDesc.trim().isEmpty())
            {
                diseaseDesc = modelResult.getString("briefDesc");
            }
            if (diseaseDesc == null || diseaseDesc.trim().isEmpty())
            {
                diseaseDesc = modelResult.getString("brief");
            }
            if (diseaseDesc == null || diseaseDesc.trim().isEmpty())
            {
                diseaseDesc = "暂无症状描述";
            }

            String diseaseAdvice = modelResult.getString("diseaseAdvice");
            if (diseaseAdvice == null || diseaseAdvice.trim().isEmpty())
            {
                diseaseAdvice = modelResult.getString("prevention");
            }
            if (diseaseAdvice == null || diseaseAdvice.trim().isEmpty())
            {
                diseaseAdvice = "建议结合田间实际症状、发生部位和作物生育期进行综合判断，必要时采取绿色防控或药剂防治措施。";
            }

            String diseaseResultImgUrl = modelResult.getString("diseaseResultImgUrl");
            if (diseaseResultImgUrl == null || diseaseResultImgUrl.trim().isEmpty())
            {
                diseaseResultImgUrl = modelResult.getString("resultImageUrl");
            }
            if (diseaseResultImgUrl == null || diseaseResultImgUrl.trim().isEmpty())
            {
                diseaseResultImgUrl = image.getImageUrl();
            }

            // 6. 保存病虫害识别结果 AiDisease
            AiDisease disease = new AiDisease();
            disease.setRecordId(record.getRecordId());
            disease.setImageId(image.getImageId());
            disease.setDiseaseName(diseaseName);
            disease.setDiseaseCrop(cropType);
            disease.setDiseaseConfidence(confidence);
            disease.setDiseaseResult(diseaseResult);
            disease.setDiseaseDesc(diseaseDesc);
            disease.setDiseaseAdvice(diseaseAdvice);
            disease.setDiseaseResultImgUrl(diseaseResultImgUrl);
            disease.setIsWarning(0L);
            disease.setWarningLevel("无");
            disease.setCreateTime(recognitionTime);

            // 7. 使用短事务保存结果并更新识别记录、图片状态。
            recognitionPersistenceService.completeRecognition(disease, record, image.getImageId());

            // 8. 返回前端
            Map<String, Object> vo = new HashMap<>();
            vo.put("diseaseId", disease.getDiseaseId());
            vo.put("recordId", record.getRecordId());
            vo.put("imageId", image.getImageId());
            vo.put("imageName", image.getImageName());
            vo.put("imageUrl", image.getImageUrl());
            vo.put("diseaseName", diseaseName);
            vo.put("diseaseConfidence", confidence);
            vo.put("diseaseResult", diseaseResult);
            vo.put("diseaseDesc", diseaseDesc);
            vo.put("diseaseAdvice", diseaseAdvice);
            vo.put("diseaseResultImgUrl", diseaseResultImgUrl);
            vo.put("warningLevel", "无");
            vo.put("modelAttemptCount", modelAttemptCount);

            return vo;
        }
        catch (Exception e)
        {
            String msg = e.getMessage();
            try
            {
                recognitionPersistenceService.failRecognition(record, image.getImageId(), msg);
            }
            catch (Exception statusException)
            {
                log.error("更新病虫害识别失败状态异常，recordId={}，imageId={}",
                        record.getRecordId(), image.getImageId(), statusException);
            }

            if (preserveModelCallException)
            {
                if (e instanceof DiseaseModelCallException)
                {
                    throw (DiseaseModelCallException) e;
                }
                throw new DiseaseModelCallException(
                        "病虫害识别失败：" + e.getMessage(), Math.max(modelAttemptCount, 1), false, e);
            }
            throw new ServiceException("病虫害识别失败：" + e.getMessage());
        }
    }

    private String resolveRecordUsername(AiImage image, String recordMode)
    {
        // 抓拍图片已通过摄像头 -> 位置 -> 地块关联园区，定时分析归属园区用户。
        // 即使管理员手动执行定时任务，也不能用管理员覆盖园区用户。
        if (recordMode != null && (recordMode.contains("自动") || recordMode.contains("定时")))
        {
            Park park = image.getParkId() == null ? null : parkMapper.selectParkById(image.getParkId());
            SysUser user = park == null || park.getUserId() == null
                    ? null : sysUserMapper.selectUserById(park.getUserId());
            if (user != null && user.getUserName() != null && !user.getUserName().trim().isEmpty())
            {
                return user.getUserName();
            }
            log.warn("定时病虫害识别未找到园区用户，imageId={}，deviceId={}，parkId={}",
                    image.getImageId(), image.getDeviceId(), image.getParkId());
            return "system";
        }
        return getCurrentUsername();
    }

    private String getCurrentUsername()
    {
        try
        {
            String username = SecurityUtils.getUsername();
            return username == null || username.trim().isEmpty() ? "system" : username;
        }
        catch (Exception e)
        {
            // 非登录调用没有操作人时保留系统来源，定时任务已单独解析园区用户。
            return "system";
        }
    }

    /**
     * 解析模型返回结果
     *
     * 支持：
     * 1. [{...}]
     * 2. {"data": {...}}
     * 3. {"data": [{...}]}
     * 4. {...}
     */
    private JSONObject parseModelResult(String responseBody)
    {
        String text = responseBody.trim();

        if (text.startsWith("["))
        {
            JSONArray arr = JSONArray.parseArray(text);
            if (arr.isEmpty())
            {
                throw new ServiceException("模型未识别到病虫害目标");
            }
            return arr.getJSONObject(0);
        }

        JSONObject obj = JSONObject.parseObject(text);

        if (obj.containsKey("data"))
        {
            Object data = obj.get("data");

            if (data instanceof JSONArray)
            {
                JSONArray arr = obj.getJSONArray("data");
                if (arr.isEmpty())
                {
                    throw new ServiceException("模型未识别到病虫害目标");
                }
                return arr.getJSONObject(0);
            }

            if (data instanceof JSONObject)
            {
                return obj.getJSONObject("data");
            }
        }

        return obj;
    }

    /**
     * 获取置信度
     *
     * 数据库存 DECIMAL(6,5)，建议保存 0.95600。
     * 如果模型返回 95.6，则自动转为 0.95600。
     */
    private BigDecimal getConfidence(JSONObject obj)
    {
        BigDecimal confidence = obj.getBigDecimal("diseaseConfidence");

        if (confidence == null)
        {
            confidence = obj.getBigDecimal("confidence");
        }

        if (confidence == null)
        {
            confidence = BigDecimal.ZERO;
        }

        if (confidence.compareTo(BigDecimal.ONE) > 0)
        {
            confidence = confidence.divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP);
        }

        return confidence.setScale(5, RoundingMode.HALF_UP);
    }

}
