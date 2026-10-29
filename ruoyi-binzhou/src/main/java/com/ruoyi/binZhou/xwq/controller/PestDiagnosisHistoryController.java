package com.ruoyi.binZhou.xwq.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.binZhou.ld.domain.Knowledge;
import com.ruoyi.binZhou.xwq.domain.PestDiagnosisHistory;
import com.ruoyi.binZhou.ld.service.IKnowledgeService;
import com.ruoyi.binZhou.xwq.service.IPestDiagnosisHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 农业病虫害历史诊断记录 Controller
 */
@RestController
@RequestMapping("/binZhou/xwq/history")
public class PestDiagnosisHistoryController extends BaseController {

    @Autowired
    private IPestDiagnosisHistoryService service;
    @Autowired
    private IKnowledgeService knowledgeService;

    /** 查询列表（分页） */
    @GetMapping("/list")
    public TableDataInfo list(PestDiagnosisHistory query) {
        startPage();
        return getDataTable(service.selectList(query));
    }

    /** 获取统计数据 */
    @GetMapping("/statistics")
    public AjaxResult statistics(PestDiagnosisHistory query) {
        return AjaxResult.success(service.getStatistics(query));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return AjaxResult.success(service.selectById(id));
    }

    /** 新增（单条） */
    @Log(title = "历史诊断记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PestDiagnosisHistory history) {
        return toAjax(service.insert(history));
    }

    /** 批量新增 */
    @Log(title = "历史诊断记录-批量", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchAdd(@RequestBody List<PestDiagnosisHistory> list) {
        return toAjax(service.batchInsert(list));
    }

    /** 更新 */
    @Log(title = "历史诊断记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PestDiagnosisHistory history) {
        return toAjax(service.update(history));
    }

    /** 删除（逻辑删除，支持批量） */
    @Log(title = "历史诊断记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(service.deleteByIds(ids));
    }

    /** 导出 Excel */
    @Log(title = "历史诊断记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PestDiagnosisHistory query) {
        List<PestDiagnosisHistory> list = service.selectList(query);
        ExcelUtil<PestDiagnosisHistory> util = new ExcelUtil<>(PestDiagnosisHistory.class);
        util.exportExcel(response, list, "病虫害历史诊断记录");
    }

    // 注入 RestTemplate
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 接收前端图像 -> 转发给 Python 模型 -> 返回诊断结果
     */
    @PostMapping("/predict")
    public AjaxResult predictImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 调用真实 Python 大模型接口
            String pythonApiUrl = "http://127.0.0.1:5002/predict_pest";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 注意：Python 端返回的是一个 JSON 数组格式，例如 [{"name":"...", "knowledgeId":..., "confidence":...}]
            // 所以这里用 String 接收，然后手动转为 JSONArray
            ResponseEntity<String> response = restTemplate.postForEntity(pythonApiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 2. 解析 Python 返回的数据
                JSONArray pythonDataArray = JSONArray.parseArray(response.getBody());

                if (pythonDataArray.isEmpty()) {
                    return AjaxResult.error("模型未能识别出任何目标");
                }

                // 获取置信度最高（Top 1）的识别结果
                JSONObject topResult = pythonDataArray.getJSONObject(0);
                String diseaseName = topResult.getString("name");
                String knowledgeIdStr = topResult.getString("knowledgeId");

                // 将置信度转换为百分比格式 (Python 传回的 0.9567 -> 95.67 -> 前端保留一位 95.7)
                double conf = topResult.getDoubleValue("confidence") * 100;
                String formattedConf = String.format("%.1f", conf);

                // 3. 根据 knowledgeId 查询数据库补全信息
                String brief = "未找到病害详细描述";
                String prevention = "暂无防治建议";

                if (knowledgeIdStr != null && !knowledgeIdStr.equals("None")) {
                    try {
                        Long knowledgeId = Long.parseLong(knowledgeIdStr);
                        // 调用 Service 查数据库
                        Knowledge knowledge = knowledgeService.selectKnowledgeById(knowledgeId);

                        if (knowledge != null) {
                            if (knowledge.getPlusDsiIntroduction() != null) {
                                brief = knowledge.getPlusDsiIntroduction();
                            }
                            if (knowledge.getPlusDsiCmethod() != null) {
                                prevention = knowledge.getPlusDsiCmethod();
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("知识图谱ID格式转换异常: " + knowledgeIdStr);
                    }
                }

                // 4. 组装前端需要的统一 JSON 格式并返回
                JSONObject finalData = new JSONObject();
                finalData.put("diseaseName", diseaseName);
                finalData.put("confidence", formattedConf);
                finalData.put("brief", brief);
                finalData.put("prevention", prevention);

                return AjaxResult.success("识别成功", finalData);
            } else {
                return AjaxResult.error("模型推理服务异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("上传识别失败: " + e.getMessage());
        }
    }
}
