package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.domain.IotControlEvent;
import com.ruoyi.iot.ld.mapper.IotControlEventMapper;
import com.ruoyi.iot.ld.service.IIotControlEventService;

/**
 * 控制事件下发Service业务层处理
 */
@Service
public class IotControlEventServiceImpl implements IIotControlEventService
{
    @Autowired
    private IotControlEventMapper iotControlEventMapper;

    @Override
    public IotControlEvent selectIotControlEventByEventId(Long eventId)
    {
        return iotControlEventMapper.selectIotControlEventByEventId(eventId);
    }

    @Override
    public List<IotControlEvent> selectIotControlEventList(IotControlEvent iotControlEvent)
    {
        return iotControlEventMapper.selectIotControlEventList(iotControlEvent);
    }

    @Override
    public int insertIotControlEvent(IotControlEvent iotControlEvent)
    {
        try {
            iotControlEvent.setCreateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            // System-triggered events may not have a login context.
        }
        iotControlEvent.setCreateTime(DateUtils.getNowDate());
        return iotControlEventMapper.insertIotControlEvent(iotControlEvent);
    }

    @Override
    public int updateIotControlEvent(IotControlEvent iotControlEvent)
    {
        try {
            iotControlEvent.setUpdateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            // System-triggered updates may not have a login context.
        }
        iotControlEvent.setUpdateTime(DateUtils.getNowDate());
        return iotControlEventMapper.updateIotControlEvent(iotControlEvent);
    }

    @Override
    public int updateIotControlEventResponseByRes(String deviceSn, String res, String responsePayload, String responseDesc)
    {
        return iotControlEventMapper.updateIotControlEventResponseByRes(deviceSn, res, responsePayload, responseDesc);
    }

    @Override
    public int deleteIotControlEventByEventIds(Long[] eventIds)
    {
        return iotControlEventMapper.deleteIotControlEventByEventIds(eventIds);
    }

    @Override
    public int deleteIotControlEventByEventId(Long eventId)
    {
        return iotControlEventMapper.deleteIotControlEventByEventId(eventId);
    }
}
