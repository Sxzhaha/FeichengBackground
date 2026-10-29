package com.ruoyi.binZhou.ld.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.binZhou.ld.domain.AgriChatMessage;
import com.ruoyi.binZhou.ld.domain.AgriChatSession;
import com.ruoyi.binZhou.ld.domain.Knowledge;
import com.ruoyi.binZhou.ld.service.IAgriChatService;
import com.ruoyi.binZhou.ld.service.IKnowledgeService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/binZhou/ld/chat")
public class AgriChatController extends BaseController {

	@Autowired
	private IAgriChatService chatService;

	@Autowired
	private IKnowledgeService knowledgeService;

	@org.springframework.beans.factory.annotation.Value("${dashscope.apiKey}")
	private String dashscopeApiKey;

	@org.springframework.beans.factory.annotation.Value("${dashscope.model:qwen3.7-plus}")
	private String dashscopeModel;

	/**
	 * 代理大模型流式对话请求
	 */
	@PostMapping("/completions")
	public void completions(@RequestBody String requestBody, javax.servlet.http.HttpServletResponse response) {
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("X-Accel-Buffering", "no");

		java.net.HttpURLConnection connection = null;
		try {
			// 1. 解析请求体
			com.fasterxml.jackson.databind.JsonNode reqJson = new com.fasterxml.jackson.databind.ObjectMapper().readTree(requestBody);
			String text = reqJson.path("text").asText("");
			com.fasterxml.jackson.databind.JsonNode historyMessages = reqJson.path("historyMessages");
			com.fasterxml.jackson.databind.JsonNode images = reqJson.path("images");

			// 2. 获取枣树病虫害字典数据
			Knowledge query = new Knowledge();
			query.setPlusDsiCategorythird("枣树");
			List<Knowledge> dbPestList = knowledgeService.selectKnowledgeList(query);

			// 进行名称去重，避免重复
			List<Knowledge> uniquePestList = new java.util.ArrayList<>();
			java.util.Set<String> seen = new java.util.HashSet<>();
			for (Knowledge k : dbPestList) {
				if (k.getPlusDsiName() != null && !seen.contains(k.getPlusDsiName())) {
					seen.add(k.getPlusDsiName());
					uniquePestList.add(k);
				}
			}

			// 3. 构造参考字典与提示词
			StringBuilder dictNamesBuilder = new StringBuilder();
			StringBuilder dbReferencePromptBuilder = new StringBuilder();
			if (!uniquePestList.isEmpty()) {
				dbReferencePromptBuilder.append("\n【枣树病虫害数据库参考资料（你的回答必须严格符合以下数据描述）】：\n");
				for (int i = 0; i < uniquePestList.size(); i++) {
					Knowledge item = uniquePestList.get(i);
					if (i > 0) {
						dictNamesBuilder.append("、");
						dbReferencePromptBuilder.append("\n---\n");
					}
					dictNamesBuilder.append(item.getPlusDsiName());
					
					dbReferencePromptBuilder.append("病害名称：").append(item.getPlusDsiName()).append("\n")
						.append("症状特征：").append(item.getPlusDsiDamagesym() != null ? item.getPlusDsiDamagesym() : "无").append("\n")
						.append("发生原因：").append(item.getPlusDsiOfactor() != null ? item.getPlusDsiOfactor() : "无").append("\n")
						.append("防治建议：").append(item.getPlusDsiCmethod() != null ? item.getPlusDsiCmethod() : "无");
				}
			}
			String dictNames = dictNamesBuilder.toString();
			String dbReferencePrompt = dbReferencePromptBuilder.toString();

			// 进行预匹配
			String dbContextStr = "";
			Knowledge matchedDbItem = null;
			if (text != null && !text.trim().isEmpty()) {
				for (Knowledge item : uniquePestList) {
					if (item.getPlusDsiName() != null && text.contains(item.getPlusDsiName())) {
						matchedDbItem = item;
						break;
					}
				}
			}

			if (matchedDbItem != null) {
				dbContextStr = "\n【数据库中该病害的参考资料】：\n" +
					"病害名称：" + matchedDbItem.getPlusDsiName() + "\n" +
					"英文名称：" + (matchedDbItem.getPlusDsiNameeng() != null ? matchedDbItem.getPlusDsiNameeng() : "无") + "\n" +
					"简介：" + (matchedDbItem.getPlusDsiIntroduction() != null ? matchedDbItem.getPlusDsiIntroduction() : "无") + "\n" +
					"危害症状：" + (matchedDbItem.getPlusDsiDamagesym() != null ? matchedDbItem.getPlusDsiDamagesym() : "无") + "\n" +
					"发生规律：" + (matchedDbItem.getPlusDsiOfactor() != null ? matchedDbItem.getPlusDsiOfactor() : "无") + "\n" +
					"防治方法：" + (matchedDbItem.getPlusDsiCmethod() != null ? matchedDbItem.getPlusDsiCmethod() : "无");
			}

			// 组装系统提示词
			String systemPrompt = "你是一位精通枣树种植与植保技术的农业专家“滨州枣树农业助手”。\n" +
				"强制要求：\n" +
				"1. 回答要果断，思考时间不要超过10秒，最终回答文本必须是正常的自然语言，绝对禁止使用Markdown特殊字符（如星号、井号等）。\n" +
				"2. 如果属于病虫害诊断，必须从以下已知字典中匹配最接近的病害名称：[" + dictNames + "]。如果不属于字典中的任何一种，请在第一点诚实作答。\n" +
				"3. 当你给出诊断和建议时，必须严格参考下面提供的数据库参考资料中对应病虫害的信息，确保你输出的“病害名称”、“症状特征”、“发生原因”和“防治建议”与数据库中的记录完全吻合，不得凭空捏造。\n" +
				(dbContextStr.isEmpty() ? "" : ("特别提醒：检测到用户问题与病害【" + matchedDbItem.getPlusDsiName() + "】密切相关，请特别参考其专属数据：" + dbContextStr + "\n")) +
				"\n" +
				"如果是病虫害则诊断问答必须严格遵循以下结构输出：\n" +
				"一、病害名称\n" +
				"（直接写出匹配到的名称，例如：枣尺蠖）\n" +
				"二、症状特征\n" +
				"（参考数据库中的症状特征进行详细回答，回答要跟数据库字段内容吻合）\n" +
				"三、发生原因\n" +
				"（参考数据库中的发生原因进行简述回答，回答要跟数据库字段内容吻合）\n" +
				"四、防治建议\n" +
				"（参考数据库中的防治建议进行回答，回答要跟数据库字段内容吻合）" + 
				(dbReferencePrompt.isEmpty() ? "" : ("\n\n" + dbReferencePrompt));

			// 4. 构建阿里百炼需要的请求载荷
			JSONObject dashscopeReq = new JSONObject();
			dashscopeReq.put("model", dashscopeModel);
			dashscopeReq.put("stream", true);

			JSONObject streamOptions = new JSONObject();
			streamOptions.put("include_usage", true);
			dashscopeReq.put("stream_options", streamOptions);

			JSONObject extraBody = new JSONObject();
			extraBody.put("enable_thinking", true);
			dashscopeReq.put("extra_body", extraBody);

			JSONArray messages = new JSONArray();

			// 注入系统提示词
			JSONObject systemMessage = new JSONObject();
			systemMessage.put("role", "system");
			systemMessage.put("content", systemPrompt);
			messages.add(systemMessage);

			// 注入历史消息
			if (historyMessages.isArray()) {
				for (com.fasterxml.jackson.databind.JsonNode historyMessage : historyMessages) {
					messages.add(JSON.parseObject(historyMessage.toString()));
				}
			}

			// 注入当前用户提问内容 (图文混合)
			JSONObject userMessage = new JSONObject();
			userMessage.put("role", "user");

			JSONArray contentArray = new JSONArray();
			if (images.isArray()) {
				for (com.fasterxml.jackson.databind.JsonNode image : images) {
					String base64 = image.asText();
					JSONObject imageObj = new JSONObject();
					imageObj.put("type", "image_url");
					
					JSONObject imageUrl = new JSONObject();
					imageUrl.put("url", base64);
					imageObj.put("image_url", imageUrl);
					
					contentArray.add(imageObj);
				}
			}

			JSONObject textObj = new JSONObject();
			textObj.put("type", "text");
			textObj.put("text", (text != null && !text.trim().isEmpty()) ? text : "请诊断这些枣树病虫害情况并提供建议。");
			contentArray.add(textObj);

			userMessage.put("content", contentArray);
			messages.add(userMessage);

			dashscopeReq.put("messages", messages);

			String finalRequestBody = dashscopeReq.toJSONString();

			// 5. 建立网络连接，发送请求并流式读回
			java.net.URL url = new java.net.URL("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
			connection = (java.net.HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("Authorization", "Bearer " + dashscopeApiKey);
			connection.setRequestProperty("Accept", "text/event-stream");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(120000);

			// 写入请求体
			try (java.io.OutputStream os = connection.getOutputStream()) {
				os.write(finalRequestBody.getBytes(java.nio.charset.StandardCharsets.UTF_8));
				os.flush();
			}

			int responseCode = connection.getResponseCode();
			if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
				try (java.io.InputStream is = connection.getInputStream();
					 java.io.OutputStream out = response.getOutputStream()) {
					byte[] buffer = new byte[2048];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						out.write(buffer, 0, bytesRead);
						out.flush();
						response.flushBuffer();
					}
				}
			} else {
				logger.error("DashScope API error: response code = " + responseCode);
				response.setStatus(responseCode);
				response.setContentType("application/json;charset=UTF-8");
				try (java.io.InputStream es = connection.getErrorStream();
					 java.io.OutputStream out = response.getOutputStream()) {
					if (es != null) {
						byte[] buffer = new byte[2048];
						int bytesRead;
						while ((bytesRead = es.read(buffer)) != -1) {
							out.write(buffer, 0, bytesRead);
							out.flush();
							response.flushBuffer();
						}
					} else {
						response.sendError(responseCode, "API Error");
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error proxying DashScope API", e);
			try {
				response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (Exception ignored) {}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * 获取历史会话列表
	 */
	@GetMapping("/list")
	public AjaxResult list() {
		Long userId = getUserId();
		List<AgriChatSession> sessions = chatService.getUserSessions(userId);
		return AjaxResult.success(sessions);
	}

	/**
	 * 新建会话
	 */
	@PostMapping("/session")
	public AjaxResult createSession(@RequestBody AgriChatSession session) {
		Long userId = getUserId();
		AgriChatSession newSession = chatService.createSession(userId, session.getTitle());
		return AjaxResult.success(newSession);
	}

	/**
	 * 删除会话
	 */
	@Log(title = "删除问答会话", businessType = BusinessType.DELETE)
	@DeleteMapping("/session/{sessionId}")
	public AjaxResult removeSession(@PathVariable Long sessionId) {
		return toAjax(chatService.deleteSession(sessionId));
	}

	/**
	 * 保存一轮完整的对话记录
	 */
	@PostMapping("/saveRound")
	public AjaxResult saveRound(@RequestBody ChatRoundReq req) {
		chatService.saveChatRound(req.getSessionId(), req.getUserMessage(), req.getAiMessage());
		return AjaxResult.success();
	}
	/**
	 * 更新会话 (如修改标题)
	 */
	@Log(title = "更新问答会话", businessType = BusinessType.UPDATE)
	@PutMapping("/session")
	public AjaxResult updateSession(@RequestBody AgriChatSession session) {
		// 出于安全考虑，也可以加一句校验，确保只能修改自己的会话
		session.setUpdateTime(com.ruoyi.common.utils.DateUtils.getNowDate());
		// 调用我们刚才在 AgriChatServiceImpl 里的 sessionMapper.updateSession
		// (为了规范，你可以在 Service 层加个 public int updateSession(AgriChatSession session) 方法代理一下)
		return toAjax(chatService.updateSession(session));
	}
}

/**
 * 内部 DTO：用于接收前端传来的对话轮次
 */
class ChatRoundReq {
	private Long sessionId;
	private AgriChatMessage userMessage;
	private AgriChatMessage aiMessage;

	public Long getSessionId() { return sessionId; }
	public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
	public AgriChatMessage getUserMessage() { return userMessage; }
	public void setUserMessage(AgriChatMessage userMessage) { this.userMessage = userMessage; }
	public AgriChatMessage getAiMessage() { return aiMessage; }
	public void setAiMessage(AgriChatMessage aiMessage) { this.aiMessage = aiMessage; }
}
