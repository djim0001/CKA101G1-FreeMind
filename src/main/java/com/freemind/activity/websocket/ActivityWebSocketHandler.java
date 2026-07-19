package com.freemind.activity.websocket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ActivityWebSocketHandler extends TextWebSocketHandler {

    // key/value->活動ID, 這個活動頁面上所有的連線                              // 已實作Thread-safety
    private final Map<Integer, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer activityId = getActivityId(session);
        												// Thread-safety(檢查有無此活動,沒有就新增,拿到該set),把新建活動的連線加進set 
        roomSessions.computeIfAbsent(activityId, k -> new CopyOnWriteArraySet<>()).add(session);
        System.out.println("Session ID = " + session.getId() + ", connected; activityId = " + activityId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer activityId = getActivityId(session);
        Set<WebSocketSession> room = roomSessions.get(activityId);
        if (room != null) {
            room.remove(session);
        }
        System.out.println("Session ID = " + session.getId() + ", disconnected; code = " + status.getCode());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable e) throws Exception {
        System.out.println("Error: " + e);
    }

    // 給 Service 層呼叫的廣播方法(不是給前端訊息觸發,是後端業務邏輯執行完主動呼叫)
    public void broadcastSlotUpdate(Integer activityId, String jsonPayload) {
        Set<WebSocketSession> room = roomSessions.get(activityId);
        if (room == null) return;
        for (WebSocketSession s : room) {
            // 確認連線還開著
        		if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(jsonPayload));
                } catch (Exception e) {
                    System.out.println("廣播失敗, session ID = " + s.getId() + ", error: " + e);
                }
            }
        }
    }

    // 從連線URI取出活動ID,例如網址 /ws/activity/10 → 取出 10
    private Integer getActivityId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String idStr = path.substring(path.lastIndexOf('/') + 1);
        return Integer.parseInt(idStr);
    }
}
