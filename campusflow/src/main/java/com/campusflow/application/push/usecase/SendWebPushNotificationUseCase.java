package com.campusflow.application.push.usecase;

import java.util.Map;

public interface SendWebPushNotificationUseCase {
    void sendToUser(Long userId, String title, String body, Map<String, Object> data);
}

