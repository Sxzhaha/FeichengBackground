package com.ruoyi.binZhou.ld.mapper;

import com.ruoyi.binZhou.ld.domain.AgriChatMessage;
import java.util.List;

public interface AgriChatMessageMapper {
	public List<AgriChatMessage> selectMessagesBySessionId(Long sessionId);
	public int insertMessage(AgriChatMessage message);
	public int deleteMessageBySessionId(Long sessionId);
}