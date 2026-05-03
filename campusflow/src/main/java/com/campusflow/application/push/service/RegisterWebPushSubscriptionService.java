package com.campusflow.application.push.service;

import com.campusflow.application.push.usecase.RegisterWebPushSubscriptionUseCase;
import com.campusflow.domain.user.model.PushToken;
import com.campusflow.domain.user.port.PushTokenRepositoryPort;
import com.campusflow.presentation.rest.push.PushSubscriptionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;

public class RegisterWebPushSubscriptionService implements RegisterWebPushSubscriptionUseCase {
    private final PushTokenRepositoryPort pushTokenRepositoryPort;
    private final ObjectMapper objectMapper;

    public RegisterWebPushSubscriptionService(PushTokenRepositoryPort pushTokenRepositoryPort, ObjectMapper objectMapper) {
        this.pushTokenRepositoryPort = pushTokenRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public void register(Long userId, PushSubscriptionRequest subscriptionRequest) {
        // store subscription JSON compactly in PushToken.token
        String tokenJson = toJson(subscriptionRequest);

        PushToken existing = pushTokenRepositoryPort.findByToken(tokenJson).orElse(null);
        pushTokenRepositoryPort.save(PushToken.builder()
                .id(existing == null ? null : existing.getId())
                .userId(userId)
                .token(tokenJson)
                .platform("WEB_PUSH")
                .createdAt(existing == null ? LocalDateTime.now() : existing.getCreatedAt())
                .lastSeenAt(LocalDateTime.now())
                .build());
    }

    private String toJson(PushSubscriptionRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize push subscription", e);
        }
    }
}
