package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.CameraPreset;

/**
 * 摄像头预置点Service接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface ICameraPresetService 
{
    /**
     * 查询摄像头预置点
     * 
     * @param id 摄像头预置点主键
     * @return 摄像头预置点
     */
    public CameraPreset selectCameraPresetById(Long id);

    /**
     * 查询摄像头预置点列表
     * 
     * @param cameraPreset 摄像头预置点
     * @return 摄像头预置点集合
     */
    public List<CameraPreset> selectCameraPresetList(CameraPreset cameraPreset);

    /**
     * 新增摄像头预置点
     * 
     * @param cameraPreset 摄像头预置点
     * @return 结果
     */
    public int insertCameraPreset(CameraPreset cameraPreset);

    /**
     * 修改摄像头预置点
     * 
     * @param cameraPreset 摄像头预置点
     * @return 结果
     */
    public int updateCameraPreset(CameraPreset cameraPreset);

    /**
     * 批量删除摄像头预置点
     * 
     * @param deviceIds 需要删除的摄像头预置点主键集合
     * @return 结果
     */
    public int deleteCameraPresetByIds(Long[] ids);

    /**
     * 删除摄像头预置点信息
     * 
     * @param deviceId 摄像头预置点主键
     * @return 结果
     */
    public int deleteCameraPresetById(Long deviceId);

}
