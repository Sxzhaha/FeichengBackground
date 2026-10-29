package com.ruoyi.iot.ld.mapper;

import java.util.List;
import com.ruoyi.iot.ld.domain.IotControlEvent;
import org.apache.ibatis.annotations.Param;

/**
 * 控制事件下发Mapper接口
 */
public interface IotControlEventMapper
{
    public IotControlEvent selectIotControlEventByEventId(Long eventId);

    public List<IotControlEvent> selectIotControlEventList(IotControlEvent iotControlEvent);

    public int insertIotControlEvent(IotControlEvent iotControlEvent);

    public int updateIotControlEvent(IotControlEvent iotControlEvent);

    public int updateIotControlEventResponseByRes(@Param("deviceSn") String deviceSn,
                                                  @Param("res") String res,
                                                  @Param("responsePayload") String responsePayload,
                                                  @Param("responseDesc") String responseDesc);

    public int deleteIotControlEventByEventId(Long eventId);

    public int deleteIotControlEventByEventIds(Long[] eventIds);
}
