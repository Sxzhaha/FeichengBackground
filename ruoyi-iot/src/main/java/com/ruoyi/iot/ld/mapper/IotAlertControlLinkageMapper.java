package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.IotAlertControlLinkage;

/**
 * Alert control linkage Mapper interface.
 */
public interface IotAlertControlLinkageMapper
{
    public IotAlertControlLinkage selectIotAlertControlLinkageById(Long id);

    public List<IotAlertControlLinkage> selectIotAlertControlLinkageList(IotAlertControlLinkage iotAlertControlLinkage);

    public int insertIotAlertControlLinkage(IotAlertControlLinkage iotAlertControlLinkage);

    public int updateIotAlertControlLinkage(IotAlertControlLinkage iotAlertControlLinkage);

    public int deleteIotAlertControlLinkageById(Long id);

    public int deleteIotAlertControlLinkageByIds(Long[] ids);
}
