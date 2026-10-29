package com.ruoyi.binZhou.ylm.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.binZhou.ylm.domain.AgriTask;
import com.ruoyi.binZhou.ylm.service.IAgriTaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 农事任务Controller
 *
 * @author ruoyi
 * @date 2026-06-28
 */
@RestController
@RequestMapping("/ylm/task")
public class AgriTaskController extends BaseController
{
    @Autowired
    private IAgriTaskService agriTaskService;

    /**
     * 园区-地块-角落三级区域树
     */
    @GetMapping("/region/tree")
    public AjaxResult regionTree()
    {
        return success(agriTaskService.selectRegionTree());
    }

    /**
     * 生成下一个农事任务编号。
     */
    @GetMapping({"/generate/taskNo", "/generateTaskNo"})
    public AjaxResult generateTaskNo()
    {
        return AjaxResult.success("操作成功", agriTaskService.generateTaskNo());
    }

    /**
     * 管理员接口：全量农事列表
     */
    @PreAuthorize("@ss.hasPermi('ylm:task:list')")
    @GetMapping("/list")
    public Map<String,Object> list(AgriTask agriTask)
    {
        Map<String,Object> stats = agriTaskService.selectAgriTaskStats(agriTask);
        startPage();
        Map<String,Object> result = agriTaskService.selectAgriTaskList(agriTask);
        result.putAll(stats);
        return result;
    }

    // ========= 新增农户独立查询接口，无管理员权限校验 =========
    /**
     * 农户专属接口：仅查询本人农事数据
     */
    @GetMapping("/farmer/list")
    public Map<String,Object> farmerList(AgriTask agriTask)
    {
        Long loginOperatorId = SecurityUtils.getUserId();

        // 强制绑定当前用户
        // agriTask.setOperatorId(String.valueOf(loginOperatorId));

        // farmerTask页面展示全部农事记录，不按当前登录人过滤。

        Map<String,Object> stats = agriTaskService.selectAgriTaskStats(agriTask);
        startPage();
        Map<String,Object> result = agriTaskService.selectAgriTaskList(agriTask);
        markOwnRecords(result, loginOperatorId);
        result.putAll(stats);
        return result;
    }

    /**
     * farmerTask页面：查询当前登录人的农事记录
     */
    @GetMapping("/farmer/my/list")
    public Map<String,Object> farmerMyList(AgriTask agriTask)
    {
        Long loginOperatorId = SecurityUtils.getUserId();
        agriTask.setCreateById(String.valueOf(loginOperatorId));
        agriTask.setExactCreateById(true);
        Map<String,Object> stats = agriTaskService.selectAgriTaskStats(agriTask);
        startPage();
        Map<String,Object> result = agriTaskService.selectAgriTaskList(agriTask);
        markOwnRecords(result, loginOperatorId);
        result.putAll(stats);
        keepOwnRecords(result, loginOperatorId);
        return result;
    }

    /**
     * 农户专属接口：仅查询本人农事详情
     */
    @GetMapping("/farmer/{id:\\d+}")
    public AjaxResult farmerInfo(@PathVariable("id") Long id)
    {
        return success(agriTaskService.selectAgriTaskById(id));
    }

    /**
     * 农户新增本人农事记录
     */
    @Log(title = "农户新增农事记录", businessType = BusinessType.INSERT)
    @PostMapping("/farmer")
    public AjaxResult farmerAdd(@RequestBody AgriTask agriTask)
    {
        Long loginOperatorId = SecurityUtils.getUserId();
        AjaxResult requiredError = validateRequiredTaskFields(agriTask);
        if (requiredError != null) {
            return requiredError;
        }
        AjaxResult timeError = validateTaskTime(agriTask);
        if (timeError != null) {
            return timeError;
        }
        AjaxResult photoError = validateAndNormalizePhotoUrls(agriTask);
        if (photoError != null) {
            return photoError;
        }
        agriTask.setCreateById(String.valueOf(loginOperatorId));
        return toAjax(agriTaskService.insertAgriTask(agriTask));
    }

    /**
     * farmerTask页面：只允许删除当前登录人自己的农事记录
     */
    @Log(title = "农户删除本人农事记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/farmer/{id:\\d+}")
    public AjaxResult farmerRemove(@PathVariable("id") Long id)
    {
        Long loginOperatorId = SecurityUtils.getUserId();
        AgriTask task = agriTaskService.selectAgriTaskById(id);
        if (task == null || task.getCreateById() == null || !task.getCreateById().equals(String.valueOf(loginOperatorId))) {
            return AjaxResult.error("只能删除自己的农事记录");
        }
        return toAjax(agriTaskService.deleteAgriTaskByIds(new Long[] { id }));
    }

    /**
     * 农户导出农事记录
     */
    @Log(title = "农户导出农事记录", businessType = BusinessType.EXPORT)
    @RequestMapping(value = "/farmer/export", method = {RequestMethod.GET, RequestMethod.POST})
    public void farmerExport(HttpServletResponse response, AgriTask agriTask)
    {
        Map<String,Object> resultMap = agriTaskService.selectAgriTaskList(agriTask);
        List<AgriTask> list = (List<AgriTask>) resultMap.get("rows");
        exportAgriTaskExcel(response, list, "农事记录", "农事记录数据");
    }

