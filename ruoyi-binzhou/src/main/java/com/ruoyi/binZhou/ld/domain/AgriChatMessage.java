package com.ruoyi.binZhou.ld.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AgriChatMessage extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private Long messageId;
	private Long sessionId;
	private String role;
	private String content;
	private String reasoningContent;
	private String images; // JSON 字符串格式存储

	public Long getMessageId() { return messageId; }
	public void setMessageId(Long messageId) { this.messageId = messageId; }
	public Long getSessionId() { return sessionId; }
	public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
	public String getReasoningContent() { return reasoningContent; }
	public void setReasoningContent(String reasoningContent) { this.reasoningContent = reasoningContent; }
	public String getImages() { return images; }
	public void setImages(String images) { this.images = images; }

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("messageId", getMessageId())
				.append("sessionId", getSessionId())
				.append("role", getRole())
				.append("content", getContent())
				.append("createTime", getCreateTime())
				.toString();
	}
}