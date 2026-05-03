package com.campusflow.infrastructure.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class StudyGroupWebSocketRegistry {
    private final Map<Long, Set<WebSocketSession>> sessionsByGroup = new ConcurrentHashMap<>();

    public void add(Long groupId, WebSocketSession session) {
        sessionsByGroup.computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(Long groupId, WebSocketSession session) {
        Set<WebSocketSession> sessions = sessionsByGroup.get(groupId);
        if (sessions == null) {
            return;
        }
        sessions.remove(session);
        if (sessions.isEmpty()) {
            sessionsByGroup.remove(groupId);
        }
    }

    public void broadcast(Long groupId, String payload) {
        Set<WebSocketSession> sessions = sessionsByGroup.get(groupId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        TextMessage message = new TextMessage(payload);
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                remove(groupId, session);
                continue;
            }
            try {
                session.sendMessage(message);
            } catch (IOException _e) {
                remove(groupId, session);
            }
        }
    }
}

