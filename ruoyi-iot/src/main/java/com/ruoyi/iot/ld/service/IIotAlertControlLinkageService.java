package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.IotAlertControlLinkage;

/**
 * Alert control linkage Service interface.
 */
public interface IIotAlertControlLinkageService
{
    public IotAlertControlLinkage selectIotAlertControlLinkageById(Long id);

    public List<IotAlertControlLinkage> selectIotAlertControlLinkageList(IotAlertControlLinkage iotAlertControlLinkage);

    public int insertIotAlertControlLinkage(IotAlertControlLinkage iotAlertControlLinkage);

    public int updateIotAlertControlLinkage(IotAlertControlLinkage iotAlertControlLinkage);

    public int deleteIotAlertControlLinkageByIds(Long[] ids);

    public int deleteIotAlertControlLinkageById(Long id);
}