    /**
     * 农户导出当前账号农事记录
     */
    @Log(title = "农户导出本人农事记录", businessType = BusinessType.EXPORT)
    @RequestMapping(value = "/farmer/my/export", method = {RequestMethod.GET, RequestMethod.POST})
    public void farmerMyExport(HttpServletResponse response, AgriTask agriTask)
    {
        Long loginOperatorId = SecurityUtils.getUserId();
        agriTask.setCreateById(String.valueOf(loginOperatorId));
        agriTask.setExactCreateById(true);
        Map<String,Object> resultMap = agriTaskService.selectAgriTaskList(agriTask);
        List<AgriTask> list = (List<AgriTask>) resultMap.get("rows");
        exportAgriTaskExcel(response, list, "农事记录", "农事记录数据");
    }

    /**
     * 下载农事记录导入模板
     */
    @RequestMapping(value = {"/farmer/importTemplate", "/farmer/import/template"}, method = {RequestMethod.GET, RequestMethod.POST})
    public void farmerImportTemplate(HttpServletResponse response)
    {
        agriTaskService.downloadFarmerImportTemplate(response);
    }

    /**
     * 批量导入农事记录。图片不从 Excel 导入，后续在页面编辑上传。
     */
    @Log(title = "农事记录批量导入", businessType = BusinessType.IMPORT)
    @PostMapping({"/farmer/importData", "/farmer/import/data"})
    public AjaxResult farmerImportData(MultipartFile file) throws Exception
    {
        return agriTaskService.importFarmerAgriTask(file, SecurityUtils.getUserId());
    }

    /**
     * 导出农事任务列表（仅管理员使用）
     */
    @PreAuthorize("@ss.hasPermi('ylm:task:export')")
    @Log(title = "农事任务", businessType = BusinessType.EXPORT)
    @RequestMapping(value = "/export", method = {RequestMethod.GET, RequestMethod.POST})
    public void export(HttpServletResponse response, AgriTask agriTask)
    {
        Map<String,Object> resultMap = agriTaskService.selectAgriTaskList(agriTask);
        List<AgriTask> list = (List<AgriTask>) resultMap.get("rows");
        exportAgriTaskExcel(response, list, "农事记录", "农事记录数据");
    }

