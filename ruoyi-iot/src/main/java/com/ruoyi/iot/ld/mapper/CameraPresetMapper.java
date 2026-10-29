package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.CameraPreset;

/**
 * 摄像头预置点Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface CameraPresetMapper 
{
    /**
     * 查询摄像头预置点
     * 
     * @param deviceId 摄像头主键
     * @return 摄像头预置点
     */
    public List<CameraPreset> selectCameraPresetByDeviceId(Long deviceId);

    /**
     * 查询预置点
     *
     * @param presetId 预置点主键
     * @return 摄像头预置点
     */
    public CameraPreset selectCameraPresetById(Long presetId);

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
     * 删除摄像头预置点
     * 
     * @param deviceId 摄像头预置点主键
     * @return 结果
     */
    public int deleteCameraPresetById(Long id);

    /**
     * 批量删除摄像头预置点
     * 
     * @param deviceIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCameraPresetByIds(Long[] ids);
}
