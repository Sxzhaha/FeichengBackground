package com.ruoyi.iot.ld.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 抓拍图片记录对象 image_record
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public class ImageRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 设备ID */
    @Excel(name = "设备ID")
    private Long deviceId;


    /** 图片访问URL */
    @Excel(name = "图片访问URL")
    private String url;

    /** 文件大小(字节) */
    @Excel(name = "文件大小(字节)")
    private Long fileSize;

    @Excel(name = "任务编号,为null表示手动")
    private Long taskId;

    @Excel(name = "任务名称,为null表示手动")
    private String taskName;
    @Excel(name = "执行人id")
    private Long userId;
    @Excel(name = "执行人姓名")
    private String userName;

    /** 拍摄时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "拍摄时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date captureTime;



    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }


    public void setUrl(String url) 
    {
        this.url = url;
    }

    public String getUrl() 
    {
        return url;
    }

    public void setFileSize(Long fileSize) 
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize() 
    {
        return fileSize;
    }


    public void setCaptureTime(Date captureTime) 
    {
        this.captureTime = captureTime;
    }

    public Date getCaptureTime() 
    {
        return captureTime;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceId", getDeviceId())
            .append("url", getUrl())
            .append("fileSize", getFileSize())
            .append("captureTime", getCaptureTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