    /**
     * 获取农事任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('ylm:task:query')")
    @GetMapping(value = "/{id:\\d+}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(agriTaskService.selectAgriTaskById(id));
    }

    /**
     * 新增农事任务
     */
    @PreAuthorize("@ss.hasPermi('ylm:task:add')")
    @Log(title = "农事任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AgriTask agriTask)
    {
        AjaxResult requiredError = validateRequiredTaskFields(agriTask);
        if (requiredError != null) {
            return requiredError;
        }
        AjaxResult timeError = validateTaskTime(agriTask);
        if (timeError != null) {
            return timeError;
        }
        AjaxResult photoError = validateAndNormalizePhotoUrls(agriTask);
        if (photoError != null) {
            return photoError;
        }
        // 自动填充创建人ID
        agriTask.setCreateById(String.valueOf(SecurityUtils.getUserId()));
        return toAjax(agriTaskService.insertAgriTask(agriTask));
    }

    /**
     * 修改农事任务（管理员完整编辑）
     */
    @PreAuthorize("@ss.hasPermi('ylm:task:edit')")
    @Log(title = "农事任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AgriTask agriTask)
    {
        AjaxResult timeError = validateTaskTime(agriTask);
        if (timeError != null) {
            return timeError;
        }
        AjaxResult photoError = validateAndNormalizePhotoUrls(agriTask);
        if (photoError != null) {
            return photoError;
        }
        return toAjax(agriTaskService.updateAgriTask(agriTask));
    }

    /**
     * 农户提交作业（仅修改照片、操作时间、备注）
     */
    @Log(title = "农户提交农事作业", businessType = BusinessType.UPDATE)
    @PutMapping("/submit")
    public AjaxResult submitTask(@RequestBody AgriTask agriTask)
    {
        AjaxResult timeError = validateTaskTime(agriTask);
        if (timeError != null) {
            return timeError;
        }
        AjaxResult photoError = validateAndNormalizePhotoUrls(agriTask);
        if (photoError != null) {
            return photoError;
        }
        return toAjax(agriTaskService.submitAgriTask(agriTask));
    }

    /**
     * 删除农事任务
     */
    @PreAuthorize("@ss.hasPermi('ylm:task:remove')")
    @Log(title = "农事任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids)
    {
        return toAjax(agriTaskService.deleteAgriTaskByIds(ids));
    }

    private void markOwnRecords(Map<String, Object> result, Long loginUserId)
    {
        Object rows = result.get("rows");
        if (!(rows instanceof List))
        {
            return;
        }
        String currentUserId = String.valueOf(loginUserId);
        for (Object row : (List<?>) rows)
        {
            if (row instanceof AgriTask)
            {
                AgriTask task = (AgriTask) row;
                task.setOwnRecord(currentUserId.equals(task.getCreateById()));
            }
        }
    }

    private void keepOwnRecords(Map<String, Object> result, Long loginUserId)
    {
        Object rows = result.get("rows");
        if (!(rows instanceof List))
        {
            return;
        }
        String currentUserId = String.valueOf(loginUserId);
        List<AgriTask> ownRows = new java.util.ArrayList<>();
        for (Object row : (List<?>) rows)
        {
            if (row instanceof AgriTask)
            {
                AgriTask task = (AgriTask) row;
                if (currentUserId.equals(task.getCreateById()))
                {
                    task.setOwnRecord(true);
                    ownRows.add(task);
                }
            }
        }
        if (ownRows.size() != ((List<?>) rows).size())
        {
            result.put("rows", ownRows);
            result.put("total", ownRows.size());
        }
    }

    private AjaxResult validateTaskTime(AgriTask agriTask)
    {
        if (agriTask.getStartTime() != null
                && agriTask.getEndTime() != null
                && !agriTask.getStartTime().before(agriTask.getEndTime())) {
            return AjaxResult.error("结束时间不能早于开始时间");
        }
        return null;
    }

    private AjaxResult validateRequiredTaskFields(AgriTask agriTask)
    {
        if (agriTask == null) {
            return AjaxResult.error("农事记录不能为空");
        }
        if (isBlank(agriTask.getTaskNo())) {
            return AjaxResult.error("任务编号不能为空");
        }
        if (agriTask.getWorkTypeId() == null) {
            return AjaxResult.error("农事类型不能为空");
        }
        if (agriTask.getWorkMethodId() == null) {
            return AjaxResult.error("作业方式不能为空");
        }
        if (agriTask.getLocationId() == null) {
            return AjaxResult.error("区域不能为空");
        }
        if (isBlank(agriTask.getOperatorId())) {
            return AjaxResult.error("操作人不能为空");
        }
        if (agriTask.getStartTime() == null) {
            return AjaxResult.error("开始时间不能为空");
        }
        if (agriTask.getEndTime() == null) {
            return AjaxResult.error("结束时间不能为空");
        }
        return null;
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }

    private AjaxResult validateAndNormalizePhotoUrls(AgriTask agriTask)
    {
        if (agriTask == null || isBlank(agriTask.getPhotoUrls()))
        {
            return null;
        }
        List<String> photoUrls = new ArrayList<>();
        for (String item : agriTask.getPhotoUrls().split(","))
        {
            String photoUrl = normalizePhotoUrl(item);
            if (photoUrl == null)
            {
                return AjaxResult.error("农事图片必须先上传到服务器，不能保存本地图片路径");
            }
            if (!photoUrl.isEmpty())
            {
                photoUrls.add(photoUrl);
            }
        }
        agriTask.setPhotoUrls(String.join(",", photoUrls));
        return null;
    }

    private String normalizePhotoUrl(String value)
    {
        if (isBlank(value))
        {
            return "";
        }
        String original = value.trim();
        String lower = original.toLowerCase();
        if (lower.startsWith("blob:") || lower.startsWith("file:")
                || lower.contains("fakepath")
                || original.matches("^[a-zA-Z]:[\\\\/].*")
                || original.startsWith("\\\\"))
        {
            return null;
        }

        String path = original.replace('\\', '/');
        if (lower.startsWith("http://") || lower.startsWith("https://"))
        {
            try
            {
                URL url = new URL(path);
                String urlPath = url.getPath();
                if (!isBlank(url.getQuery()))
                {
                    urlPath += "?" + url.getQuery();
                }
                String profilePath = urlPath.replaceFirst("^/(dev-api|prod-api|stage-api)(/profile/)", "$2");
                if (profilePath.startsWith("/profile/"))
                {
                    return profilePath;
                }
                return original;
            }
            catch (Exception e)
            {
                return original;
            }
        }
        path = path.replaceFirst("^/(dev-api|prod-api|stage-api)(/profile/)", "$2");
        if (path.startsWith("profile/"))
        {
            path = "/" + path;
        }
        return path;
    }

    private void fillExportFields(List<AgriTask> list)
    {
        if (list == null)
        {
            return;
        }
        for (AgriTask task : list)
        {
            task.setExportCreateTime(task.getCreateTime());
            task.setExportRemark(task.getRemark());
        }
    }

    private void exportAgriTaskExcel(HttpServletResponse response, List<AgriTask> list, String filePrefix, String sheetName)
    {
        fillExportFields(list);
        setExportResponseHeader(response, filePrefix + "_" + DateUtils.dateTimeNow() + ".xlsx");
        ExcelUtil<AgriTask> util = new ExcelUtil<AgriTask>(AgriTask.class);
        util.exportExcel(response, list, sheetName);
    }

    private void setExportResponseHeader(HttpServletResponse response, String fileName)
    {
        try
        {
            FileUtils.setAttachmentResponseHeader(response, fileName);
        }
        catch (Exception e)
        {
            throw new RuntimeException("设置导出文件名失败", e);
        }
    }
}
