package com.campusflow.application.notification.service;

import com.campusflow.application.notification.usecase.MarkNotificationsReadUseCase;
import com.campusflow.domain.notification.port.NotificationRepositoryPort;

public class MarkNotificationsReadService implements MarkNotificationsReadUseCase {
    private final NotificationRepositoryPort notificationRepositoryPort;

    public MarkNotificationsReadService(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    public void markAllRead(Long userId) {
        notificationRepositoryPort.markAllRead(userId);
    }
}

