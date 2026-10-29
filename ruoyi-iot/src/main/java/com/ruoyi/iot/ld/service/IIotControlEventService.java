package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.IotControlEvent;

/**
 * 控制事件下发Service接口
 */
public interface IIotControlEventService
{
    public IotControlEvent selectIotControlEventByEventId(Long eventId);

    public List<IotControlEvent> selectIotControlEventList(IotControlEvent iotControlEvent);

    public int insertIotControlEvent(IotControlEvent iotControlEvent);

    public int updateIotControlEvent(IotControlEvent iotControlEvent);

    public int updateIotControlEventResponseByRes(String deviceSn, String res, String responsePayload, String responseDesc);

    public int deleteIotControlEventByEventIds(Long[] eventIds);

    public int deleteIotControlEventByEventId(Long eventId);
}
