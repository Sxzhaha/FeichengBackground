package com.ruoyi.iot.ld.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.service.IIotDeviceService;
import com.ruoyi.iot.ld.service.IYs7Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.iot.ld.domain.CameraPreset;
import com.ruoyi.iot.ld.service.ICameraPresetService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 摄像头预置点Controller
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/binZhou/ld/iot/cameraPreset")
public class CameraPresetController extends BaseController
{
    @Autowired
    private ICameraPresetService cameraPresetService;
    // 【新增注入 1】设备服务，用于根据 ID 查 SN 码
    @Autowired
    private IIotDeviceService deviceService;
    // 【新增注入 2】萤石云服务
    @Autowired
    private IYs7Service ys7Service;
    /**
     * 查询摄像头预置点列表
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraPreset:list')")
    @GetMapping("/list")
    public TableDataInfo list(CameraPreset cameraPreset)
    {
        startPage();
        List<CameraPreset> list = cameraPresetService.selectCameraPresetList(cameraPreset);
        return getDataTable(list);
    }

    /**
     * 导出摄像头预置点列表
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraPreset:export')")
    @Log(title = "摄像头预置点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CameraPreset cameraPreset)
    {
        List<CameraPreset> list = cameraPresetService.selectCameraPresetList(cameraPreset);
        ExcelUtil<CameraPreset> util = new ExcelUtil<CameraPreset>(CameraPreset.class);
        util.exportExcel(response, list, "摄像头预置点数据");
    }

    /**
     * 获取摄像头预置点详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraPreset:query')")
    @GetMapping(value = "/{presetId}")
    public AjaxResult getInfo(@PathVariable("presetId") Long id)
    {
        return success(cameraPresetService.selectCameraPresetById(id));
    }

    /**
     * 新增摄像头预置点
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraPreset:add')")
    @Log(title = "摄像头预置点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CameraPreset cameraPreset)
    {

        // 1. 校验：先根据 deviceId 查询设备信息，获取 SN 码
        IotDevice device = deviceService.selectIotDeviceByDeviceId(cameraPreset.getDeviceId());
        if (device == null) {
            return AjaxResult.error("关联的设备不存在");
        }
        // 2. 构建查询条件
        CameraPreset query = new CameraPreset();
        query.setDeviceId(cameraPreset.getDeviceId());
        query.setPointIndex(cameraPreset.getPointIndex());

        // 3. 查询数据库是否存在
        List<CameraPreset> list = cameraPresetService.selectCameraPresetList(query);

        // 4. 如果列表不为空，说明已经存在了
        if (list != null && !list.isEmpty()) {
            return AjaxResult.error("操作失败：该设备下已存在编号为 [" + cameraPreset.getPointIndex() + "] 的预置点，请勿重复添加！");
        }

        // 5. 核心：调用萤石云接口，命令摄像头记住当前位置
        try {
            ys7Service.addPreset(device.getDeviceSn(), cameraPreset.getPointIndex());
        } catch (Exception e) {
            // 如果远程设置失败，直接返回错误，不保存到数据库
            return AjaxResult.error(e.getMessage());
        }


        // 若依生成的 insert 方法，如果有唯一索引冲突（deviceId + pointIndex），
        // 全局异常处理器通常会捕获，或者你可以自己 try-catch 处理 DuplicateKeyException
        return toAjax(cameraPresetService.insertCameraPreset(cameraPreset));
    }

    /**
     * 修改摄像头预置点
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraPreset:edit')")
    @Log(title = "摄像头预置点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CameraPreset cameraPreset)
    {
        return toAjax(cameraPresetService.updateCameraPreset(cameraPreset));
    }

    /**
     * 删除摄像头预置点
     */
    @PreAuthorize("@ss.hasPermi('iot:cameraPreset:remove')")
    @Log(title = "摄像头预置点", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        // ================== 【新增同步删除逻辑】 ==================
        for (Long id : ids) {
            // 1. 查询预置点详情 (为了拿到 pointIndex 和 deviceId)
            CameraPreset preset = cameraPresetService.selectCameraPresetById(id);
            if (preset == null) {
                continue;
            }

            // 2. 查询设备详情 (为了拿到 snCode)
            IotDevice device = deviceService.selectIotDeviceByDeviceId(preset.getDeviceId());

            // 3. 调用萤石云接口删除远程预置点
            // 注意：如果设备不存在或已离线，你可能希望允许只删除本地数据，
            // 这里我写的是严格模式：远程删不掉，本地也不让删。
            if (device != null) {
                try {
                    ys7Service.clearPreset(device.getDeviceSn(), preset.getPointIndex());
                } catch (Exception e) {
                    return AjaxResult.error("远程删除失败，本地未删除：" + e.getMessage());
                }
            }
        }
        // ================== 【逻辑结束】 ==================

        // 4. 执行本地数据库删除
        return toAjax(cameraPresetService.deleteCameraPresetByIds(ids));
    }
}
