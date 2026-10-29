package com.ruoyi.binZhou.ld.mapper;

import com.ruoyi.binZhou.ld.domain.AgriChatSession;
import java.util.List;

public interface AgriChatSessionMapper {
	public AgriChatSession selectSessionById(Long sessionId);
	public List<AgriChatSession> selectSessionList(AgriChatSession session);
	public int insertSession(AgriChatSession session);
	public int updateSession(AgriChatSession session);
	public int deleteSessionById(Long sessionId);
}