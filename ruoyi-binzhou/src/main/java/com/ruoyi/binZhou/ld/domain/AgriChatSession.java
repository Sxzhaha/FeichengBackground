package com.ruoyi.binZhou.ld.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.List;

public class AgriChatSession extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private Long sessionId;
	private Long userId;
	private String title;
	private String delFlag;

	// 非数据库字段：用于一次性返回会话及其包含的所有消息给前端
	private List<AgriChatMessage> messages;

	public Long getSessionId() { return sessionId; }
	public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getDelFlag() { return delFlag; }
	public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
	public List<AgriChatMessage> getMessages() { return messages; }
	public void setMessages(List<AgriChatMessage> messages) { this.messages = messages; }

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("sessionId", getSessionId())
				.append("userId", getUserId())
				.append("title", getTitle())
				.append("createTime", getCreateTime())
				.toString();
	}
}