package com.ruoyi.binZhou.ylm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 农事记录批量导入对象。模板只保留用户需要填写的字段。
 */
public class AgriTaskImport
{
    /** 任务编号 */
    @Excel(name = "任务编号")
    private String taskNo;

    /** 农事类型 */
    @Excel(name = "农事类型*", dictType = "ylm_agri_work_type", comboReadDict = true)
    private String workTypeName;

    /** 作业方式 */
    @Excel(name = "作业方式*", dictType = "ylm_agri_work_method", comboReadDict = true)
    private String workMethodName;

    /** 区域完整名称 */
    @Excel(name = "区域*", prompt = "填写完整区域名称，例如：滨州园区南地块东角落")
    private String regionFullName;

    /** 任务描述 */
    @Excel(name = "任务描述", width = 40)
    private String description;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间*", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间*", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 备注 */
    @Excel(name = "备注", width = 30)
    private String remark;

    /** 操作人 */
    @Excel(name = "操作人*", dictType = "ylm_agri_operator", comboReadDict = true, separator = ",", prompt = "多名操作人请使用英文逗号分隔，例如：张三,李四")
    private String operatorName;

    public String getTaskNo()
    {
        return taskNo;
    }

    public void setTaskNo(String taskNo)
    {
        this.taskNo = taskNo;
    }

    public String getWorkTypeName()
    {
        return workTypeName;
    }

    public void setWorkTypeName(String workTypeName)
    {
        this.workTypeName = workTypeName;
    }

    public String getWorkMethodName()
    {
        return workMethodName;
    }

    public void setWorkMethodName(String workMethodName)
    {
        this.workMethodName = workMethodName;
    }

    public String getRegionFullName()
    {
        return regionFullName;
    }

    public void setRegionFullName(String regionFullName)
    {
        this.regionFullName = regionFullName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getOperatorName()
    {
        return operatorName;
    }

    public void setOperatorName(String operatorName)
    {
        this.operatorName = operatorName;
    }
}
