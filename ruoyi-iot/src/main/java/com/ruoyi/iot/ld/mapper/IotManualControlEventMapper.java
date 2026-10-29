package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.IotManualControlEvent;
import org.apache.ibatis.annotations.Param;

/**
 * Manual control event Mapper interface.
 */
public interface IotManualControlEventMapper
{
    public IotManualControlEvent selectIotManualControlEventByEventId(Long eventId);

    public List<IotManualControlEvent> selectIotManualControlEventList(IotManualControlEvent iotManualControlEvent);

    public int insertIotManualControlEvent(IotManualControlEvent iotManualControlEvent);

    public int updateIotManualControlEvent(IotManualControlEvent iotManualControlEvent);

    public int updateIotManualControlEventResponseByRes(@Param("deviceSn") String deviceSn,
                                                        @Param("res") String res,
                                                        @Param("responsePayload") String responsePayload,
                                                        @Param("responseDesc") String responseDesc);

    public int deleteIotManualControlEventByEventId(Long eventId);

    public int deleteIotManualControlEventByEventIds(Long[] eventIds);
}
