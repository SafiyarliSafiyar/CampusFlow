package com.campusflow.infrastructure.websocket;

import com.campusflow.domain.message.model.ReadReceipt;
import com.campusflow.domain.message.port.MessagePushPort;
import com.campusflow.domain.message.port.ReadReceiptRepositoryPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
public class StudyGroupWebSocketHandler extends TextWebSocketHandler {
    private final StudyGroupWebSocketRegistry registry;
    private final ObjectMapper objectMapper;
    private final MessagePushPort messagePushPort;
    private final ReadReceiptRepositoryPort readReceiptRepositoryPort;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long groupId = (Long) session.getAttributes().get(StudyGroupWebSocketHandshakeInterceptor.ATTR_GROUP_ID);
        if (groupId != null) {
            registry.add(groupId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long groupId = (Long) session.getAttributes().get(StudyGroupWebSocketHandshakeInterceptor.ATTR_GROUP_ID);
        if (groupId != null) {
            registry.remove(groupId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), new TypeReference<>() {});
        String type = payload.get("type") instanceof String t ? t : null;
        if (type == null) {
            return;
        }

        Long groupId = (Long) session.getAttributes().get(StudyGroupWebSocketHandshakeInterceptor.ATTR_GROUP_ID);
        Long userId = (Long) session.getAttributes().get(StudyGroupWebSocketHandshakeInterceptor.ATTR_USER_ID);
        String username = (String) session.getAttributes().get(StudyGroupWebSocketHandshakeInterceptor.ATTR_USERNAME);
        if (groupId == null || userId == null || username == null) {
            return;
        }

        switch (type) {
            case "typing" -> handleTyping(groupId, userId, username, payload);
            case "read" -> handleRead(groupId, userId, payload);
            default -> {
                // ignore unknown
            }
        }
    }

    private void handleTyping(Long groupId, Long userId, String username, Map<String, Object> payload) {
        boolean isTyping = Boolean.TRUE.equals(payload.get("isTyping"));
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("studyGroupId", groupId);
        event.put("userId", userId);
        event.put("username", username);
        event.put("isTyping", isTyping);
        event.put("sentAt", LocalDateTime.now().toString());
        messagePushPort.broadcastTyping(groupId, event);
    }

    private void handleRead(Long groupId, Long userId, Map<String, Object> payload) {
        Object messageIdValue = payload.get("messageId");
        if (!(messageIdValue instanceof String messageId) || messageId.isBlank()) {
            return;
        }

        if (readReceiptRepositoryPort.findByMessageIdAndUserId(messageId, userId).isPresent()) {
            return;
        }

        ReadReceipt saved = readReceiptRepositoryPort.save(ReadReceipt.builder()
                .id(null)
                .studyGroupId(groupId)
                .messageId(messageId)
                .userId(userId)
                .readAt(LocalDateTime.now())
                .build());

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("studyGroupId", saved.getStudyGroupId());
        event.put("messageId", saved.getMessageId());
        event.put("userId", saved.getUserId());
        event.put("readAt", saved.getReadAt().toString());
        messagePushPort.broadcastReadReceipt(groupId, event);
    }
}

