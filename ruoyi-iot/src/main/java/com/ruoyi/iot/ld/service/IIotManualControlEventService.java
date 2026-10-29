package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.IotManualControlEvent;

/**
 * Manual control event Service interface.
 */
public interface IIotManualControlEventService
{
    public IotManualControlEvent selectIotManualControlEventByEventId(Long eventId);

    public List<IotManualControlEvent> selectIotManualControlEventList(IotManualControlEvent iotManualControlEvent);

    public int insertIotManualControlEvent(IotManualControlEvent iotManualControlEvent);

    public int sendIotManualControlEvent(IotManualControlEvent iotManualControlEvent);

    public int updateIotManualControlEvent(IotManualControlEvent iotManualControlEvent);

    public int updateIotManualControlEventResponseByRes(String deviceSn, String res, String responsePayload, String responseDesc);

    public int deleteIotManualControlEventByEventIds(Long[] eventIds);

    public int deleteIotManualControlEventByEventId(Long eventId);
}
