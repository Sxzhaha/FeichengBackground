package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.IotAlertControlLinkageMapper;
import com.ruoyi.iot.ld.domain.IotAlertControlLinkage;
import com.ruoyi.iot.ld.service.IIotAlertControlLinkageService;

/**
 * Alert control linkage Service implementation.
 */
@Service
public class IotAlertControlLinkageServiceImpl implements IIotAlertControlLinkageService
{
    @Autowired
    private IotAlertControlLinkageMapper iotAlertControlLinkageMapper;

    @Override
    public IotAlertControlLinkage selectIotAlertControlLinkageById(Long id)
    {
        return iotAlertControlLinkageMapper.selectIotAlertControlLinkageById(id);
    }

    @Override
    public List<IotAlertControlLinkage> selectIotAlertControlLinkageList(IotAlertControlLinkage iotAlertControlLinkage)
    {
        return iotAlertControlLinkageMapper.selectIotAlertControlLinkageList(iotAlertControlLinkage);
    }

    @Override
    public int insertIotAlertControlLinkage(IotAlertControlLinkage iotAlertControlLinkage)
    {
        try {
            iotAlertControlLinkage.setCreateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            // Keep system-triggered inserts from failing when no login context exists.
        }
        iotAlertControlLinkage.setCreateTime(DateUtils.getNowDate());
        return iotAlertControlLinkageMapper.insertIotAlertControlLinkage(iotAlertControlLinkage);
    }

    @Override
    public int updateIotAlertControlLinkage(IotAlertControlLinkage iotAlertControlLinkage)
    {
        try {
            iotAlertControlLinkage.setUpdateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            // Keep system-triggered updates from failing when no login context exists.
        }
        iotAlertControlLinkage.setUpdateTime(DateUtils.getNowDate());
        return iotAlertControlLinkageMapper.updateIotAlertControlLinkage(iotAlertControlLinkage);
    }

    @Override
    public int deleteIotAlertControlLinkageByIds(Long[] ids)
    {
        return iotAlertControlLinkageMapper.deleteIotAlertControlLinkageByIds(ids);
    }

    @Override
    public int deleteIotAlertControlLinkageById(Long id)
    {
        return iotAlertControlLinkageMapper.deleteIotAlertControlLinkageById(id);
    }
}
