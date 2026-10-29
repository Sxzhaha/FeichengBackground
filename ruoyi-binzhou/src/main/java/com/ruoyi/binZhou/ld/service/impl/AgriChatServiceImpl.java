package com.ruoyi.binZhou.ld.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.binZhou.ld.domain.AgriChatMessage;
import com.ruoyi.binZhou.ld.domain.AgriChatSession;
import com.ruoyi.binZhou.ld.mapper.AgriChatMessageMapper;
import com.ruoyi.binZhou.ld.mapper.AgriChatSessionMapper;
import com.ruoyi.binZhou.ld.service.IAgriChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgriChatServiceImpl implements IAgriChatService {

	@Autowired
	private AgriChatSessionMapper sessionMapper;

	@Autowired
	private AgriChatMessageMapper messageMapper;

	@Override
	public List<AgriChatSession> getUserSessions(Long userId) {
		AgriChatSession query = new AgriChatSession();
		query.setUserId(userId);
		List<AgriChatSession> sessions = sessionMapper.selectSessionList(query);

		for (AgriChatSession session : sessions) {
			List<AgriChatMessage> messages = messageMapper.selectMessagesBySessionId(session.getSessionId());
			session.setMessages(messages);
		}
		return sessions;
	}

	@Override
	@Transactional
	public void saveChatRound(Long sessionId, AgriChatMessage userMsg, AgriChatMessage aiMsg) {
		userMsg.setSessionId(sessionId);
		userMsg.setCreateTime(DateUtils.getNowDate());
		messageMapper.insertMessage(userMsg);

		aiMsg.setSessionId(sessionId);
		aiMsg.setCreateTime(DateUtils.getNowDate());
		messageMapper.insertMessage(aiMsg);

		AgriChatSession sessionUpdate = new AgriChatSession();
		sessionUpdate.setSessionId(sessionId);
		sessionUpdate.setUpdateTime(DateUtils.getNowDate());
		sessionMapper.updateSession(sessionUpdate);
	}

	@Override
	public AgriChatSession createSession(Long userId, String initialTitle) {
		AgriChatSession session = new AgriChatSession();
		session.setUserId(userId);
		session.setTitle(initialTitle);
		session.setCreateTime(DateUtils.getNowDate());
		session.setUpdateTime(DateUtils.getNowDate());
		sessionMapper.insertSession(session);
		return session;
	}

	@Override
	@Transactional
	public int deleteSession(Long sessionId) {
		return sessionMapper.deleteSessionById(sessionId);
	}
	@Override
	public int updateSession(AgriChatSession session) {
		return sessionMapper.updateSession(session);
	}
}