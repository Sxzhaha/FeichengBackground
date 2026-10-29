package com.ruoyi.binZhou.wr.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 预警消息 WebSocket 服务端
 */
@ServerEndpoint("/ws/warning") // 前端连接的地址
@Component
public class WarningWebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WarningWebSocketServer.class);

    // 用来存放每个客户端对应的 WebSocket 对象
    private static CopyOnWriteArraySet<WarningWebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        log.info("🔗 [WebSocket] 前端连接成功！当前在线大屏/浏览器数量: {}", webSocketSet.size());
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info("❌ [WebSocket] 前端连接断开！当前在线数量: {}", webSocketSet.size());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("⚠️ [WebSocket] 发生错误", error);
    }

    /**
     * 发送广播消息的核心方法
     */
    public static void sendInfo(String message) {
        for (WarningWebSocketServer item : webSocketSet) {
            try {
                item.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("发送消息失败", e);
            }
        }
    }
}