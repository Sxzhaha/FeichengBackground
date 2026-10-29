package com.ruoyi.iot.ld.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

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
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.iot.ld.domain.IotDevice;
import com.ruoyi.iot.ld.service.IIotDeviceService;

/**
 * 物联网设备Controller
 * * @author ruoyi
 */
@RestController
@RequestMapping("/binZhou/ld/iot/device")
public class IotDeviceController extends BaseController
{
	@Autowired
	private IIotDeviceService iotDeviceService;
	@Autowired
	private IYs7Service ys7Service;

	/**
	 * 查询物联网设备列表
	 */
	@PreAuthorize("@ss.hasPermi('iot:device:list')")
	@GetMapping("/list")
	public TableDataInfo list(IotDevice iotDevice)
	{
		startPage();
		List<IotDevice> list = iotDeviceService.selectIotDeviceList(iotDevice);
		return getDataTable(list);
	}

	/**
	 * 导出物联网设备列表
	 */
	@PreAuthorize("@ss.hasPermi('iot:device:export')")
	@Log(title = "物联网设备", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public void export(HttpServletResponse response, IotDevice iotDevice)
	{
		List<IotDevice> list = iotDeviceService.selectIotDeviceList(iotDevice);
		ExcelUtil<IotDevice> util = new ExcelUtil<IotDevice>(IotDevice.class);
		util.exportExcel(response, list, "物联网设备数据");
	}

	/**
	 * 获取物联网设备详细信息
	 */
	@PreAuthorize("@ss.hasPermi('iot:device:query')")
	@GetMapping(value = "/{deviceId}")
	public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId)
	{
		return success(iotDeviceService.selectIotDeviceByDeviceId(deviceId));
	}

	/**
	 * 新增物联网设备
	 */
	@PreAuthorize("@ss.hasPermi('iot:device:add')")
	@Log(title = "物联网设备", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@RequestBody IotDevice iotDevice)
	{
		if (!iotDeviceService.checkDeviceSnUnique(iotDevice))
		{
			return error("新增设备'" + iotDevice.getDeviceName() + "'失败，设备SN已存在");
		}
		iotDevice.setCreateBy(getUsername());
		return toAjax(iotDeviceService.insertIotDevice(iotDevice));
	}

	/**
	 * 修改物联网设备
	 */
	@PreAuthorize("@ss.hasPermi('iot:device:edit')")
	@Log(title = "物联网设备", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@RequestBody IotDevice iotDevice)
	{
		if (!iotDeviceService.checkDeviceSnUnique(iotDevice))
		{
			return error("修改设备'" + iotDevice.getDeviceName() + "'失败，设备SN已存在");
		}
		iotDevice.setUpdateBy(getUsername());
		return toAjax(iotDeviceService.updateIotDevice(iotDevice));
	}

	/**
	 * 删除物联网设备
	 */
	@PreAuthorize("@ss.hasPermi('iot:device:remove')")
	@Log(title = "物联网设备", businessType = BusinessType.DELETE)
	@DeleteMapping("/{deviceIds}")
	public AjaxResult remove(@PathVariable Long[] deviceIds)
	{
		return toAjax(iotDeviceService.deleteIotDeviceByDeviceIds(deviceIds));
	}
	/**
	 * 批量抓拍接口
	 * 请求体: { "snList": ["GB6872136", "SN888888"] }
	 */
	@PostMapping("/batchCapture")
	public AjaxResult batchCapture(@RequestBody Map<String, Object> params) {
		AjaxResult result;
		List<String> snList = (List<String>) params.get("snList");

		if (params.get("userId") == null) {
			return AjaxResult.error("用户信息异常");
		}
		Long userId = Long.parseLong(params.get("userId").toString());

		if (snList == null || snList.isEmpty()) {
			result = AjaxResult.error("请至少选择一个设备");
		} else {
			int success = 0;
			StringBuilder errorMsg = new StringBuilder();

			for (String sn : snList) {
				try {
					// 调用 Service，现在 Service 失败会抛出异常
					ys7Service.captureAndSave(sn, userId, (short)0, null, 1);
					success++;
				} catch (Exception e) {
					// 【关键点】捕获 Service 抛出的具体 RuntimeException
					String msg = e.getMessage();
					// 优化：去掉可能存在的重复前缀，保持提示简洁
					if(msg != null && msg.contains("RuntimeException:")) {
						msg = msg.replace("java.lang.RuntimeException: ", "");
					}
					errorMsg.append("[").append(sn).append("]: ").append(msg).append("; ");
				}
			}

			if (success == snList.size()) {
				result = AjaxResult.success("全部抓拍成功");
			} else if (success == 0) {
				// 如果全部失败，返回 Error 类型
				result = AjaxResult.error("抓拍失败: " + errorMsg.toString());
			} else {
				// 部分成功，返回 Warn 类型
				result = AjaxResult.warn("成功 " + success + " 台，失败 " + (snList.size() - success) + " 台。详情: " + errorMsg.toString());
			}
		}

		return result;
	}
	/**
	 * 获取萤石云AccessToken (新增接口)
	 */
	@GetMapping("/getToken")
	public AjaxResult getToken() {
		// 直接调用 Service 获取缓存或新请求的 Token
		String token = ys7Service.getAccessToken();
		return AjaxResult.success("操作成功", token);
	}

}