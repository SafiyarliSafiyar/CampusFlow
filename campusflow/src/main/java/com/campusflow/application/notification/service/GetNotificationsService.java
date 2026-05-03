package com.campusflow.application.notification.service;

import com.campusflow.application.notification.usecase.GetNotificationsUseCase;
import com.campusflow.domain.notification.model.Notification;
import com.campusflow.domain.notification.port.NotificationRepositoryPort;
import java.util.List;

public class GetNotificationsService implements GetNotificationsUseCase {
    private final NotificationRepositoryPort notificationRepositoryPort;

    public GetNotificationsService(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    public List<Notification> getLatest(Long userId, int limit) {
        return notificationRepositoryPort.findLatestForUser(userId, limit);
    }
}

