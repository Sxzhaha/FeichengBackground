package com.ruoyi.binZhou.ylm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.ruoyi.binZhou.ylm.domain.AgriTask;
import com.ruoyi.binZhou.ylm.mapper.AgriTaskMapper;
import com.ruoyi.binZhou.ylm.service.IAgriTaskService;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 农事任务Service业务层处理
 *
 * @author ruoyi
 * @date 2026-06-28
 */
@Service
public class AgriTaskServiceImpl extends ServiceImpl<AgriTaskMapper, AgriTask> implements IAgriTaskService
{
    private static final String IMPORT_SHEET_NAME = "农事记录";
    private static final String OPTION_SHEET_NAME = "选项";
    private static final String INSTRUCTION_SHEET_NAME = "填写说明";
    private static final int TEMPLATE_DATA_ROWS = 1000;
    private static final int MAX_IMPORT_ROWS = 1000;
    private static final int TASK_NO_MAX_LENGTH = 64;
    private static final int DESCRIPTION_MAX_LENGTH = 1000;
    private static final int OPERATOR_ID_MAX_LENGTH = 64;
    private static final int REMARK_MAX_LENGTH = 500;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String TASK_NO_PREFIX = "RW";
    private static final int TASK_NO_SERIAL_LENGTH = 4;
    private static final int TASK_NO_MAX_DAILY_SERIAL = 9999;
    private static final String TASK_NO_EXAMPLE = "RW202607200001";
    private static final String TASK_NO_IMPORT_REGEX = "^" + TASK_NO_PREFIX + "\\d{8}\\d{" + TASK_NO_SERIAL_LENGTH + "}$";
    private static final String TASK_NO_RULE_TEXT = "可手动填写，也可留空由系统自动生成。手动填写时必须符合“RW + 8位日期 + 4位流水号”格式，例如 "
            + TASK_NO_EXAMPLE + "；任务编号在导入文件内和数据库中均不得重复。";
    private static final String TASK_NO_EXISTS_MESSAGE = "任务编号已存在，请修改后重试";
    private static final String[] IMPORT_HEADERS = {
            "任务编号",
            "农事类型*",
            "作业方式*",
            "区域*",
            "任务描述",
            "开始时间*",
            "结束时间*",
            "备注",
            "操作人*"
    };

    private static final int COL_TASK_NO = 0;
    private static final int COL_WORK_TYPE = 1;
    private static final int COL_WORK_METHOD = 2;
    private static final int COL_REGION = 3;
    private static final int COL_DESCRIPTION = 4;
    private static final int COL_START_TIME = 5;
    private static final int COL_END_TIME = 6;
    private static final int COL_REMARK = 7;
    private static final int COL_OPERATOR = 8;

    @Override
    public AgriTask selectAgriTaskById(Long id)
    {
        return baseMapper.selectAgriTaskById(id);
    }

    @Override
    public AgriTask selectFarmerAgriTaskById(Long id, Long operatorId)
    {
        return baseMapper.selectFarmerAgriTaskById(id, operatorId);
    }

    /**
     * 管理员接口：直接全量查询，不再内存过滤农户数据
     */
    @Override
    public Map<String, Object> selectAgriTaskList(AgriTask agriTask)
    {
        List<AgriTask> list = baseMapper.selectAgriTaskList(agriTask);

        // 内存统计本月、近7天数量
        Map<String, Long> stat = countRecentTasks(list);
        PageInfo<AgriTask> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", list);
        result.put("total", pageInfo.getTotal());
        result.put("monthCount", stat.get("monthCount"));
        result.put("weekCount", stat.get("weekCount"));
        return result;
    }

    @Override
    public Map<String, Object> selectAgriTaskStats(AgriTask agriTask)
    {
        List<AgriTask> list = baseMapper.selectAgriTaskList(agriTask);
        Map<String, Long> stat = countRecentTasks(list);

        Map<String, Object> result = new HashMap<>();
        result.put("total", list.size());
        result.put("monthCount", stat.get("monthCount"));
        result.put("weekCount", stat.get("weekCount"));
        return result;
    }

    /**
     * 农户独立接口：调用mapper专用sql强制过滤operatorId
     */
    @Override
    public Map<String, Object> selectFarmerAgriTaskList(AgriTask agriTask, Long operatorId)
    {
        List<AgriTask> list = baseMapper.selectFarmerAgriTaskList(agriTask, operatorId);

        // 农户列表统计
        Map<String, Long> stat = countRecentTasks(list);
        PageInfo<AgriTask> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", list);
        result.put("total", pageInfo.getTotal());
        result.put("monthCount", stat.get("monthCount"));
        result.put("weekCount", stat.get("weekCount"));
        return result;
    }

    @Override
    public Map<String, Object> selectFarmerAgriTaskStats(AgriTask agriTask, Long operatorId)
    {
        List<AgriTask> list = baseMapper.selectFarmerAgriTaskList(agriTask, operatorId);
        Map<String, Long> stat = countRecentTasks(list);

        Map<String, Object> result = new HashMap<>();
        result.put("total", list.size());
        result.put("monthCount", stat.get("monthCount"));
        result.put("weekCount", stat.get("weekCount"));
        return result;
    }

    @Override
    public List<Map<String, Object>> selectRegionTree()
    {
        return baseMapper.selectRegionTree();
    }

