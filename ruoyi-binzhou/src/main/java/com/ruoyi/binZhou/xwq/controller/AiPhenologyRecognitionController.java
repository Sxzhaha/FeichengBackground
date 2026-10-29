package com.ruoyi.binZhou.xwq.controller;

import javax.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.ruoyi.binZhou.xwq.domain.PhenologyPredictRequest;
import com.ruoyi.binZhou.xwq.domain.PhenologyPredictResultVo;
import com.ruoyi.binZhou.xwq.service.IAiPhenologyRecognitionService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.binZhou.xwq.domain.PhenologyRecognitionListVo;
import com.ruoyi.binZhou.xwq.domain.PhenologyRecognitionQuery;
import com.ruoyi.binZhou.xwq.service.IAiPhenologyRecognitionQueryService;
import com.ruoyi.common.core.page.TableDataInfo;
/**
 * 物候期AI识别Controller。
 *
 * 原 AiPhenologyController 继续负责物候期识别结果的增删改查；
 * 本 Controller 只负责前台物候期识别工作台的识别请求。
 *
 * 完整接口地址：POST /xwq/phenology/predict
 *
 * @author ruoyi
 * @date 2026-06-28
 */
@RestController
@RequestMapping("/xwq/phenology")
public class AiPhenologyRecognitionController extends BaseController
{
    @Autowired
    private IAiPhenologyRecognitionService aiPhenologyRecognitionService;
    @Autowired
    private IAiPhenologyRecognitionQueryService
            aiPhenologyRecognitionQueryService;

    /**
     * 查询物候期识别工作台历史记录。
     *
     * 该接口只供物候期识别前台工作台使用，
     * 不影响原有 /xwq/phenology/list 后台管理接口。
     *
     * @param query 查询条件
     * @return 分页历史记录
     */
    @PreAuthorize("@ss.hasPermi('xwq:phenology:list')")
    @GetMapping("/recognition/list")
    public TableDataInfo recognitionList(
            PhenologyRecognitionQuery query)
    {
        startPage();

        List<PhenologyRecognitionListVo> list =
                aiPhenologyRecognitionQueryService
                        .selectRecognitionList(query);

        return getDataTable(list);
    }
    /**
     * 执行单张图片物候期识别。
     *
     * @param request 识别请求
     * @return 识别结果
     */
    @PreAuthorize("@ss.hasPermi('xwq:phenology:predict')")
    @Log(title = "物候期AI识别", businessType = BusinessType.OTHER)
    @PostMapping("/predict")
    public AjaxResult predict(@Valid @RequestBody PhenologyPredictRequest request)
    {
        PhenologyPredictResultVo result =
                aiPhenologyRecognitionService.predict(request);
        return success(result);
    }
}
