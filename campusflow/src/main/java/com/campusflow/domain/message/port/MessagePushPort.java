package com.campusflow.domain.message.port;

import java.util.Map;

public interface MessagePushPort {
    void broadcastMessage(Long studyGroupId, Map<String, Object> payload);

    void broadcastTyping(Long studyGroupId, Map<String, Object> payload);

    void broadcastReadReceipt(Long studyGroupId, Map<String, Object> payload);
}