    @Override
    public synchronized String generateTaskNo()
    {
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String prefix = TASK_NO_PREFIX + datePart;
        int nextSerial = 1;
        List<String> taskNos = baseMapper.selectTaskNosByPrefix(prefix);
        if (taskNos != null)
        {
            for (String taskNo : taskNos)
            {
                int serial = parseTaskNoSerial(taskNo, prefix);
                if (serial >= nextSerial)
                {
                    nextSerial = serial + 1;
                }
            }
        }
        if (nextSerial > TASK_NO_MAX_DAILY_SERIAL)
        {
            throw new ServiceException("当天任务编号流水号已用尽，无法继续生成任务编号");
        }
        return prefix + String.format("%0" + TASK_NO_SERIAL_LENGTH + "d", nextSerial);
    }

    @Override
    public boolean checkTaskNoUnique(String taskNo, Long excludeId)
    {
        if (isBlank(taskNo))
        {
            return false;
        }
        Long count = baseMapper.selectTaskCountByTaskNo(taskNo.trim(), excludeId);
        return count == null || count == 0;
    }

    @Override
    public int insertAgriTask(AgriTask agriTask)
    {
        normalizeTaskNo(agriTask);
        assertTaskNoUnique(agriTask == null ? null : agriTask.getTaskNo(), null);
        fillAreaIdByLocationId(agriTask);
        try
        {
            return baseMapper.insertAgriTask(agriTask);
        }
        catch (DataIntegrityViolationException e)
        {
            handleTaskNoConflict(e);
            throw e;
        }
    }

    @Override
    public int updateAgriTask(AgriTask agriTask)
    {
        normalizeTaskNo(agriTask);
        if (agriTask != null && agriTask.getTaskNo() != null)
        {
            assertTaskNoUnique(agriTask.getTaskNo(), agriTask.getId());
        }
        fillAreaIdByLocationId(agriTask);
        try
        {
            return baseMapper.updateAgriTask(agriTask);
        }
        catch (DataIntegrityViolationException e)
        {
            handleTaskNoConflict(e);
            throw e;
        }
    }

    @Override
    public void downloadFarmerImportTemplate(HttpServletResponse response)
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            List<DictOption> workTypeOptions = buildDictOptions("ylm_agri_work_type");
            List<DictOption> workMethodOptions = buildDictOptions("ylm_agri_work_method");
            List<DictOption> operatorOptions = buildDictOptions("ylm_agri_operator");
            RegionLookup regionLookup = buildRegionLookup();

            Sheet importSheet = createImportTemplateSheet(workbook);
            createOptionSheet(workbook, workTypeOptions, workMethodOptions, regionLookup.options, operatorOptions);
            addTemplateValidations(importSheet, workbook);
            createInstructionSheet(workbook, workTypeOptions, workMethodOptions, regionLookup.options, operatorOptions);

