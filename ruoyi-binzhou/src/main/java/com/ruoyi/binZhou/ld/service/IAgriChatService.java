package com.ruoyi.binZhou.ld.service;

import com.ruoyi.binZhou.ld.domain.AgriChatSession;
import com.ruoyi.binZhou.ld.domain.AgriChatMessage;
import java.util.List;

public interface IAgriChatService {
	public List<AgriChatSession> getUserSessions(Long userId);
	public void saveChatRound(Long sessionId, AgriChatMessage userMsg, AgriChatMessage aiMsg);
	public AgriChatSession createSession(Long userId, String initialTitle);
	public int deleteSession(Long sessionId);
	public int updateSession(AgriChatSession session);
}