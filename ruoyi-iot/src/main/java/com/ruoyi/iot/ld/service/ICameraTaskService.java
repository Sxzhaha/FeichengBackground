package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.CameraTask;

/**
 * 摄像头定时抓拍任务Service接口
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public interface ICameraTaskService 
{
    /**
     * 查询摄像头定时抓拍任务
     * 
     * @param taskId 摄像头定时抓拍任务主键
     * @return 摄像头定时抓拍任务
     */
    public CameraTask selectCameraTaskByTaskId(Long taskId);

    /**
     * 查询摄像头定时抓拍任务列表
     * 
     * @param cameraTask 摄像头定时抓拍任务
     * @return 摄像头定时抓拍任务集合
     */
    public List<CameraTask> selectCameraTaskList(CameraTask cameraTask);

    /**
     * 新增摄像头定时抓拍任务
     * 
     * @param cameraTask 摄像头定时抓拍任务
     * @return 结果
     */
    public int insertCameraTask(CameraTask cameraTask);

    /**
     * 修改摄像头定时抓拍任务
     * 
     * @param cameraTask 摄像头定时抓拍任务
     * @return 结果
     */
    public int updateCameraTask(CameraTask cameraTask);

    /**
     * 批量删除摄像头定时抓拍任务
     * 
     * @param taskIds 需要删除的摄像头定时抓拍任务主键集合
     * @return 结果
     */
    public int deleteCameraTaskByTaskIds(Long[] taskIds);

    /**
     * 删除摄像头定时抓拍任务信息
     * 
     * @param taskId 摄像头定时抓拍任务主键
     * @return 结果
     */
    public int deleteCameraTaskByTaskId(Long taskId);
}