            response.setContentType(XLSX_CONTENT_TYPE);
            response.setCharacterEncoding("utf-8");
            FileUtils.setAttachmentResponseHeader(response, "农事记录导入模板_" + DateUtils.dateTimeNow() + ".xlsx");
            workbook.write(response.getOutputStream());
        }
        catch (Exception e)
        {
            throw new ServiceException("下载农事记录导入模板失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized AjaxResult importFarmerAgriTask(MultipartFile file, Long loginUserId)
    {
        List<String> errors = new ArrayList<>();
        if (file == null || file.isEmpty())
        {
            errors.add("请选择需要导入的 Excel 文件");
            return buildImportError(errors);
        }
        if (loginUserId == null)
        {
            errors.add("无法获取当前登录用户，请重新登录后再导入");
            return buildImportError(errors);
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null || !Arrays.asList("xls", "xlsx").contains(extension.toLowerCase()))
        {
            errors.add("文件格式错误：仅支持 .xls 或 .xlsx 文件");
            return buildImportError(errors);
        }

        List<PendingImportTask> tasks = new ArrayList<>();
        int autoGeneratedCount = 0;
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream))
        {
            Sheet sheet = workbook.getSheet(IMPORT_SHEET_NAME);
            if (sheet == null)
            {
                sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            }
            if (sheet == null)
            {
                errors.add("Excel 文件中未找到可导入的工作表");
                return buildImportError(errors);
            }

            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            validateImportHeader(sheet, formatter, evaluator, errors);
            if (!errors.isEmpty())
            {
                return buildImportError(errors);
            }

            Map<String, String> workTypeMap = buildDictValueMap("ylm_agri_work_type");
            Map<String, String> workMethodMap = buildDictValueMap("ylm_agri_work_method");
            Map<String, String> operatorMap = buildDictValueMap("ylm_agri_operator");
            RegionLookup regionLookup = buildRegionLookup();

            Set<String> fileTaskNos = new HashSet<>();
            int dataRows = 0;
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++)
            {
                Row row = sheet.getRow(rowIndex);
                if (isBlankImportRow(row, formatter, evaluator))
                {
                    continue;
                }
                dataRows++;
                if (dataRows > MAX_IMPORT_ROWS)
                {
                    errors.add("导入数据不能超过 " + MAX_IMPORT_ROWS + " 行");
                    break;
                }
                AgriTask task = parseImportRow(row, rowIndex + 1, loginUserId, formatter, evaluator,
                        workTypeMap, workMethodMap, operatorMap, regionLookup, fileTaskNos, errors);
                if (task != null)
                {
                    tasks.add(new PendingImportTask(rowIndex + 1, task));
                }
            }

            if (dataRows == 0)
            {
                errors.add("Excel 中没有可导入的数据行");
            }

            for (PendingImportTask pendingTask : tasks)
            {
                if (isBlank(pendingTask.task.getTaskNo()))
                {
                    continue;
                }
                Long exists = baseMapper.selectTaskCountByTaskNo(pendingTask.task.getTaskNo(), null);
                if (exists != null && exists > 0)
                {
                    errors.add("第" + pendingTask.rowNum + "行【任务编号】：“" + pendingTask.task.getTaskNo() + "”已存在");
                }
            }

            if (!errors.isEmpty())
            {
                return buildImportError(errors);
            }

            autoGeneratedCount = assignGeneratedTaskNos(tasks, errors);
            if (!errors.isEmpty())
            {
                return buildImportError(errors);
            }
        }
        catch (Exception e)
        {
            errors.add("Excel 文件无法解析，请确认文件未损坏且格式正确：" + e.getMessage());
            return buildImportError(errors);
        }

        for (PendingImportTask pendingTask : tasks)
        {
            try
            {
                insertAgriTask(pendingTask.task);
            }
            catch (ServiceException e)
            {
                if (pendingTask.autoGeneratedTaskNo && TASK_NO_EXISTS_MESSAGE.equals(e.getMessage()))
                {
                    throw new ServiceException("任务编号生成冲突，请重新导入");
                }
                throw e;
            }
        }

        AjaxResult result = AjaxResult.success("成功导入 " + tasks.size() + " 条农事记录"
                + (autoGeneratedCount > 0 ? "，其中 " + autoGeneratedCount + " 条任务编号由系统自动生成" : ""));
        result.put("count", tasks.size());
        result.put("autoGeneratedCount", autoGeneratedCount);
        return result;
    }

    @Override
    public int submitAgriTask(AgriTask agriTask)
    {
        normalizeTaskNo(agriTask);
        if (agriTask != null && agriTask.getTaskNo() != null)
        {
            assertTaskNoUnique(agriTask.getTaskNo(), agriTask.getId());
        }
        fillAreaIdByLocationId(agriTask);
        Long loginUserId = SecurityUtils.getUserId();
        try
        {
            return baseMapper.submitAgriTask(agriTask, String.valueOf(loginUserId));
        }
        catch (DataIntegrityViolationException e)
        {
            handleTaskNoConflict(e);
            throw e;
        }
    }

    @Override
    public int deleteAgriTaskByIds(Long[] ids)
    {
        return baseMapper.deleteAgriTaskByIds(ids);
    }

    private Map<String, Long> countRecentTasks(List<AgriTask> list)
    {
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate weekStart = now.minusDays(7);
        long monthCount = 0;
        long weekCount = 0;
        for (AgriTask task : list) {
            Date statTime = task.getStartTime();
            if (statTime == null) {
                continue;
            }
            LocalDate operateDate = statTime.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (!operateDate.isBefore(monthStart)) monthCount++;
            if (!operateDate.isBefore(weekStart)) weekCount++;
        }

        Map<String, Long> result = new HashMap<>();
        result.put("monthCount", monthCount);
        result.put("weekCount", weekCount);
        return result;
    }

    private Sheet createImportTemplateSheet(Workbook workbook)
    {
        Sheet sheet = workbook.createSheet(IMPORT_SHEET_NAME);
        sheet.createFreezePane(0, 1);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(24);
        for (int i = 0; i < IMPORT_HEADERS.length; i++)
        {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(IMPORT_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }

        int[] widths = {18, 18, 18, 32, 40, 24, 24, 28, 30};
        for (int i = 0; i < widths.length; i++)
        {
            sheet.setColumnWidth(i, widths[i] * 256);
        }

        CreationHelper creationHelper = workbook.getCreationHelper();
        DataFormat dataFormat = creationHelper.createDataFormat();
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(dataFormat.getFormat("@"));
        CellStyle dateTimeStyle = workbook.createCellStyle();
        dateTimeStyle.setDataFormat(dataFormat.getFormat(DATE_TIME_PATTERN));
        sheet.setDefaultColumnStyle(COL_TASK_NO, textStyle);
        sheet.setDefaultColumnStyle(COL_START_TIME, dateTimeStyle);
        sheet.setDefaultColumnStyle(COL_END_TIME, dateTimeStyle);
        return sheet;
    }

    private void createOptionSheet(Workbook workbook, List<DictOption> workTypeOptions,
                                   List<DictOption> workMethodOptions, List<RegionInfo> regionOptions,
                                   List<DictOption> operatorOptions)
    {
        Sheet sheet = workbook.createSheet(OPTION_SHEET_NAME);
        writeOptionColumn(sheet, 0, "农事类型", dictLabels(workTypeOptions));
        writeOptionColumn(sheet, 1, "作业方式", dictLabels(workMethodOptions));
        writeOptionColumn(sheet, 2, "区域", regionNames(regionOptions));
        writeOptionColumn(sheet, 3, "操作人", dictLabels(operatorOptions));

        createName(workbook, "workTypeOptions", 0, workTypeOptions.size());
        createName(workbook, "workMethodOptions", 1, workMethodOptions.size());
        createName(workbook, "regionOptions", 2, regionOptions.size());
        createName(workbook, "operatorOptions", 3, operatorOptions.size());
        workbook.setSheetHidden(workbook.getSheetIndex(sheet), true);
    }

    private void addTemplateValidations(Sheet sheet, Workbook workbook)
    {
        addTaskNoValidation(sheet);
        addListValidation(sheet, "workTypeOptions", COL_WORK_TYPE);
        addListValidation(sheet, "workMethodOptions", COL_WORK_METHOD);
        addListValidation(sheet, "regionOptions", COL_REGION);
    }

    private void createInstructionSheet(Workbook workbook, List<DictOption> workTypeOptions,
                                        List<DictOption> workMethodOptions, List<RegionInfo> regionOptions,
                                        List<DictOption> operatorOptions)
    {
        Sheet sheet = workbook.createSheet(INSTRUCTION_SHEET_NAME);
        String[][] rows = {
                {"字段", "是否必填", "填写说明", "示例"},
                {"任务编号", "可选", TASK_NO_RULE_TEXT, TASK_NO_EXAMPLE},
                {"任务编号补充说明", "说明", "留空时由系统在导入时自动生成；自动生成规则为 RWyyyyMMddNNNN；手动填写的编号必须符合格式要求，且不能与文件内或数据库已有记录重复。", ""},
                {"农事类型", "必填", "从系统字典 ylm_agri_work_type 的可选项中填写标签或值", firstLabel(workTypeOptions)},
                {"作业方式", "必填", "从系统字典 ylm_agri_work_method 的可选项中填写标签或值", firstLabel(workMethodOptions)},
                {"区域", "必填", "填写完整区域名称，系统会解析为 area_id 和 location_id", firstRegionName(regionOptions)},
                {"任务描述", "可空", "长度不超过" + DESCRIPTION_MAX_LENGTH + "个字符", "李四去滨州园区南地块东角落人工除草。"},
                {"开始时间", "必填", "格式为 " + DATE_TIME_PATTERN + "，也兼容 Excel 日期单元格", "2026-07-13 08:30:00"},
                {"结束时间", "必填", "格式同开始时间，必须晚于开始时间", "2026-07-13 18:00:00"},
                {"备注", "可空", "长度不超过" + REMARK_MAX_LENGTH + "个字符", "暂无"},
                {"操作人", "必填", "可多选，多个操作人请使用英文逗号分隔；可兼容中文逗号、顿号和分号并统一存储为英文逗号", firstOperatorExample(operatorOptions)},
                {"系统字段", "无需填写", "图片、创建人、创建时间、ID、区域ID均由系统自动处理。农事图片不从 Excel 导入，导入成功后可在记录编辑页面上传图片。", ""}
        };

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++)
        {
            Row row = sheet.createRow(rowIndex);
            for (int colIndex = 0; colIndex < rows[rowIndex].length; colIndex++)
            {
                Cell cell = row.createCell(colIndex);
                cell.setCellValue(rows[rowIndex][colIndex]);
                if (rowIndex == 0)
                {
                    cell.setCellStyle(headerStyle);
                }
            }
        }
        sheet.setColumnWidth(0, 18 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 72 * 256);
        sheet.setColumnWidth(3, 34 * 256);
    }

    private void writeOptionColumn(Sheet sheet, int columnIndex, String header, List<String> values)
    {
        Row headerRow = getOrCreateRow(sheet, 0);
        headerRow.createCell(columnIndex).setCellValue(header);
        List<String> safeValues = values.isEmpty() ? Collections.singletonList("暂无可选项") : values;
        for (int i = 0; i < safeValues.size(); i++)
        {
            Row row = getOrCreateRow(sheet, i + 1);
            row.createCell(columnIndex).setCellValue(safeValues.get(i));
        }
        sheet.setColumnWidth(columnIndex, 30 * 256);
    }

    private Row getOrCreateRow(Sheet sheet, int rowIndex)
    {
        Row row = sheet.getRow(rowIndex);
        return row == null ? sheet.createRow(rowIndex) : row;
    }

    private void createName(Workbook workbook, String nameText, int columnIndex, int optionSize)
    {
        int size = Math.max(optionSize, 1);
        char column = (char) ('A' + columnIndex);
        Name name = workbook.createName();
        name.setNameName(nameText);
        name.setRefersToFormula("'" + OPTION_SHEET_NAME + "'!$" + column + "$2:$" + column + "$" + (size + 1));
    }

    private void addListValidation(Sheet sheet, String rangeName, int columnIndex)
    {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createFormulaListConstraint(rangeName);
        CellRangeAddressList addressList = new CellRangeAddressList(1, TEMPLATE_DATA_ROWS, columnIndex, columnIndex);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("填写错误", "请从模板提供的下拉选项中选择有效值");
        sheet.addValidationData(validation);
    }

    private void addTaskNoValidation(Sheet sheet)
    {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createCustomConstraint(
                "OR(LEN(TRIM(A2))=0,AND(LEFT(TRIM(A2),2)=\"RW\",LEN(TRIM(A2))=14,IFERROR(ISNUMBER(VALUE(MID(TRIM(A2),3,12))),FALSE),IFERROR(VALUE(RIGHT(TRIM(A2),4))>0,FALSE)))");
        CellRangeAddressList addressList = new CellRangeAddressList(1, TEMPLATE_DATA_ROWS, COL_TASK_NO, COL_TASK_NO);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setShowPromptBox(true);
        validation.createPromptBox("任务编号填写规则",
                "可留空由系统自动生成；手动填写格式：RWyyyyMMddNNNN，例如 " + TASK_NO_EXAMPLE + "。yyyyMMdd 为日期，NNNN 为4位流水号。");
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("填写错误", "任务编号可留空；手动填写时格式应为 RWyyyyMMddNNNN，例如 " + TASK_NO_EXAMPLE);
        sheet.addValidationData(validation);
    }

    private AjaxResult buildImportError(List<String> errors)
    {
        List<String> safeErrors = errors == null ? Collections.emptyList() : errors;
        AjaxResult result = AjaxResult.error(HttpStatus.ERROR, "导入失败，共发现 " + safeErrors.size() + " 个错误");
        result.put("errors", safeErrors);
        return result;
    }

    private void validateImportHeader(Sheet sheet, DataFormatter formatter, FormulaEvaluator evaluator, List<String> errors)
    {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null)
        {
            errors.add("Excel 表头不能为空，请使用最新模板");
            return;
        }
        for (int i = 0; i < IMPORT_HEADERS.length; i++)
        {
            String expected = normalizeHeader(IMPORT_HEADERS[i]);
            String actual = normalizeHeader(getCellText(headerRow.getCell(i), formatter, evaluator));
            if (!expected.equals(actual))
            {
                errors.add("表头第" + (i + 1) + "列应为【" + IMPORT_HEADERS[i] + "】，当前为【" + emptyToDash(getCellText(headerRow.getCell(i), formatter, evaluator)) + "】");
            }
        }
        short lastCellNum = headerRow.getLastCellNum();
        if (lastCellNum > IMPORT_HEADERS.length)
        {
            for (int i = IMPORT_HEADERS.length; i < lastCellNum; i++)
            {
                String extraHeader = getCellText(headerRow.getCell(i), formatter, evaluator);
                if (!isBlank(extraHeader))
                {
                    errors.add("模板只允许 9 个字段，请移除多余表头【" + extraHeader + "】");
                }
            }
        }
    }

    private AgriTask parseImportRow(Row row, int rowNum, Long loginUserId, DataFormatter formatter, FormulaEvaluator evaluator,
                                    Map<String, String> workTypeMap, Map<String, String> workMethodMap,
                                    Map<String, String> operatorMap, RegionLookup regionLookup,
                                    Set<String> fileTaskNos, List<String> errors)
    {
        String taskNoRaw = getCellText(row.getCell(COL_TASK_NO), formatter, evaluator);
        String taskNo = trim(taskNoRaw);
        String workTypeRaw = getCellText(row.getCell(COL_WORK_TYPE), formatter, evaluator);
        String workMethodRaw = getCellText(row.getCell(COL_WORK_METHOD), formatter, evaluator);
        String regionRaw = getCellText(row.getCell(COL_REGION), formatter, evaluator);
        String description = trim(getCellText(row.getCell(COL_DESCRIPTION), formatter, evaluator));
        String operatorRaw = getCellText(row.getCell(COL_OPERATOR), formatter, evaluator);
        String remark = trim(getCellText(row.getCell(COL_REMARK), formatter, evaluator));
        Date startTime = parseCellDate(row.getCell(COL_START_TIME), formatter, evaluator);
        Date endTime = parseCellDate(row.getCell(COL_END_TIME), formatter, evaluator);

        boolean valid = true;
        if (!isBlank(taskNo))
        {
            if (taskNo.length() > TASK_NO_MAX_LENGTH)
            {
                errors.add("第" + rowNum + "行【任务编号】：“" + taskNo + "”长度不能超过" + TASK_NO_MAX_LENGTH + "个字符");
                valid = false;
            }
            if (!isValidTaskNoFormat(taskNo))
            {
                errors.add("第" + rowNum + "行【任务编号】：“" + taskNo + "”格式不正确，应为 RWyyyyMMddNNNN，例如 " + TASK_NO_EXAMPLE + "，且日期需为有效日期");
                valid = false;
            }
            if (!fileTaskNos.add(taskNo))
            {
                errors.add("第" + rowNum + "行【任务编号】：“" + taskNo + "”在导入文件中重复");
                valid = false;
            }
        }

        String workTypeValue = resolveDictValue(workTypeMap, workTypeRaw);
        if (isBlank(workTypeValue))
        {
            errors.add("第" + rowNum + "行【农事类型】：“" + emptyToDash(workTypeRaw) + "”不在可选范围内");
            valid = false;
        }

        String workMethodValue = resolveDictValue(workMethodMap, workMethodRaw);
        if (isBlank(workMethodValue))
        {
            errors.add("第" + rowNum + "行【作业方式】：“" + emptyToDash(workMethodRaw) + "”不在可选范围内");
            valid = false;
        }

        RegionInfo regionInfo = resolveRegion(regionLookup, regionRaw);
        if (regionInfo == null)
        {
            String fieldValue = emptyToDash(regionRaw);
            if (isBlank(regionRaw))
            {
                errors.add("第" + rowNum + "行【区域】：不能为空");
            }
            else if (regionLookup.ambiguousKeys.contains(normalizeText(regionRaw)))
            {
                errors.add("第" + rowNum + "行【区域】：“" + fieldValue + "”匹配到多个区域，请填写更完整的区域名称");
            }
            else
            {
                errors.add("第" + rowNum + "行【区域】：“" + fieldValue + "”无法匹配唯一有效区域");
            }
            valid = false;
        }

        OperatorResolveResult operatorResult = resolveOperatorValues(operatorMap, operatorRaw);
        String operatorIds = String.join(",", operatorResult.values);
        if (operatorResult.values.isEmpty() || !operatorResult.missingNames.isEmpty())
        {
            if (isBlank(operatorRaw))
            {
                errors.add("第" + rowNum + "行【操作人】：不能为空");
            }
            else
            {
                errors.add("第" + rowNum + "行【操作人】：“" + String.join(",", operatorResult.missingNames) + "”不存在");
            }
            valid = false;
        }
        else if (operatorIds.length() > OPERATOR_ID_MAX_LENGTH)
        {
            errors.add("第" + rowNum + "行【操作人】：转换后的操作人ID长度超过" + OPERATOR_ID_MAX_LENGTH + "个字符，请减少操作人数");
            valid = false;
        }

        if (description.length() > DESCRIPTION_MAX_LENGTH)
        {
            errors.add("第" + rowNum + "行【任务描述】：长度不能超过" + DESCRIPTION_MAX_LENGTH + "个字符");
            valid = false;
        }
        if (remark.length() > REMARK_MAX_LENGTH)
        {
            errors.add("第" + rowNum + "行【备注】：长度不能超过" + REMARK_MAX_LENGTH + "个字符");
            valid = false;
        }

        String startRaw = getCellText(row.getCell(COL_START_TIME), formatter, evaluator);
        String endRaw = getCellText(row.getCell(COL_END_TIME), formatter, evaluator);
        if (startTime == null)
        {
            errors.add("第" + rowNum + "行【开始时间】：“" + emptyToDash(startRaw) + "”格式错误，应为 " + DATE_TIME_PATTERN);
            valid = false;
        }
        if (endTime == null)
        {
            errors.add("第" + rowNum + "行【结束时间】：“" + emptyToDash(endRaw) + "”格式错误，应为 " + DATE_TIME_PATTERN);
            valid = false;
        }
        if (startTime != null && endTime != null && !endTime.after(startTime))
        {
            errors.add("第" + rowNum + "行【结束时间】：必须晚于开始时间");
            valid = false;
        }

        Long workTypeId = parseLongValue(workTypeValue);
        Long workMethodId = parseLongValue(workMethodValue);
        if (!isBlank(workTypeValue) && workTypeId == null)
        {
            errors.add("第" + rowNum + "行【农事类型】：“" + emptyToDash(workTypeRaw) + "”对应的字典值不是有效数字");
            valid = false;
        }
        if (!isBlank(workMethodValue) && workMethodId == null)
        {
            errors.add("第" + rowNum + "行【作业方式】：“" + emptyToDash(workMethodRaw) + "”对应的字典值不是有效数字");
            valid = false;
        }

        if (!valid)
        {
            return null;
        }

        AgriTask task = new AgriTask();
        task.setTaskNo(taskNo);
        task.setWorkTypeId(workTypeId);
        task.setWorkMethodId(workMethodId);
        task.setAreaId(regionInfo.areaId);
        task.setLocationId(regionInfo.locationId);
        task.setDescription(description);
        task.setPhotoUrls("");
        task.setCreateById(String.valueOf(loginUserId));
        task.setOperatorId(operatorIds);
        task.setStartTime(startTime);
        task.setEndTime(endTime);
        task.setRemark(remark);
        return task;
    }

    private Map<String, String> buildDictValueMap(String dictType)
    {
        Map<String, String> result = new HashMap<>();
        for (DictOption option : buildDictOptions(dictType))
        {
            result.put(normalizeText(option.label), option.value);
            result.put(normalizeText(option.value), option.value);
        }
        return result;
    }

    private List<DictOption> buildDictOptions(String dictType)
    {
        List<DictOption> result = new ArrayList<>();
        List<Map<String, Object>> dictRows = baseMapper.selectDictDataByType(dictType);
        for (Map<String, Object> row : dictRows)
        {
            String label = trim(valueOfMap(row, "dictLabel", "dict_label", "DICTLABEL", "DICT_LABEL"));
            String value = trim(valueOfMap(row, "dictValue", "dict_value", "DICTVALUE", "DICT_VALUE"));
            if (!isBlank(label) && !isBlank(value))
            {
                result.add(new DictOption(label, value));
            }
        }
        return result;
    }

    private RegionLookup buildRegionLookup()
    {
        RegionLookup lookup = new RegionLookup();
        List<Map<String, Object>> rows = baseMapper.selectRegionTree();
        for (Map<String, Object> row : rows)
        {
            String parkName = trim(valueOfMap(row, "parkName", "park_name", "PARKNAME", "PARK_NAME"));
            String areaName = trim(valueOfMap(row, "areaName", "area_name", "AREANAME", "AREA_NAME"));
            String locationName = trim(valueOfMap(row, "locationName", "location_name", "LOCATIONNAME", "LOCATION_NAME"));
            Long areaId = parseLongValue(valueOfMap(row, "areaId", "area_id", "AREAID", "AREA_ID"));
            Long locationId = parseLongValue(valueOfMap(row, "locationId", "location_id", "LOCATIONID", "LOCATION_ID"));
            if (isBlank(parkName) || isBlank(areaName) || isBlank(locationName) || areaId == null || locationId == null)
            {
                continue;
            }
            String fullName = parkName + areaName + locationName;
            RegionInfo info = new RegionInfo(areaId, locationId, fullName);
            lookup.options.add(info);
            putRegionKey(lookup, fullName, info);
            putRegionKey(lookup, parkName + " " + areaName + " " + locationName, info);
            putRegionKey(lookup, parkName + "/" + areaName + "/" + locationName, info);
        }
        return lookup;
    }

    private void putRegionKey(RegionLookup lookup, String key, RegionInfo value)
    {
        String normalizedKey = normalizeText(key);
        if (isBlank(normalizedKey))
        {
            return;
        }
        RegionInfo exists = lookup.regionMap.get(normalizedKey);
        if (exists != null && (!exists.areaId.equals(value.areaId) || !exists.locationId.equals(value.locationId)))
        {
            lookup.regionMap.remove(normalizedKey);
            lookup.ambiguousKeys.add(normalizedKey);
            return;
        }
        if (!lookup.ambiguousKeys.contains(normalizedKey))
        {
            lookup.regionMap.put(normalizedKey, value);
        }
    }

    private String resolveDictValue(Map<String, String> dictMap, String input)
    {
        String key = normalizeText(input);
        return isBlank(key) ? null : dictMap.get(key);
    }

    private OperatorResolveResult resolveOperatorValues(Map<String, String> operatorMap, String input)
    {
        OperatorResolveResult result = new OperatorResolveResult();
        String text = trim(input);
        if (isBlank(text))
        {
            return result;
        }
        text = text.replace('，', ',')
                .replace('、', ',')
                .replace('；', ',')
                .replace(';', ',');
        String[] parts = text.split(",");
        LinkedHashSet<String> values = new LinkedHashSet<>();
        for (String part : parts)
        {
            String name = trim(part);
            if (isBlank(name))
            {
                continue;
            }
            String value = resolveDictValue(operatorMap, name);
            if (isBlank(value))
            {
                result.missingNames.add(name);
            }
            else
            {
                values.add(value);
            }
        }
        result.values.addAll(values);
        return result;
    }

    private RegionInfo resolveRegion(RegionLookup lookup, String input)
    {
        String key = normalizeText(input);
        return isBlank(key) ? null : lookup.regionMap.get(key);
    }

    private boolean isBlankImportRow(Row row, DataFormatter formatter, FormulaEvaluator evaluator)
    {
        if (row == null)
        {
            return true;
        }
        for (int i = 0; i < IMPORT_HEADERS.length; i++)
        {
            if (!isBlank(getCellText(row.getCell(i), formatter, evaluator)))
            {
                return false;
            }
        }
        return true;
    }

    private String getCellText(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator)
    {
        if (cell == null)
        {
            return "";
        }
        try
        {
            if (cell.getCellType() == CellType.NUMERIC && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell))
            {
                return DateUtils.parseDateToStr(DATE_TIME_PATTERN, cell.getDateCellValue());
            }
            String value = formatter.formatCellValue(cell, evaluator);
            return trim(value);
        }
        catch (Exception e)
        {
            return "";
        }
    }

    private Date parseCellDate(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator)
    {
        if (cell == null)
        {
            return null;
        }
        try
        {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.FORMULA && evaluator != null)
            {
                cellType = evaluator.evaluateFormulaCell(cell);
            }
            if (cellType == CellType.NUMERIC && org.apache.poi.ss.usermodel.DateUtil.isValidExcelDate(cell.getNumericCellValue()))
            {
                return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(cell.getNumericCellValue());
            }
        }
        catch (Exception ignored)
        {
        }

        String text = getCellText(cell, formatter, evaluator);
        if (isBlank(text))
        {
            return null;
        }
        try
        {
            SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_PATTERN);
            format.setLenient(false);
            return format.parse(text);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    private String normalizeHeader(String header)
    {
        return trim(header).replace("*", "").replaceAll("\\s+", "");
    }

    private String emptyToDash(String value)
    {
        String text = trim(value);
        return isBlank(text) ? "-" : text;
    }

    private List<String> dictLabels(List<DictOption> options)
    {
        List<String> labels = new ArrayList<>();
        for (DictOption option : options)
        {
            labels.add(option.label);
        }
        return labels;
    }

    private List<String> regionNames(List<RegionInfo> options)
    {
        List<String> names = new ArrayList<>();
        for (RegionInfo option : options)
        {
            names.add(option.fullName);
        }
        return names;
    }

    private String firstLabel(List<DictOption> options)
    {
        return options.isEmpty() ? "" : options.get(0).label;
    }

    private String firstRegionName(List<RegionInfo> options)
    {
        return options.isEmpty() ? "" : options.get(0).fullName;
    }

    private String firstOperatorExample(List<DictOption> options)
    {
        if (options.isEmpty())
        {
            return "";
        }
        if (options.size() == 1)
        {
            return options.get(0).label;
        }
        return options.get(0).label + "," + options.get(1).label;
    }

    private String normalizeText(Object value)
    {
        String text = trim(value);
        if (isBlank(text)) {
            return "";
        }
        return text.replaceAll("[\\s　/／,，、;；-]+", "");
    }

    private String trim(Object value)
    {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private void normalizeTaskNo(AgriTask agriTask)
    {
        if (agriTask != null && agriTask.getTaskNo() != null)
        {
            agriTask.setTaskNo(agriTask.getTaskNo().trim());
        }
    }

    private int parseTaskNoSerial(String taskNo, String prefix)
    {
        if (isBlank(taskNo) || !taskNo.startsWith(prefix))
        {
            return 0;
        }
        String serialText = taskNo.substring(prefix.length());
        if (isBlank(serialText) || !serialText.matches("\\d+"))
        {
            return 0;
        }
        try
        {
            return Integer.parseInt(serialText);
        }
        catch (NumberFormatException ignored)
        {
            return 0;
        }
    }

    private boolean isValidTaskNoFormat(String taskNo)
    {
        if (isBlank(taskNo) || !taskNo.matches(TASK_NO_IMPORT_REGEX))
        {
            return false;
        }
        String datePart = taskNo.substring(TASK_NO_PREFIX.length(), TASK_NO_PREFIX.length() + 8);
        String serialPart = taskNo.substring(TASK_NO_PREFIX.length() + 8);
        if (Integer.parseInt(serialPart) <= 0)
        {
            return false;
        }
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            format.setLenient(false);
            format.parse(datePart);
            return true;
        }
        catch (ParseException e)
        {
            return false;
        }
    }

    private synchronized int assignGeneratedTaskNos(List<PendingImportTask> tasks, List<String> errors)
    {
        int missingCount = 0;
        Set<String> reservedTaskNos = new HashSet<>();
        for (PendingImportTask pendingTask : tasks)
        {
            String taskNo = pendingTask.task.getTaskNo();
            if (isBlank(taskNo))
            {
                missingCount++;
            }
            else
            {
                reservedTaskNos.add(taskNo);
            }
        }
        if (missingCount == 0)
        {
            return 0;
        }

        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String prefix = TASK_NO_PREFIX + datePart;
        int nextSerial = 1;
        List<String> taskNos = baseMapper.selectTaskNosByPrefix(prefix);
        if (taskNos != null)
        {
            for (String taskNo : taskNos)
            {
                int serial = parseTaskNoSerial(taskNo, prefix);
                if (serial >= nextSerial)
                {
                    nextSerial = serial + 1;
                }
            }
        }

        List<String> generatedTaskNos = new ArrayList<>();
        while (generatedTaskNos.size() < missingCount)
        {
            if (nextSerial > TASK_NO_MAX_DAILY_SERIAL)
            {
                errors.add("当天任务编号流水号已用尽，无法继续生成任务编号");
                return 0;
            }
            String taskNo = prefix + String.format("%0" + TASK_NO_SERIAL_LENGTH + "d", nextSerial);
            nextSerial++;
            if (reservedTaskNos.contains(taskNo))
            {
                continue;
            }
            generatedTaskNos.add(taskNo);
            reservedTaskNos.add(taskNo);
        }

        int generatedIndex = 0;
        for (PendingImportTask pendingTask : tasks)
        {
            if (isBlank(pendingTask.task.getTaskNo()))
            {
                pendingTask.task.setTaskNo(generatedTaskNos.get(generatedIndex++));
                pendingTask.autoGeneratedTaskNo = true;
            }
        }
        return generatedTaskNos.size();
    }

    private void assertTaskNoUnique(String taskNo, Long excludeId)
    {
        if (isBlank(taskNo))
        {
            throw new ServiceException("任务编号不能为空");
        }
        if (!checkTaskNoUnique(taskNo, excludeId))
        {
            throw new ServiceException(TASK_NO_EXISTS_MESSAGE);
        }
    }

    private void handleTaskNoConflict(DataIntegrityViolationException e)
    {
        if (isTaskNoConflict(e))
        {
            throw new ServiceException(TASK_NO_EXISTS_MESSAGE);
        }
    }

    private boolean isTaskNoConflict(Throwable throwable)
    {
        Throwable current = throwable;
        while (current != null)
        {
            String message = current.getMessage();
            if (message != null)
            {
                String lowerMessage = message.toLowerCase();
                if (lowerMessage.contains("uk_ylm_agri_task_task_no") || lowerMessage.contains("task_no"))
                {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isBlank(Object value)
    {
        return value == null || String.valueOf(value).trim().isEmpty();
    }

    private Long parseLongValue(Object value)
    {
        String text = trim(value);
        if (isBlank(text)) {
            return null;
        }
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String valueOfMap(Map<String, Object> row, String... keys)
    {
        for (String key : keys) {
            if (row.containsKey(key) && row.get(key) != null) {
                return String.valueOf(row.get(key));
            }
        }
        return "";
    }

    private void fillAreaIdByLocationId(AgriTask agriTask)
    {
        if (agriTask == null || agriTask.getLocationId() == null) {
            return;
        }
        Long areaId = baseMapper.selectAreaIdByLocationId(agriTask.getLocationId());
        if (areaId != null) {
            agriTask.setAreaId(areaId);
        }
    }

    private static class RegionInfo
    {
        private final Long areaId;
        private final Long locationId;
        private final String fullName;

        private RegionInfo(Long areaId, Long locationId, String fullName)
        {
            this.areaId = areaId;
            this.locationId = locationId;
            this.fullName = fullName;
        }
    }

    private static class RegionLookup
    {
        private final Map<String, RegionInfo> regionMap = new HashMap<>();
        private final Set<String> ambiguousKeys = new HashSet<>();
        private final List<RegionInfo> options = new ArrayList<>();
    }

    private static class DictOption
    {
        private final String label;
        private final String value;

        private DictOption(String label, String value)
        {
            this.label = label;
            this.value = value;
        }
    }

    private static class OperatorResolveResult
    {
        private final List<String> values = new ArrayList<>();
        private final List<String> missingNames = new ArrayList<>();
    }

    private static class PendingImportTask
    {
        private final int rowNum;
        private final AgriTask task;
        private boolean autoGeneratedTaskNo;

        private PendingImportTask(int rowNum, AgriTask task)
        {
            this.rowNum = rowNum;
            this.task = task;
        }
    }
}
