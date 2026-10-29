package com.ruoyi.iot.ld.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.CameraPresetMapper;
import com.ruoyi.iot.ld.domain.CameraPreset;
import com.ruoyi.iot.ld.service.ICameraPresetService;

/**
 * 摄像头预置点Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@Service
public class CameraPresetServiceImpl implements ICameraPresetService 
{
    @Autowired
    private CameraPresetMapper cameraPresetMapper;


    /**
     * 查询摄像头预置点
     * 
     * @param Id 摄像头预置点主键
     * @return 摄像头预置点
     */
    @Override
    public CameraPreset selectCameraPresetById(Long id)
    {
        return cameraPresetMapper.selectCameraPresetById(id);
    }

    /**
     * 查询摄像头预置点列表
     * 
     * @param cameraPreset 摄像头预置点
     * @return 摄像头预置点
     */
    @Override
    public List<CameraPreset> selectCameraPresetList(CameraPreset cameraPreset)
    {
        return cameraPresetMapper.selectCameraPresetList(cameraPreset);
    }

    /**
     * 新增摄像头预置点
     * 
     * @param cameraPreset 摄像头预置点
     * @return 结果
     */
    @Override
    public int insertCameraPreset(CameraPreset cameraPreset)
    {
        cameraPreset.setCreateTime(DateUtils.getNowDate());
        return cameraPresetMapper.insertCameraPreset(cameraPreset);
    }

    /**
     * 修改摄像头预置点
     * 
     * @param cameraPreset 摄像头预置点
     * @return 结果
     */
    @Override
    public int updateCameraPreset(CameraPreset cameraPreset)
    {
        cameraPreset.setUpdateTime(DateUtils.getNowDate());
        return cameraPresetMapper.updateCameraPreset(cameraPreset);
    }

    /**
     * 批量删除摄像头预置点
     * 
     * @param deviceIds 需要删除的摄像头预置点主键
     * @return 结果
     */
    @Override
    public int deleteCameraPresetByIds(Long[] ids)
    {
        return cameraPresetMapper.deleteCameraPresetByIds(ids);
    }

    /**
     * 删除摄像头预置点信息
     * 
     * @param deviceId 摄像头预置点主键
     * @return 结果
     */
    @Override
    public int deleteCameraPresetById(Long deviceId) {
        return cameraPresetMapper.deleteCameraPresetById(deviceId);
    }
}
