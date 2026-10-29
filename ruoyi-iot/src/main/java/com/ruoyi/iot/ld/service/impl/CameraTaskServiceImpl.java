package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.CameraTaskMapper;
import com.ruoyi.iot.ld.domain.CameraTask;
import com.ruoyi.iot.ld.service.ICameraTaskService;

/**
 * 摄像头定时抓拍任务Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@Service
public class CameraTaskServiceImpl implements ICameraTaskService 
{
    @Autowired
    private CameraTaskMapper cameraTaskMapper;

    /**
     * 查询摄像头定时抓拍任务
     * 
     * @param taskId 摄像头定时抓拍任务主键
     * @return 摄像头定时抓拍任务
     */
    @Override
    public CameraTask selectCameraTaskByTaskId(Long taskId)
    {
        return cameraTaskMapper.selectCameraTaskByTaskId(taskId);
    }

    /**
     * 查询摄像头定时抓拍任务列表
     * 
     * @param cameraTask 摄像头定时抓拍任务
     * @return 摄像头定时抓拍任务
     */
    @Override
    public List<CameraTask> selectCameraTaskList(CameraTask cameraTask)
    {
        return cameraTaskMapper.selectCameraTaskList(cameraTask);
    }

    /**
     * 新增摄像头定时抓拍任务
     * 
     * @param cameraTask 摄像头定时抓拍任务
     * @return 结果
     */
    @Override
    public int insertCameraTask(CameraTask cameraTask)
    {
        // 1. 设置创建时间
        cameraTask.setCreateTime(DateUtils.getNowDate());

        // 2. 默认状态设为 "0" (正常/启动)
        // 只有状态为 0，调度器才会去执行它
        if (cameraTask.getStatus() == null) {
            cameraTask.setStatus("0");
        }

        return cameraTaskMapper.insertCameraTask(cameraTask);
    }

    /**
     * 修改摄像头定时抓拍任务
     * 
     * @param cameraTask 摄像头定时抓拍任务
     * @return 结果
     */
    @Override
    public int updateCameraTask(CameraTask cameraTask)
    {
        cameraTask.setUpdateTime(DateUtils.getNowDate());
        return cameraTaskMapper.updateCameraTask(cameraTask);
    }

    /**
     * 批量删除摄像头定时抓拍任务
     * 
     * @param taskIds 需要删除的摄像头定时抓拍任务主键
     * @return 结果
     */
    @Override
    public int deleteCameraTaskByTaskIds(Long[] taskIds)
    {
        return cameraTaskMapper.deleteCameraTaskByTaskIds(taskIds);
    }

    /**
     * 删除摄像头定时抓拍任务信息
     * 
     * @param taskId 摄像头定时抓拍任务主键
     * @return 结果
     */
    @Override
    public int deleteCameraTaskByTaskId(Long taskId)
    {
        return cameraTaskMapper.deleteCameraTaskByTaskId(taskId);
    }
}
