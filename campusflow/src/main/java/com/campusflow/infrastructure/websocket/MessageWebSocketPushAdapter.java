package com.campusflow.infrastructure.websocket;

import com.campusflow.domain.message.port.MessagePushPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageWebSocketPushAdapter implements MessagePushPort {
    private final StudyGroupWebSocketRegistry registry;
    private final ObjectMapper objectMapper;

    @Override
    public void broadcastMessage(Long studyGroupId, Map<String, Object> payload) {
        broadcast(studyGroupId, "message", payload);
    }

    @Override
    public void broadcastTyping(Long studyGroupId, Map<String, Object> payload) {
        broadcast(studyGroupId, "typing", payload);
    }

    @Override
    public void broadcastReadReceipt(Long studyGroupId, Map<String, Object> payload) {
        broadcast(studyGroupId, "read", payload);
    }

    private void broadcast(Long studyGroupId, String type, Map<String, Object> payload) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("type", type);
        envelope.putAll(payload);
        try {
            registry.broadcast(studyGroupId, objectMapper.writeValueAsString(envelope));
        } catch (JsonProcessingException _e) {
            // ignore serialization errors
        }
    }
}

